/* Copyright © 2012-2015 IBM Corporation. */
package ${package}.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractGlobalScopeDelegatingScopeProvider;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 * 
 */
public class CrsxScopeProvider extends AbstractGlobalScopeDelegatingScopeProvider   {

	/**
	 * Gets scope by delegating to the global scope.
	 */
	public IScope getScope(EObject context, EReference reference) {
		return getGlobalScope(context.eResource(), reference);
	}
	 
}
