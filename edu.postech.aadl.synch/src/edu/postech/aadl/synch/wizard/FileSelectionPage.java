package edu.postech.aadl.synch.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osate.workspace.WorkspacePlugin;

import edu.postech.aadl.utils.IOUtils;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (prop).
 */

public class FileSelectionPage extends WizardPage implements ISelectDialogPage {

	private ModelChooseControl mcc;
	private DestSelectionControl dsc;


	/**
	 * Constructor for SampleNewWizardPage.
	 *
	 * @param pageName
	 */
	public FileSelectionPage(ISelection selection) {
		super("wizardPage");
		dsc = new DestSelectionControl(this);
		mcc = new ModelChooseControl(this, dsc, selection);
	}

	@Override
	public IResource getSelectedResource() {
		return mcc.getSelectedResource();
	}

	public IPath getLocation() {
		if (getSelectedResource() != null) {
			return new Path(dsc.getDestPath()).append(dsc.getDestContainer());
		}
		return null;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		setTitle(RtmAadlPropertyWizard._TITLE);
		setDescription(RtmAadlPropertyWizard._DESCRIPTION);

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout lo = new GridLayout();	lo.verticalSpacing = 9;
		container.setLayout(lo);

		// for choosing an aaxl instance model
		Composite mccw = mcc.createControl(container);
		mccw.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		// for a destination path
		Composite dscw = dsc.createControl(container);
		dscw.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		mcc.init();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Ensures that both text fields are set.
	 */
	@Override
	public void dialogChanged() {
		if (mcc.getSelectedResource() == null) {
			setErrorMessage("The AADL Instance model (*." + WorkspacePlugin.INSTANCE_FILE_EXT + ") must be selected");
			setPageComplete(false);
			return;
		}

		IResource path = IOUtils.getResource(new Path(dsc.getDestPath()));
		if (path == null || (path.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			setErrorMessage("Destination location must exist");
			setPageComplete(false);
			return;
		}
		if (!path.isAccessible()) {
			setErrorMessage("Project must be writable");
			setPageComplete(false);
			return;
		}

		String fileName = dsc.getDestContainer();
		if (fileName.length() == 0) {
			setErrorMessage("Container name must be specified");
			setPageComplete(false);
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			setErrorMessage("Container name must be valid");
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

}
