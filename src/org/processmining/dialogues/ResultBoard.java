package org.processmining.dialogues;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class ResultBoard extends JFrame {

	JTextField fitness;
	JTextField stepSize;
	JTextField precision;
	JTable resultTable;
	DefaultTableModel dtm;
	ArrayList<String[][]> colsList;

	public ResultBoard() {
		setTitle("F-Score");
		resultTable = new JTable();
		String[] rows = { "Index","Precision", "fitness", "f-Measure" };
		colsList = new ArrayList<String[][]>();
		String [][] cols = null;
		dtm = new DefaultTableModel(cols, rows);
		resultTable.setModel(dtm);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(resultTable);

		add(sp);
		setSize(500, 500);
		setVisible(true);
		setLocationRelativeTo(null);

	}

	public void setRow(Object[] rows) {
		//colsList.add(rows);
		dtm.addRow(rows);
		dtm.fireTableDataChanged();
		
	}

	
	
	public void sycn() {
		revalidate();
		repaint();
		
	}
	
	public void createRow(PNRepResult pnRep, float noiseThreshold, double precision ) {
		Map<String, Object> info = pnRep.getInfo();

		System.out.println("TraceFitness");
		System.out.println(info.get(pnRep.TRACEFITNESS).toString());
		double traceFitness = Double.parseDouble((info.get(pnRep.TRACEFITNESS)).toString());
		traceFitness = Math.round(traceFitness*10000d)/10000d;
		double fScore = (2 * traceFitness * precision) / (traceFitness + precision);
		fScore = Math.round(fScore*10000d)/10000d;
		Object[] row = { noiseThreshold , precision, traceFitness, fScore };
		System.out.printf("Testing: ResultBoard: noiseThreshold=%.2f , moveModelFitness=%.2f, traceFitness=%.2f, fScore=%.2f %n",noiseThreshold , precision, traceFitness, fScore );
		setRow(row);
	}

}
