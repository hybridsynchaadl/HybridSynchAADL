package edu.postech.aadl.maude.controller;

import java.io.File;
import java.io.OutputStream;

import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.maude.preferences.MaudePrefPage;

public class MaudeTestProcess extends Thread {

	private String maudeExecPath = null;
	private String maudeDirPath = null;
	private MaudePrefPage pref = null;
	private boolean error = false;

	public MaudeTestProcess() {
		pref = new MaudePrefPage();
		maudeExecPath = pref.getMaudeExecPath();
		maudeDirPath = pref.getMaudeDirPath();
	}

	public void checkFilesExists() throws Exception {
		File smt = new File(maudeDirPath + "/smt.maude");
		File prelude = new File(maudeDirPath + "/prelude.maude");
		if (!smt.exists()) {
			throw new Exception("The file \'smt.maude\' is missing in maude directory");
		}
		if (!prelude.exists()) {
			throw new Exception("The file \'prelude.maude\' is missing in maude directory");
		}
	}

	@Override
	public void run() {
		try {
			Process process = Runtime.getRuntime().exec(maudeExecPath + " -no-prelude ");
			if (process.isAlive()) {
				OutputStream os = process.getOutputStream();
				os.write("quit".getBytes());
				os.close();
			}
			process.waitFor();
			if (process.exitValue() != 0) {
				throw new Exception("Maude binary is not executable : " + maudeExecPath);
			}
		} catch (Exception e) {
			error = true;
			Dialog.showError("Maude Error", e.getMessage());
		}
	}

	public boolean hasError() {
		return error;
	}

}
