package ${package}.ui.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage;

/**
 * 
 * @author villardl
 */
public class CrsxRootPreferencePage extends LanguageRootPreferencePage {

	@Override
	protected void createFieldEditors() {
		StringFieldEditor field = new StringFieldEditor("CRSXPATH", "&Crsx path:",
				getFieldEditorParent());
		field.setPreferenceStore(doGetPreferenceStore());
		addField(field);
	}

	@Override
	public void init(IWorkbench workbench) {
		
		//getPreferenceStore().setDefault("CRSXPATH", "");
	}
}
