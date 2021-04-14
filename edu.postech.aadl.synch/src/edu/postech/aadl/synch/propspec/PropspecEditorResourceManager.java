package edu.postech.aadl.synch.propspec;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class PropspecEditorResourceManager extends PropSpecResourceManager {

	public enum Status {
		CLEARED, CHANGED, NOT_CHANGED
	};

	private XtextEditor editor = null;

	public ITextEditor getEditor() {
		return editor;
	}

	public Status setEditor(XtextEditor newEditor) {
		if (newEditor == null) {
			if (editor != null) {
				editor = null;
				content = null;
				return Status.CLEARED;
			}
		} else {
			if (editor != newEditor || editor.isDirty()) {
				editor = newEditor;
				updateResource(newEditor);
				return Status.CHANGED;
			}
		}
		return Status.NOT_CHANGED;
	}

	private void updateResource(XtextEditor newEditor) {
		newEditor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) {
				IParseResult parseResult = resource.getParseResult();
				if (parseResult != null) {
					EObject root = parseResult.getRootASTElement();
					if (root instanceof Top) {
						content = (Top) root;
						setModelResource();
						setPSPCFile();
					}
				}
			}
		});
	}

	private void setPSPCFile() {
		IEditorInput ipt = editor.getEditorInput();
		if (ipt instanceof IFileEditorInput) {
			pspcFile = ((IFileEditorInput) ipt).getFile();
		}
	}

}
