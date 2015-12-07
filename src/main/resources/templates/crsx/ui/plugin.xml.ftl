<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.0"?>

<plugin>

    <extension
            point="org.eclipse.ui.editors">
        <editor
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.XtextEditor"
            contributorClass="org.eclipse.ui.editors.text.TextEditorActionContributor"
            default="true"
            extensions="crs"
            id="${package}"
            name="Crsx Editor">
        </editor>
    </extension>
    <extension
        point="org.eclipse.ui.handlers">
        <handler
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.hyperlinking.OpenDeclarationHandler"
            commandId="org.eclipse.xtext.ui.editor.hyperlinking.OpenDeclaration">
            <activeWhen>
                <reference
                    definitionId="${package}.Editor.opened">
                </reference>
            </activeWhen>
        </handler>
        <handler
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.handler.ValidateActionHandler"
            commandId="${package}.validate">
         <activeWhen>
            <reference
                    definitionId="${package}.Editor.opened">
            </reference>
         </activeWhen>
      </handler>
    </extension>
    <extension point="org.eclipse.core.expressions.definitions">
        <definition id="${package}.Editor.opened">
            <and>
                <reference definitionId="isActiveEditorAnInstanceOfXtextEditor"/>
                <with variable="activeEditor">
                    <test property="org.eclipse.xtext.ui.editor.XtextEditor.languageName" 
                        value="${package}" 
                        forcePluginActivation="true"/>
                </with>        
            </and>
        </definition>
    </extension>
    <extension
            point="org.eclipse.ui.preferencePages">
        <page
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage"
            id="${package}"
            name="Crsx">
            <keywordReference id="${package}.ui.keyword_Crsx"/>
        </page>
        <page
            category="${package}"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.syntaxcoloring.SyntaxColoringPreferencePage"
            id="${package}.coloring"
            name="Syntax Coloring">
            <keywordReference id="${package}.ui.keyword_Crsx"/>
        </page>
        <page
            category="${package}"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.templates.XtextTemplatePreferencePage"
            id="${package}.templates"
            name="Templates">
            <keywordReference id="${package}.ui.keyword_Crsx"/>
        </page>
    </extension>
    <extension
            point="org.eclipse.ui.propertyPages">
        <page
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage"
            id="${package}"
            name="Crsx">
            <keywordReference id="${package}.ui.keyword_Crsx"/>
            <enabledWhen>
	            <adapt type="org.eclipse.core.resources.IProject"/>
			</enabledWhen>
	        <filter name="projectNature" value="org.eclipse.xtext.ui.shared.xtextNature"/>
        </page>
    </extension>
    <extension
        point="org.eclipse.ui.keywords">
        <keyword
            id="${package}.ui.keyword_Crsx"
            label="Crsx"/>
    </extension>
    <extension
         point="org.eclipse.ui.commands">
      <command
            description="Trigger expensive validation"
            id="${package}.validate"
            name="Validate">
      </command>
    </extension>
    <extension point="org.eclipse.ui.menus">
        <menuContribution
            locationURI="popup:#TextEditorContext?after=group.edit">
             <command
                 commandId="${package}.validate"
                 style="push"
                 tooltip="Trigger expensive validation">
            <visibleWhen checkEnabled="false">
                <reference
                    definitionId="${package}.Editor.opened">
                </reference>
            </visibleWhen>
         </command>  
         </menuContribution>
    </extension>
    <extension point="org.eclipse.ui.menus">
		<menuContribution locationURI="popup:#TextEditorContext?endof=group.find">
			<command commandId="org.eclipse.xtext.ui.editor.FindReferences">
				<visibleWhen checkEnabled="false">
                	<reference definitionId="${package}.Editor.opened">
                	</reference>
            	</visibleWhen>
			</command>
		</menuContribution>
	</extension>
	<extension point="org.eclipse.ui.handlers">
	    <handler
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.findrefs.FindReferencesHandler"
            commandId="org.eclipse.xtext.ui.editor.FindReferences">
            <activeWhen>
                <reference
                    definitionId="${package}.Editor.opened">
                </reference>
            </activeWhen>
        </handler>
    </extension>   

