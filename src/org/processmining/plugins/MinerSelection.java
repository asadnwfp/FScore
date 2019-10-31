package org.processmining.plugins;

import java.awt.event.WindowEvent;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogues.DialogueChooser;
import org.processmining.dialogues.ResultBoard;
import org.processmining.framework.plugin.PluginContext;
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
	private ILPMiner ilp;
	private IM im;
	private SM splitMiner;

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
				ilp = new ILPMiner();
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
				im = new IM();

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
				splitMiner = new SM(dialog, log);
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

		

		//		return results;
		return returnResult;
	}

	private ResultBoard calculateFScore(PluginContext context,XLog log,Petrinet pn, Miners miner) {
		
		String[] splitter = String.valueOf(stepIncremnet).split("\\.");
		int stepLength = splitter[1].length();
		CalculateFScore fScore = new CalculateFScore(context,log,pn);
		
		minEpsilon = (float) ReusableMethods.get2DecimalPlaces(minEpsilon, true, stepLength);
		minFreq  = (float) ReusableMethods.get2DecimalPlaces(minFreq, true, stepLength);
		
		maxEpsilon  = (float) ReusableMethods.get2DecimalPlaces(maxEpsilon, true, stepLength);
		maxFreq  = (float) ReusableMethods.get2DecimalPlaces(maxFreq, true, stepLength);
		
		ResultBoard results = new ResultBoard();
		results.setTitle(miner.getMinerName());
		double [] precisionAndFitness;
		switch(miner) {
		case Inductive_Miner:
			while(maxFreq>minFreq) {
				minFreq+= stepIncremnet;
				minFreq = (float) ReusableMethods.get2DecimalPlaces(minFreq, true, stepLength);
				precisionAndFitness = fScore.calcultate();
				results.createRow(minFreq, precisionAndFitness[0], precisionAndFitness[1]);
				im.getParameters().setNoiseThreshold((float) minFreq);
				fScore.setPn(pn);
			}
			break;
		case Split_Miner:
			
			break;
		default:
			return null;
		}
				
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