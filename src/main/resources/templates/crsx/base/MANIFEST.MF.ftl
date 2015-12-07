Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: ${package}
Bundle-Vendor: ${vendor}
Bundle-Version: ${arguments.version}<#if arguments.snapshot>.qualifier</#if>
Bundle-SymbolicName: ${package}; singleton:=true
Bundle-ActivationPolicy: lazy
Require-Bundle: org.eclipse.xtext;bundle-version="${arguments.xtextVersionNoSnapshot}",
 org.apache.log4j,
 org.apache.commons.logging;bundle-version="1.0.4";resolution:=optional;visibility:=reexport,
 org.eclipse.xtext.generator;resolution:=optional,
 org.eclipse.emf.codegen.ecore;resolution:=optional,
 org.eclipse.emf.mwe.utils;resolution:=optional,
 org.eclipse.emf.mwe2.launch;resolution:=optional,
 org.eclipse.xtext.util,
 org.eclipse.emf.ecore,
 org.eclipse.emf.common,
 org.antlr.runtime,
 org.eclipse.xtext.common.types,
 org.eclipse.equinox.preferences,
 org.eclipse.core.runtime,
 org.objectweb.asm;bundle-version="[5.0.1,6.0.0)";resolution:=optional,
 org.eclipse.xtext.xbase.lib
Import-Package: org.apache.log4j,
 org.apache.commons.logging,
 org.eclipse.xtext.xbase.lib,
 org.eclipse.xtend2.lib
Bundle-RequiredExecutionEnvironment: JavaSE-1.6
Export-Package: ${package},
 ${package}.crsx,
 ${package}.crsx.impl,
 ${package}.crsx.util,
 ${package}.formatting,
 ${package}.generator,
 ${package}.lexer,
 ${package}.parser.antlr,
 ${package}.parser.antlr.internal,
 ${package}.parser.antlr.lexer,
 ${package}.scoping,
 ${package}.serializer,
 ${package}.services,
 ${package}.validation

