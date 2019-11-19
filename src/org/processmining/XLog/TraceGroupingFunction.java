package org.processmining.XLog;

import org.deckfour.xes.model.XTrace;

import com.google.common.base.Function;

/**
 * Groups {@link XTrace} into {@link XESTraceVariant}
 * 
 * @author F. Mannhardt
 *
 */
public interface TraceGroupingFunction extends Function<XTrace, XESTraceVariant> {

}
