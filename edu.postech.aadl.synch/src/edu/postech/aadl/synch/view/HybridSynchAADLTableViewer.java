package edu.postech.aadl.synch.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import edu.postech.aadl.maude.Maude;

public class HybridSynchAADLTableViewer {

	private TableViewer viewer;

	public HybridSynchAADLTableViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public void setLayout() {
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(2, true));

		viewer.getTable().setLayout(layout);
	}

	public void setTableViewerColumns() {
		setTableViewerColumn(viewer, "PSPC File", new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getPspcFileName();
			}
		});

		setTableViewerColumn(viewer, "Property Id", new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getPropId();
			}
		});

		setTableViewerColumn(viewer, "Result", new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getResult();
			}
		});

		setTableViewerColumn(viewer, "Method", new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getMethod();
			}
		});

		setTableViewerColumn(viewer, "CPUTime", new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getCPUTime();
			}
		});

		setTableViewerColumn(viewer, "RunningTime", new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getRunningTime();
			}
		});

		setTableViewerColumn(viewer, "Location", new LinkLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Maude maude = (Maude) element;
				return maude.getMaudeProfile().getResultLocationString();
			}
		}, getLinkHander()));
	}

	private LinkOpener getLinkHander() {
		LinkOpener linkHandler = rowObject -> {
			Maude maude = (Maude) rowObject;
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath path = new Path(maude.getMaudeProfile().getResultLocationString());
			IFile ifile = root.getFile(path);
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), ifile);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		};
		return linkHandler;
	}

	private void setTableViewerColumn(TableViewer viewer, String text, CellLabelProvider provider) {
		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.LEFT);
		tvc.getColumn().setText(text);
		tvc.getColumn().setResizable(true);
		tvc.getColumn().setMoveable(true);
		tvc.setLabelProvider(provider);
	}

	public void setHeaderVisible(boolean visible) {
		viewer.getTable().setHeaderVisible(visible);
	}

	public void setLinesVisible(boolean visible) {
		viewer.getTable().setLinesVisible(visible);
	}

}
