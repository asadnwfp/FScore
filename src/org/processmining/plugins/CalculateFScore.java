package org.processmining.plugins;

import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.dialogues.ETCUtils;
import org.processmining.dialogues.LogReplayer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.utils.ReusableMethods;

public class CalculateFScore {

	private PluginContext context;
	private XLog log;
	private Petrinet pn;
	private ETCUtils utils;

	public CalculateFScore(PluginContext context, XLog log, Petrinet pn) {
		this.context = context;
		this.log = log;
		this.pn = pn;
		utils = new ETCUtils(context, log, pn);
	}

	public double[] calcultate() {
		PNRepResult pnRep = null;

		// Creating New Instances for Fitness Plugins
		PNLogReplayer replayer = new PNLogReplayer();
		LogReplayer logReplayer = new LogReplayer(replayer);

		utils.performETCUtils();
		ETCResults res = utils.getRes();
		System.out.println("Results of ETCResults: " + res.getEtcp());
		pnRep = utils.getPnRepResult(logReplayer);
		
		
		
		Map<String, Object> info = pnRep.getInfo();
		double traceFitness = Double.parseDouble((info.get(pnRep.TRACEFITNESS)).toString());
		double precision = ReusableMethods.get2DecimalPlaces(res.getEtcp(), false);

		System.out.println("CalculateFScore: TraceFitness: " + traceFitness);
		System.out.println("CalculateFScore: Precision: " + precision);
		return new double[] {precision,traceFitness};
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
	}

	
}
