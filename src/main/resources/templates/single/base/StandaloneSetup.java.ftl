/*
 * generated by Xtext
 */
package ${package};

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class ${name}StandaloneSetup extends ${name}StandaloneSetupGenerated{

    public static void doSetup() {
        new ${name}StandaloneSetup().createInjectorAndDoEMFRegistration();
    }
}

