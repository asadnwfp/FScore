package org.processmining.PNetReplayer;

import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.ui.PNAlgorithmStep;

/**
 * 
 * @author saadn
 * This class contains the configuration for the PNetReplayer UI, that will be automated to get Mappings and Algorithem
 * BY Default Single Algorithm will be selected.
 *
 */
public class PNetConfiguration {
	public static final int MAPPING = 0;
	public static final int ALGORITHM = 1;
	public static final int PARAMETERS = 2;
	
	public Object[] getConfiguration(UIPluginContext context, PetrinetGraph net, XLog log) {
		// init local parameter
		EvClassLogPetrinetConnection conn = null;

		// check existence of initial marking
		try {
			InitialMarkingConnection initCon = context.getConnectionManager()
					.getFirstConnection(InitialMarkingConnection.class, context, net);

			if (((Marking) initCon.getObjectWithRole(InitialMarkingConnection.MARKING)).isEmpty()) {
				JOptionPane.showMessageDialog(new JPanel(),
						"The initial marking is an empty marking. If this is not intended, remove the currently existing InitialMarkingConnection object and then use \"Create Initial Marking\" plugin to create a non-empty initial marking.",
						"Empty Initial Marking", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No initial marking is found for this model. Do you want to create one?", "No Initial Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, InitialMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check existence of final marking
		try {
			context.getConnectionManager().getFirstConnection(FinalMarkingConnection.class, context, net);
		} catch (ConnectionCannotBeObtained exc) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(new JPanel(),
					"No final marking is found for this model. Do you want to create one?", "No Final Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, FinalMarkingConnection.class);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Here  is the finalResultStatement
//		return new Object[] { mapping, ((PNAlgorithmStep) replaySteps[0]).getAlgorithm(),
//				paramProvider.constructReplayParameter(replaySteps[1]) };
		return null;
	}
	
	
	
	private boolean createMarking(UIPluginContext context, PetrinetGraph net, Class<? extends Connection> classType) {
		boolean result = false;
		Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(
				ConnectionObjectFactory.class, classType, context.getClass(), true, false, false, net.getClass());
		PluginContext c2 = context.createChildContext("Creating connection of Type " + classType);
		Pair<Integer, PluginParameterBinding> pair = plugins.iterator().next();
		PluginParameterBinding binding = pair.getSecond();
		try {
			PluginExecutionResult pluginResult = binding.invoke(c2, net);
			pluginResult.synchronize();
			context.getProvidedObjectManager().createProvidedObjects(c2); // push the objects to main context
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c2.getParentContext().deleteChild(c2);
		}
		return result;
	}

}
