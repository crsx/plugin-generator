module ${module}

import org.eclipse.emf.mwe.utils.*
import org.eclipse.xtext.generator.*
import org.eclipse.xtext.ui.generator.*

var projectName = "${projectName}"
var grammarURI = "platform:/resource/${r"${projectName}"}/src/${grammarURI}"
var runtimeProject = "../${r"${projectName}"}"
var fileExtensions = "${extensions}"
var generateXtendStub = false
var encoding = "UTF-8"

Workflow {
    bean = StandaloneSetup {
    
        // use an XtextResourceset throughout the process, which is able to resolve classpath:/ URIs.
        resourceSet = org.eclipse.xtext.resource.XtextResourceSet:theResourceSet {}

        // add mappings from platform:/resource to classpath:/
        uriMap = {
          from = "platform:/resource/org.eclipse.xtext.xbase/"
          to = "classpath:/"
        }
        uriMap = {
          from = "platform:/resource/org.eclipse.xtext.common.types/"
          to = "classpath:/"
        }
        
        platformUri = ".."
        // The following two lines can be removed, if Xbase is not used.
        registerGeneratedEPackage = "org.eclipse.xtext.xbase.XbasePackage"
        registerGenModelFile = "platform:/resource/org.eclipse.xtext.xbase/model/Xbase.genmodel"
    }
    
    component = DirectoryCleaner {
        directory = "${r"${runtimeProject}"}/src-gen"
    }
    
    component = DirectoryCleaner {
        directory = "${r"${runtimeProject}"}/model/generated"
    }
    
    component = DirectoryCleaner {
        directory = "${r"${runtimeProject}"}.ui/src-gen"
    }
    
    /*component = DirectoryCleaner {
        directory = "${r"${runtimeProject}"}.tests/src-gen"
    }*/
    
    component = Generator {
        pathRtProject = runtimeProject
        pathUiProject = "${r"${runtimeProject}"}.ui"
        //pathTestProject = "${r"${runtimeProject}"}.tests"
        projectNameRt = projectName
        projectNameUi = "${r"${projectName}"}.ui"
        encoding = encoding
        language = auto-inject {
            uri = grammarURI
    
            // Java API to access grammar elements (required by several other fragments)
            fragment = grammarAccess.GrammarAccessFragment auto-inject {}
    
            // generates Java API for the generated EPackages
            fragment = ecore.EMFGeneratorFragment auto-inject {}
    
            // the old serialization component
            // fragment = parseTreeConstructor.ParseTreeConstructorFragment auto-inject {}    
    
            // serializer 2.0
            fragment = serializer.SerializerFragment auto-inject {
                generateStub = false
            }
    
            // a custom ResourceFactory for use with EMF
            fragment = resourceFactory.ResourceFactoryFragment auto-inject {}
    
            // The antlr parser generator fragment.
            fragment = parser.antlr.XtextAntlrGeneratorFragment auto-inject {
            //  options = {
            //      backtrack = true
            //  }
            }
    
            fragment = validation.JavaValidatorFragment auto-inject {}
    
            // scoping and exporting API
            fragment = scoping.ImportNamespacesScopingFragment auto-inject {}
            fragment = exporting.QualifiedNamesFragment auto-inject {}
            fragment = builder.BuilderIntegrationFragment auto-inject {}
    
            // generator API
            fragment = generator.GeneratorFragment auto-inject {}
    
            // formatter API
            fragment = formatting.FormatterFragment auto-inject {}
    
            // labeling API
            fragment = labeling.LabelProviderFragment auto-inject {}
    
            // outline API
            fragment = outline.OutlineTreeProviderFragment auto-inject {}
            fragment = outline.QuickOutlineFragment auto-inject {}
    
            // quickfix API
            fragment = quickfix.QuickfixProviderFragment auto-inject {}
    
            // content assist API
            fragment = contentAssist.ContentAssistFragment auto-inject {}
    
            // generates a more lightweight Antlr parser and lexer tailored for content assist
            fragment = parser.antlr.XtextAntlrUiGeneratorFragment auto-inject {}
    
            // generates junit test support classes into Generator#pathTestProject
            // fragment = junit.Junit4Fragment auto-inject {}
    
            // rename refactoring
            fragment = refactoring.RefactorElementNameFragment auto-inject {}
    
            // provides the necessary bindings for java types integration
            fragment = types.TypesGeneratorFragment auto-inject {}
    
            // generates the required bindings only if the grammar inherits from Xbase
            fragment = xbase.XbaseGeneratorFragment auto-inject {}
            
            // generates the required bindings only if the grammar inherits from Xtype
            fragment = xbase.XtypeGeneratorFragment auto-inject {}
    
            // provides a preference page for template proposals
            fragment = templates.CodetemplatesGeneratorFragment auto-inject {}
    
            // provides a compare view
            fragment = compare.CompareFragment auto-inject {}
        }
    }
}

