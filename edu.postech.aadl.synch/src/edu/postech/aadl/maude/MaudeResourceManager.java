package edu.postech.aadl.maude;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.maude.controller.MaudeController;
import edu.postech.aadl.synch.propspec.PropSpecFileResourceManager;
import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class MaudeResourceManager {

	private PropSpecFileResourceManager resManager;
	private MaudeController controller;


	public MaudeResourceManager(PropSpecFileResourceManager resManager) {
		this.resManager = resManager;
		controller = new MaudeController();
	}

	public MaudeController getMaudeController() {
		return controller;
	}

	public IResource getModelResource() {
		return resManager.getModelResource();
	}

	public IPath getCodegenFilePath() {
		return resManager.getCodegenFilePath();
	}

	public IFile getPSPCFile() {
		return resManager.getPSPCFile();
	}

	public String getPSPCFileName() {
		IPath pspcFilePath = getPSPCFile().getFullPath();
		return pspcFilePath.removeFileExtension().lastSegment();
	}

	public Top getPropSpecResource() {
		return resManager.getContent();
	}

	public IPath getTargetMaudePath(String propId, String type) {
		IPath instanceMaudePath = resManager.getCodegenFilePath();
		if(type.equals("symbolic")) {
			return instanceMaudePath.removeLastSegments(2).append("symbolic-reachability")
					.append(getPSPCFileName() + "-" + propId + ".maude");
		}
		else {
			return instanceMaudePath.removeLastSegments(2).append("randomized-simulation")
					.append(getPSPCFileName() + "-" + propId + ".maude");
		}
	}

	public IPath getAnalysisResultPath(String propId, String type) {
		IPath path = resManager.getCodegenFilePath().removeLastSegments(2).append("result")
				.append(getPSPCFileName() + "-" + type + "-" + propId + ".txt");
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		if (!workspaceRoot.exists(path)) {
			try {
				workspaceRoot.getFile(path).delete(true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return path;
	}

	public void writeAnalysisMaudeFile(String txt, String propName, String modeName) {
		IFile maudeSearchFile = IOUtils.getFile(getTargetMaudePath(propName, modeName));
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(txt.getBytes()), maudeSearchFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
