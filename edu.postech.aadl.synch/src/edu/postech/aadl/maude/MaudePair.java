package edu.postech.aadl.maude;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import edu.postech.aadl.xtext.propspec.propSpec.Property;

public class MaudePair implements Maude {
	private MaudeSingle random = null;
	private MaudeSingle symbolic = null;
	private MaudeSingle representor = null;

	public MaudePair(IFile pspcFile, Property prop, MaudeSingle random, MaudeSingle symbolic) {
		this.random = random;
		this.symbolic = symbolic;
		String simpleResult = checkErrorInEachMode();
		if(simpleResult == null) {
			simpleResult = "Running..";
		}
		MaudeProfile profile = new MaudeProfile(pspcFile, prop, simpleResult, "portfolio", new Path(""),
				new String(""), new String(""));
		this.representor = new MaudeSingle(profile, new Path(""), new Path(""));
	}

	public String checkErrorInEachMode() {
		if (random.getMaudeProfile().getResult().contains("Error:")) {
			return random.getMaudeProfile().getResult();
		}
		if (symbolic.getMaudeProfile().getResult().contains("Error:")) {
			return symbolic.getMaudeProfile().getResult();
		}
		return null;
	}

	public void setRepresentingMaude(MaudeSingle maude) {
		this.representor.getMaudeProfile().setCPUTime(maude.getMaudeProfile().getCPUTime());
		this.representor.getMaudeProfile().setRunningTime(maude.getMaudeProfile().getRunningTime());
		this.representor.getMaudeProfile().setResultLocation(maude.getMaudeProfile().getResultLocationIPath());
		this.representor.getMaudeProfile().setResult(maude.getMaudeProfile().getResult());
		this.representor.getMaudeProfile().setMethod(maude.getMaudeProfile().getMethod());
		this.representor.setProcess(maude.getProcess());
		this.representor.setTimeout(maude.getTimeout());
	}

	public MaudeSingle getRunningMaudeProcess() {
		if (random.getProcess().isAlive()) {
			return random;
		} else if (symbolic.getProcess().isAlive()) {
			return symbolic;
		} else {
			return null;
		}
	}

	public MaudeSingle getRandomMaude() {
		return random;
	}

	public MaudeSingle getSymbolicMaude() {
		return symbolic;
	}

	public MaudeSingle getRepresentor() {
		return representor;
	}

	@Override
	public MaudeProfile getMaudeProfile() {
		return representor.getMaudeProfile();
	}

	@Override
	public IPath getTargetPath() {
		return representor.getTargetPath();
	}

	@Override
	public IPath getAnalysisResultPath() {
		return representor.getAnalysisResultPath();
	}

	@Override
	public long getTimeout() {
		return representor.getTimeout();
	}

	@Override
	public void setTimeout(Long timeout) {
		random.setTimeout(timeout);
		symbolic.setTimeout(timeout);
		representor.setTimeout(timeout);
	}

	@Override
	public void setTimeout(String timeout) {
		random.setTimeout(timeout);
		symbolic.setTimeout(timeout);
		representor.setTimeout(timeout);
	}

	@Override
	public boolean isProcessAlive() {
		return random.isProcessAlive() || symbolic.isProcessAlive();
	}

	@Override
	public void killProcess() {
		random.killProcess();
		symbolic.killProcess();
	}

	@Override
	public Integer findPropIdLine() {
		try {
			InputStream is = representor.getMaudeProfile().getPSPCFile().getContents();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			int idx = 1;
			while ((line = br.readLine()) != null) {
				if (line.contains("[" + representor.getMaudeProfile().getPropId() + "]")) {
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
	public Process getProcess() {
		return representor.getProcess();
	}
}
