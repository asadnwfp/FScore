package org.processmining.plugins.ilp;

import java.util.prefs.Preferences;

import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.ilpminer.ILPMiner;
import org.processmining.plugins.ilpminer.ILPMinerLogPetrinetConnection;
import org.processmining.plugins.ilpminer.ILPMinerSettings;
import org.processmining.plugins.ilpminer.ILPMinerStrategyManager;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverSetting;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverType;

public class ILPMinerAutomation {
	private XLog log;
	private PluginContext context;
	
	public ILPMinerAutomation(PluginContext context, XLog log) {
		this.log = log;
		this.context = context;
	}
	
	public Object[] mineLogwithILP() throws Exception {
//		ILPMinerLogPetrinetConnection conn = context.getConnectionManager()
//				.getFirstConnection(ILPMinerLogPetrinetConnection.class,
//						context, log);
		ILPMinerSettings settings = getILPSettings();
		saveSettings(settings);
		
		ILPMiner miner = new ILPMiner();
		return miner.doILPMiningWithSettings(context, log, XLogInfoFactory
				.createLogInfo(log), settings);
		
	}

	private ILPMinerSettings getILPSettings() {
		ILPMinerSettings settings = new ILPMinerSettings();
		// load the settings from the registry
		Preferences prefs = Preferences.userNodeForPackage(ILPMiner.class);
		settings.setSolverSetting(SolverSetting.TYPE, SolverType.fromInt(prefs
				.getInt("SolverEnum", SolverType.JAVAILP_LPSOLVE.ordinal())));
		settings.setSolverSetting(SolverSetting.LICENSE_DIR, prefs.get(
				"LicenseDir", ""));
//		Class<?>[] strategies = ILPMinerStrategyManager
//				.getILPMinerStrategyExtensions();
		settings
		.setSolverSetting(SolverSetting.TYPE,SolverType.JAVAILP_LPSOLVE);
		
		Class<?>[] strategies = ILPMinerStrategyManager
				.getILPMinerStrategies((SolverType) settings
						.getSolverSetting(SolverSetting.TYPE));
	
		
		return settings;
	}
	
	/**
	 * Stores the settings of the chosen ILP solver in the
	 * (HKEY_Current_User/Software/JavaSoft/Prefs) registry
	 * 
	 * @param ILPMinerSettings
	 *            containing the user settings about the solver to use (and its
	 *            components locations)
	 */
	private void saveSettings(ILPMinerSettings settings) {
		Preferences prefs = Preferences.userNodeForPackage(ILPMiner.class);
		prefs.putInt("SolverEnum", ((SolverType) settings
				.getSolverSetting(SolverSetting.TYPE)).ordinal());
		prefs.put("LicenseDir", (String) settings
				.getSolverSetting(SolverSetting.LICENSE_DIR));
	}
}
