package org.processmining.plugins;

import java.awt.event.WindowEvent;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogues.DialogueChooser;
import org.processmining.dialogues.ResultBoard;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.ilp.ILPMiner;
import org.processmining.plugins.ilp.ILPMinerAutomation;
import org.processmining.plugins.inductiveminer.IM;
import org.processmining.plugins.parameter.MatrixFilterParameter;
import org.processmining.plugins.splitminer.SM;
import org.processmining.plugins.splitminer.SplitMinerinProMPlugin;
import org.processmining.utils.ReusableMethods;

import nl.tue.astar.AStarException;

public class MinerSelection {

	private double minEpsilon, maxEpsilon, minFreq, maxFreq, stepIncremnet;
	private ILPMiner ilp;
	private IM im;
	private SM splitMiner;
	private MiningParametersIMf parametersInductive;
	private MatrixFilterParameter parametersSplit;

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
		Object[] returnResult = new Object[] {null,null,null}; // PetriNet , Marking and ResultBoard
		Petrinet pn;
		switch (dialog.getMiner()) {
		
			case ILP_Miner :

				System.out.println("This is the " + Miners.ILP_Miner + " Case");
				ILPMinerAutomation ilpMiner = new ILPMinerAutomation(context, log);
				Object[] ilpObjects = ilpMiner.mineLogwithILP();
//				
//				ilp = new ILPMiner();
//				Object[] ilpObjects = ilp.doILPMining(context, log);
				ReusableMethods.nameAndClassOfObjects(ilpObjects);
				
				// Return Result
				returnResult[0] = ilpObjects[0];
				returnResult[1] = ilpObjects[1];
				returnResult[2] = calculateFScore(context, log, (Petrinet) ilpObjects[0], Miners.ILP_Miner);
				break;
			case Inductive_Miner :
				setMinFreq(dialog.getMinFreq());
				setMaxFreq(dialog.getMaxFreq());

				System.out.println("This is the " + Miners.Inductive_Miner + " Case");
				im = new IM();
				parametersInductive = new MiningParametersIMf();
				parametersInductive.setNoiseThreshold((float) getMinFreq());
				Object[] resultObjects = IMPetriNet.minePetriNet(context, log, parametersInductive);
				pn = (Petrinet) resultObjects[0];

				
				returnResult[0] = pn;
				returnResult[2] = calculateFScore(context, log, pn, Miners.Inductive_Miner);
				break;
			case Split_Miner :
				setMinEpsilon(dialog.getMinEpsilon());
				setMaxEpsilon(dialog.getMaxEpsilon());
				setMinFreq(dialog.getMinFreq());
				setMaxFreq(dialog.getMaxFreq());
				
				System.out.println("This is the " + Miners.Split_Miner + " Case");
				splitMiner = new SM(dialog, log);
				parametersSplit = splitMiner.getParameters();
				pn = SplitMinerinProMPlugin.run(context, log, parametersSplit);
				
				// Return Result
				returnResult[0] = pn;
				returnResult[2] = calculateFScore(context, log, pn, Miners.Split_Miner);
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

	private ResultBoard calculateFScore(PluginContext context,XLog log,Petrinet pn, Miners miner) throws ConnectionCannotBeObtained, AStarException {
		
		String[] splitter = String.valueOf(stepIncremnet).split("\\.");
		int stepLength = splitter[1].length();
		getDialogueSettings(stepLength);
		
		CalculateFScore fScore = new CalculateFScore(context,log,pn);
		
		double min = minFreq; // for resetting the value.
		
		ResultBoard results = new ResultBoard();
		results.createTableColumns(miner);
		double [] precisionAndFitness = {0d,0d}; // Precision & fitness
		
		
		switch(miner) {
		case Inductive_Miner:
			while(maxFreq>=minFreq) {
				minFreq = ReusableMethods.get2DecimalPlaces(minFreq, true, stepLength);
				float minThreshold = (float) minFreq;
				minThreshold = (float) ReusableMethods.get2DecimalPlaces(minThreshold, true, stepLength);
				System.out.println("MinerSelection: Inductive MinThreshold :" + minThreshold);
				System.out.println("MinerSelection: Inductive MaxFreq :" + maxFreq);
				System.out.println("MinerSelection: Inductive MinFreq :" + minFreq);
				System.out.println("MinerSelection: Inductive StepIncrement :" + stepIncremnet);
				
				// Generating FScore
				precisionAndFitness = fScore.calcultate();
				results.createRow(minFreq, precisionAndFitness[0], precisionAndFitness[1]);
				
				// Creating new PetriNet
				parametersInductive = new MiningParametersIMf();
				parametersInductive.setNoiseThreshold(minThreshold);
				Object[] resultObjects = IMPetriNet.minePetriNet(context, log, parametersInductive);
				pn = (Petrinet) resultObjects[0];
				fScore.setPn(pn);
				
				// Incrementing minFreq
				minFreq+= stepIncremnet;
			}
			break;
		case Split_Miner:
			while(maxEpsilon>=minEpsilon){
				minEpsilon = ReusableMethods.get2DecimalPlaces(minEpsilon, true, stepLength);
				
				while(maxFreq >= minFreq) {
					minFreq  =  ReusableMethods.get2DecimalPlaces(minFreq, true, stepLength);
					// Run Function for SplitMiner
					parametersSplit.setSecondDoubleVariable(minEpsilon);
					parametersSplit.setProbabilityOfRemoval(minFreq);
					pn = SplitMinerinProMPlugin.run(context, log, parametersSplit);
					fScore.setPn(pn);
					
					// Running Fscore:
					precisionAndFitness = fScore.calcultate();
//					pnf.generateLogFromPetrinet((UIPluginContext) context, log, pn, marking);
//					precisionAndFitness[0] = pnf.getPrecision();
//					precisionAndFitness[1] = pnf.getFitness();
					
					
					results.createRow(minEpsilon,minFreq, precisionAndFitness[0], precisionAndFitness[1]);
					
					
					minFreq+=stepIncremnet;
				}
				/**
				 * This is required, cz, the min value will be changed in the inner loop,
				 */
				minFreq = min; // reseting minFrequency to its original value.
				minEpsilon+= stepIncremnet;
			}
			
			break;
		case ILP_Miner:
			precisionAndFitness = fScore.calcultate();
			results.createRow(precisionAndFitness[0], precisionAndFitness[1]);
			break;
		default:
			return null;
		}
				
		return results;


	}

	private void getDialogueSettings(int stepLength) {
		minEpsilon = ReusableMethods.get2DecimalPlaces(minEpsilon, true, stepLength);
		minFreq  =  ReusableMethods.get2DecimalPlaces(minFreq, true, stepLength);
		
		maxEpsilon  = ReusableMethods.get2DecimalPlaces(maxEpsilon, true, stepLength);
		maxFreq  = ReusableMethods.get2DecimalPlaces(maxFreq, true, stepLength);
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