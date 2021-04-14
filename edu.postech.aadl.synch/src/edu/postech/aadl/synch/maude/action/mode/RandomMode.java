package edu.postech.aadl.synch.maude.action.mode;

public class RandomMode implements Mode {

	private int randomSeed;
	private float minParamValue;
	private float maxParamValue;

	public RandomMode(int randomSeed, float minParamValue, float maxParamValue) {
		this.randomSeed = randomSeed;
		this.minParamValue = minParamValue;
		this.maxParamValue = maxParamValue;
	}

	public float getMinParamSignedValue() {
		return minParamValue;
	}

	public float getMaxParamSignedValue() {
		return maxParamValue;
	}

	public float getMinParamValue() {
		return minParamValue < 0 ? minParamValue * -1 : minParamValue;
	}

	public float getMaxParamValue() {
		return maxParamValue < 0 ? maxParamValue * -1 : maxParamValue;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public String getName() {
		return "random";
	}

}
