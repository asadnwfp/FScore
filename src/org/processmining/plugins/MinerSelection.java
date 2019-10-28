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
import org.processmining.plugins.parameter.MatrixFilterParameter;
import org.processmining.plugins.splitminer.SM;
import org.processmining.plugins.splitminer.SplitMinerinProMPlugin;

public class MinerSelection {
	@Plugin(name = "Dialogue Chooser SM/IM", level = PluginLevel.Local, returnLabels = { "JTable" }, returnTypes = {
			Petrinet.class }, parameterLabels = {"Log"}, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Saad Ahmed", email = "saad.ahmed@rwth-aachen.de")
	
	public Object mineJTable(UIPluginContext context, XLog log) {
		
		DialogueChooser dialog = new DialogueChooser();
		InteractionResult result = context.showWizard("Choose Miner Dialogue", true, true, dialog);
		if(result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}
		
		switch(dialog.getMiner()) {
			case ILP_Miner:
				System.out.println("This is the "+ Miners.ILP_Miner +" Case");
				break;
			case Inductive_Miner:
				System.out.println("This is the "+ Miners.Inductive_Miner+" Case");
				break;
				
			case Split_Miner:
				System.out.println("This is the "+ Miners.Split_Miner +" Case");
				SM splitMiner = new SM(dialog,log);
				MatrixFilterParameter parameters = splitMiner.getParameters();
				return SplitMinerinProMPlugin.run(context, log, parameters);
				//splitMiner.runSM();
//				break;
			default:
				System.out.println("This is the Default Case");
		}
		
//		if(dialog.isSplitMiner()) {
//			SplitMinerNewVersion miner = new SplitMinerNewVersion();
//			// added the classifier to the arguments of mineBPMNModel
//			XEventClassifier xEventClassifier = XLogInfoImpl.STANDARD_CLASSIFIER;
//			BPMNDiagram bpmn = miner.mineBPMNModel(log,  xEventClassifier, 0.4,0.1, FilterType.WTH, false, true, false, StructuringTime.NONE);
//			
//		Object[] resultedModel =BPMNToPetriNetConverter.convert(bpmn);
////		return resultedModel;
//		}else {
//			try {
//				 im.mineFScore(context, log);
//			} catch (AStarException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
		
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