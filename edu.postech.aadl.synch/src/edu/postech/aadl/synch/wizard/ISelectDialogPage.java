package edu.postech.aadl.synch.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogPage;

public interface ISelectDialogPage extends IDialogPage {

	public IResource getSelectedResource();
	public void dialogChanged();

}
