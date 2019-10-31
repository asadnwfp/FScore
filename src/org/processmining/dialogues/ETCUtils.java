package org.processmining.dialogues;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCAlgorithm;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.etconformance.ETCSettings;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import nl.tue.astar.AStarException;

public class ETCUtils {
	private Marking marking;
	private EvClassLogPetrinetConnection con;
	private TransEvClassMapping mapping;
	private ETCResults res;
	private PluginContext context;
	private XLog log;
	private Petrinet net;
	private XEventClass xEventClass;
	private XEventClassifier xEventClassifier;

	private IPNReplayAlgorithm[] listAlgorithms;

	private boolean checkFirstRun = true;

	public ETCUtils(PluginContext context, XLog log, Petrinet net) {
		setContext(context);
		setLog(log);
		setNet(net);
	}

	public void setContext(PluginContext context) {
		this.context = context;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	public void setNet(Petrinet net) {
		this.net = net;
	}

	public ETCResults getRes() {
		return res;
	}

	private void setxEventClass(XEventClass xEventClass) {
		this.xEventClass = xEventClass;
	}

	private void setxEventClassifier(XEventClassifier xEventClassifier) {
		this.xEventClassifier = xEventClassifier;
	}

	public void performETCUtils() {
		marking = getMarkings();
		if (checkFirstRun) {
			con = getEvClassLogPetrinetConnection();
			mapping = getTransEvClassMapping();
		} else {
			mapping = constructMapping();
		}
		res = getETCResults();
	}

	private Marking getMarkings() {
		System.out.println("ETCUtils: getMarkings");
		Marking marking;
		try {
			InitialMarkingConnection connection = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net);
			marking = connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained ex) {
			cancel(context, "Petri Net lacks initial marking");
			return null;
		}
		return marking;
	}

