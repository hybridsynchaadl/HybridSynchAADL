package edu.postech.aadl.synch.maude.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osate.aadl2.Element;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

public abstract class Action extends org.osate.ui.handlers.AbstractInstanceOrDeclarativeModelReadOnlyHandler {

	protected boolean showDialog = true;

	public void setDialogBox(boolean turn) {
		showDialog = turn;
	}

	protected StructuredSelection selection;

	public void selectionChanged(ISelection selection) {
		this.selection = (StructuredSelection) selection;
	}

	@Override
	protected Object getCurrentSelection(ExecutionEvent event) {
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			// Set initial mode only
			Element elem = AadlUtil.getElement(object);
			setInitialModeOnly(elem);
			return elem;
		} else {
			return null;
		}
	}

	private void setInitialModeOnly(Element elem) {
		NamedElement root = ((NamedElement) elem).getElementRoot();
		SystemInstance si = (SystemInstance) root;
		SystemOperationMode initial = si.getInitialSystemOperationMode();
		si.getSystemOperationModes().clear();
		si.getSystemOperationModes().add(initial);
	}

	@Override
	protected boolean canAnalyzeDeclarativeModels() {
		return false;
	}

	@Override
	protected void analyzeDeclarativeModel(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			Element declarativeObject) {

	}

	@Override
	protected void analyzeInstanceModel(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			SystemInstance root, SystemOperationMode som) {
		try {
			performAnalysis(monitor, errManager, root, som);
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.showError(getActionName(), e.getMessage());
		}
	}

	abstract protected void performAnalysis(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			SystemInstance root, SystemOperationMode som) throws Exception;

	@Override
	abstract protected String getActionName();

}
