package org.processmining.PNetReplayer;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.tools.ant.taskdefs.Sleep;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;
import org.processmining.utils.XEventAnalysis;

/**
 * 
 * @author saadn This class contains the configuration for the PNetReplayer UI,
 *         that will be automated to get Mappings and Algorithem BY Default
 *         Single Algorithm will be selected.
 *
 */
public class PNetConfiguration {
	public static final int MAPPING = 0;
	public static final int ALGORITHM = 1;
	public static final int PARAMETERS = 2;

	private TransEvClassMapping mapping;
	private boolean mappingAvailable = false;

	// Algorithm
	private IPNReplayAlgorithm selectedAlg;
	private boolean algoAvailable = false;

	// Parameters CostBasedCompleteParam:
	private IPNReplayParameter selectedParam;
	private boolean paramAvailable = false;

	public Object[] getConfiguration(UIPluginContext context, PetrinetGraph net, XLog log) {
		System.out.println("PNetConfiguration: getConfiguration");
		// init local parameter
		EvClassLogPetrinetConnection conn = null;

		// Check Initial and Final Markings
		checkMarkings(context, net);

		// Construct Mapping, Autonomously if Event Class matches
		if (!mappingAvailable) {
			mapping = constructMapping(net, log);
			if (mapping.size() >= XEventAnalysis.getEventClassCount(log)) {
				mappingAvailable = true;
				checkInvisibleTransitions(mapping);
				System.out.println("PNetConfiguration: MappingAvailable: " + mappingAvailable);
				System.out.println("PNetConfiguration: Mapping created via constructMapping()");
			}
		}

		// Create Mapping, Via user input
		if (!mappingAvailable) {
			// Creating Mapping via Asking User
			if (!createMapping(context, conn, log, net)) {
				return null; // Mapping not created, finishing plugin
			}
		}

		// create Algorithm
		if (!algoAvailable) {
			selectedAlg = getAlgo(context, net, log);
		}

		// Create Params:// Requesting Parameters
		if (!paramAvailable) {
			PNetParameters pNetParam = new PNetParameters(context, net, log, mapping);
			selectedParam = pNetParam.constructReplayParameter();

			// Makking paramAvailable true for this Object.
			paramAvailable = true;
			
			System.out.println("Param Class: " + selectedParam.getClass().toString());
			boolean satisfaction = selectedAlg.isAllReqSatisfied(context, net, log, mapping, selectedParam);
			System.out.println("Algo Satisfied with param: " + satisfaction);
		}

		System.out.println("PNetConfiguration: SelectedAlgo: " + selectedAlg.toString());
		// for Debug Purpose, checking no. of Mapping Transitions.
		System.out.println("PNetConfiguration: Count of Mapping Size:" + mapping.size());

		// Here is the finalResultStatement
		return new Object[] { mapping, selectedAlg, selectedParam };

	}

