/* Copyright © 2012-2015 IBM Corporation. */
package ${package}.scoping;

import ${package}.Utils;
import ${package}.crsx.Binder;
import ${package}.crsx.Declaration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.SimpleAttributeResolver;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * 
 * @author villardl
 */
public class CrsxQualifiedNameProvider extends
		DefaultDeclarativeQualifiedNameProvider {

	private Function<EObject, String> resolver = new NullFunction();

	protected Function<EObject, String> getResolver() {
		return resolver;
	}

	public QualifiedName qualifiedName(Declaration decl) {
		if (Utils.isFunctionSort(decl)) {
			return QualifiedName.create(Utils.functionSortName(decl));
		}
		if (Utils.isDataSort(decl)) {
			return QualifiedName.create(Utils.dataSortName(decl));
		}
		return null;
	}

	public QualifiedName qualifiedName(Binder binder) {
		return null;
	}

	
	// Resolver always return null
	
	private static class NullFunction implements Function<EObject, String> 
	{
	   	public String apply(EObject from) {
			return null;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}
}
