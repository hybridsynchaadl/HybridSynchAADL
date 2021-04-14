package edu.postech.aadl.synch.view;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import edu.postech.aadl.maude.Maude;
import edu.postech.aadl.utils.IOUtils;

public class HybridSynchAADLTableAction {

	private TableViewer viewer;

	public HybridSynchAADLTableAction(TableViewer viewer) {
		this.viewer = viewer;
	}

	public Action stopProcess() {
		Action stopProcess = new Action() {
			@Override
			public void run() {
				for (Object maude : viewer.getStructuredSelection().toArray()) {
					if (((Maude) maude).isProcessAlive()) {
						((Maude) maude).killProcess();
						((Maude) maude).getMaudeProfile().setResult("Terminated");
						viewer.update(maude, null);
					}
				}
			}
		};
		stopProcess.setText("Stop Process");
		return stopProcess;
	}

	public Action deleteColumn() {
		Action deleteResult = new Action() {
			@Override
			public void run() {
				for (Object maude : viewer.getStructuredSelection().toArray()) {
					viewer.remove(maude);
				}
			}
		};
		deleteResult.setText("Delete Result");
		return deleteResult;
	}

	public Action makeCSV() {
		Action makeCSV = new Action() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("PSPC File,Property Id,Result,Method,CPU Time,Running Time,Location\n");
				for (TableItem ti : viewer.getTable().getItems()) {
					Maude maude = (Maude) ti.getData();
					String pspcFileName = maude.getMaudeProfile().getPspcFileName();
					String propId = maude.getMaudeProfile().getPropId();
					String result = maude.getMaudeProfile().getResult();
					String method = maude.getMaudeProfile().getMethod();
					String cpuTime = maude.getMaudeProfile().getCPUTime();
					String runningTime = maude.getMaudeProfile().getRunningTime();
					String location = maude.getMaudeProfile().getResultLocationString();

					sb.append(pspcFileName + "," + propId + "," + result + "," + method + "," + cpuTime + ","
							+ runningTime + "," + location + "\n");
				}
				Maude mr = (Maude) viewer.getStructuredSelection().getFirstElement();
				String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());
				IPath csvPath = mr.getMaudeProfile().getResultLocationIPath().removeLastSegments(2).append("csv")
						.append("result_" + time + ".csv");
				try {
					IOUtils.setFileContent(new ByteArrayInputStream(sb.toString().getBytes()),
							IOUtils.getFile(csvPath));
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		};
		makeCSV.setText("Export all data into CSV file");

		return makeCSV;
	}

	public Action doubleClick() {
		Action doubleClick = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Maude maude = (Maude) selection.getFirstElement();
				Integer line = maude.findPropIdLine();
				try {
					IMarker marker = maude.getMaudeProfile().getPSPCFile().createMarker(IMarker.TEXT);
					marker.setAttribute(IMarker.LINE_NUMBER, line);
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker);
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		};
		return doubleClick;
	}
}
