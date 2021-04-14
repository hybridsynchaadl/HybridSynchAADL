package edu.postech.aadl.maude;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class MaudeSingle implements Maude {

	private MaudeProfile profile;
	private long timeout = -1;
	private IPath targetPath;
	private IPath analysisResultPath;
	private Process process;

	public MaudeSingle(MaudeProfile maudeProfile, IPath targetMaudePath, IPath analysisResultPath) {
		this.profile = maudeProfile;
		this.targetPath = targetMaudePath;
		this.analysisResultPath = analysisResultPath;
	}

	@Override
	public void setTimeout(String timeout) {
		this.timeout = Long.parseLong(timeout);
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	@Override
	public boolean isProcessAlive() {
		return process.isAlive();
	}

	@Override
	public void killProcess() {
		if (this.isProcessAlive()) {
			this.process.destroy();
		}
	}

	public int exitValueProcess() {
		return this.process.exitValue();
	}


	@Override
	public Integer findPropIdLine() {
		try {
			InputStream is = profile.getPSPCFile().getContents();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			int idx = 1;
			while ((line = br.readLine()) != null) {
				if (line.contains("[" + profile.getPropId() + "]")) {
					return idx;
				}
				idx++;
			}
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public MaudeProfile getMaudeProfile() {
		return profile;
	}

	@Override
	public IPath getTargetPath() {
		return targetPath;
	}

	@Override
	public IPath getAnalysisResultPath() {
		return analysisResultPath;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override
	public Process getProcess() {
		return process;
	}

	@Override
	public void setTimeout(Long timeout) {
		this.timeout = timeout;

	}
}
