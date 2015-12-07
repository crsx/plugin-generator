/* Copyright � 2012 IBM Corporation. */
package ${package}.ui.syntaxcoloring;

import ${package}.Utils;
import ${package}.crsx.Declaration;
import ${package}.crsx.Embedded;
import ${package}.crsx.Term;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * Semantic highlighting
 * 
 * @author villardl
 * 
 */
public class CrsxSemanticHighlightingCalculator implements
		ISemanticHighlightingCalculator {

	public void provideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor) {
		if (resource == null || resource.getContents().isEmpty())
			return;

		EObject topmostEObject = (EObject) resource.getContents().get(0);
		TreeIterator<EObject> iterator = topmostEObject.eAllContents();
		provideHighlightingFor(iterator, acceptor);
	}

	private void provideHighlightingFor(TreeIterator<EObject> iterator,
			IHighlightedPositionAcceptor acceptor) {
		while (iterator.hasNext()) {
			EObject eobject = iterator.next();

			if (Utils.isFunctionSort(eobject)) {
				provideHighlightingForSort((Declaration) eobject, acceptor,
						CrsxHighlightingConfiguration.FUNCTION_SORT_ID);
				//iterator.prune();
			} else if (Utils.isDataSort(eobject)) {
				provideHighlightingForSort((Declaration) eobject, acceptor,
						CrsxHighlightingConfiguration.DATA_SORT_ID);
				 
			} else if (eobject instanceof Embedded) {
				provideHighlightingForEmbedded((Embedded) eobject, acceptor);
			} else if (eobject instanceof Term) {
				provideHighlightingForTerm((Term) eobject, acceptor);
			}
		}

	}

	private void provideHighlightingForTerm(Term term,
			IHighlightedPositionAcceptor acceptor) {
		if (Utils.isPrimitive(term))
		{
			ICompositeNode node = NodeModelUtils.getNode(term);
			
			acceptor.addPosition(node.getOffset(), 1, CrsxHighlightingConfiguration.PRIMITIVE_ID);
			
			// Highlight first argument
			EList<Declaration> args = term.getArgs();
			if (args != null && args.size()>=1)
			{
				Declaration arg = args.get(0);
				
				// That should be a Constructor eventually with some property. Extract contructor
				if (arg.getOptionOrTerm() != null)
				{
					Term innerTerm = arg.getOptionOrTerm();
					if (innerTerm.getConstructor() != null)
					{
						node = NodeModelUtils.getNode(innerTerm.getConstructor());
						if (node != null)
						{
							acceptor.addPosition(node.getOffset(), node.getLength(), CrsxHighlightingConfiguration.PRIMITIVE_ID);
						}
					}
				}
			}
		}
		
	}

	private void provideHighlightingForEmbedded(Embedded embed,
			IHighlightedPositionAcceptor acceptor) {
		ICompositeNode node = NodeModelUtils.getNode(embed);

		if (node != null) {
			if (embed.getPrefix() != null) {
				acceptor.addPosition(node.getOffset(), embed.getPrefix()
						.length() + 1, CrsxHighlightingConfiguration.KEYWORD_ID);
			}

			acceptor.addPosition(node.getOffset() + node.getLength() - 1, 1,
					CrsxHighlightingConfiguration.KEYWORD_ID);
		}
	}

	private void provideHighlightingForSort(Declaration declaration,
			IHighlightedPositionAcceptor acceptor, String conf) {
		if (declaration.getOptionOrTerm() != null) {
			Term term = declaration.getOptionOrTerm();

			if (term.getConstructor() != null) {
				ICompositeNode node = NodeModelUtils.getNode(term
						.getConstructor());
				
				if (node != null)
					acceptor.addPosition(node.getOffset(), node.getLength(),
							conf);
			}
		}

	}

}