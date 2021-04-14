package edu.postech.aadl.synch.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.ui.PlatformUI;

import edu.postech.aadl.configuration.AnalysisTab;
import edu.postech.aadl.synch.view.HybridSynchAADLView;

public class Analysis extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		HybridSynchAADLView.setView();
		ILaunchConfiguration selection = null;

		try {
			for (ILaunchConfiguration config : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations()) {
				if (config.getType().getName().contains("Analysis")) {
					selection = config;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		LaunchConfigurationsDialog dialog = new LaunchConfigurationsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				DebugUIPlugin.getDefault().getLaunchConfigurationManager().getDefaultLaunchGroup("run"));
		dialog.setBlockOnOpen(false);
		dialog.open();


		try {
			AnalysisTab tab = (AnalysisTab) dialog.getSelectedPage();
			if (tab == null) {
				return null;
			}
			tab.setMethod(event.getCommand().getName());
		} catch (NotDefinedException e) {
			e.printStackTrace();
		}

		return null;
	}

}
