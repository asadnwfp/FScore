package org.processmining.XLog;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XTrace;
import org.processmining.utils.ReusableMethods;

/**
 * @author christian
 * 
 */
public class ProcessInstanceView  {

	protected static DecimalFormat format = new DecimalFormat("##0.00%");
	protected static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

	protected static int trackPadding = 80;
	protected static int trackY = 40;
	protected static int trackHeight = 35;
	protected static int elementWidth = 4;
	protected static int elementTriOffset = 6;

	protected XLogInfo info;
	protected int maxOccurrenceCount;
	protected XTrace instance;

	public ProcessInstanceView(XTrace instance, XLogInfo info) {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		ReusableMethods.printLogs(ReusableMethods.DEBUG, this.getClass().getSimpleName() + ": ProcessInstanceView()");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,
				"*****************************************************************");
		
		this.instance = instance;
		this.info = info;
		maxOccurrenceCount = 0;
		for (XEventClass eventClass : info.getEventClasses().getClasses()) {
			if (eventClass.size() > maxOccurrenceCount) {
				maxOccurrenceCount = eventClass.size();
			}
		}
		int width = (instance.size() * elementWidth) + trackPadding + 300;
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Max Current Count: " + maxOccurrenceCount);
	}



	protected int mapEventIndex(int x, int y) {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"##################################################");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"ProcessInstanceView: mapEventIndex()" );
		if ((y >= trackY) && (y <= (trackY + trackHeight))) {
			// y-coordinate matches, remap x to index
			x -= trackPadding;
			x /= elementWidth;
			if ((x >= 0) && (x < instance.size())) {
				return x;
			} else {
				return -1;
			}
		} else {
			return -1;
		}

	}

	private String extractName(XTrace instance) {
ReusableMethods.printLogs(ReusableMethods.DEBUG, "*****************************************************************");
ReusableMethods.printLogs(ReusableMethods.DEBUG, this.getClass().getSimpleName() + ": extractName()");
ReusableMethods.printLogs(ReusableMethods.DEBUG, "*****************************************************************");

		String name = XConceptExtension.instance().extractName(instance);
		if (name == null) {
			return "<no name>";
		}
		return name;
	}

	protected void drawInstanceFlag() {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"##################################################");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"ProcessInstanceView: drawInstanceFlag()" );
		String name = extractName(instance);
		String size = instance.size() + " events";
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Name: " + name);
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Size: " + size);
	}

	protected void drawEventFlag(int index){
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"##################################################");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"ProcessInstanceView: drawEventFlag() : eventIndex: " + index );
		XExtendedEvent ate = new XExtendedEvent(instance.get(index));
		XEventClass eventClass = info.getEventClasses().getClassOf(instance.get(index));
		int occurrence = (eventClass != null ? eventClass.size() : 0);
		//		int occurrence = summary.getLogEvents().findLogEvent(ate.getElement(), ate.getType()).getOccurrenceCount();
		double frequency = (maxOccurrenceCount == 0 ? 0.0 : (double) occurrence / (double) maxOccurrenceCount);

		String ateName = (ate.getName() != null ? ate.getName() : "<no name>");
		String ateTransition = (ate.getTransition() != null ? ate.getTransition() : "<no transition>");
		String ateResource = (ate.getResource() != null ? ate.getResource() : "<no resource>");
		String name = index + ": " + ateName + " (" + ateTransition + ")";
		String originator = ateResource + "; freq: " + format.format(frequency);
		Date ts = ate.getTimestamp();
		String timestamp;
		if (ts != null) {
			timestamp = dateFormat.format(ate.getTimestamp());
		} else {
			timestamp = "<no timestamp>";
		}
		
		String [] eventFlag =  {ateName,ateTransition,ateResource,name,originator,timestamp};
		for(String flag : eventFlag) {
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"EventFlag: " + flag);
			ReusableMethods.printLogs(ReusableMethods.DEBUG,"**********************************************");
		}
	}

	protected void drawEvent( int index) {
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"##################################################");
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"ProcessInstanceView: drawEvent()  eventIndex: " + index );
		// set correct color for event
		XEventClass eventClass = info.getEventClasses().getClassOf(instance.get(index));
		int occurrence = (eventClass != null ? eventClass.size() : 0);
		double frequency = (maxOccurrenceCount == 0 ? 0.0 : (double) occurrence / (double) maxOccurrenceCount);
		ReusableMethods.printLogs(ReusableMethods.DEBUG,"Occurance: " + occurrence);
	}


}
