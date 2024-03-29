package org.processmining.plugins.parameter;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.basicutils.parameters.impl.PluginParametersImpl;
import org.processmining.logfiltering.legacy.plugins.logfiltering.enumtypes.ClusteringType;
import org.processmining.logfiltering.legacy.plugins.logfiltering.enumtypes.PrototypeType;
import org.processmining.logfiltering.legacy.plugins.logfiltering.enumtypes.SamplingType;
import org.processmining.logfiltering.legacy.plugins.logfiltering.enumtypes.SimilarityMeasure;
import org.processmining.logfiltering.parameters.AdjustingType;
import org.processmining.logfiltering.parameters.FilterLevel;
import org.processmining.logfiltering.parameters.FilterSelection;
import org.processmining.logfiltering.parameters.ProbabilityType;
public class MatrixFilterParameter extends PluginParametersImpl {
	
	public ClusteringType getClusterType() {
		return clusterType;
	}

	public void setClusterType(ClusteringType clusterType) {
		this.clusterType = clusterType;
	}

	private double probabilityOfRemoval = 0.1;
	private int SubsequenceLength = 2;
	private FilterLevel FilteringMethod = FilterLevel.TRACE;
	private XEventClassifier EventAttribute = new XEventNameClassifier();
	private AdjustingType AdjustingThresholdMethod= AdjustingType.None;
	private ProbabilityType ProbabilitycomutingMethod= ProbabilityType.DIRECT;
	private double secondDoubleVariable=0.05;
	private SamplingType samplingType= SamplingType.Hybrid;
	private FilterSelection FilteringSelection = FilterSelection.REMOVE;
	private SimilarityMeasure similarityMeasure = SimilarityMeasure.Levenstein;
	private SamplingReturnType returnType= SamplingReturnType.Variants;
	private PrototypeType prototypeType= PrototypeType.Cluster;
	private ClusteringType clusterType= ClusteringType.Manual;
	public PrototypeType getPrototypeType() {
		return prototypeType;
	}

	public void setPrototypeType(PrototypeType prototypeType) {
		this.prototypeType = prototypeType;
	}

	public MatrixFilterParameter(){
		super();
	}
			
	public AdjustingType getAdjustingThresholdMethod() {
		return AdjustingThresholdMethod;
	}

	public ProbabilityType getProbabilitycomutingMethod() {
		return ProbabilitycomutingMethod;
	}

	public void setProbabilitycomutingMethod(ProbabilityType probabilitycomutingMethod) {
		ProbabilitycomutingMethod = probabilitycomutingMethod;
	}

	public void setAdjustingThresholdMethod(AdjustingType adjustingThresholdMethod) {
		AdjustingThresholdMethod = adjustingThresholdMethod;
	}

	public FilterSelection getFilteringSelection() {
		return FilteringSelection;
	}

	public void setFilteringSelection(FilterSelection filteringSelection) {
		FilteringSelection = filteringSelection;
	}

	public MatrixFilterParameter(double probabilityOfRemoval, AdjustingType  adjustingThresholdMethod,int subsequencelenth ,FilterLevel filteringmethod, FilterSelection filteringSelection, 
				ProbabilityType probabilitycomutingMethod	,XEventClassifier eventattribute) {
		super();
		this.probabilityOfRemoval = probabilityOfRemoval;
		this.FilteringMethod= filteringmethod;
		this.EventAttribute = eventattribute;
		this.FilteringSelection = filteringSelection;
		this.SubsequenceLength=subsequencelenth;
		this.AdjustingThresholdMethod= adjustingThresholdMethod;
		this.ProbabilitycomutingMethod=probabilitycomutingMethod;
	}

	public int getSubsequenceLength() {
		return SubsequenceLength;
	}

	public void setSubsequenceLength(int subsequenceLength) {
		SubsequenceLength = subsequenceLength;
	}

	public MatrixFilterParameter(double probabilityOfRemoval, FilterSelection filterSelection , XEventClassifier eventattribute) {
		super();
		this.probabilityOfRemoval = probabilityOfRemoval;
		this.FilteringSelection=filterSelection;
		this.EventAttribute = eventattribute;
	}
	public MatrixFilterParameter( XEventClassifier eventattribute) {
		super();
		this.EventAttribute = eventattribute;
	}

