Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: ${package}
Bundle-Vendor: ${vendor}
Bundle-Version: ${arguments.version}<#if arguments.snapshot>.qualifier</#if>
Bundle-SymbolicName: ${package}; singleton:=true
Bundle-ActivationPolicy: lazy
Require-Bundle: org.eclipse.xtext;visibility:=reexport,
 org.eclipse.equinox.common;bundle-version="3.5.0",
 org.eclipse.xtext.util,
 org.eclipse.emf.ecore,
 org.eclipse.emf.common,
 org.eclipse.xtext.xbase.lib,
 org.antlr.runtime,
 org.eclipse.xtext.common.types,
 org.objectweb.asm;bundle-version="[5.0.1,6.0.0)";resolution:=optional
Import-Package: org.apache.log4j
Bundle-RequiredExecutionEnvironment: JavaSE-1.8
Export-Package: ${package},
 ${package}.services,
 ${package}.${name?lower_case},
 ${package}.${name?lower_case}.impl,
 ${package}.${name?lower_case}.util,
 ${package}.serializer,
 ${package}.parser.antlr,
 ${package}.parser.antlr.internal,
 ${package}.validation,
 ${package}.scoping,
 ${package}.generator,
 ${package}.formatting

