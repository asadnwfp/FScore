package org.processmining.XLog;

import org.deckfour.xes.model.XTrace;

final class TraceGroupingFunctionByExactSequence implements TraceGroupingFunction {

	private LogViewModel logViewModel;

	public TraceGroupingFunctionByExactSequence(LogViewModel logViewModel) {
		this.logViewModel = logViewModel;
	}

	public XESTraceVariantExactSequence apply(XTrace trace) {
		return new XESTraceVariantExactSequence(trace, logViewModel);
	}

}