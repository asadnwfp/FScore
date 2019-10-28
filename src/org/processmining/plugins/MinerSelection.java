package org.processmining.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogues.DialogueChooser;
import org.processmining.dialogues.ResultBoard;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.ilp.ILPMiner;
import org.processmining.plugins.inductiveminer.IM;
import org.processmining.plugins.parameter.MatrixFilterParameter;
import org.processmining.plugins.splitminer.SM;
import org.processmining.plugins.splitminer.SplitMinerinProMPlugin;

public class MinerSelection {
	@Plugin(name = "Dialogue Chooser SM/IM", level = PluginLevel.Local, returnLabels = { "JTable" }, returnTypes = {
			ResultBoard.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Saad Ahmed", email = "saad.ahmed@rwth-aachen.de")

	public Object mineLogs(UIPluginContext context, XLog log) throws Exception {

		DialogueChooser dialog = new DialogueChooser();
		InteractionResult result = context.showWizard("Choose Miner Dialogue", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		switch (dialog.getMiner()) {
			case ILP_Miner :
				System.out.println("This is the " + Miners.ILP_Miner + " Case");
				ILPMiner ilp = new ILPMiner();
				ilp.doILPMining(context, log);

				break;
			case Inductive_Miner :
				System.out.println("This is the " + Miners.Inductive_Miner + " Case");
				IM im = new IM();
				return im.mineFScore(context, log);
			//				break;

			case Split_Miner :
				System.out.println("This is the " + Miners.Split_Miner + " Case");
				SM splitMiner = new SM(dialog, log);
				MatrixFilterParameter parameters = splitMiner.getParameters();
				Petrinet pn = SplitMinerinProMPlugin.run(context, log, parameters);
				//splitMiner.runSM();
				//				break;
			default :
				System.out.println("This is the Default Case");
		}

		String max = "11";
		String min = "22";
		String stepSize = "3";

		ResultBoard results = new ResultBoard();
		results.setTitle("Dummy Board");
		String[] row = { max, min, stepSize };
		results.setRow(row);

		return results;
	}
}