	private EvClassLogPetrinetConnection getEvClassLogPetrinetConnection() {
		System.out.println("ETCUtils: getEvClassLogPetrinetConnection");
		checkFirstRun = false;
		//Get PetriNet - Log Connection
		EvClassLogPetrinetConnection con;
		try {
			con = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, log,
					net);
		} catch (ConnectionCannotBeObtained ex) {
			cancel(context, "No connection between Log and Petri Net");
			return null;
		}
		return con;
	}

	private TransEvClassMapping getTransEvClassMapping() {
		System.out.println("ETCUtils: getTransEvClassMapping");
		TransEvClassMapping mapping = con.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		System.out.println("XEventClass String: " + mapping.getDummyEventClass().getId());
		System.out.println("XEventClass Index: " + mapping.getDummyEventClass().getIndex());
		System.out.println("XEventClassifier name: " + mapping.getEventClassifier().name());

		this.setxEventClass(mapping.getDummyEventClass());
		this.setxEventClassifier(mapping.getEventClassifier());

		return mapping;
	}

	public TransEvClassMapping constructMapping() {

		TransEvClassMapping mapping = new TransEvClassMapping(xEventClassifier, xEventClass);

		XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

		for (Transition t : net.getTransitions()) {
			boolean mapped = false;

			for (XEventClass evClass : summary.getEventClasses().getClasses()) {
				String id = evClass.getId();
				String label = t.getLabel();

				if (label.equals(id)) {
					mapping.put(t, evClass);
					mapped = true;
					break;
				}
			}
		}
		System.out.println("mapping");
		System.out.println(mapping);

		return mapping;
	}

	private ETCResults getETCResults() {
		System.out.println("ETCUtils: getETCResults");
		ETCResults res = new ETCResults();//Create the result object to store the settings on it
		ETCSettings sett = new ETCSettings(res);
		sett.initComponents();
		try {
			ETCAlgorithm.exec(context, log, net, marking, mapping, res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Cancel the future results, display a error message and return all nulls.
	 * 
	 * @param context
	 *            Context of the plug-in.
	 * @param msg
	 *            Error message.
	 * @return Return an object array of nulls;
	 */
	private void cancel(PluginContext context, String msg) {
		context.log(msg);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		//return new Object[] { null, null };
	}

	public PNRepResult getFitness() {
		//		IPNReplayAlgorithm algo = 
		return null;
	}

	private IPNReplayAlgorithm[] getAvailabelAlgorithms() {
		// get all algorithms from the framework
		Set<Class<?>> coverageEstimatorClasses = context.getPluginManager()
				.getKnownClassesAnnotatedWith(PNReplayAlgorithm.class);
		IPNReplayAlgorithm[] availAlgorithms = null;
		if (coverageEstimatorClasses != null) {
			List<IPNReplayAlgorithm> algList = new LinkedList<IPNReplayAlgorithm>();
			for (Class<?> coverClass : coverageEstimatorClasses) {
				try {
					IPNReplayAlgorithm alg = (IPNReplayAlgorithm) coverClass.newInstance();
					if (alg.isReqWOParameterSatisfied(context, net, log, mapping)) {
						algList.add(alg);
					}
				} catch (InstantiationException e1) {
					// do nothing
				} catch (IllegalAccessException e1) {
					// do nothing
				} catch (Exception exc) {
					// do nothing
				}
			}
			Collections.sort(algList, new Comparator<IPNReplayAlgorithm>() {

				public int compare(IPNReplayAlgorithm o1, IPNReplayAlgorithm o2) {
					return o1.toString().compareTo(o2.toString());
				}
			});
			availAlgorithms = algList.toArray(new IPNReplayAlgorithm[algList.size()]);

		}
		return availAlgorithms;
	}

	/**
	 * Obtain the selected algorithm
	 * 
	 * @return
	 */
	public Object[] getAlgorithmAndParam() {
		//		IPNReplayAlgorithm[] availAlgorithms = getAvailabelAlgorithms();
		listAlgorithms = getAvailabelAlgorithms();
		IPNReplayAlgorithm selectedAlg = getAlgo();
		IPNReplayParamProvider paramProvider = selectedAlg.constructParamProvider(context, net,
				log, mapping) ;
		JComponent paramComponent = paramProvider.constructUI();
		
		
		IPNReplayParameter algParameters = paramProvider.constructReplayParameter(paramComponent);
		

//AlgParamenter Class is : class org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam
//TESTING: method replayLogPrivate is started

		System.out.println("Algoritham Name: " + selectedAlg);
		return new Object[] { selectedAlg, algParameters };

	}

	public IPNReplayAlgorithm getAlgo() {
		System.out.println("ETCUtils: getAlgo");

		IPNReplayAlgorithm selectedAlg = null;
		for (IPNReplayAlgorithm algo : listAlgorithms) {
			System.out.println("########## Algo ############");
			System.out.println("Name of Algorithm: " + algo.toString());
			System.out.println("Algorithm Class: " + algo.getClass());
			//A* Cost-based Fitness Express with ILP (swap+replacement aware), assuming at most 32767 tokens in each place.
			//A* Cost-based Fitness Express with ILP and Partial aware, assuming at most 32767 tokens in each place.
			//A* Cost-based Replay with ILP with move model restriction, assuming at most 32767 tokens in each place.
			//A* Cost-based Replay without ILP with move model restriction, assuming at most 32767 tokens in each place.
			//ILP-based replayer assuming at most 32767 tokens in each place.
			//Splitting replayer assuming at most 127 tokens in each place.
		}
		for (IPNReplayAlgorithm algo : listAlgorithms) {
			System.out.println("########## Selected Algo ############");
			if (algo.toString().contentEquals("ILP-based replayer assuming at most 32767 tokens in each place.")) {
				System.out.println("Name of Algorithm: " + algo.toString());
				System.out.println("Algorithm Class: " + algo.getClass());
				selectedAlg = algo;
			}
		}
		return selectedAlg;
	}
	
	public PNRepResult getPnRepResult(LogReplayer logReplayer) {
		Object[] algParams = getAlgorithmAndParam();
		try {
			return logReplayer.replayLogPrivate(
					context, net, log, mapping, (IPNReplayAlgorithm)algParams[0], (IPNReplayParameter)algParams[1]);
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ETCUtils: Reply Log out of context : sending back null");
		return null;
	}

}
