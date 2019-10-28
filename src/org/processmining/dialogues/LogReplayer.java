package org.processmining.dialogues;

import java.text.NumberFormat;

import javax.swing.JLabel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import nl.tue.astar.AStarException;

public class LogReplayer {
	private PNLogReplayer pnLogReplayer;
	private PNReplyerUIAutomation pnReplayerUI;
	public Object[] resultConfiguration ;
	public IPNReplayAlgorithm selectedAlg;
	public IPNReplayParameter algParameters;
	
	
	
	public PNReplyerUIAutomation getPnReplayerUI() {
		return pnReplayerUI;
	}

	public LogReplayer(PNLogReplayer pnLogReplayer){
		this.pnLogReplayer = pnLogReplayer;
	}
	
	public PNRepResult getGUIpnRep(UIPluginContext context, XLog log, Petrinet pn) {
		try {
			return replayLogGUI(context, pn, log);
			

		} catch (ConnectionCannotBeObtained e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		context.showConfiguration("Error", new JLabel("Cannot replay on a Petri net that does not contain transitions. Select Cancel or Continue to continue."));
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
	public PNRepResult replayLogGUI(final UIPluginContext context, PetrinetGraph net, XLog log)
			throws ConnectionCannotBeObtained, AStarException {
		if (net.getTransitions().isEmpty()) {
			context.showConfiguration("Error", new JLabel("Cannot replay on a Petri net that does not contain transitions. Select Cancel or Continue to continue."));
			context.getFutureResult(0).cancel(true);
			return null;
		}
		pnReplayerUI = new PNReplyerUIAutomation();
		resultConfiguration = pnReplayerUI.getConfiguration(context, net, log);
		return getPNRepResult(context, net, log, resultConfiguration);
	}

	public PNRepResult getPNRepResult(final UIPluginContext context, PetrinetGraph net, XLog log, Object[] configurations )
			throws ConnectionCannotBeObtained, AStarException {
		if (configurations == null) {
			context.getFutureResult(0).cancel(true);
			System.out.println("TESTING: Resultconfiguration is Null");
			return null;
		}

		// if all parameters are set, replay log
		if (configurations[PNReplayerUI.MAPPING] != null) {
			PNRepResult res = replayParametersSet(context, net, log);
			System.out.println("TESTING: Resultconfiguration is not  Null");
			return res;

		} else {
			context.log("replay is not performed because not enough parameter is submitted");
			context.getFutureResult(0).cancel(true);
			System.out.println("TESTING: Resultconfiguration is not  Null but replay not performed");
			return null;
		}
	}

	public PNRepResult replayParametersSet(final UIPluginContext context, PetrinetGraph net, XLog log)
			throws ConnectionCannotBeObtained, AStarException {
		context.log("replay is performed. All parameters are set.");
		System.out.println("TESTING: method replayParametersSet is started");
		// This connection MUST exists, as it is constructed by the configuration if necessary
		context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net, log);

		// get all parameters
		selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
		algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];
		System.out.println("AlgParamenter Class is : " + algParameters.getClass());

		// since based on GUI, create connection
		algParameters.setCreateConn(true);
		algParameters.setGUIMode(true);

		PNRepResult res = replayLogPrivate(context, net, log,
				(TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING], selectedAlg, algParameters);

		context.getFutureResult(0).setLabel(
				"Replay result - log " + XConceptExtension.instance().extractName(log) + " on " + net.getLabel()
						+ " using " + selectedAlg.toString());
		return res;
	}
	
	public TransEvClassMapping constructMapping(PetrinetGraph net, XLog log, XEventClass dummyEvClass,
			XEventClassifier eventClassifier) {
		TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier, dummyEvClass);

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
	
	public PNRepResult replayLogPrivate(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) throws AStarException {
		System.out.println("TESTING: method replayLogPrivate is started");
		if (selectedAlg.isAllReqSatisfied(context, net, log, mapping, parameters)) {
			System.out.println("TESTING: method replayLogPrivate: selectedAlg.isAllReqSatisfied");
			// for each trace, replay according to the algorithm. Only returns two objects
			PNRepResult replayRes = null;

			if (parameters.isGUIMode()) {
				long start = System.nanoTime();

				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
				System.out.println("TESTING: method replayLogPrivate: GUIMODE: ReplyLog");

				long period = System.nanoTime() - start;
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumFractionDigits(2);
				nf.setMaximumFractionDigits(2);

				context.log("Replay is finished in " + nf.format(period / 1000000000) + " seconds");
			} else {
				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
				System.out.println("TESTING: method replayLogPrivate: No GUIMODE: ReplyLog");
			}

			// add connection
			if (replayRes != null) {
				if (parameters.isCreatingConn()) {
					createConnections(context, net, log, mapping, selectedAlg, parameters, replayRes);
				}
			}

			return replayRes;
		} else {
			if (context != null) {
				context.log("The provided parameters is not valid for the selected algorithm.");
				System.out.println("TESTING: method replayLogPrivate: The provided parameters is not valid for the selected algorithm.");
				context.getFutureResult(0).cancel(true);
			}
			return null;
		}
	}

	protected void createConnections(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters, PNRepResult replayRes) {
		context.addConnection(new PNRepResultAllRequiredParamConnection(
				"Connection between replay result, " + XConceptExtension.instance().extractName(log)
						+ ", and " + net.getLabel(), net, log, mapping, selectedAlg, parameters, replayRes));
	}

	public Object[] getResultConfiguration() {
		return resultConfiguration;
	}

	public IPNReplayAlgorithm getSelectedAlg() {
		return selectedAlg;
	}

	public IPNReplayParameter getAlgParameters() {
		return algParameters;
	}

	
	
	
	

}
