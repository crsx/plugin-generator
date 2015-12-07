<?xml version="1.0" encoding="UTF-8"?>
<feature
      id="${package}.sdk"
      label="${name}"
      version="${arguments.version}<#if arguments.snapshot>.qualifier</#if>"
      provider-name="${vendor}">

<#if description??>
   <description>
      ${description}
   </description>
</#if>

   <includes
         id="org.eclipse.xtext.runtime"
         version="0.0.0"/>

   <includes
         id="org.eclipse.xtext.ui"
         version="0.0.0"/>

   <requires>
      <import plugin="org.eclipse.xtext"/>
      <import plugin="org.eclipse.equinox.common" version="3.5.0" match="greaterOrEqual"/>
      <import plugin="org.eclipse.xtext.util"/>
      <import plugin="org.eclipse.emf.ecore"/>
      <import plugin="org.eclipse.emf.common"/>
      <import plugin="org.eclipse.xtext.xbase.lib"/>
      <import plugin="org.antlr.runtime"/>
      <import plugin="org.eclipse.xtext.common.types"/>
      <import plugin="org.apache.log4j"/>
      <import plugin="org.eclipse.xtext.ui"/>
      <import plugin="org.eclipse.ui.editors" version="3.5.0" match="greaterOrEqual"/>
      <import plugin="org.eclipse.ui.ide" version="3.5.0" match="greaterOrEqual"/>
      <import plugin="org.eclipse.xtext.ui.shared"/>
      <import plugin="org.eclipse.ui"/>
      <import plugin="org.eclipse.xtext.builder"/>
      <import plugin="org.eclipse.xtext.common.types.ui"/>
      <import plugin="org.eclipse.xtext.ui.codetemplates.ui"/>
      <import plugin="org.eclipse.compare"/>
   </requires>

   <plugin
         id="${package}"
         download-size="0"
         install-size="0"
         version="${arguments.version}<#if arguments.snapshot>.qualifier</#if>"
         unpack="false"/>

   <plugin
         id="${package}.ui"
         download-size="0"
         install-size="0"
         version="${arguments.version}<#if arguments.snapshot>.qualifier</#if>"
         unpack="false"/>

</feature>
