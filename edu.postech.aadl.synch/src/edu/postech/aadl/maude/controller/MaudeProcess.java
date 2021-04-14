package edu.postech.aadl.maude.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.maude.MaudePair;
import edu.postech.aadl.maude.MaudeResultParser;
import edu.postech.aadl.maude.MaudeSingle;
import edu.postech.aadl.maude.preferences.MaudePrefPage;
import edu.postech.aadl.synch.view.HybridSynchAADLViewController;
import edu.postech.aadl.utils.IOUtils;

public class MaudeProcess extends Thread {
	private MaudeSingle maude = null;
	private MaudePair pair = null;
	private String maudeExecPath = null;
	private IPath targetPath = null;
	private IPath analysisResultPath = null;

	public MaudeProcess(MaudeSingle maude) {
		this.maude = maude;
		this.targetPath = maude.getTargetPath();
		this.analysisResultPath = maude.getAnalysisResultPath();
		this.maudeExecPath = new MaudePrefPage().getMaudeExecPath();
	}

	public void setPair(MaudePair pair) {
		this.pair = pair;
	}

	@Override
	public void run() {
		if (!validCommand()) {
			return;
		}
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(compileCommand());
			maude.setProcess(process);

			StringBuffer inputBuffer = new StringBuffer();
			StringBuffer errorBuffer = new StringBuffer();
			MaudeProcessReader mpr = new MaudeProcessReader(process, inputBuffer, errorBuffer);
			mpr.start();
			long start = System.currentTimeMillis();
			boolean processTimeOut = waitProcess(process);
			long end = System.currentTimeMillis();
			long elapsedTime = end - start;
			mpr.join();
			if (processTimeOut) {
				updateTimeoutInMaudeProfile();
			} else {
				updateMaudeProfile(inputBuffer, errorBuffer, elapsedTime);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				process.getInputStream().close();
				process.getErrorStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean waitProcess(Process process) throws Exception {
		boolean processTimeOut = process.waitFor(maude.getTimeout(), TimeUnit.SECONDS);
		if (pair != null) {
			pair.getRunningMaudeProcess().killProcess();
		}
		return !processTimeOut;
	}

	private void updateTimeoutInMaudeProfile() {
		maude.getMaudeProfile().setResult("Timeout");
		maude.getMaudeProfile().setRunningTime(maude.getTimeout() * 1000 + "ms");
		if (pair != null) {
			maude.getMaudeProfile().setMethod("portfolio");
			pair.setRepresentingMaude(maude);
			HybridSynchAADLViewController.updateView(pair);
		} else {
			HybridSynchAADLViewController.updateView(maude);
		}
	}

	private void updateMaudeProfile(StringBuffer inputBuffer, StringBuffer errorBuffer, long elapsedTime) {
		boolean isInvariant = maude.getMaudeProfile().isInvariantProperty();
		MaudeResultParser parser = new MaudeResultParser(inputBuffer, errorBuffer, isInvariant);
		parser.parse();
		if (parser.hasError()) {
			maude.getMaudeProfile().setResult(parser.getErrorResult());
		} else {
			maude.getMaudeProfile().setResult(parser.getSimpleResult());
			maude.getMaudeProfile().setRunningTime(Long.toString(elapsedTime) + "ms");
			maude.getMaudeProfile().setCPUTime(parser.getCPUTime());
			maude.getMaudeProfile().setResultLocation(analysisResultPath);
			writeResultFile(parser.getTrace(), analysisResultPath);
		}
		if (maude.exitValueProcess() == 0) {
			if (pair != null) {
				pair.setRepresentingMaude(maude);
				HybridSynchAADLViewController.updateView(pair);
			} else {
				HybridSynchAADLViewController.updateView(maude);
			}
		}
	}

	private boolean validCommand() {
		return targetPath != null && maudeExecPath != null
				&& IOUtils.getFile(targetPath).getLocation().toFile().getPath() != null;
	}

	private String compileCommand() {
		return maudeExecPath + " " + "-no-prelude" + " " + IOUtils.getFile(targetPath).getLocation().toFile().getPath();
	}

	private void writeResultFile(String result, IPath file) {
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(result.getBytes()), IOUtils.getFile(file));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
