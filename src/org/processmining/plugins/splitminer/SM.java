package org.processmining.plugins.splitminer;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.parameter.MatrixFilterParameter;
import org.processmining.splitMinerNewVersion.SplitMinerNewVersion;

import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult.FilterType;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult.StructuringTime;

public class SM {

	private final double minEpsilon, maxEpsilon, minFreq, maxFreq, stepIncrement;
	private final XLog log;
	SM(DialogueChooser dialog, XLog log){
	this(
			dialog.getMinEpsilon(),
			dialog.getMaxEpsilon(),
			dialog.getMinFreq(),
			dialog.getMaxFreq(),
			dialog.getStepIncrement(),
			log
			);
	}
	SM(double minEpsilon, double maxEpsilon, double minFreq, double maxFreq, double stepIncrement, XLog log){
		this.minEpsilon = minEpsilon;
		this.maxEpsilon = maxEpsilon;
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
		this.stepIncrement = stepIncrement;
		this.log = log;
	}
	
	public Object[] runSM() {
		MatrixFilterParameter parameters = getParameters();
//		double index = stepIncrement;
//		
//		Object[] sss=null;
//		while (index < 1) {
//			index+=stepIncrement;
//			
//			sss =SplitMinerinProM.apply2(log,parameters);
//		}
////		return SplitMinerinProM.apply(log,parameters);
//		return sss;
		
		SplitMinerNewVersion miner = new SplitMinerNewVersion();
		// added the classifier to the arguments of mineBPMNModel
		XEventClassifier xEventClassifier = XLogInfoImpl.STANDARD_CLASSIFIER;
		BPMNDiagram bpmn = miner.mineBPMNModel(log,  xEventClassifier, parameters.getSecondDoubleVariable(),parameters.getProbabilityOfRemoval(), FilterType.WTH, false, true, true, StructuringTime.NONE);
		Object[] resultedModel =BPMNToPetriNetConverter.convert(bpmn);
		
		for(Object model : resultedModel) {
			System.out.println("ModelClass: " + model.getClass());
			System.out.println("ModelString: " + model.toString());
			System.out.println("################");
		}
//		return resultedModel;
		return null;
	}
	
	public MatrixFilterParameter getParameters() {

		System.out.println("Printing Event Attributes: ");
		XEventClassifier[] EventAttribute = log.getClassifiers().toArray(new XEventClassifier[log.getClassifiers().size()]);
		System.out.println("Lenght: " + log.getClassifiers().size() );
		XEventClassifier eventName = XLogInfoImpl.NAME_CLASSIFIER;
		System.out.println("EventName: " + eventName.name());
		for(XEventClassifier attribute: EventAttribute) {
				if (attribute.name().equals("Event Name"))
						eventName = attribute;
		}
		System.out.println("AttributeName: " + eventName.name());
//		return new MatrixFilterParameter(minEpsilon,minFreq,1, FilterSelection.SELECT ,	eventName);
		return new MatrixFilterParameter();

	}
	
}
