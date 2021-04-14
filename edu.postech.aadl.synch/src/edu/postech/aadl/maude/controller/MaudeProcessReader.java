package edu.postech.aadl.maude.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MaudeProcessReader extends Thread {

	private Process process;
	private StringBuffer input;
	private StringBuffer error;

	public MaudeProcessReader(Process process, StringBuffer input, StringBuffer error) {
		this.process = process;
		this.input = input;
		this.error = error;
	}

	@Override
	public void run() {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line;
		String error;
		try {
			while ((line = inputReader.readLine()) != null) {
				this.input.append(line + "\n");
			}
			while ((error = errorReader.readLine()) != null) {
				this.error.append(error + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
