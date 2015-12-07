<?xml version="1.0" encoding="UTF-8"?>
<site>
<#list plugins as plugin>
   <feature url="features/${plugin.pluginPackage}.sdk_0.0.0.jar" id="${plugin.pluginPackage}.sdk" version="0.0.0">
      <category name="${plugin.pluginPackage}"/>
   </feature>
</#list>
<#list plugins as plugin>
    <category-def name="${plugin.pluginPackage}" label="${plugin.pluginName}"/>
</#list>
</site>
