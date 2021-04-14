package edu.postech.aadl.synch.view;


import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.postech.aadl.maude.Maude;

public class HybridSynchAADLView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.postech.aadl.view.HybridSynchAADLView";

	@Inject
	public IWorkbench workbench;

	private TableViewer viewer;

	private static HybridSynchAADLView view = null;

	public static HybridSynchAADLView getView() {
		return view;
	}

	public static void setView() {
		if (view == null) {
			try {
				view = (HybridSynchAADLView) PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage()
						.showView(ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		viewer.setContentProvider(new ArrayContentProvider());

		HybridSynchAADLTableViewer tableViewer = new HybridSynchAADLTableViewer(viewer);
		tableViewer.setTableViewerColumns();
		tableViewer.setLayout();
		tableViewer.setHeaderVisible(true);
		tableViewer.setLinesVisible(true);

		getSite().setSelectionProvider(viewer);

		HybridSynchAADLTableAction tableAction = new HybridSynchAADLTableAction(viewer);
		hookContextMenu(tableAction);

		viewer.refresh();
	}


	private void hookContextMenu(HybridSynchAADLTableAction tableAction) {
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(manager -> HybridSynchAADLView.this.fillContextMenu(manager, tableAction));

		hookDoubleClickAction(tableAction);

		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void fillContextMenu(IMenuManager manager, HybridSynchAADLTableAction tableAction) {
		manager.add(tableAction.stopProcess());
		manager.add(tableAction.deleteColumn());
		manager.add(tableAction.makeCSV());
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void hookDoubleClickAction(HybridSynchAADLTableAction tableAction) {
		viewer.addDoubleClickListener(event -> tableAction.doubleClick().run());
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public Maude initialData(Maude maude) {
		viewer.add(maude);
		return maude;
	}

	public void removeData(IFile prop) {
		for (TableItem item : viewer.getTable().getItems()) {
			Maude maude = (Maude) item.getData();
			if (maude.getMaudeProfile().getPspcFileName().equals(prop.getName())) {
				viewer.remove(maude);
			}
		}
	}

	public void removeData(Maude maude) {
		viewer.remove(maude);
	}

	public void updateData(Maude oldElement, Maude newElement) {
		viewer.remove(oldElement);
		viewer.add(newElement);
	}
}
