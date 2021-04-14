package edu.postech.aadl.maude.controller;

import java.util.ArrayList;
import java.util.List;

import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.maude.MaudePair;
import edu.postech.aadl.maude.MaudeSingle;
import edu.postech.aadl.synch.view.HybridSynchAADLViewController;

public class MaudeController {
	private List<MaudeSingle> maudeSingleList;
	private List<MaudePair> maudePairList;

	public MaudeController() {
		this.maudeSingleList = new ArrayList<MaudeSingle>();
		this.maudePairList = new ArrayList<MaudePair>();
	}

	public void addMaude(MaudePair pair) {
		maudePairList.add(pair);
	}

	public void addMaude(MaudeSingle maude) {
		maudeSingleList.add(maude);
	}

	public void runMaude() {
		if (testMaudeRunning()) {
			return;
		}
		removeMaudesInView();

		if (maudeSingleList.size() > 0) {
			for (MaudeSingle maude : maudeSingleList) {
				HybridSynchAADLViewController.initDataInView(maude);
				MaudeProcess process = new MaudeProcess(maude);
				process.start();
			}
		}
		else if (maudePairList.size() > 0) {
			for (MaudePair maude : maudePairList) {
				HybridSynchAADLViewController.initDataInView(maude);
				MaudeProcess process = new MaudeProcess(maude.getRandomMaude());
				process.setPair(maude);
				process.start();
				MaudeProcess process2 = new MaudeProcess(maude.getSymbolicMaude());
				process2.setPair(maude);
				process2.start();
			}
		} else {
			Dialog.showError("Error: There is no given property", null);
		}

	}

	private boolean testMaudeRunning() {
		boolean hasError = false;
		try {
			MaudeTestProcess tester = new MaudeTestProcess();
			tester.checkFilesExists();
			tester.run();
			tester.join();
			hasError = tester.hasError();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Dialog.showError("Error: Maude cannot be executed", e.getMessage());
			hasError = true;
		} catch (Exception e) {
			Dialog.showError("Error: Maude cannot be executed", e.getMessage());
			hasError = true;
		}
		return hasError;
	}

	private void removeMaudesInView() {
		for (MaudeSingle single : maudeSingleList) {
			HybridSynchAADLViewController.removeDataInView(single.getMaudeProfile().getPSPCFile());
		}
		for (MaudePair pair : maudePairList) {
			HybridSynchAADLViewController.removeDataInView(pair.getMaudeProfile().getPSPCFile());
		}
	}
}
