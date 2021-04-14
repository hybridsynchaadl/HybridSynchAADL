package edu.postech.aadl.synch.maude.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.google.common.collect.HashMultimap;

import edu.postech.aadl.synch.Activator;
import edu.postech.aadl.synch.checker.ConstraintsCheckErrorReporter;
import edu.postech.aadl.synch.maude.template.RtmAadlModel;
import edu.postech.aadl.synch.maude.template.RtmAadlSetting;
import edu.postech.aadl.utils.IOUtils;


/**
 *
 * @author Kyungmin Bae
 * @author Jaehun Lee
 *
 */
public final class CodeGenerationAction extends Action {

	private IPath instancePath;

	public CodeGenerationAction(IPath path) {
		instancePath = path;
	}

	@Override
	protected String getActionName() {
		return "Maude Instance Model Generator";
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
	public void performAnalysis(IProgressMonitor monitor, AnalysisErrorReporterManager errManager, SystemInstance root,
			SystemOperationMode som) throws Exception {
		ConstraintsCheckAction constAction = new ConstraintsCheckAction();
		constAction.setDialogBox(false);
		constAction.performAnalysis(monitor, errManager, root, som);


		doCodeGeneration(monitor, errManager, root, som);
		if (errManager.getNumErrors() > 0) {
			throw new Exception("Invalid code generation!");
		} else {
			if (showDialog) {
				Dialog.showInfo(getActionName(), "Code succeeded!");
			}
		}
	}

	private void doCodeGeneration(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			final SystemInstance root, SystemOperationMode som)
			throws CoreException, IOException {

		IPath context = instancePath.removeLastSegments(2);
		int count = AadlUtil.countElementsBySubclass(root, ComponentInstance.class);
		monitor.beginTask("Generating a Maude instance model", count + 2);

		try {
			HashMultimap<String, String> opTable = HashMultimap.create();
			RtmAadlModel compiler = new RtmAadlModel(monitor, errManager, opTable);

			final StringBuffer instanceCode = new StringBuffer();
			instanceCode.append(compiler.doGenerate(root));
			final StringBuffer symbolicCode = new StringBuffer();
			symbolicCode.append(compiler.doGenerateForSymbolic(root));

			monitor.setTaskName("Saving the Maude model file...");

			String lastSegment = instancePath.removeFileExtension().lastSegment();
			IFile instanceCodeFile = IOUtils.getFile(instancePath);
			IPath symbolicCodePath = instancePath.removeLastSegments(1).append(lastSegment + "-symbolic.maude");
			IFile symbolicCodeFile = IOUtils.getFile(symbolicCodePath);

			if (instanceCodeFile != null && symbolicCodeFile != null) {
				IOUtils.setFileContent(new ByteArrayInputStream(instanceCode.toString().getBytes()), instanceCodeFile);
				IOUtils.setFileContent(new ByteArrayInputStream(symbolicCode.toString().getBytes()), symbolicCodeFile);
			}
			monitor.worked(1);

			monitor.setTaskName("Copying the Maude semantics file...");
			copySemanticsFolder(context);
			monitor.worked(1);

			monitor.setTaskName("Saving the Maude property file...");
			monitor.worked(1);
		}
		finally {
			monitor.done();
		}
	}

	private static void copySemanticsFolder(IPath loc) throws IOException, CoreException {
		Enumeration<URL> urls = Activator.getDefault().getBundle().findEntries(RtmAadlSetting.SEMANTICS_PATH, "*.maude",
				true);
		while (urls.hasMoreElements()) {
			URL su = urls.nextElement();
			IFile nfile = IOUtils.getFile(loc.append(su.getFile()));
			if (nfile != null) {
				IOUtils.setFileContent(su.openStream(), nfile);
			}
		}
	}
}
