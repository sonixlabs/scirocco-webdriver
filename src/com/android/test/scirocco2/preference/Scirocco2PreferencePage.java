package com.android.test.scirocco2.preference;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.android.test.scirocco2.Activator;

public class Scirocco2PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

	public Scirocco2PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(
				Scirocco2PreferenceInitializer.ANDROID_SDK_LOCATION,"Android SDK Location:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
	    return super.performOk();
	}

}
