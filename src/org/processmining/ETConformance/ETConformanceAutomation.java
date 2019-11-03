package org.processmining.ETConformance;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.multietc.plugins.MultiETCPlugin;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.multietc.sett.MultiETCSettings;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class ETConformanceAutomation {
	private Petrinet net;
	private XLog log;
	private PluginContext context;
	
	
	public ETConformanceAutomation(PluginContext context, Petrinet net, XLog log) {
		this.net = net;
		this.log = log;
		this.context = context;
	}
	
	/**
	 * This method is here to mimic checkMultiETCAlign1 from ETConformance.
	 * @return
	 * @throws ConnectionCannotBeObtained 
	 */
	public MultiETCResult checkETCAlign1(PNRepResult replayRes) throws ConnectionCannotBeObtained {
		MultiETCSettings sett = createETCSetting();
		
		// Acctual ETConformance Plugin
		MultiETCPlugin plugin = new MultiETCPlugin();
		
		 Object[] etcResults = plugin.checkMultiETCAlign1(context,log, net, sett, replayRes);
		 return (MultiETCResult) etcResults[0];
	}

	private MultiETCSettings createETCSetting() {
		MultiETCSettings sett =  new MultiETCSettings();
		sett.put(MultiETCSettings.REPRESENTATION, MultiETCSettings.Representation.ORDERED);
		sett.put(MultiETCSettings.ALGORITHM, MultiETCSettings.Algorithm.ALIGN_1);
		return sett;
	}

	public void setNet(Petrinet net) {
		this.net = net;
	}

}
