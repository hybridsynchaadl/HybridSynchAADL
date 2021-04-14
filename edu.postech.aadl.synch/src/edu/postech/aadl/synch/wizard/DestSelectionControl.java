package edu.postech.aadl.synch.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;


public class DestSelectionControl {

	private final static  String defalutLoc = "verification";

	private final class BrowseLocationListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ContainerSelectionDialog sdg = new ContainerSelectionDialog(
					dialog.getControl().getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
					"Select a location");
			if (sdg.open() == Window.OK) {
				Object[] result = sdg.getResult();
				if (result.length == 1) {
					destPathText.setText(((Path) result[0]).toString());
				}
			}
		}
	}

	private ISelectDialogPage dialog;
	private Button destDefaultCheck = null;
	private Text destPathText = null;
	private Text destContainerText = null;
	private Button browseLocation = null;

	protected DestSelectionControl(ISelectDialogPage dialog) {
		this.dialog = dialog;
	}

	public String getDestPath() {
		return destPathText.getText().trim();
	}

	public String getDestContainer() {
		return destContainerText.getText().trim();
	}

	public void update(IResource modelRes) {
		if (destDefaultCheck.getSelection()) {	// use default folder

			destPathText.setEnabled(false);
			destPathText.setText(modelRes != null ? modelRes.getProject().getFullPath().toString() : "");

			destContainerText.setEnabled(false);
			destContainerText.setText(modelRes != null ? "requirement" : "");

			browseLocation.setEnabled(false);
		}
		else {
			destPathText.setEnabled(true);
			destContainerText.setEnabled(true);
			browseLocation.setEnabled(true);
		}
	}


	public Composite createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3,false));

		destDefaultCheck = new Button(container, SWT.CHECK);
		destDefaultCheck.setText("&Use default location");
		destDefaultCheck.setSelection(true);
		destDefaultCheck.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,false,false,3,1));

		Label desDirLabel = new Label(container, SWT.NULL);
		desDirLabel.setText("&Location:");

		destPathText = new Text(container, SWT.BORDER | SWT.SINGLE);
		destPathText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));

		browseLocation = new Button(container, SWT.PUSH);
		browseLocation.setText("&Browse...");

		Label desFileLabel = new Label(container, SWT.NULL);
		desFileLabel.setText("&Container:");

		destContainerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		destContainerText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));

		addListeners();
		return container;
	}

	private void addListeners() {
		destDefaultCheck.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						update(dialog.getSelectedResource());
						dialog.dialogChanged();
					}
				});
		destPathText.addModifyListener(
				e -> dialog.dialogChanged());
		destContainerText.addModifyListener(
				e -> dialog.dialogChanged());
		browseLocation.addSelectionListener(new BrowseLocationListener());
	}
}
