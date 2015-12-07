<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.0"?>

<plugin>

    <extension
            point="org.eclipse.ui.editors">
        <editor
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.XtextEditor"
            contributorClass="org.eclipse.ui.editors.text.TextEditorActionContributor"
            default="true"
            extensions="${extensions}"
            id="${package}.${name}"
            name="${name} Editor">
        </editor>
    </extension>
    <extension
        point="org.eclipse.ui.handlers">
        <handler
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.hyperlinking.OpenDeclarationHandler"
            commandId="org.eclipse.xtext.ui.editor.hyperlinking.OpenDeclaration">
            <activeWhen>
                <reference
                    definitionId="${package}.${name}.Editor.opened">
                </reference>
            </activeWhen>
        </handler>
        <handler
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.handler.ValidateActionHandler"
            commandId="${package}.${name}.validate">
         <activeWhen>
            <reference
                    definitionId="${package}.${name}.Editor.opened">
            </reference>
         </activeWhen>
          </handler>
         <!-- copy qualified name -->
        <handler
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.copyqualifiedname.EditorCopyQualifiedNameHandler"
            commandId="org.eclipse.xtext.ui.editor.copyqualifiedname.EditorCopyQualifiedName">
            <activeWhen>
                <reference definitionId="${package}.${name}.Editor.opened" />
            </activeWhen>
        </handler>
        <handler
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.copyqualifiedname.OutlineCopyQualifiedNameHandler"
            commandId="org.eclipse.xtext.ui.editor.copyqualifiedname.OutlineCopyQualifiedName">
            <activeWhen>
                <and>
                    <reference definitionId="${package}.${name}.XtextEditor.opened" />
                    <iterate>
                        <adapt type="org.eclipse.xtext.ui.editor.outline.IOutlineNode" />
                    </iterate>
                </and>
            </activeWhen>
        </handler>
    </extension>
    <extension point="org.eclipse.core.expressions.definitions">
        <definition id="${package}.${name}.Editor.opened">
            <and>
                <reference definitionId="isActiveEditorAnInstanceOfXtextEditor"/>
                <with variable="activeEditor">
                    <test property="org.eclipse.xtext.ui.editor.XtextEditor.languageName" 
                        value="${package}.${name}" 
                        forcePluginActivation="true"/>
                </with>        
            </and>
        </definition>
        <definition id="${package}.${name}.XtextEditor.opened">
            <and>
                <reference definitionId="isXtextEditorActive"/>
                <with variable="activeEditor">
                    <test property="org.eclipse.xtext.ui.editor.XtextEditor.languageName" 
                        value="${package}.${name}" 
                        forcePluginActivation="true"/>
                </with>        
            </and>
        </definition>
    </extension>
    <extension
            point="org.eclipse.ui.preferencePages">
        <page
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage"
            id="${package}.${name}"
            name="${name}">
            <keywordReference id="${package}.ui.keyword_${name}"/>
        </page>
        <page
            category="${package}.${name}"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.syntaxcoloring.SyntaxColoringPreferencePage"
            id="${package}.${name}.coloring"
            name="Syntax Coloring">
            <keywordReference id="${package}.ui.keyword_${name}"/>
        </page>
        <page
            category="${package}.${name}"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.templates.XtextTemplatePreferencePage"
            id="${package}.${name}.templates"
            name="Templates">
            <keywordReference id="${package}.ui.keyword_${name}"/>
        </page>
    </extension>
    <extension
            point="org.eclipse.ui.propertyPages">
        <page
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage"
            id="${package}.${name}"
            name="${name}">
            <keywordReference id="${package}.ui.keyword_${name}"/>
            <enabledWhen>
                <adapt type="org.eclipse.core.resources.IProject"/>
            </enabledWhen>
            <filter name="projectNature" value="org.eclipse.xtext.ui.shared.xtextNature"/>
        </page>
    </extension>
    <extension
        point="org.eclipse.ui.keywords">
        <keyword
            id="${package}.ui.keyword_${name}"
            label="${name}"/>
    </extension>
    <extension
         point="org.eclipse.ui.commands">
      <command
            description="Trigger expensive validation"
            id="${package}.${name}.validate"
            name="Validate">
      </command>
      <!-- copy qualified name -->
      <command
            id="org.eclipse.xtext.ui.editor.copyqualifiedname.EditorCopyQualifiedName"
            categoryId="org.eclipse.ui.category.edit"
            description="Copy the qualified name for the selected element"
            name="Copy Qualified Name">
      </command>
      <command
            id="org.eclipse.xtext.ui.editor.copyqualifiedname.OutlineCopyQualifiedName"
            categoryId="org.eclipse.ui.category.edit"
            description="Copy the qualified name for the selected element"
            name="Copy Qualified Name">
      </command>
    </extension>
    <extension point="org.eclipse.ui.menus">
        <menuContribution
            locationURI="popup:#TextEditorContext?after=group.edit">
             <command
                 commandId="${package}.${name}.validate"
                 style="push"
                 tooltip="Trigger expensive validation">
            <visibleWhen checkEnabled="false">
                <reference
                    definitionId="${package}.${name}.Editor.opened">
                </reference>
            </visibleWhen>
         </command>  
         </menuContribution>
         <!-- copy qualified name -->
         <menuContribution locationURI="popup:#TextEditorContext?after=copy">
             <command commandId="org.eclipse.xtext.ui.editor.copyqualifiedname.EditorCopyQualifiedName" 
                 style="push" tooltip="Copy Qualified Name">
                <visibleWhen checkEnabled="false">
                    <reference definitionId="${package}.${name}.Editor.opened" />
                </visibleWhen>
             </command>  
         </menuContribution>
         <menuContribution locationURI="menu:edit?after=copy">
             <command commandId="org.eclipse.xtext.ui.editor.copyqualifiedname.EditorCopyQualifiedName"
                style="push" tooltip="Copy Qualified Name">
                <visibleWhen checkEnabled="false">
                    <reference definitionId="${package}.${name}.Editor.opened" />
                </visibleWhen>
             </command>  
         </menuContribution>
         <menuContribution locationURI="popup:org.eclipse.xtext.ui.outline?after=additions">
            <command commandId="org.eclipse.xtext.ui.editor.copyqualifiedname.OutlineCopyQualifiedName" 
                style="push" tooltip="Copy Qualified Name">
                 <visibleWhen checkEnabled="false">
                    <and>
                        <reference definitionId="${package}.${name}.XtextEditor.opened" />
                        <iterate>
                            <adapt type="org.eclipse.xtext.ui.editor.outline.IOutlineNode" />
                        </iterate>
                    </and>
                </visibleWhen>
            </command>
         </menuContribution>
    </extension>
    <extension point="org.eclipse.ui.menus">
        <menuContribution locationURI="popup:#TextEditorContext?endof=group.find">
            <command commandId="org.eclipse.xtext.ui.editor.FindReferences">
                <visibleWhen checkEnabled="false">
                    <reference definitionId="${package}.${name}.Editor.opened">
                    </reference>
                </visibleWhen>
            </command>
        </menuContribution>
    </extension>
    <extension point="org.eclipse.ui.handlers">
        <handler
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.findrefs.FindReferencesHandler"
            commandId="org.eclipse.xtext.ui.editor.FindReferences">
            <activeWhen>
                <reference
                    definitionId="${package}.${name}.Editor.opened">
                </reference>
            </activeWhen>
        </handler>
    </extension>   

