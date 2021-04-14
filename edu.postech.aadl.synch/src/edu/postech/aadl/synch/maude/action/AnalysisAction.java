package edu.postech.aadl.synch.maude.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterFactory;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;

import edu.postech.aadl.maude.MaudePair;
import edu.postech.aadl.maude.MaudeProfile;
import edu.postech.aadl.maude.MaudeResourceManager;
import edu.postech.aadl.maude.MaudeSingle;
import edu.postech.aadl.maude.controller.MaudeController;
import edu.postech.aadl.maude.preferences.MaudePrefPage;
import edu.postech.aadl.synch.checker.ConstraintsCheckErrorReporter;
import edu.postech.aadl.synch.maude.action.mode.Mode;
import edu.postech.aadl.synch.maude.action.mode.RandomMode;
import edu.postech.aadl.synch.maude.action.mode.SymbolicMode;
import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropSpecFileResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class AnalysisAction extends Action {

	private MaudeResourceManager manager;
	private MaudePrefPage pref;
	private RandomMode randomMode = null;
	private SymbolicMode symbolicMode = null;
	private long timeout = -1;

	public AnalysisAction(IFile pspcFile) {
		PropSpecFileResourceManager resManager = new PropSpecFileResourceManager();
		resManager.setPSPCFile(pspcFile);
		manager = new MaudeResourceManager(resManager);
		pref = new MaudePrefPage();
	}

	public void setSelection() {
		selection = new StructuredSelection(manager.getModelResource());
	}

	public boolean hasAADLInstance() {
		return manager.getModelResource() != null ? true : false;
	}

	public void setRandomMode(int randomSeed, float minParamValue, float maxParamValue) {
		randomMode = new RandomMode(randomSeed, minParamValue, maxParamValue);
	}

	public void setSymbolicMode(int loopBound, int transBound) {
		symbolicMode = new SymbolicMode(loopBound, transBound);
	}

	public void setTimeout(long time) {
		timeout = time;
	}

	@Override
	protected String getActionName() {
		return "Analysis";
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
		CodeGenerationAction codegenAction = new CodeGenerationAction(manager.getCodegenFilePath());
		codegenAction.setDialogBox(false);
		codegenAction.performAnalysis(monitor, errManager, root, som);

		doAnalysis(monitor, errManager, root, som);
		if (errManager.getNumErrors() > 0) {
			throw new Exception("Invalid Analysis!");
		}
	}

	private void doAnalysis(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			final SystemInstance root, SystemOperationMode som) throws Exception {
		try {
			pref.validMaudePreferences();

			if (randomMode != null) {
				writeAnalysisMaudeFile(randomMode);
			}
			if (symbolicMode != null) {
				writeAnalysisMaudeFile(symbolicMode);
			}

			MaudeController controller = manager.getMaudeController();
			Top propSpecRes = manager.getPropSpecResource();
			for (Property prop : propSpecRes.getProperty()) {
				if (randomMode != null && symbolicMode == null) {
					MaudeSingle maude = makeMaudeInstance(controller, prop, randomMode);
					controller.addMaude(maude);
				}
				else if (symbolicMode != null && randomMode == null) {
					MaudeSingle maude = makeMaudeInstance(controller, prop, symbolicMode);
					controller.addMaude(maude);
				}
				else if (randomMode != null && symbolicMode != null) {
					MaudeSingle rMaude = makeMaudeInstance(controller, prop, randomMode);
					MaudeSingle sMaude = makeMaudeInstance(controller, prop, symbolicMode);
					MaudePair pair = new MaudePair(manager.getPSPCFile(), prop, rMaude, sMaude);
					controller.addMaude(pair);
				}
			}
			controller.runMaude();

		} catch (NullPointerException e) {
			throw new Exception("Cannot find the instance file. Create the instance file!");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			monitor.done();
		}
	}

	private void writeAnalysisMaudeFile(Mode mode) {
		Top propSpecRes = manager.getPropSpecResource();
		for (Property prop : propSpecRes.getProperty()) {
			String propertyMaudeContents = RtmPropSpec
					.compilePropertyCommand(propSpecRes, prop, pref.getMaudeDirPath(), mode).toString();
			manager.writeAnalysisMaudeFile(propertyMaudeContents, prop.getName(), mode.getName());

		}
	}

	private MaudeSingle makeMaudeInstance(MaudeController controller, Property prop, Mode mode) {
		MaudeProfile initProfile = new MaudeProfile(manager.getPSPCFile(), prop,
				new String("Running.."), mode.getName(), new Path(""), new String(""),
				new String(""));
		MaudeSingle maude = new MaudeSingle(initProfile, manager.getTargetMaudePath(prop.getName(), mode.getName()),
				manager.getAnalysisResultPath(prop.getName(), mode.getName()));
		maude.setTimeout(timeout);
		return maude;
	}

}
