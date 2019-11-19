package org.processmining.XLog;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.xeslite.query.XIndex;
import org.processmining.xeslite.query.XIndexedTraces;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;

public final class LogViewModel {

	private final Collection<XTrace> allTraces;
	private XIndexedTraces indexedTraces;

	private Collection<XTrace> visibleTraces = ImmutableList.of();
	private ImmutableListMultimap<XESTraceVariant, XTrace> traceVariants = ImmutableListMultimap.of();

	private XEventClasses eventClasses;
	private Set<String> globalAttributes = ImmutableSet.of();
	

	public LogViewModel() {
		this(Collections.<XTrace>emptyList());
	}

	public LogViewModel(final Collection<XTrace> allTraces) {
		this.allTraces = allTraces;

		if (allTraces instanceof XLog) {
			globalAttributes = new HashSet<>();
			for (XAttribute attr : ((XLog) allTraces).getGlobalEventAttributes()) {
				if (attr instanceof XAttributeLiteral) {
					globalAttributes.add(attr.getKey());
				}
			}
		}

		this.visibleTraces = allTraces;
		if (allTraces instanceof XIndexedTraces) {
			// log already indexed
			this.indexedTraces = ((XIndexedTraces) allTraces);
		} else {
			new SwingWorker<XIndexedTraces, Void>() {

				protected XIndexedTraces doInBackground() throws Exception {
					return indexedTraces = XIndex.newTraces(allTraces);
				}

				protected void done() {
					try {
						LogViewModel.this.indexedTraces = get();
					} catch (InterruptedException | ExecutionException e) {
						// Could not build index for some reason, but we don't care
					}
				}

			}.execute();
		}
	}

	public Set<String> getGlobalAttributes() {
		return globalAttributes;
	}

	public XEventClasses getEventClasses() {
		return eventClasses;
	}

	public void setEventClasses(XEventClasses eventClasses) {
		this.eventClasses = eventClasses;
	}

	public Collection<XTrace> getTraces() {
		return visibleTraces;
	}

	public void setTraces(final Collection<XTrace> traces) {
		this.visibleTraces = traces;
	}


	public XIndexedTraces getIndexedTraces() {
		return indexedTraces;
	}

	public boolean hasIndex() {
		return indexedTraces != null;
	}

	public Collection<XTrace> getAllTraces() {
		return allTraces;
	}

	public void setTraceVariants(ImmutableListMultimap<XESTraceVariant, XTrace> traceVariants) {
		this.traceVariants = traceVariants;
	}

	public ImmutableListMultimap<XESTraceVariant, XTrace> getTraceVariants() {
		return traceVariants;
	}
	
}
