/* Copyright © 2012-2015 IBM Corporation. */

package ${package};

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class CrsxStandaloneSetup extends CrsxStandaloneSetupGenerated{

	public static void doSetup() {
		new CrsxStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

