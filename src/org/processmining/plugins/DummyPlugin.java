package org.processmining.plugins;

import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.deckfour.xes.model.XLog;
import org.processmining.ETConformance.ETConformanceAutomation;
import org.processmining.PNetReplayer.PNetReplayerAutomate;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.inductiveminer.IM;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.parameter.MatrixFilterParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.splitminer.SM;
import org.processmining.plugins.splitminer.SplitMinerinProMPlugin;

import nl.tue.astar.AStarException;

public class DummyPlugin {
	private boolean inductive = false;

	@Plugin(name = "DummyPlugin", level = PluginLevel.Local, returnLabels = { "Petrinet" }, returnTypes = {
			Petrinet.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Saad Ahmed", email = "saad.ahmed@rwth-aachen.de")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Object[] dummyPlugin(UIPluginContext context, XLog log) throws ConnectionCannotBeObtained, AStarException {
		System.out.println("DummyPlugin: Start");

		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
				"Current Miner is Split Miner, do you want to Choose Inductive?", "Choose Miner",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
			inductive = true;

		}
		
		System.out.println("Inductive Miner is:" + inductive);
		Petrinet net;
		if (inductive) {
			net = createPetrinetWithInductiveMiner(context, log);
		} else {
			net = createPetrinetWithSplitMiner(context, log);
		}

		// Calculating Fitness
		PNetReplayerAutomate pNetReplayer = new PNetReplayerAutomate(context, net, log);
		PNRepResult pnRepResult = pNetReplayer.getPNetRepResult();
		Map<String, Object> info = pnRepResult.getInfo();
		System.out.println("TraceFitness: " + info.get(PNRepResult.TRACEFITNESS));
		
		// Calculating Precision
		ETConformanceAutomation etConf = new ETConformanceAutomation(context, net, log);
		MultiETCResult etcResult = etConf.checkETCAlign1(pnRepResult);
		System.out.println("Precession: " + etcResult.getAttribute(MultiETCResult.PRECISION)); 
		System.out.println("DummyPlugin: End");
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return null;

	}

	private Petrinet createPetrinetWithSplitMiner(UIPluginContext context, XLog log) {
		// Creating Petrinet with Split Miner
		System.out.println("This is the " + Miners.Split_Miner + " Case");
		SM splitMiner = new SM(0.1d, 0.1d, 0.4d, 0.4d, 1, log);
		MatrixFilterParameter parametersSplit = splitMiner.getParameters();
		Petrinet net = SplitMinerinProMPlugin.run(context, log, parametersSplit);
		return net;
	}

	private Petrinet createPetrinetWithInductiveMiner(UIPluginContext context, XLog log) {
		// Creating Petrinet with Inductive Miner
		MiningParametersIMf parametersInductive = new MiningParametersIMf();
		parametersInductive.setNoiseThreshold(0.8f);
		Object[] resultObjects = IMPetriNet.minePetriNet(context, log, parametersInductive);
		Petrinet net = (Petrinet) resultObjects[0];
		return net;
	}

}
