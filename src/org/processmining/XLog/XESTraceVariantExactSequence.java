package org.processmining.XLog;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.ui.widgets.traceview.ProMTraceView;
import org.processmining.framework.util.ui.widgets.traceview.ProMTraceView.Event;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

final class XESTraceVariantExactSequence extends XESTraceVariant {

	private static final class EventImpl extends ProMTraceView.AbstractEvent {

		private final String label;

		private EventImpl(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

	

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			XESTraceVariantExactSequence.EventImpl other = (XESTraceVariantExactSequence.EventImpl) obj;
			if (getLabel() == null) {
				if (other.getLabel() != null)
					return false;
			} else if (!getLabel().equals(other.getLabel()))
				return false;
			return true;
		}

		public String toString() {
			return label;
		}

	}

	private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("##0.00% of the log");
	private static final int MAX_TRACE_SIZE = 1000000;

	private final LogViewModel logViewModel;
	private final XTrace trace;

	private int groupCount = 0;

	public XESTraceVariantExactSequence(XTrace trace, final LogViewModel logViewModel) {
		this.logViewModel = logViewModel;
		this.trace = trace;
	}

	public String getName() {
		String count = NumberFormat.getIntegerInstance().format(groupCount);
		if (trace.size() > MAX_TRACE_SIZE) {
			return count + " traces (showing first " + MAX_TRACE_SIZE + " events)";
		} else {
			return count.concat(" traces");
		}
	}

	public Color getNameColor() {
		return null;
	}

	public String getInfo() {
		return FLOAT_FORMAT.format(groupCount / (double) logViewModel.getTraces().size());
	}

	public Color getInfoColor() {
		return null;
	}

	public List<Event> getEvents() {
		return delegate();
	}

	protected List<Event> delegate() {
		if (trace.size() > MAX_TRACE_SIZE) {
			List<XEvent> subList = trace.subList(0, MAX_TRACE_SIZE);
			return Lists.transform(subList, new Function<XEvent, Event>() {

				public Event apply(XEvent e) {
					return new EventImpl(logViewModel.getEventClasses().getClassifier().getClassIdentity(e));
				}
			});
		} else {
			return Lists.transform(trace, new Function<XEvent, Event>() {

				public Event apply(XEvent e) {
					return new EventImpl(logViewModel.getEventClasses().getClassifier().getClassIdentity(e));
				}
			});
		}
	}

	public void setCount(int count) {
		this.groupCount = count;
	}

	public int hashCode() {
		// based on event classification only
		XEventClasses eventClasses = logViewModel.getEventClasses();
		int hashCode = 1;
		for (XEvent e : trace) {
			XEventClass eventClass = eventClasses.getClassOf(e);
			hashCode = 31 * hashCode + (e == null ? 0 : Ints.hashCode(eventClass.getIndex()));
		}
		return hashCode;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		XESTraceVariantExactSequence other = (XESTraceVariantExactSequence) obj;
		if (trace == null) {
			if (other.trace != null)
				return false;
		} else if (!isEqualVariant(trace, other.trace))
			return false;
		return true;
	}

	private boolean isEqualVariant(XTrace t1, XTrace t2) {
		if (t1 == t2) {
			return true;
		}

		Iterator<XEvent> it1 = t1.iterator();
		Iterator<XEvent> it2 = t2.iterator();
		XEventClasses eventClasses = logViewModel.getEventClasses();

		while (it1.hasNext() && it2.hasNext()) {
			XEventClass cl1 = eventClasses.getClassOf(it1.next());
			XEventClass cl2 = eventClasses.getClassOf(it2.next());
			if (cl1.getIndex() != cl2.getIndex()) {
				return false;
			}
		}
		return !(it1.hasNext() || it2.hasNext());
	}

}
