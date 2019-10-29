package org.processmining.plugins;

import java.awt.event.WindowEvent;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogues.DialogueChooser;
import org.processmining.dialogues.ResultBoard;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.ilp.ILPMiner;
import org.processmining.plugins.inductiveminer.IM;
import org.processmining.plugins.parameter.MatrixFilterParameter;
import org.processmining.plugins.splitminer.SM;
import org.processmining.plugins.splitminer.SplitMinerinProMPlugin;
import org.processmining.utils.ReusableMethods;

public class MinerSelection {

	private double minEpsilon, maxEpsilon, minFreq, maxFreq, stepIncremnet;

	@Plugin(name = "Dialogue Chooser SM/IM", level = PluginLevel.Local, returnLabels = { "Petrinet", "Marking",
			"ResultBoard" }, returnTypes = { Petrinet.class, Marking.class,
					ResultBoard.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Saad Ahmed", email = "saad.ahmed@rwth-aachen.de")

	public Object[] mineLogs(UIPluginContext context, XLog log) throws Exception {

		DialogueChooser dialog = new DialogueChooser();
		InteractionResult result = context.showWizard("Choose Miner Dialogue", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			context.getFutureResult(1).cancel(false);
			context.getFutureResult(2).cancel(false);
			return new Object[] {null,null,null};
		}

		setStepIncremnet(dialog.getStepIncrement());
		Object[] returnResult = new Object[] {null,null,null};
		switch (dialog.getMiner()) {
			case ILP_Miner :

				System.out.println("This is the " + Miners.ILP_Miner + " Case");
				ILPMiner ilp = new ILPMiner();
				Object[] ilpObjects = ilp.doILPMining(context, log);
				ReusableMethods.nameAndClassOfObjects(ilpObjects);
				
				// Return Result
				returnResult[0] = ilpObjects[0];
				returnResult[1] = ilpObjects[1];
				break;
			case Inductive_Miner :
				setMinFreq(dialog.getMinFreq());
				setMaxFreq(dialog.getMaxFreq());

				System.out.println("This is the " + Miners.Inductive_Miner + " Case");
				IM im = new IM();

				// Setting values for IM
				im.setMax((float) maxFreq);
				im.setMin((float) minFreq);
				im.setStep((float) stepIncremnet);

				Object [] imObjects = im.mineFScore(context, log);
				returnResult[0] = imObjects[0];
				returnResult[2] = imObjects[1];
				break;
			case Split_Miner :
				setMinEpsilon(dialog.getMinEpsilon());
				setMaxEpsilon(dialog.getMaxEpsilon());
				setMinFreq(dialog.getMinFreq());
				setMaxFreq(dialog.getMaxFreq());
				
				System.out.println("This is the " + Miners.Split_Miner + " Case");
				SM splitMiner = new SM(dialog, log);
				MatrixFilterParameter parameters = splitMiner.getParameters();
				Petrinet pn = SplitMinerinProMPlugin.run(context, log, parameters);
				
				// Return Result
				returnResult[0] = pn;
				break;
			default :

				System.out.println("This is the Default Case");
				context.getFutureResult(0).cancel(false);
				context.getFutureResult(1).cancel(false);
				context.getFutureResult(2).cancel(false);
				return returnResult;
				
		}

		String max = "11";
		String min = "22";
		String stepSize = "3";

		ResultBoard results = new ResultBoard();
		results.setTitle("Dummy Board");
		String[] row = { max, min, stepSize };
		results.setRow(row);
		
		if (returnResult[2] == null) {
			System.out.println("MinerSelection: ResultBoard is Null, putting dummy board");
			returnResult[2] = results;
			
			// Currently we dont need it, so closing it.
			results.dispatchEvent(new WindowEvent(results, WindowEvent.WINDOW_CLOSING));
		}else {
			results.dispatchEvent(new WindowEvent(results, WindowEvent.WINDOW_CLOSING));
		}

		//		return results;
		return returnResult;
	}

	private ResultBoard calculateFScore(Petrinet pn) {

		return null;

	}

	public double getMinEpsilon() {
		return minEpsilon;
	}

	public double getMaxEpsilon() {
		return maxEpsilon;
	}

	public double getMinFreq() {
		return minFreq;
	}

	public double getMaxFreq() {
		return maxFreq;
	}

	public double getStepIncremnet() {
		return stepIncremnet;
	}

	public void setMinEpsilon(double minEpsilon) {
		this.minEpsilon = minEpsilon;
	}

	public void setMaxEpsilon(double maxEpsilor) {
		this.maxEpsilon = maxEpsilor;
	}

	public void setMinFreq(double minFreq) {
		this.minFreq = minFreq;
	}

	public void setMaxFreq(double maxFreq) {
		this.maxFreq = maxFreq;
	}

	public void setStepIncremnet(double stepIncremnet) {
		this.stepIncremnet = stepIncremnet;
	}

}