	public MatrixFilterParameter(int subsequencelenth, XEventClassifier eventattribute) {
		super();
		this.EventAttribute = eventattribute;
		this.SubsequenceLength=subsequencelenth;
	}

	public MatrixFilterParameter(double threshold, XEventClassifier eventattribute) {
		super();
		this.EventAttribute = eventattribute;
		this.probabilityOfRemoval = threshold;
	}

	public MatrixFilterParameter(double oddRulesSupport, double highProbableRulesConf, int supportOfHighProbableRules, FilterSelection filterselection,
			XEventClassifier eventattribute) {
		this.probabilityOfRemoval=oddRulesSupport;
		this.secondDoubleVariable= highProbableRulesConf;
		this.SubsequenceLength=supportOfHighProbableRules;
		this.FilteringSelection=filterselection;
		this.EventAttribute=eventattribute;
	}

	public MatrixFilterParameter(int subsequencelenth, XEventClassifier eventattribute, SamplingType SampleTypeChosenItem, SamplingReturnType ReturnTypeChosenItem) {
		super();
		this.returnType= ReturnTypeChosenItem;
		this.samplingType= SampleTypeChosenItem; 
		this.EventAttribute = eventattribute;
		this.SubsequenceLength=subsequencelenth;
		
	}

	
	public MatrixFilterParameter(int subsequencelenth, XEventClassifier eventattribute, SimilarityMeasure similarityMeasure,
			SamplingReturnType samplingRetunType) {
		super();
		this.returnType= samplingRetunType;
		this.similarityMeasure= similarityMeasure; 
		this.EventAttribute = eventattribute;
		this.SubsequenceLength=subsequencelenth;
	}

	public MatrixFilterParameter(int subsequencelenth, XEventClassifier eventattribute, SimilarityMeasure similarityMeasure,
			SamplingReturnType samplingRetunType, PrototypeType prototypeTypeValue) {
		super();
		this.SubsequenceLength=subsequencelenth;
		this.returnType= samplingRetunType;
		this.similarityMeasure= similarityMeasure; 
		this.EventAttribute = eventattribute;
		this.prototypeType= prototypeTypeValue;
	}

	public MatrixFilterParameter(int NumerofCluster, XEventClassifier classifier, ClusteringType clusterT) {
		
		super();
		this.SubsequenceLength=NumerofCluster;
		this.EventAttribute = classifier;
		clusterType=clusterT;

	}

	public FilterLevel getFilteringMethod() {
		return FilteringMethod;
	}

	public void setFilteringMethod(FilterLevel filteringMethod) {
		FilteringMethod = filteringMethod;
	}

	public SamplingType getSamplingType() {
		return samplingType;
	}

	public void setSamplingType(SamplingType samplingType) {
		this.samplingType = samplingType;
	}

	public SamplingReturnType getReturnType() {
		return returnType;
	}

	public void setReturnType(SamplingReturnType returnType) {
		this.returnType = returnType;
	}

	public XEventClassifier getEventClassifier() {
		return EventAttribute;
	}

	public void setEventClassifier(XEventClassifier eventAttribute) {
		EventAttribute = eventAttribute;
	}

	public FilterLevel getFilterLevel() {
		return FilteringMethod;
	}

	public void setFilterLevel(FilterLevel filteringMethod) {
		FilteringMethod = filteringMethod;
	}

	public double getProbabilityOfRemoval() {
		return probabilityOfRemoval;
	}

	public void setProbabilityOfRemoval(double probabilityOfRemoval) {
		this.probabilityOfRemoval = probabilityOfRemoval;
	}

	public double getSecondDoubleVariable() {
		return secondDoubleVariable;
	}

	public void setSecondDoubleVariable(double secondDoubleVariable) {
		this.secondDoubleVariable = secondDoubleVariable;
	}

	public SimilarityMeasure getSimilarityMeasure() {
		return similarityMeasure;
	}

	public void setSimilarityMeasure(SimilarityMeasure similarityMeasure) {
		this.similarityMeasure = similarityMeasure;
	}

	

	

}
