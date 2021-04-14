package edu.postech.aadl.synch.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.osate.aadl2.Element;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.synch.propspec.DefaultPropSpec;
import edu.postech.aadl.utils.IOUtils;


/**
 * If the container resource is selected in the workspace
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension "pspc".
 */
public class RtmAadlPropertyWizard extends Wizard implements INewWizard {

	protected final static String _TITLE = "HybridSynchAADL Property Specification";
	protected final static String _DESCRIPTION = "This wizard creates a new property spec file (*.pspc)" +
			" that can be used to verify an HybridSynchAADL instance model";

	protected final static  String propExt = "pspc";

	private FileSelectionPage page;
	private ISelection selection;

	/**
	 * Constructor for RtmAadlPropertyWizard.
	 */
	public RtmAadlPropertyWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new FileSelectionPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		IResource res = page.getSelectedResource();
		Element elm = (res.getType() == IResource.FILE) ? AadlUtil.getElement(res) : null;
		final SystemInstance model = (elm instanceof SystemInstance) ? (SystemInstance)elm : null;

		if (model == null) {
			Dialog.showError("Error", "No instance model chosen!");
			return false;
		}

		try {
			getContainer().run(false, false, monitor -> {
				try {
					doFinish(monitor, page.getLocation(), model);
				} catch (IOException e1) {
					e1.printStackTrace();
					throw new InterruptedException(e1.getMessage());
				} catch (CoreException e2) {
					e2.printStackTrace();
					throw new InterruptedException(e2.getMessage());
				}
			});
		} catch (InterruptedException e) {
			Dialog.showWarning("Interrupted", e.getMessage());
			return false;
		} catch (InvocationTargetException e) {
			Dialog.showError("Error", e.getTargetException().getMessage());
			return false;
		}
		return true;
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private void doFinish(IProgressMonitor monitor, IPath locPath, SystemInstance model)
			throws IOException, CoreException {
		try {
			IPath filePath = locPath.append(model.getName()).addFileExtension(propExt);
			monitor.beginTask("Creating " + filePath, 2);

			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
			createPropSpecFile(model, file);
			monitor.worked(1);

			monitor.setTaskName("Opening file for editing...");
			Display.getDefault().asyncExec(() -> {
				try {
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			});
			monitor.worked(1);
		}
		finally {
			monitor.done();
		}
	}

	private static void createPropSpecFile(SystemInstance model, final IFile file) throws IOException, CoreException {
		IFile modelFile = IOUtils.getFile(model.eResource());

		InputStream is = new ByteArrayInputStream(DefaultPropSpec.deGenerate(model, modelFile).toString().getBytes());
		if (file.exists()) {
			file.setContents(is, true, true, null);
		} else {
			AadlUtil.makeSureFoldersExist(file.getFullPath());
			file.create(is, true, null);
		}
		is.close();
		file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
	}

}