	private void checkMarkings(UIPluginContext context, PetrinetGraph net) {
		// check existence of initial marking
		try {
			InitialMarkingConnection initCon = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net);

			if (((Marking) initCon.getObjectWithRole(InitialMarkingConnection.MARKING)).isEmpty()) {
				JOptionPane.showMessageDialog(new JPanel(),
						"The initial marking is an empty marking. If this is not intended, remove the currently existing InitialMarkingConnection object and then use \"Create Initial Marking\" plugin to create a non-empty initial marking.",
						"Empty Initial Marking", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No initial marking is found for this model. Do you want to create one?", "No Initial Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, InitialMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check existence of final marking
		try {
			context.getConnectionManager().getFirstConnection(FinalMarkingConnection.class, context, net);
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No final marking is found for this model. Do you want to create one?", "No Final Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, FinalMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean createMapping(UIPluginContext context, EvClassLogPetrinetConnection conn, XLog log,
			PetrinetGraph net) {
		System.out.println("PNetConfiguration: createMapping()");
		mappingAvailable = true;
		try {
			// connection is found, no need for mapping step
			// connection is not found, another plugin to create such connection
			// is automatically
			// executed
			conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,
					log);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JPanel(), "No mapping can be constructed between the net and the log");
			mappingAvailable = false;
			return mappingAvailable;
		}

		// init gui for each step
		mapping = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);

		checkInvisibleTransitions(mapping);

		System.out.println("PNetConfiguration: Mapping created via createMapping()");
		return mappingAvailable; // Mapping Created
	}

	private void checkInvisibleTransitions(TransEvClassMapping mapping) {
		// TransEvClassMapping mapping = constructMapping(net, log);
		// check invisible transitions
		Set<Transition> unmappedTrans = new HashSet<Transition>();
		for (Entry<Transition, XEventClass> entry : mapping.entrySet()) {
			if (entry.getValue().equals(mapping.getDummyEventClass())) {
				if (!entry.getKey().isInvisible()) {
					unmappedTrans.add(entry.getKey());
				}
			}
		}
		if (!unmappedTrans.isEmpty()) {
			JList list = new JList(unmappedTrans.toArray());
			JPanel panel = new JPanel();
			BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
			panel.setLayout(layout);
			panel.add(new JLabel("The following transitions are not mapped to any event class:"));

			JScrollPane sp = new JScrollPane(list);
			panel.add(sp);
			panel.add(new JLabel("Do you want to consider these transitions as invisible (unlogged activities)?"));

			Object[] options = { "Yes, set them to invisible", "No, keep them as they are" };

			if (0 == JOptionPane.showOptionDialog(null, panel, "Configure transition visibility",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
				for (Transition t : unmappedTrans) {
					t.setInvisible(true);
				}
			}
		}

	}

	// Method Provided by Reza for Mapping of Logs
	private TransEvClassMapping constructMapping(PetrinetGraph net, XLog log) {
		System.out.println("PNetConfiguration: constructMapping()");
		// LogAnalysis
		XEventAnalysis.getAnalysis(log);

		XEventClass dummyEvClass = new XEventClass("Dummy", -1);
		XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;

		mapping = new TransEvClassMapping(eventClassifier, dummyEvClass);
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

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

	public IPNReplayAlgorithm getAlgo(PluginContext context, PetrinetGraph net, XLog log) {
		System.out.println("PNetConfiguration: getAlgo()");
		algoAvailable = true;

		IPNReplayAlgorithm[] listAlgorithms = getAvailabelAlgorithms(context, net, log);
		selectedAlg = null;
		for (IPNReplayAlgorithm algo : listAlgorithms) {
			System.out.println("########## Algo ############");
			System.out.println("Name of Algorithm: " + algo.toString());
			System.out.println("Algorithm Class: " + algo.getClass());
			// A* Cost-based Fitness Express with ILP (swap+replacement aware), assuming at
			// most 32767 tokens in each place.
			// A* Cost-based Fitness Express with ILP and Partial aware, assuming at most
			// 32767 tokens in each place.
			// A* Cost-based Replay with ILP with move model restriction, assuming at most
			// 32767 tokens in each place.
			// A* Cost-based Replay without ILP with move model restriction, assuming at
			// most 32767 tokens in each place.
			// ILP-based replayer assuming at most 32767 tokens in each place.
			// Splitting replayer assuming at most 127 tokens in each place.
		}
		System.out.println("########## Selected Algo ############");
		for (IPNReplayAlgorithm algo : listAlgorithms) {
			if (algo.toString().contentEquals("ILP-based replayer assuming at most 32767 tokens in each place.")) {
				System.out.println("Name of Algorithm: " + algo.toString());
				System.out.println("Algorithm Class: " + algo.getClass());
				selectedAlg = algo;
				System.out.println(
						"Algo is Instance of PetrinetReplayerWithILP: " + (algo instanceof PetrinetReplayerWithILP));
			}
		}

		PetrinetReplayerWithILP algoPetrinetReplayerWithILP = new PetrinetReplayerWithILP();
		System.out.println("Is Request without parameter Satisfied: "
				+ algoPetrinetReplayerWithILP.isReqWOParameterSatisfied(context, net, log, mapping));

		return selectedAlg;
	}

	private IPNReplayAlgorithm[] getAvailabelAlgorithms(PluginContext context, PetrinetGraph net, XLog log) {
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

//	private IPNReplayParameter createParameters(PluginContext context, PetrinetGraph net, XLog log) {
//		System.out.println("PNetConfiguration: createParameters()");
//		// Making paramAvailibility true
//		paramAvailable = true;
//
////		IPNReplayParamProvider paramProvider = selectedAlg.constructParamProvider(context, net,
////				log, mapping) ;
////		JComponent paramComponent = paramProvider.constructUI();
////		IPNReplayParameter algParameters = paramProvider.constructReplayParameter(paramComponent);
//
//		// Creating Params
//		XEventClass dummyEvClass = new XEventClass("Dummy", -1);
//		selectedParam = new CostBasedCompleteParam(XEventAnalysis.getEventClassCollection(log), dummyEvClass,
//				net.getTransitions(), 1, 1);
//		System.out.println("Param Class: " + selectedParam.getClass().toString());
//		boolean satisfaction = selectedAlg.isAllReqSatisfied(context, net, log, mapping, selectedParam);
//		System.out.println("Algo Satisfied with param: " + satisfaction);
//		return selectedParam;
//	}

	private boolean createMarking(UIPluginContext context, PetrinetGraph net, Class<? extends Connection> classType) {
		boolean result = false;
		Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager()
				.find(ConnectionObjectFactory.class, classType, context.getClass(), true, false, false, net.getClass());
		PluginContext c2 = context.createChildContext("Creating connection of Type " + classType);
		Pair<Integer, PluginParameterBinding> pair = plugins.iterator().next();
		PluginParameterBinding binding = pair.getSecond();
		try {
			PluginExecutionResult pluginResult = binding.invoke(c2, net);
			pluginResult.synchronize();
			context.getProvidedObjectManager().createProvidedObjects(c2); // push the objects to main context
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c2.getParentContext().deleteChild(c2);
		}
		return result;
	}

}
