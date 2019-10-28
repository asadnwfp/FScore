package org.processmining.plugins.splitminer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class DialogueChooser extends JPanel {

	private static final long serialVersionUID = 7693870370139578439L;
	private final JComboBox<String> minerComboBox;
	private Miners miner;

	// Split Miner: Components
	private final NiceDoubleSlider minEpsilon, maxEpsilon, minFreq, maxFreq, stepIncrement;
	private final JLabel freqLabel,epsilonLabel, stepIncrementLabel;

	public static final String email = "saad.ahmed@rwth-aachen.de";
	public static final String affiliation = "RWTH Aachen";
	public static final String author = "Saad Ahmed";

	// Getters
	public Miners getMiner() {
		return miner;
	}

	

	public double getMinEpsilon() {
		System.out.println("Dialogue: MinEpsilon: " + minEpsilon);
		return minEpsilon.getValue();
	}



	public double getMaxEpsilon() {
		System.out.println("Dialogue: MaxEpsilon: " + maxEpsilon);
		return maxEpsilon.getValue();
	}



	public double getMinFreq() {
		System.out.println("Dialogue: MinFreq: " + minFreq);
		return minFreq.getValue();
	}



	public double getMaxFreq() {
		System.out.println("Dialogue: MaxFreq: " + maxFreq);
		return maxFreq.getValue();
	}



	public double getStepIncrement() {
		System.out.println("Dialogue: StepIncrement: " + stepIncrement);
		return stepIncrement.getValue();
	}



	@SuppressWarnings("unchecked")
	public DialogueChooser() {
		SlickerFactory factory = SlickerFactory.instance();

		int gridy = 1;

		setLayout(new GridBagLayout());

		//algorithm
		final JLabel variantLabel = factory.createLabel("Miner");
		{
			GridBagConstraints cVariantLabel = new GridBagConstraints();
			cVariantLabel.gridx = 0;
			cVariantLabel.gridy = gridy;
			cVariantLabel.weightx = 0.2;
			cVariantLabel.anchor = GridBagConstraints.NORTHWEST;
			add(variantLabel, cVariantLabel);
		}
		minerComboBox = factory.createComboBox(Miners.values());
		{
			GridBagConstraints cVariantCombobox = new GridBagConstraints();
			cVariantCombobox.gridx = 1;
			cVariantCombobox.gridy = gridy;
			cVariantCombobox.anchor = GridBagConstraints.NORTHWEST;
			cVariantCombobox.fill = GridBagConstraints.HORIZONTAL;
			cVariantCombobox.weightx = 0.6;
			add(minerComboBox, cVariantCombobox);
			minerComboBox.setSelectedIndex(1);
			miner = Miners.Split_Miner; // Index 1
		}

		// Creating space
		gridy++;
		createGridySpace(factory, gridy);
		gridy++;

		// Createing Epsilon Slider

		epsilonLabel= factory.createLabel("Epsilon");
		{
			GridBagConstraints cEpsilonLabel = new GridBagConstraints();
			cEpsilonLabel.gridx = 0;
			cEpsilonLabel.gridy = gridy;
			cEpsilonLabel.anchor = GridBagConstraints.WEST;
			cEpsilonLabel.weightx = 0.4;
			add(epsilonLabel, cEpsilonLabel);
		}

		minEpsilon = factory.createNiceDoubleSlider("min", 0, 1, 0.1, Orientation.HORIZONTAL);
		{
			GridBagConstraints cDoubleSlider1 = new GridBagConstraints();
			cDoubleSlider1.gridx = 1;
			cDoubleSlider1.gridy = gridy;
			cDoubleSlider1.fill = GridBagConstraints.HORIZONTAL;
			cDoubleSlider1.weightx = 0.6;
			add(minEpsilon, cDoubleSlider1);
		}
		gridy++;
		maxEpsilon = factory.createNiceDoubleSlider("max", 0, 1, 0.1, Orientation.HORIZONTAL);
		{
			GridBagConstraints cDoubleSlider1 = new GridBagConstraints();
			cDoubleSlider1.gridx = 1;
			cDoubleSlider1.gridy = gridy;
			cDoubleSlider1.fill = GridBagConstraints.HORIZONTAL;
			cDoubleSlider1.weightx = 0.6;
			add(maxEpsilon, cDoubleSlider1);
		}

		// Creating space
		gridy++;
		createGridySpace(factory, gridy);
		gridy++;

		// Createing Frequency Threshold
		freqLabel = factory.createLabel("Frequency Threshold");
		{
			GridBagConstraints cNoiseLabel = new GridBagConstraints();
			cNoiseLabel.gridx = 0;
			cNoiseLabel.gridy = gridy;
			cNoiseLabel.anchor = GridBagConstraints.WEST;
			cNoiseLabel.weightx = 0.4;
			add(freqLabel, cNoiseLabel);
		}
		

		minFreq = factory.createNiceDoubleSlider("min", 0, 1, 0.4,
				Orientation.HORIZONTAL);
		{
			GridBagConstraints cDoubleSlider2 = new GridBagConstraints();
			cDoubleSlider2.gridx = 1;
			cDoubleSlider2.gridy = gridy;
			cDoubleSlider2.fill = GridBagConstraints.HORIZONTAL;
			cDoubleSlider2.weightx = 0.6; 
			add(minFreq, cDoubleSlider2);
		}
		gridy++;
		maxFreq = factory.createNiceDoubleSlider("max", 0, 1, 0.4,
				Orientation.HORIZONTAL);
		{
			GridBagConstraints cDoubleSlider2 = new GridBagConstraints();
			cDoubleSlider2.gridx = 1;
			cDoubleSlider2.gridy = gridy;
			cDoubleSlider2.fill = GridBagConstraints.HORIZONTAL;
			cDoubleSlider2.weightx = 0.6; 
			add(maxFreq, cDoubleSlider2);
		}
		

		// Creating String of MIners
		//		String[] miners = new String[Miners.values().length];
		//		int minerIndex = 0;
		//		for(Miners miner: Miners.values()) {
		//			miners[minerIndex++] = miner.getMinerName();
		//		}

		

		gridy++;

		{
			createGridySpace(factory, gridy);
		}

		gridy++;
		
		
		{
			stepIncrementLabel = factory.createLabel("Increment Step");
			GridBagConstraints cIncrement = new GridBagConstraints();
			cIncrement.gridx = 0;
			cIncrement.gridy = gridy;
			cIncrement.anchor = GridBagConstraints.WEST;
			cIncrement.weightx = 0.4;
			add(stepIncrementLabel, cIncrement);
		}
		stepIncrement = factory.createNiceDoubleSlider("", 0, 1, 1,
				Orientation.HORIZONTAL);
		{
			GridBagConstraints cIncrement = new GridBagConstraints();
			cIncrement.gridx = 1;
			cIncrement.gridy = gridy;
			cIncrement.fill = GridBagConstraints.HORIZONTAL;
			cIncrement.weightx = 0.6; 
			add(stepIncrement, cIncrement);
		}

		minerComboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				miner = (Miners) minerComboBox.getSelectedItem();
				makeSliderInvisible();
				
			
				System.out.println("DialogueChooser: MinerComboBox : Miner: " + miner);
				if(miner.hasEpsilon()) {
					epsilonLabel.setVisible(true);
					minEpsilon.setVisible(true);
					maxEpsilon.setVisible(true);
				}
				if(miner.hasFrequency()) {
					freqLabel.setVisible(true);
					minFreq.setVisible(true);
					maxFreq.setVisible(true);
				}

			}
		});

	}
	
	private void makeSliderInvisible() {
		minEpsilon.setVisible(false);
		maxEpsilon.setVisible(false);
		minFreq.setVisible(false);
		maxFreq.setVisible(false);
		freqLabel.setVisible(false);
		epsilonLabel.setVisible(false);
	}

	private void createGridySpace(SlickerFactory factory, int gridy) {
		JLabel spacer = factory.createLabel(" ");
		GridBagConstraints cSpacer = new GridBagConstraints();
		cSpacer.gridx = 0;
		cSpacer.gridy = gridy;
		cSpacer.anchor = GridBagConstraints.WEST;
		add(spacer, cSpacer);
	}

}
