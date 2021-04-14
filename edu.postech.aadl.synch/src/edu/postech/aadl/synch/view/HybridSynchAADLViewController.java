package edu.postech.aadl.synch.view;

import org.eclipse.core.resources.IFile;

import edu.postech.aadl.maude.Maude;
import edu.postech.aadl.maude.MaudePair;
import edu.postech.aadl.maude.MaudeSingle;

public class HybridSynchAADLViewController {

	private static HybridSynchAADLView view = HybridSynchAADLView.getView();

	public static void updateView(Maude maude) {
		view.workbench.getDisplay().asyncExec(() -> view.updateData(maude, maude));
	}

	public static void initDataInView(MaudePair pair) {
		view.workbench.getDisplay()
				.asyncExec(() -> view.initialData(pair));
	}

	public static void initDataInView(MaudeSingle maude) {
		view.workbench.getDisplay()
				.asyncExec(() -> view.initialData(maude));
	}

	public static void removeDataInView(MaudeSingle maude) {
		view.workbench.getDisplay()
				.asyncExec(() -> view.removeData(maude));
	}

	public static void removeDataInView(IFile pspcFile) {
		view.workbench.getDisplay()
				.asyncExec(() -> view.removeData(pspcFile));
	}


}
