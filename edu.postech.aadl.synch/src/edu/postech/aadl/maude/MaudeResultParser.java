package edu.postech.aadl.maude;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaudeResultParser {

	private String input;
	private String error;
	private boolean hasError = false;
	private boolean isInvariant;
	private String errorCase = null;
	private String simpleResult = null;
	private String cpuTime = null;
	private String trace = null;

	public MaudeResultParser(StringBuffer inputBuffer, StringBuffer errorBuffer, boolean isInvariant) {
		this.input = inputBuffer.toString();
		this.error = errorBuffer.toString();
		this.isInvariant = isInvariant;
	}

	public void parse() {
		if (hasWarnings()) {
			parseErrorCase();
		} else {
			parseSimpleResult();
			parseCPUTime();
			parseTrace();
		}
	}

	private boolean hasWarnings() {
		return error.contains("Maude internal error");
	}

	private void parseErrorCase() {
		if (error.contains("Maude internal error")) {
			errorCase = "Error: The internal maude error occured!";
		} else if (error.contains("Warning: ")) {
			errorCase = "Error: The semantics error occured!";
		}
	}

	private void parseSimpleResult() {
		if (input.contains("(nil).TimedReachabilityPath")) {
			simpleResult = isInvariant ? "No counterexample found" : "Unreachable";
		} else if (input.contains("result TimedReachabilityPath:") || input.contains("result ConfigWithRand:")) {
			simpleResult = isInvariant ? "Counterexample found" : "Reachable";
		} else {
			simpleResult = "Error: Abnormal Termination";
		}
	}

	private void parseCPUTime() {
		Pattern p = Pattern.compile("[0-9]+ms cpu");
		Matcher m = p.matcher(input);
		if (m.find()) {
			cpuTime = m.group().split(" ")[0];
		}
	}

	private void parseTrace() {
		if (simpleResult.equals("No counterexample found") || simpleResult.equals("Unreachable")) {
			trace = "";
			return;
		}

		int pathIdx = input.indexOf("result TimedReachabilityPath: ") + "result TimedReachabilityPath: ".length();
		int byeIdx = input.indexOf("Bye.");
		if (pathIdx != -1 && byeIdx != -1) {
			trace = input.substring(pathIdx, byeIdx);
		} else {
			trace = "Error: Output Trace Error";
		}

	}

	public boolean hasError() {
		return hasError;
	}

	public String getErrorResult() {
		return errorCase;
	}

	public String getSimpleResult() {
		return simpleResult;
	}

	public String getCPUTime() {
		return cpuTime;
	}

	public String getTrace() {
		return trace;
	}
}
