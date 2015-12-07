/* Copyright © 2012-2015 IBM Corporation. */
package ${package}.scoping;

import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;

/**
 * 
 * @author villardl
 */
public class CrsxLinkingDiagnosticMessageProvider extends
		LinkingDiagnosticMessageProvider {

	@Override
	public DiagnosticMessage getUnresolvedProxyMessage(
			ILinkingDiagnosticContext context) {
		return null;
	}

	@Override
	public DiagnosticMessage getIllegalNodeMessage(
			ILinkingDiagnosticContext context, IllegalNodeException ex) {
		return null;
	}

	@Override
	public DiagnosticMessage getIllegalCrossReferenceMessage(
			ILinkingDiagnosticContext context, CrossReference reference) {
		return null;
	}

	@Override
	public DiagnosticMessage getViolatedBoundsConstraintMessage(
			ILinkingDiagnosticContext context, int size) {
		return null;
	}

	@Override
	public DiagnosticMessage getViolatedUniqueConstraintMessage(
			ILinkingDiagnosticContext context) {
		return null;
	}

}