<!-- adding resource factories -->

	<extension
		point="org.eclipse.emf.ecore.extension_parser">
		<parser
			class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.resource.IResourceFactory"
			type="crs">
		</parser>
	</extension>
	<extension point="org.eclipse.xtext.extension_resourceServiceProvider">
        <resourceServiceProvider
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.resource.IResourceUIServiceProvider"
            uriExtension="crs">
        </resourceServiceProvider>
    </extension>



   <extension
         point="org.eclipse.xtext.builder.participant">
      <participant
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.builder.IXtextBuilderParticipant">
      </participant>
   </extension>
   <extension
            point="org.eclipse.ui.preferencePages">
        <page
            category="${package}"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.builder.preferences.BuilderPreferencePage"
            id="${package}.compiler.preferencePage"
            name="Compiler">
            <keywordReference id="${package}.ui.keyword_Crsx"/>
        </page>
    </extension>
    <extension
            point="org.eclipse.ui.propertyPages">
        <page
            category="${package}"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.builder.preferences.BuilderPreferencePage"
            id="${package}.compiler.propertyPage"
            name="Compiler">
            <keywordReference id="${package}.ui.keyword_Crsx"/>
            <enabledWhen>
	            <adapt type="org.eclipse.core.resources.IProject"/>
			</enabledWhen>
	        <filter name="projectNature" value="org.eclipse.xtext.ui.shared.xtextNature"/>
        </page>
    </extension>

	<!-- Quick Outline -->
	<extension
		point="org.eclipse.ui.handlers">
		<handler 
			class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.outline.quickoutline.ShowQuickOutlineActionHandler"
			commandId="org.eclipse.xtext.ui.editor.outline.QuickOutline">
			<activeWhen>
				<reference
					definitionId="${package}.Editor.opened">
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
					<reference definitionId="${package}.Editor.opened"/>
				</visibleWhen>
			</command>
		</menuContribution>
	</extension>
   <!-- quickfix marker resolution generator -->
   <extension
         point="org.eclipse.ui.ide.markerResolution">
      <markerResolutionGenerator
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.quickfix.MarkerResolutionGenerator">
      </markerResolutionGenerator>
   </extension>
   	<!-- Rename Refactoring -->
	<extension point="org.eclipse.ui.handlers">
		<handler 
			class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.refactoring.ui.IRenameElementHandler"
			commandId="org.eclipse.xtext.ui.refactoring.RenameElement">
			<activeWhen>
				<reference
					definitionId="${package}.Editor.opened">
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
                     definitionId="${package}.Editor.opened">
               </reference>
            </visibleWhen>
         </command>
      </menuContribution>
   </extension>
   <extension point="org.eclipse.ui.preferencePages">
	    <page
	        category="${package}"
	        class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.refactoring.ui.RefactoringPreferencePage"
	        id="${package}.refactoring"
	        name="Refactoring">
	        <keywordReference id="${package}.ui.keyword_Crsx"/>
	    </page>
	</extension>

  <extension point="org.eclipse.compare.contentViewers">
    <viewer id="${package}.compare.contentViewers"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.compare.InjectableViewerCreator"
            extensions="crs">
    </viewer>
  </extension>
  <extension point="org.eclipse.compare.contentMergeViewers">
    <viewer id="${package}.compare.contentMergeViewers"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.compare.InjectableViewerCreator"
            extensions="crs" label="Crsx Compare">
     </viewer>
  </extension>
  <extension point="org.eclipse.ui.editors.documentProviders">
    <provider id="${package}.editors.documentProviders"
            class="${package}.ui.CrsxExecutableExtensionFactory:org.eclipse.xtext.ui.editor.model.XtextDocumentProvider"
            extensions="crs">
    </provider>
  </extension>

</plugin>
