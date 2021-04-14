package edu.postech.aadl.maude;

import org.eclipse.core.runtime.IPath;

public interface Maude {

	public MaudeProfile getMaudeProfile();

	public IPath getTargetPath();

	public IPath getAnalysisResultPath();

	public long getTimeout();

	public Process getProcess();

	public void setTimeout(String timeout);

	public void setTimeout(Long timeout);

	public boolean isProcessAlive();

	public void killProcess();

	public Integer findPropIdLine();
}
