package edu.postech.aadl.synch.maude.contspec;

import org.osate.ba.aadlba.SimpleExpression;
import org.osate.ba.aadlba.ValueVariable;

public class ODE extends ContSpecItem {

	public ODE(ValueVariable target, SimpleExpression expression) {
		super(target, expression);
	}
}
