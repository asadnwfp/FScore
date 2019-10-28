package org.processmining.plugins;


public enum Miners {
	ILP_Miner("ILP Miner",false,false),
	Split_Miner("Split Miner",true,true),
	Inductive_Miner("Inductive Miner",false,false);
	
	private final String name;
	private final boolean frequency;
	private final boolean epsilon;
	
	Miners(String name,boolean noise,boolean epsilon){
		this.name = name;
		this.frequency = noise;
		this.epsilon = epsilon;
	}
	
	
	
	public boolean hasFrequency() {
		return frequency;
	}



	public boolean hasEpsilon() {
		return epsilon;
	}



	public String getMinerName() {
		return name;
	}

	
	public String toString() {
		return this.name;
	}
}
