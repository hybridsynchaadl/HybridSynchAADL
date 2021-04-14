package edu.postech.aadl.maude;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.Property;

public class MaudeProfile {

	private String result;
	private IFile pspc;
	private Property prop;
	private IPath resultPath;
	private String cpuTime;
	private String runningTime;
	private String method;

	private Process process = null;

	public MaudeProfile(IFile pspc, Property prop, String result, String method, IPath path, String cpuTime,
			String runningTime) {
		this.pspc = pspc;
		this.result = result;
		this.method = method;
		this.prop = prop;
		this.resultPath = path;
		this.cpuTime = cpuTime;
		this.runningTime = runningTime;
	}

	public boolean isInvariantProperty() {
		return (this.prop instanceof Invariant);
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setCPUTime(String time) {
		cpuTime = time;
	}

	public void setRunningTime(String time) {
		runningTime = time;
	}

	public void setResultLocation(IPath path) {
		this.resultPath = path;
	}

	public void killProcess() {
		if (process != null) {
			process.destroy();
		}
	}

	public IFile getPSPCFile() {
		return pspc;
	}

	public Property getProp() {
		return prop;
	}

	public boolean checkProcess() {
		return process.isAlive();
	}

	public String getResult() {
		return result;
	}

	public String getMethod() {
		return method;
	}

	public String getPropId() {
		return prop.getName();
	}

	public String getPspcFileName() {
		return pspc.getName();
	}

	public String getResultLocationString() {
		return resultPath.toString();
	}

	public IPath getResultLocationIPath() {
		return resultPath;
	}

	public String getCPUTime() {
		return cpuTime;
	}

	public String getRunningTime() {
		return runningTime;
	}
}
