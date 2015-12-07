module ${package}.GenerateCrsx

import org.eclipse.emf.mwe.utils.*
import org.eclipse.xtext.generator.*
import org.eclipse.xtext.ui.generator.*

var projectName = "${package}"
var grammarURI = "platform:/resource/${r"${projectName}"}/${xtextPath}"
var file.extensions = "crs"
var runtimeProject = "../${r"${projectName}"}"

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
    }

    component = DirectoryCleaner {
        directory = "${r"${runtimeProject}"}/src-gen"
    }

    component = DirectoryCleaner {
        directory = "${r"${runtimeProject}"}.ui/src-gen"
    }

    component = Generator {
        pathRtProject = runtimeProject
        pathUiProject = "${r"${runtimeProject}"}.ui"
        projectNameRt = projectName
        projectNameUi = "${r"${projectName}"}.ui"
        language = {
            uri = grammarURI
            fileExtensions = file.extensions

            // Java API to access grammar elements (required by several other fragments)
            fragment = grammarAccess.GrammarAccessFragment {
			//	xmlVersion = "1.0"
            }

            // generates Java API for the generated EPackages
            fragment = ecore.EcoreGeneratorFragment { }

            // Serializer 2.0
            fragment = serializer.SerializerFragment {
            	generateStub = false
            }
            
            // the serialization component (1.0)
            // fragment = parseTreeConstructor.ParseTreeConstructorFragment {}

            // a custom ResourceFactory for use with EMF
            fragment = resourceFactory.ResourceFactoryFragment {
                fileExtensions = file.extensions
            }

            
            fragment = org.eclipse.xtext.generator.parser.antlr.ex.rt.AntlrGeneratorFragment {
                options = {
                    backtrack = false
                    backtrackLexer = false
                    classSplitting=true
                    fieldsPerClass = "500"
                    methodsPerClass= "500" 
                }
            }
            
            // Custom Antlr lexer
            fragment = parser.antlr.ex.ExternalAntlrLexerFragment {
                lexerGrammar = "${package}.lexer.CrsxCustomLexer"
                runtime = true
                classSplitting = true
                antlrParam = "-lib"
                // This is the folder where the lexer will be created
                antlrParam = "${r"${runtimeProject}"}/${lexerPath}"
            }

            // java-based API for validation
            fragment = validation.JavaValidatorFragment { }

            // scoping and exporting API
            fragment = scoping.ImportNamespacesScopingFragment {}
            fragment = exporting.QualifiedNamesFragment {}
            fragment = builder.BuilderIntegrationFragment {}

            // generator API
            fragment = generator.GeneratorFragment {
                generateMwe = false
                generateJavaMain = false
            }

            // formatter API
            fragment = formatting.FormatterFragment {}

            // labeling API
            fragment = labeling.LabelProviderFragment {}

            // outline API
            fragment = outline.OutlineTreeProviderFragment {}
            fragment = outline.QuickOutlineFragment {}

            // quickfix API
            fragment = quickfix.QuickfixProviderFragment {}

            // content assist API
            fragment = contentAssist.JavaBasedContentAssistFragment {}

            // generates a more lightweight Antlr parser and lexer tailored for content assist
            fragment = parser.antlr.XtextAntlrUiGeneratorFragment {
                options = {
                    classSplitting = true
                }
            }

            // rename refactoring
            fragment = refactoring.RefactorElementNameFragment {}

            // provides the necessary bindings for java types integration
            fragment = types.TypesGeneratorFragment {}

            // generates the required bindings only if the grammar inherits from Xbase
            fragment = xbase.XbaseGeneratorFragment {}

            // provides a preference page for template proposals
            fragment = templates.CodetemplatesGeneratorFragment {}

            // provides a compare view
            fragment = compare.CompareFragment {
                 fileExtensions = file.extensions
            }

        }
    }
}
