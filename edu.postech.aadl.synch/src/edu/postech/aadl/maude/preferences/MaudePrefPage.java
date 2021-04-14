package edu.postech.aadl.maude.preferences;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class MaudePrefPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String MAUDE_DIR = "MAUDE_DIR";
	public static final String MAUDE = "MAUDE";
	public static final String MAUDE_OPTS = "MAUDE_OPTS";
	private IPreferenceStore pref = new ScopedPreferenceStore(InstanceScope.INSTANCE,
			"edu.postech.maude.preferences.page");

	public MaudePrefPage() {
		super(GRID);
		ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}


	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(pref);
		setDescription("Maude Preferences");
		performDefaults();
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(MAUDE_DIR, "Maude directory:", getFieldEditorParent()));
		addField(new FileFieldEditor(MAUDE, "Maude executable:", getFieldEditorParent()));
	}

	public void validMaudePreferences() throws ExecutionException {
		String maudeDirPath = getMaudeDirPath();
		if (maudeDirPath.length() == 0) {
			throw new ExecutionException("Maude directory is not set properly. Please check Maude Preferences.");
		}
		String maudeExecPath = getMaudeExecPath();
		if (maudeExecPath.length() == 0) {
			throw new ExecutionException("Maude binary is not set properly. Please check Maude Preferences.");
		}
	}

	public String getMaudeDirPath() {
		String maudeDirPath = pref.getString(MAUDE_DIR);
		return maudeDirPath;
	}

	public String getMaudeExecPath() {
		String maudeExecPath = pref.getString(MAUDE);
		return maudeExecPath;
	}

}
