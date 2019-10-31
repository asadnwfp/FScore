package org.processmining.dialogues;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.processmining.plugins.Miners;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class ResultBoard extends JFrame {

	private JTable resultTable;
	private DefaultTableModel dtm;

	public ResultBoard() {
		resultTable = new JTable();

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(resultTable);

		add(sp);
		setSize(500, 500);
		setVisible(true);
		setLocationRelativeTo(null);

	}

	public void setRow(Object[] rows) {
		// colsList.add(rows);
		dtm.addRow(rows);
		dtm.fireTableDataChanged();

	}

	public void createTableColumns(Miners miner) {
		setTitle(miner.getMinerName());
		String[] rows = new String[5];
		String[][] cols = null;
		switch (miner) {
		case ILP_Miner:
			rows[0] = "Index";
			rows[1] = "Precision"; 
			rows[2] = "fitness" ;
			rows[3] = "f-Measure";
			rows[4] = null;
			break;
		case Inductive_Miner:
			rows[0] = "NoiseFreq";
			rows[1] = "Precision"; 
			rows[2] = "fitness" ;
			rows[3] = "f-Measure";
			rows[4] = null;
			break;
		case Split_Miner:
			rows[0] = "Epsilon";
			rows[1] = "Frequency";
			rows[2] = "Precision"; 
			rows[3] = "fitness" ;
			rows[4] = "f-Measure";
			
			break;
		}
		dtm = new DefaultTableModel(cols, rows);
		resultTable.setModel(dtm);
	}

	public void sycn() {
		revalidate();
		repaint();

	}

	public void createRow(PNRepResult pnRep, float noiseThreshold, double precision) {
		Map<String, Object> info = pnRep.getInfo();

		System.out.println("TraceFitness");
		System.out.println(info.get(pnRep.TRACEFITNESS).toString());
		double traceFitness = Double.parseDouble((info.get(pnRep.TRACEFITNESS)).toString());
		traceFitness = Math.round(traceFitness * 10000d) / 10000d;
		double fScore = (2 * traceFitness * precision) / (traceFitness + precision);
		fScore = Math.round(fScore * 10000d) / 10000d;
		Object[] row = { noiseThreshold, precision, traceFitness, fScore };
		System.out.printf(
				"Testing: ResultBoard: noiseThreshold=%.2f , moveModelFitness=%.2f, traceFitness=%.2f, fScore=%.2f %n",
				noiseThreshold, precision, traceFitness, fScore);
		setRow(row);
	}
	
	public void createRow( double precision, double fitness) {
		createRow(1.0d, precision, fitness);
	}

	public void createRow(double freq, double precision, double fitness) {

		double fScore = (2 * fitness * precision) / (fitness + precision);
		fScore = Math.round(fScore * 10000d) / 10000d;
		Object[] row = { freq, precision, fitness, fScore };
		System.out.printf(
				"Testing: ResultBoard: noiseThreshold=%.2f , moveModelFitness=%.2f, traceFitness=%.2f, fScore=%.2f %n",
				freq, precision, fitness, fScore);
		setRow(row);
	}
	
	public void createRow(double epsilon, double freq, double precision, double fitness) {

		double fScore = (2 * fitness * precision) / (fitness + precision);
		fScore = Math.round(fScore * 10000d) / 10000d;
		Object[] row = {epsilon, freq, precision, fitness, fScore };
		System.out.printf(
				"Testing: ResultBoard: noiseThreshold=%.2f , moveModelFitness=%.2f, traceFitness=%.2f, fScore=%.2f %n",
				freq, precision, fitness, fScore);
		setRow(row);
	}

}
