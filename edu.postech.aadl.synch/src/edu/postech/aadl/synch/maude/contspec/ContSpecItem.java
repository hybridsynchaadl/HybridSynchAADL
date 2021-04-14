package edu.postech.aadl.synch.maude.contspec;

import org.osate.ba.aadlba.SimpleExpression;
import org.osate.ba.aadlba.ValueVariable;

public abstract class ContSpecItem {
	private ValueVariable target;
	private SimpleExpression exp;

	ContSpecItem(ValueVariable target, SimpleExpression expression) {
		this.target = target;
		this.exp = expression;
	}

	public ValueVariable getTarget() {
		return target;
	}

	public SimpleExpression getExpression() {
		return exp;
	}
}
