/*
 * generated by Xtext
 */
package ${package}.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.core.runtime.preferences.IPreferencesService
import org.eclipse.core.runtime.Platform 

class CrsxGenerator implements IGenerator {
     
    override void doGenerate(Resource resource, IFileSystemAccess fsa) {
        val IPreferencesService prefs = Platform::getPreferencesService()
        val String s = prefs.getString(getQualifier(), "CRSXPATH", "fallback", null)
        //System::out.println(s)
    }
    
     
     def String getQualifier() { return "net.sf.crsx.xtext.Crsx" }
      
}