<!-- adding resource factories -->

    <extension
        point="org.eclipse.emf.ecore.extension_parser">
        <parser
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.resource.IResourceFactory"
            type="fix">
        </parser>
    </extension>
    <extension point="org.eclipse.xtext.extension_resourceServiceProvider">
        <resourceServiceProvider
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.resource.IResourceUIServiceProvider"
            uriExtension="fix">
        </resourceServiceProvider>
    </extension>


    <!-- marker definitions for ${package}.${name} -->
    <extension
            id="fix.check.fast"
            name="${name} Problem"
            point="org.eclipse.core.resources.markers">
        <super type="org.eclipse.xtext.ui.check.fast"/>
        <persistent value="true"/>
    </extension>
    <extension
            id="fix.check.normal"
            name="${name} Problem"
            point="org.eclipse.core.resources.markers">
        <super type="org.eclipse.xtext.ui.check.normal"/>
        <persistent value="true"/>
    </extension>
    <extension
            id="fix.check.expensive"
            name="${name} Problem"
            point="org.eclipse.core.resources.markers">
        <super type="org.eclipse.xtext.ui.check.expensive"/>
        <persistent value="true"/>
    </extension>

   <extension
         point="org.eclipse.xtext.builder.participant">
      <participant
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.builder.IXtextBuilderParticipant"
            fileExtensions="${extensions}"
            >
      </participant>
   </extension>
   <extension
            point="org.eclipse.ui.preferencePages">
        <page
            category="${package}.${name}"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.builder.preferences.BuilderPreferencePage"
            id="${package}.${name}.compiler.preferencePage"
            name="Compiler">
            <keywordReference id="${package}.ui.keyword_${name}"/>
        </page>
    </extension>
    <extension
            point="org.eclipse.ui.propertyPages">
        <page
            category="${package}.${name}"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.builder.preferences.BuilderPreferencePage"
            id="${package}.${name}.compiler.propertyPage"
            name="Compiler">
            <keywordReference id="${package}.ui.keyword_${name}"/>
            <enabledWhen>
                <adapt type="org.eclipse.core.resources.IProject"/>
            </enabledWhen>
            <filter name="projectNature" value="org.eclipse.xtext.ui.shared.xtextNature"/>
        </page>
    </extension>
    <extension point="org.eclipse.ui.menus">
        <menuContribution locationURI="popup:#TextEditorContext?after=xtext.ui.openDeclaration">
            <command
                commandId="org.eclipse.xtext.ui.OpenGeneratedFileCommand"
                id="${package}.${name}.OpenGeneratedCode"
                style="push">
                    <visibleWhen checkEnabled="false">
                        <reference definitionId="${package}.${name}.Editor.opened" />
                    </visibleWhen>
            </command>
        </menuContribution>
    </extension>
    <extension point="org.eclipse.ui.handlers">
        <handler
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.generator.trace.OpenGeneratedFileHandler"
            commandId="org.eclipse.xtext.ui.OpenGeneratedFileCommand">
                <activeWhen>
                    <reference definitionId="${package}.${name}.Editor.opened" />
                </activeWhen>
        </handler>
    </extension>

    <!-- Quick Outline -->
    <extension
        point="org.eclipse.ui.handlers">
        <handler 
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.outline.quickoutline.ShowQuickOutlineActionHandler"
            commandId="org.eclipse.xtext.ui.editor.outline.QuickOutline">
            <activeWhen>
                <reference
                    definitionId="${package}.${name}.Editor.opened">
                </reference>
            </activeWhen>
        </handler>
    </extension>
    <extension
        point="org.eclipse.ui.commands">
        <command
            description="Open the quick outline."
            id="org.eclipse.xtext.ui.editor.outline.QuickOutline"
            name="Quick Outline">
        </command>
    </extension>
    <extension point="org.eclipse.ui.menus">
        <menuContribution
            locationURI="popup:#TextEditorContext?after=group.open">
            <command commandId="org.eclipse.xtext.ui.editor.outline.QuickOutline"
                style="push"
                tooltip="Open Quick Outline">
                <visibleWhen checkEnabled="false">
                    <reference definitionId="${package}.${name}.Editor.opened"/>
                </visibleWhen>
            </command>
        </menuContribution>
    </extension>
    <!-- quickfix marker resolution generator for ${package}.${name} -->
    <extension
            point="org.eclipse.ui.ide.markerResolution">
        <markerResolutionGenerator
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.quickfix.MarkerResolutionGenerator"
            markerType="${package}.ui.fix.check.fast">
            <attribute
                name="FIXABLE_KEY"
                value="true">
            </attribute>
        </markerResolutionGenerator>
        <markerResolutionGenerator
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.quickfix.MarkerResolutionGenerator"
            markerType="${package}.ui.fix.check.normal">
            <attribute
                name="FIXABLE_KEY"
                value="true">
            </attribute>
        </markerResolutionGenerator>
        <markerResolutionGenerator
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.quickfix.MarkerResolutionGenerator"
            markerType="${package}.ui.fix.check.expensive">
            <attribute
                name="FIXABLE_KEY"
                value="true">
            </attribute>
        </markerResolutionGenerator>
    </extension>
       <!-- Rename Refactoring -->
    <extension point="org.eclipse.ui.handlers">
        <handler 
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.refactoring.ui.DefaultRenameElementHandler"
            commandId="org.eclipse.xtext.ui.refactoring.RenameElement">
            <activeWhen>
                <reference
                    definitionId="${package}.${name}.Editor.opened">
                </reference>
            </activeWhen>
        </handler>
    </extension>
    <extension point="org.eclipse.ui.menus">
         <menuContribution
            locationURI="popup:#TextEditorContext?after=group.edit">
         <command commandId="org.eclipse.xtext.ui.refactoring.RenameElement"
               style="push">
            <visibleWhen checkEnabled="false">
               <reference
                     definitionId="${package}.${name}.Editor.opened">
               </reference>
            </visibleWhen>
         </command>
      </menuContribution>
   </extension>
   <extension point="org.eclipse.ui.preferencePages">
        <page
            category="${package}.${name}"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.refactoring.ui.RefactoringPreferencePage"
            id="${package}.${name}.refactoring"
            name="Refactoring">
            <keywordReference id="${package}.ui.keyword_${name}"/>
        </page>
    </extension>

  <extension point="org.eclipse.compare.contentViewers">
    <viewer id="${package}.${name}.compare.contentViewers"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.compare.InjectableViewerCreator"
            extensions="fix">
    </viewer>
  </extension>
  <extension point="org.eclipse.compare.contentMergeViewers">
    <viewer id="${package}.${name}.compare.contentMergeViewers"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.compare.InjectableViewerCreator"
            extensions="${extensions}" label="${name} Compare">
     </viewer>
  </extension>
  <extension point="org.eclipse.ui.editors.documentProviders">
    <provider id="${package}.${name}.editors.documentProviders"
            class="${package}.ui.${name}ExecutableExtensionFactory:org.eclipse.xtext.ui.editor.model.XtextDocumentProvider"
            extensions="${extensions}">
    </provider>
  </extension>
  <extension point="org.eclipse.team.core.fileTypes">
    <fileTypes
            extension="${extensions}"
            type="text">
    </fileTypes>
  </extension>

</plugin>
