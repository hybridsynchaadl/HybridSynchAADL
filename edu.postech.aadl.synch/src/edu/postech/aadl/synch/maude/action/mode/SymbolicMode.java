package edu.postech.aadl.synch.maude.action.mode;

public class SymbolicMode implements Mode {

	private int loopBound;
	private int transBound;

	public SymbolicMode(int loopBound, int transBound) {
		this.loopBound = loopBound;
		this.transBound = transBound;
	}

	public String getName() {
		return "symbolic";
	}

	public int getloopBound() {
		return loopBound;
	}

	public int gettransBound() {
		return transBound;
	}

}
