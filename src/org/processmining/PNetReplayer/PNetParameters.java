package org.processmining.PNetReplayer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.utils.XEventAnalysis;

/**
 * 
 * @author saadn The class is here to replicate the PNetReplayer
 *         CostBasedCompleteParam and its UI For more info, on these clases,
 *         search PNetReplayer plugin.
 * 
 *         Values in this class are kept default.
 */
public class PNetParameters {

	private PetrinetGraph net;
	private XLog log;

	private final int limExpInstances = 2000;
	// default value
	private static final int DEFCOSTMOVEONLOG = 1;
	private static final int DEFCOSTMOVEONMODEL = 1;
	private Map<Transition, Integer> mapTrans2RowIndex = new HashMap<Transition, Integer>();
	private Map<XEventClass, Integer> mapXEvClass2RowIndex = new HashMap<XEventClass, Integer>();
	private Map<Transition, Integer> mapSync2RowIndex = new HashMap<Transition, Integer>();

	private Collection<Transition> transCol;
	private Collection<XEventClass> evClassCol;

	public PNetParameters( PetrinetGraph net, XLog log) {
		this.net = net;
		this.log = log;
		calculatedCost();
	}

	private void calculatedCost() {
		transCol = net.getTransitions();
		evClassCol = XEventAnalysis.getEventClassCollection(log);
		
		populateMoveOnModelPanel(transCol);
		populateMoveOnLogPanel(evClassCol);
		populateMoveSyncPanel(transCol);
	}


	public IPNReplayParameter createParameters(IPNReplayParameter selectedParam) {
		System.out.println("PNetParameters: createParameters()");

		// Creating Parameters
		XEventClass dummyEvClass = new XEventClass("Dummy", -1);
//		selectedParam = new CostBasedCompleteParam(XEventAnalysis.getEventClassCollection(log), dummyEvClass,
//				net.getTransitions(), 1, 1);
		selectedParam = new CostBasedCompleteParam (mapXEvClass2RowIndex, mapTrans2RowIndex,mapSync2RowIndex);
		return selectedParam;
	}

	/**
	 * Generate event class move on log panel
	 * 
	 * @param eventClassesName
	 */
	private void populateMoveOnLogPanel(Collection<XEventClass> eventClassesName) {
		mapXEvClass2RowIndex.clear();

		// move on log cost
		List<XEventClass> sortedEventClasses = new ArrayList<XEventClass>(eventClassesName);
		Collections.sort(sortedEventClasses, new Comparator<XEventClass>() {
			public int compare(XEventClass t1, XEventClass t2) {
				return t1.getId().compareTo(t2.getId());
			}
		});
		int evClassRowCounter = 0;
		for (XEventClass evClass : sortedEventClasses) {
			mapXEvClass2RowIndex.put(evClass, evClassRowCounter);
			evClassRowCounter++;
		}
	}

	/**
	 * Generate event class move on Model panel
	 * 
	 * @param eventClassesName
	 */
	private void populateMoveOnModelPanel(Collection<Transition> transitions) {
		// create table to map move on model cost
		List<Transition> sortedTransitions = new ArrayList<Transition>(transitions);
		Collections.sort(sortedTransitions, new Comparator<Transition>() {
			public int compare(Transition t1, Transition t2) {
				return t1.getLabel().compareTo(t2.getLabel());
			}
		});
		Object[][] tableContent = new Object[transitions.size()][2];
		int rowCounter = 0;
		for (Transition trans : sortedTransitions) {

			tableContent[rowCounter] = new Object[] { trans.getLabel(), trans.isInvisible() ? 0 : DEFCOSTMOVEONMODEL };

			mapTrans2RowIndex.put(trans, rowCounter);
			rowCounter++;
		}

	}

	/**
	 * Generate event class move on sync envents panel
	 * 
	 * @param eventClassesName
	 */
	private void populateMoveSyncPanel(Collection<Transition> transitions) {
		// create table to map move on model cost
		List<Transition> sortedTransitions = new ArrayList<Transition>(transitions);
		Collections.sort(sortedTransitions, new Comparator<Transition>() {
			public int compare(Transition t1, Transition t2) {
				return t1.getLabel().compareTo(t2.getLabel());
			}
		});
		Object[][] tableContent = new Object[transitions.size()][2];
		int rowCounter = 0;
		for (Transition trans : sortedTransitions) {
			if (!trans.isInvisible()) {
					tableContent[rowCounter] = new Object[] { trans.getLabel(), 0 };
				
				mapSync2RowIndex.put(trans, rowCounter);
				rowCounter++;
			}
		}
	}

	/**
	 * Get map from event class to cost of move on log
	 * 
	 * @return
	 */
	public Map<XEventClass, Integer> getMapEvClassToCost() {
		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>();
		for (XEventClass evClass : mapXEvClass2RowIndex.keySet()) {
			mapEvClass2Cost.put(evClass, DEFCOSTMOVEONLOG);
		}
		return mapEvClass2Cost;
	}

	/**
	 * get penalty when move on model is performed
	 * 
	 * @return
	 */
	public Map<Transition, Integer> getTransitionWeight() {
		Map<Transition, Integer> costs = new HashMap<Transition, Integer>();
		for (Transition trans : mapTrans2RowIndex.keySet()) {
			costs.put(trans, DEFCOSTMOVEONMODEL);
		}
		return costs;
	}

	/**
	 * get cost of doing synchronous moves
	 * 
	 * @return
	 */
	public Map<Transition, Integer> getSyncCost() {
		Map<Transition, Integer> costs = new HashMap<Transition, Integer>(1);
		for (Entry<Transition, Integer> entry : mapSync2RowIndex.entrySet()) {
			costs.put(entry.getKey(), 0);
		}
		return costs;
	}

	/**
	 * get maximum number of explored states before stop exploration
	 * 
	 * @return
	 */
	public Integer getMaxNumOfStates() {
		return limExpInstances;
	}
}
