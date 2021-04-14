package edu.postech.aadl.synch.maude.contspec;

import org.osate.ba.aadlba.SimpleExpression;
import org.osate.ba.aadlba.ValueVariable;

public class ContFunc extends ContSpecItem {

	private ValueVariable param;

	public ContFunc(ValueVariable target, ValueVariable param, SimpleExpression expression) {
		super(target, expression);
		this.param = param;
	}

	public ValueVariable getParam() {
		return param;
	}

}
