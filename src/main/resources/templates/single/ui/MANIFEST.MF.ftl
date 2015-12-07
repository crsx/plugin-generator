Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: ${package}.ui
Bundle-Vendor: ${vendor}
Bundle-Version: ${arguments.version}<#if arguments.snapshot>.qualifier</#if>
Bundle-SymbolicName: ${package}.ui; singleton:=true
Bundle-ActivationPolicy: lazy
Require-Bundle: ${package};visibility:=reexport,
 org.eclipse.xtext.ui,
 org.eclipse.ui.editors;bundle-version="3.5.0",
 org.eclipse.ui.ide;bundle-version="3.5.0",
 org.eclipse.xtext.ui.shared,
 org.eclipse.ui,
 org.eclipse.xtext.builder,
 org.eclipse.xtext.xbase.lib,
 org.eclipse.xtext.common.types.ui,
 org.eclipse.xtext.ui.codetemplates.ui,
 org.eclipse.compare
Import-Package: org.apache.log4j
Bundle-RequiredExecutionEnvironment: JavaSE-1.8
Export-Package: ${package}.ui.quickfix,
 ${package}.ui.contentassist,
 ${package}.ui.internal,
 ${package}.ui.contentassist.antlr
Bundle-Activator: ${package}.ui.internal.${name}Activator
