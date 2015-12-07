/* Copyright © 2012-2015 IBM Corporation. */
package ${package}.scoping;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import ${package}.crsx.CrsxPackage;
import ${package}.crsx.Declaration;
import ${package}.crsx.Term;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.LoadOnDemandResourceDescriptions;
import org.eclipse.xtext.scoping.impl.SelectableBasedScope;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * 
 * @author villardl
 */
public class CrsxGlobalScopeProvider extends AbstractGlobalScopeProvider {

    // Copied from ImportUriGlobalScopeProvider
    @Inject
    private Provider<LoadOnDemandResourceDescriptions> loadOnDemandDescriptions;

    // Copied from ImportUriGlobalScopeProvider
    @Inject
    private IResourceScopeCache cache;

    // Copied from ImportUriGlobalScopeProvider
    public IResourceDescriptions getResourceDescriptions(Resource resource,
            Collection<URI> importUris) {
        IResourceDescriptions result = getResourceDescriptions(resource);
        LoadOnDemandResourceDescriptions demandResourceDescriptions = loadOnDemandDescriptions
                .get();
        demandResourceDescriptions.initialize(result, importUris, resource);
        return demandResourceDescriptions;
    }

    // Copied from ImportUriGlobalScopeProvider
    @Override
    protected IScope getScope(Resource resource, boolean ignoreCase,
            EClass type, Predicate<IEObjectDescription> filter) {
        final LinkedHashSet<URI> uniqueImportURIs = getImportedUris(resource);
        IResourceDescriptions descriptions = getResourceDescriptions(resource,
                uniqueImportURIs);
        List<URI> urisAsList = Lists.newArrayList(uniqueImportURIs);
        Collections.reverse(urisAsList);
        IScope scope = IScope.NULLSCOPE;
        for (URI uri : urisAsList) {
            scope = createLazyResourceScope(scope, uri, descriptions, type,
                    filter, ignoreCase);
        }
        return scope;
    }

    // Copied from ImportUriGlobalScopeProvider
    protected IScope createLazyResourceScope(IScope parent, final URI uri,
            final IResourceDescriptions descriptions, EClass type,
            final Predicate<IEObjectDescription> filter, boolean ignoreCase) {
        IResourceDescription description = descriptions
                .getResourceDescription(uri);
        return SelectableBasedScope.createScope(parent, description, filter,
                type, ignoreCase);
    }

    protected LinkedHashSet<URI> getImportedUris(final Resource resource) {
        return cache.get(CrsxGlobalScopeProvider.class.getName(), resource,
                new Provider<LinkedHashSet<URI>>() {
                    public LinkedHashSet<URI> get() {
                        final LinkedHashSet<URI> uniqueImportURIs = new LinkedHashSet<URI>(
                                10);
                        URI resourceURI = resource.getURI();
                    
                        uniqueImportURIs.add(resourceURI);
                    
                        String project = resourceURI.segment(1);
                        
                        TreeIterator<EObject> iterator = resource
                                .getAllContents();
                        while (iterator.hasNext()) {
                            EObject object = iterator.next();
                            /*if (object.eClass() == CrsxPackage.Literals.TERM) {
                                Term term = (Term) object;
                                if ("$Use".equals(term.getDirective())) {
                                    if (term.getArgs().size() >= 1)
                                    {
                                        Declaration pathDecl = term.getArgs().get(0);
                                        Term pathTerm = pathDecl.getOptionOrTerm();
                                        if (pathTerm != null)
                                        {
                                            String literal = pathTerm.getLiteral();
                                            if (literal != null && literal.length() >= 2)
                                            {
                                                char c0 = literal.charAt(0);
                                                if (c0 == '"' || c0 == '\'')
                                                {
                                                    literal = literal.substring(1, literal.length() - 1);
                                                    literal = project + "/" + literal;
                                                    
                                                    
                                                    URI importUri = URI.createPlatformResourceURI(literal, false);
                                                    
                                                    //URI importUri = URI.createURI(literal, true);
                                                    //importUri = importUri.resolve(resourceURI);
                                                    uniqueImportURIs.add(importUri);
                                                }
                                            }
                                        }
                                    }
                                }
                            }*/

                        }
                        Iterator<URI> uriIter = uniqueImportURIs.iterator();
                        while (uriIter.hasNext()) {
                            if (!EcoreUtil2.isValidUri(resource, uriIter.next()))
                                uriIter.remove();
                        }
                        return uniqueImportURIs;
                    }
                });
    }

}
