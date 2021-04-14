package edu.postech.aadl.configuration;

import java.io.File;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.synch.maude.action.AnalysisAction;

public class AnalysisLaunchConfiguration extends LaunchConfigurationDelegate {

	private int loopBound;
	private int transBound;
	private String pspcPath;
	private boolean isRandomMode;
	private boolean isSymbolicMode;
	private boolean isPortfolioMode;
	private int randomSeed;
	private float minParamValue;
	private float maxParamValue;
	private long timeoutVal;
	private IFile pspcFile;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		getAttributes(configuration);
		execute();
	}

	private void getAttributes(ILaunchConfiguration configuration) throws NumberFormatException, CoreException {
		System.out.println("Analysis!");
		loopBound = Integer.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.loopBound", "10"));
		transBound = Integer.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.transBound", "10"));
		pspcPath = configuration.getAttribute("edu.postech.analysis.launcher.pspcPath", "");
		isRandomMode = Boolean
				.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.randomAnalysis", "false"));
		isSymbolicMode = Boolean
				.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.symbolicAnalysis", "false"));
		isPortfolioMode = Boolean
				.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.portfolioAnalysis", "false"));
		randomSeed = Integer.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.randomSeed", "0"));
		minParamValue = Float
				.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.minParamValue", "0.0"));
		maxParamValue = Float
				.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.maxParamValue", "0.0"));
		String timeout = configuration.getAttribute("edu.postech.analysis.launcher.timeout", "1000000000");
		if (timeout.equals("infinity")) {
			timeoutVal = 1000000000;
		} else {
			timeoutVal = Long.parseLong(timeout);
		}
		if (pspcPath.contains("${workspace}")) {
			pspcPath = pspcPath.replace("${workspace}",
					ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		}

		pspcFile = getPSPCFile(pspcPath);
	}

	private IFile getPSPCFile(String pspcPath) {
		File file = new File(pspcPath);
		file.exists();
		URI location = file.toURI();
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(location);
		if (files.length == 0) {
			Dialog.showError("Analysis Error", "Cannot find the PSPC file!");
			return null;
		} else {
			return files[0];
		}
	}

	private void execute() {
		AnalysisAction analysisAction = new AnalysisAction(pspcFile);
		if (!analysisAction.hasAADLInstance()) {
			Dialog.showError("Analysis Error", "No AADL instance model!");
			return;
		}

		if (isRandomMode) {
			analysisAction.setRandomMode(randomSeed, minParamValue, maxParamValue);
		}
		if (isSymbolicMode) {
			analysisAction.setSymbolicMode(loopBound, transBound);
		}
		if (isPortfolioMode) {
			analysisAction.setRandomMode(randomSeed, minParamValue, maxParamValue);
			analysisAction.setSymbolicMode(loopBound, transBound);
		}
		analysisAction.setTimeout(timeoutVal);

		try {
			analysisAction.setSelection();
			analysisAction.execute(null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}