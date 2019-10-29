package org.processmining.plugins.inductiveminer;

import javax.swing.JOptionPane;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogues.ETCUtils;
import org.processmining.dialogues.IMMiningDialog;
import org.processmining.dialogues.LogReplayer;
import org.processmining.dialogues.ResultBoard;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.utils.ReusableMethods;

import nl.tue.astar.AStarException;

public class IM {
	private float min,max,step;
	
	@Plugin(name = "Mine F-Score with Inductive Miner", level = PluginLevel.Local, returnLabels = {
			"Petrinet","ResultBoard" }, returnTypes = { Petrinet.class, ResultBoard.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Saad Ahmed", email = "saad.ahmed@rwth-aachen.de")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Object[] mineFScore(UIPluginContext context, XLog log) throws AStarException {
		// Disabling dialogue, to run with default values.
//		IMMiningDialog dialog = new IMMiningDialog(log);
//		InteractionResult result = context.showWizard("Mine F-Score using Inductive Miner", true, true, dialog);
		context.log("Mining...");
		MiningParametersIMf parameters = new MiningParametersIMf();

		// Initializing JTABLE
		ResultBoard resultBoard = new ResultBoard();

		// Getting values from Dialogue
		

		PNRepResult pnRep = null;
		System.out.println("Index min: " + min);
		System.out.println("Index max: " + max);

		int index = 1;

		int maxProgress = (int) (max / step);
		context.getProgress().setIndeterminate(true);
		// Adding Progress
		context.getProgress().setMaximum(maxProgress);
		context.getProgress().setValue(index++);

		// PNLogReplayer - 1stTime
		parameters.setNoiseThreshold(min);
		Object[] resultObjects = IMPetriNet.minePetriNet(context, log, parameters);
		Petrinet pn = (Petrinet) resultObjects[0];

		// Creating New Instances for Fitness Plugins
		PNLogReplayer replayer = new PNLogReplayer();
		LogReplayer logReplayer = new LogReplayer(replayer);

		// Getting Precision from Multi ETC Plugin
		//MultiETCPlugin multiETCPlugin = new MultiETCPlugin();
		//ETCDialogueAutomation ui = new ETCDialogueAutomation();
		//MultiETCSettings sett =  ui.getSettings(context);

		// Getting Allignment from ETC Plugin
		//ETCPluginAutomation etcPlugin = new ETCPluginAutomation();
		//Object[] etcPluginObjects = etcPlugin.doETC(context, log, pn);

		// ETC Utils
		ETCUtils utils = new ETCUtils(context, log, pn);
		utils.performETCUtils();
		ETCResults res = utils.getRes();
		System.out.println("Results of ETCResults: " + res.getEtcp());
		pnRep = utils.getPnRepResult(logReplayer);

		// Running first for plugin
		pnRep = logReplayer.getGUIpnRep(context, log, pn);

		//		Object [] resultConfiguration = logReplayer.getResultConfiguration();
		//		IPNReplayAlgorithm selectedAlg = logReplayer.getSelectedAlg();
		//		IPNReplayParameter algParameters = logReplayer.getAlgParameters();
		//		PNReplyerUIAutomation pnReplyerUi = logReplayer.getPnReplayerUI();
		double precision = 0d;

		// Running Precision Plugin first Time with Context
		//precision = getPrecision(context, log, pn, multiETCPlugin, sett, resultConfiguration);		
		precision = ReusableMethods.get2DecimalPlaces(res.getEtcp(), false);

		// Adding score to board:
		resultBoard.createRow(pnRep, min, precision);

		String[] splitter = String.valueOf(step).split("\\.");
		int stepLength = splitter[1].length();

		while (max > min) {
			min += step;
			min = (float) ReusableMethods.get2DecimalPlaces(min, true, stepLength);
			System.out.println("Min: " + min);
			parameters.setNoiseThreshold(min);
			System.out.println("max-min: " + (min));

			// Generating PetriNet Again // IF Activiated the autonomus loop (3rd Way) wont work
			resultObjects = IMPetriNet.minePetriNet(context, log, parameters);
			pn = (Petrinet) resultObjects[0];

			utils.setContext(context);
			utils.setNet(pn);
			utils.performETCUtils();
			res = utils.getRes();
			System.out.println("Results of ETCResults: " + res.getEtcp());

			// Precision 2
			//	pnRep = logReplayer.getGUIpnRep(context, log, pn);
			//	resultConfiguration = logReplayer.getResultConfiguration();

			// Generating Precision

			//#########1st Way:############
			//	try {
			//		pnRep = logReplayer.getPNRepResult(context, pn, log, resultConfiguration);
			//	} catch (ConnectionCannotBeObtained e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}

			//#########2nd: Way########## Does Not Work
			//	try {
			//		pnRep =  logReplayer.replayParametersSet(context, pn, log);
			//	} catch (ConnectionCannotBeObtained e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}

			//#########3rd: Way########## Does Not Work

			// This connection MUST exists, as it is constructed by the configuration if necessary
			//	try {
			//		context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, pn, log);
			//
			//		// get all parameters
			//		selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
			//		algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];
			//
			//		// since based on GUI, create connection
			//		algParameters.setCreateConn(true);
			//		algParameters.setGUIMode(true);
			//
			//		PNRepResult res = logReplayer.replayLogPrivate(context, pn, log,
			//				(TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING], selectedAlg, algParameters);
			//
			//		context.getFutureResult(0).setLabel(
			//				"Replay result - log " + XConceptExtension.instance().extractName(log) + " on " + pn.getLabel()
			//						+ " using " + selectedAlg.toString());
			//	} catch (ConnectionCannotBeObtained e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}

			// 4th way : Does Not work
			//	TransEvClassMapping mappings = logReplayer.constructMapping(pn, log, pnReplyerUi.getxEventClass(), pnReplyerUi.getxEventClassifier());

			pnRep = utils.getPnRepResult(logReplayer);
			precision = ReusableMethods.get2DecimalPlaces(res.getEtcp(), false);

			// Running Precision Plugin first Time with Context
			//	precision = getPrecision(context, log, pn, multiETCPlugin, sett, resultConfiguration);
			// Printing Results to result board

			resultBoard.createRow(pnRep, min, precision);
			System.out.println("Min: " + min);
			System.out.println("max-min: " + (max - min));
			context.getProgress().setValue(index++);

		}
		context.getProgress().setValue(maxProgress);
		return new Object[] {pn,resultBoard};

	}
	
	public static boolean confirmLargeLogs(final UIPluginContext context, XLog log, IMMiningDialog dialog) {
		if (dialog.getVariant().getWarningThreshold() > 0) {
			XEventClassifier classifier = dialog.getMiningParameters().getClassifier();
			XLogInfo xLogInfo = XLogInfoFactory.createLogInfo(log, classifier);
			int numberOfActivities = xLogInfo.getEventClasses().size();
			if (numberOfActivities > dialog.getVariant().getWarningThreshold()) {
				int cResult = JOptionPane.showConfirmDialog(null, dialog.getVariant().toString()
						+ " might take a long time, as the event log contains " + numberOfActivities
						+ " activities.\nThe chosen variant of Inductive Miner is exponential in the number of activities.\nAre you sure you want to continue?",
						"Inductive Miner might take a while", JOptionPane.YES_NO_OPTION);

				return cResult == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public void setStep(float step) {
		this.step = step;
	}
	
	
}
