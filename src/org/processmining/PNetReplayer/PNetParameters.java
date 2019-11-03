package org.processmining.PNetReplayer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
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

	private PluginContext context;
	private PetrinetGraph net;
	private XLog log;
	private TransEvClassMapping mapping;

	private final int limExpInstances = 2000;
	// default value
	private static final int DEFCOSTMOVEONLOG = 1;
	private static final int DEFCOSTMOVEONMODEL = 1;
	private Map<Transition, Integer> mapTrans2RowIndex = new HashMap<Transition, Integer>();
	private Map<XEventClass, Integer> mapXEvClass2RowIndex = new HashMap<XEventClass, Integer>();
	private Map<Transition, Integer> mapSync2RowIndex = new HashMap<Transition, Integer>();

	private Collection<Transition> transCol;
	private Collection<XEventClass> evClassCol;

	// precalculated initial and final markings
	protected Marking initMarking;
	protected Marking[] finalMarkings;

	public PNetParameters(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
		this.net = net;
		this.log = log;
		this.context = context;
		this.mapping = mapping;
		calculatedCost();
	}

	private void calculatedCost() {
		// get initial marking
		initMarking = getInitialMarking(context, net);
		// get final markings
		finalMarkings = getFinalMarkings(context, net, initMarking);
		transCol = net.getTransitions();
		// populate event classes
		XEventClassifier classifier = mapping.getEventClassifier();
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses eventClassesName = summary.getEventClasses();
		evClassCol = new HashSet<XEventClass>(eventClassesName.getClasses());
		evClassCol.add(mapping.getDummyEventClass());

		populateMoveOnModelPanel(transCol);
		populateMoveOnLogPanel(evClassCol);
		populateMoveSyncPanel(transCol);
	}

	public IPNReplayParameter constructReplayParameter() {
		System.out.println("PNetParameters: createParameters()");

		CostBasedCompleteParam paramObj = new CostBasedCompleteParam(getMapEvClassToCost(), getTransitionWeight());
		paramObj.setMapSync2Cost(getSyncCost());
		paramObj.setMaxNumOfStates(getMaxNumOfStates());
		paramObj.setInitialMarking(initMarking);
		paramObj.setFinalMarkings(finalMarkings);
		paramObj.setUsePartialOrderedEvents(false);
		
		return paramObj;
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

	/**
	 * get initial marking
	 * 
	 * @param context
	 * @param net
	 * @return
	 */
	private Marking getInitialMarking(PluginContext context, PetrinetGraph net) {
		// check connection between petri net and marking
		Marking initMarking = null;
		try {
			initMarking = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net)
					.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained exc) {
			initMarking = new Marking();
		}
		return initMarking;
	}

	/**
	 * Derive final markings from accepting states
	 * 
	 * @param context
	 * @param net
	 * @param initMarking
	 * @return
	 */
	private Marking[] getFinalMarkings(PluginContext context, PetrinetGraph net, Marking initMarking) {
		// check if final marking exists
		Marking[] finalMarkings = null;
		try {
			Collection<FinalMarkingConnection> finalMarkingConnections = context.getConnectionManager()
					.getConnections(FinalMarkingConnection.class, context, net);
			if (finalMarkingConnections.size() != 0) {
				Set<Marking> setFinalMarkings = new HashSet<Marking>();
				for (FinalMarkingConnection conn : finalMarkingConnections) {
					setFinalMarkings.add((Marking) conn.getObjectWithRole(FinalMarkingConnection.MARKING));
				}
				finalMarkings = setFinalMarkings.toArray(new Marking[setFinalMarkings.size()]);
			} else {
				finalMarkings = new Marking[0];
			}
		} catch (ConnectionCannotBeObtained exc) {
			// no final marking provided, give an empty marking
			finalMarkings = new Marking[0];
		}
		return finalMarkings;
	}
}
