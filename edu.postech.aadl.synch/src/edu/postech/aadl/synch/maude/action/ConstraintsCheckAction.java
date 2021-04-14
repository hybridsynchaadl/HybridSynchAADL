package edu.postech.aadl.synch.maude.action;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.util.Aadl2Util;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.synch.checker.ConstraintsCheckErrorReporter;
import edu.postech.aadl.synch.checker.SynchAadlConstChecker;

/**
 *
 * @author Kyungmin Bae
 * @author Jaehun Lee
 *
 */
public final class ConstraintsCheckAction extends Action {

	@Override
	protected String getActionName() {
		return "Constraints Check";
	}

	@Override
	protected String getMarkerType() {
		return "edu.postech.aadl.synch.HybridSyncAadlObjectMarker";
	}

	@Override
	protected AnalysisErrorReporterFactory getAnalysisErrorReporterFactory() {
		return new ConstraintsCheckErrorReporter.Factory(getMarkerType());
	}

	@Override
	public void performAnalysis(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			SystemInstance root, SystemOperationMode som) throws Exception {
		doConstraintsCheck(monitor, errManager, root, som);
		if (errManager.getNumErrors() > 0) {
			throw new Exception("Invalid Instance Model!");
		} else {
			if (showDialog) {
				Dialog.showInfo(getActionName(), "Valid Instance Model!");
			}
		}
	}

	private void doConstraintsCheck(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			final SystemInstance root, SystemOperationMode som) {
		int count = AadlUtil.countElementsBySubclass(root, ComponentInstance.class);
		try {
			monitor.beginTask(getActionName(), count);
			// remove default error prefix
			errManager.removePrefix();
			(new SynchAadlConstChecker(monitor, errManager)).processPreOrderAll(root);
		} catch (NullPointerException e) {
			Dialog.showError(getActionName(), "Cannot find the instance file. Create the instance file!");
		} finally {
			// restore default error prefix
			errManager.addPrefix(Aadl2Util.getPrintableSOMName(som) + ": ");
			monitor.done();
		}
	}
}
