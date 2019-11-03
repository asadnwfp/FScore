package org.processmining.plugins;

import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.ETConformance.ETConformanceAutomation;
import org.processmining.PNetReplayer.PNetReplayerAutomate;
import org.processmining.dialogues.ETCUtils;
import org.processmining.dialogues.LogReplayer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.utils.ReusableMethods;

import nl.tue.astar.AStarException;

public class CalculateFScore {

	private PluginContext context;
	private XLog log;
	private Petrinet pn;
	private PNetReplayerAutomate pNetReplayer;
	private ETConformanceAutomation etConf;

	public CalculateFScore(PluginContext context, XLog log, Petrinet pn) {
		this.context = context;
		this.log = log;
		this.pn = pn;
		this.pNetReplayer = new PNetReplayerAutomate(context, pn, log);
		this.etConf = new ETConformanceAutomation(context, pn, log);
	}

	public double[] calcultate() throws ConnectionCannotBeObtained, AStarException {
		// Calculating Precision
		PNRepResult pnRepResult = pNetReplayer.getPNetRepResult();
		Map<String, Object> info = pnRepResult.getInfo();
		System.out.println("TraceFitness: " + info.get(PNRepResult.TRACEFITNESS));


		// Calculating Precision
		MultiETCResult etcResult = etConf.checkETCAlign1(pnRepResult);
		System.out.println("Precession: " + etcResult.getAttribute(MultiETCResult.PRECISION)); 
		
		double fitness = Double.parseDouble((info.get(pnRepResult.TRACEFITNESS)).toString());
		double precision  = (double) etcResult.getAttribute(MultiETCResult.PRECISION);
		fitness = ReusableMethods.get2DecimalPlaces(fitness,false);
		precision = ReusableMethods.get2DecimalPlaces(precision, false);
		return new double[] {precision,fitness};
	}

	// Getters and Setters
	public PluginContext getContext() {
		return context;
	}

	public Petrinet getPn() {
		return pn;
	}
	
	public void setContext(PluginContext context) {
		this.context = context;
	}

	public void setPn(Petrinet pn) {
		this.pn = pn;
		etConf.setNet(pn);
		pNetReplayer.setNet(pn);
	}

	
}
