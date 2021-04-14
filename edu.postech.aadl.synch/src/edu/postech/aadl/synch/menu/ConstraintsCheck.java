package edu.postech.aadl.synch.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.synch.maude.action.Action;
import edu.postech.aadl.synch.maude.action.ConstraintsCheckAction;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;

public class ConstraintsCheck extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		res.setEditor(newEditor);

		if (res.getModelResource() != null) {
			Action chkAct = new ConstraintsCheckAction();
			chkAct.selectionChanged(new StructuredSelection(res.getModelResource()));
			chkAct.execute(event);
		} else {
			Dialog.showError("Constraints Checking Error", "No AADL instance model!");
			throw new ExecutionException("No AADL instance model!");
		}
		return null;
	}
}
