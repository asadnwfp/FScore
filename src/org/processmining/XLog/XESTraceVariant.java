package org.processmining.XLog;

import org.processmining.framework.util.ui.widgets.traceview.ProMTraceView.Event;
import org.processmining.framework.util.ui.widgets.traceview.ProMTraceView.Trace;
import org.processmining.log.utils.TraceVariant;

import com.google.common.collect.ForwardingList;

public abstract class XESTraceVariant extends ForwardingList<Event> implements Trace<Event>, TraceVariant<Event> {

	public XESTraceVariant() {
		super();
	}

	abstract public void setCount(int count);

}