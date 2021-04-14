
package edu.postech.aadl.xtext.propspec;

import edu.postech.aadl.xtext.propspec.PropSpecStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages
 * without equinox extension registry
 */
public class PropSpecStandaloneSetup extends PropSpecStandaloneSetupGenerated{

	public static void doSetup() {
		new PropSpecStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

