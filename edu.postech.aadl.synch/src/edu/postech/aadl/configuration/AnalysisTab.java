package edu.postech.aadl.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class AnalysisTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {

	private Text textLoopBound = null;
	private Text textTransBound = null;
	private Text textRandomSeed = null;
	private Text textMinParam = null;
	private Text textMaxParam = null;
	private FileFieldEditor targetPSPCFile = null;
	private Button randomButton;
	private Button symbolicButton;
	private Button portfolioButton;
	private Text textTimeout;

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(comp);
		targetPSPCFile = new FileFieldEditor("Target PropSpec File", "PropSpec File", comp);
		targetPSPCFile.getTextControl(comp).addModifyListener(e -> updateLaunchConfigurationDialog());


		Group analysisGroup = new Group(comp, SWT.SHADOW_ETCHED_IN);
		analysisGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(6).create());
		analysisGroup.setText("Analysis Method");
		GridDataFactory.generate(analysisGroup, 3, 4);


		makeRandomLabel(analysisGroup);
		makeRandomButton(analysisGroup);

		makeSymbolicLabel(analysisGroup);
		makeSymbolicButton(analysisGroup);

		makePortfolioLabel(analysisGroup);
		makePortfolioButton(analysisGroup);

		makeRandomSeedLabel(analysisGroup);
		makeRandomSeedText(analysisGroup);

		makeMinParamLabel(analysisGroup);
		makeMinParamText(analysisGroup);

		makeMaxParamLabel(analysisGroup);
		makeMaxParamText(analysisGroup);

		makeLoopBoundLabel(analysisGroup);
		makeLoopBoundText(analysisGroup);

		makeTransBoundLabel(analysisGroup);
		makeTransBoundText(analysisGroup);

		makeTimeoutLabel(analysisGroup);
		makeTimeoutText(analysisGroup);

	}

	private void makeRandomLabel(Group analysisGroup) {
		Label labelRandomAnalysis = new Label(analysisGroup, SWT.NONE);
		labelRandomAnalysis.setText("Randomized Simulation:");
		GridDataFactory.swtDefaults().applyTo(labelRandomAnalysis);
	}

	private void makeRandomButton(Group analysisGroup) {
		randomButton = new Button(analysisGroup, SWT.RADIO);
		randomButton.setSelection(false);
		randomButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRandomSeed.setEnabled(randomButton.getSelection());
				textRandomSeed.setEditable(randomButton.getSelection());
				textMinParam.setEnabled(randomButton.getSelection());
				textMinParam.setEditable(randomButton.getSelection());
				textMaxParam.setEnabled(randomButton.getSelection());
				textMaxParam.setEditable(randomButton.getSelection());
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		GridDataFactory.generate(randomButton, 1, 1);
	}

	private void makeSymbolicLabel(Group analysisGroup) {
		Label labelSymbolicAnalysis = new Label(analysisGroup, SWT.NONE);
		labelSymbolicAnalysis.setText("Symbolic Reachability:");
		GridDataFactory.swtDefaults().applyTo(labelSymbolicAnalysis);
	}

	private void makeSymbolicButton(Group analysisGroup) {
		symbolicButton = new Button(analysisGroup, SWT.RADIO);
		symbolicButton.setSelection(false);
		symbolicButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
				textLoopBound.setEnabled(symbolicButton.getSelection());
				textLoopBound.setEditable(symbolicButton.getSelection());
				textTransBound.setEnabled(symbolicButton.getSelection());
				textTransBound.setEditable(symbolicButton.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		GridDataFactory.generate(symbolicButton, 1, 1);
	}

	private void makePortfolioLabel(Group analysisGroup) {
		Label labelProfileAnalysis = new Label(analysisGroup, SWT.NONE);
		labelProfileAnalysis.setText("Portfolio Analysis:");
		GridDataFactory.swtDefaults().applyTo(labelProfileAnalysis);
	}

	private void makePortfolioButton(Group analysisGroup) {
		portfolioButton = new Button(analysisGroup, SWT.RADIO);
		portfolioButton.setSelection(false);
		portfolioButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textLoopBound.setEnabled(portfolioButton.getSelection());
				textLoopBound.setEditable(portfolioButton.getSelection());
				textTransBound.setEnabled(portfolioButton.getSelection());
				textTransBound.setEditable(portfolioButton.getSelection());
				textRandomSeed.setEnabled(portfolioButton.getSelection());
				textRandomSeed.setEditable(portfolioButton.getSelection());
				textMinParam.setEnabled(portfolioButton.getSelection());
				textMinParam.setEditable(portfolioButton.getSelection());
				textMaxParam.setEnabled(portfolioButton.getSelection());
				textMaxParam.setEditable(portfolioButton.getSelection());
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		GridDataFactory.generate(portfolioButton, 1, 1);
	}

	private void makeRandomSeedLabel(Group analysisGroup) {
		Label labelRandomSeed = new Label(analysisGroup, SWT.NONE);
		labelRandomSeed.setText("Random seed:");
		GridDataFactory.swtDefaults().applyTo(labelRandomSeed);
	}

	private void makeRandomSeedText(Group analysisGroup) {
		textRandomSeed = new Text(analysisGroup, SWT.BORDER);
		textRandomSeed.setMessage("10");
		textRandomSeed.setEnabled(randomButton.getSelection() | portfolioButton.getSelection());
		textRandomSeed.setEditable(randomButton.getSelection() | portfolioButton.getSelection());
		GridDataFactory.generate(textRandomSeed, 5, 1);
		textRandomSeed.addModifyListener(e -> updateLaunchConfigurationDialog());
	}

	private void makeMinParamLabel(Group analysisGroup) {
		Label labelMinParam = new Label(analysisGroup, SWT.NONE);
		labelMinParam.setText("Default minimum bound of \"param\":");
		GridDataFactory.swtDefaults().applyTo(labelMinParam);
	}

	private void makeMinParamText(Group analysisGroup) {
		textMinParam = new Text(analysisGroup, SWT.BORDER);
		textMinParam.setMessage("10");
		textMinParam.setEnabled(randomButton.getSelection() | portfolioButton.getSelection());
		textMinParam.setEditable(randomButton.getSelection() | portfolioButton.getSelection());
		GridDataFactory.generate(textMinParam, 5, 1);
		textMinParam.addModifyListener(e -> updateLaunchConfigurationDialog());
	}

	private void makeMaxParamLabel(Group analysisGroup) {
		Label labelMaxParam = new Label(analysisGroup, SWT.NONE);
		labelMaxParam.setText("Default maximum bound of \"param\":");
		GridDataFactory.swtDefaults().applyTo(labelMaxParam);
	}

	private void makeMaxParamText(Group analysisGroup) {
		textMaxParam = new Text(analysisGroup, SWT.BORDER);
		textMaxParam.setMessage("10");
		textMaxParam.setEnabled(randomButton.getSelection() | portfolioButton.getSelection());
		textMaxParam.setEditable(randomButton.getSelection() | portfolioButton.getSelection());
		GridDataFactory.generate(textMaxParam, 5, 1);
		textMaxParam.addModifyListener(e -> updateLaunchConfigurationDialog());
	}

	private void makeLoopBoundLabel(Group analysisGroup) {
		Label labelLoopBound = new Label(analysisGroup, SWT.NONE);
		labelLoopBound.setText("Loop bound:");
		GridDataFactory.swtDefaults().applyTo(labelLoopBound);
	}

	private void makeLoopBoundText(Group analysisGroup) {
		textLoopBound = new Text(analysisGroup, SWT.BORDER);
		textLoopBound.setMessage("10");
		textLoopBound.setEnabled(symbolicButton.getSelection() | portfolioButton.getSelection());
		textLoopBound.setEditable(symbolicButton.getSelection() | portfolioButton.getSelection());
		GridDataFactory.generate(textLoopBound, 5, 1);
		textLoopBound.addModifyListener(e -> updateLaunchConfigurationDialog());
	}

	private void makeTransBoundLabel(Group analysisGroup) {
		Label labelTransBound = new Label(analysisGroup, SWT.NONE);
		labelTransBound.setText("Transition bound:");
		GridDataFactory.swtDefaults().applyTo(labelTransBound);
	}

	private void makeTransBoundText(Group analysisGroup) {
		textTransBound = new Text(analysisGroup, SWT.BORDER);
		textTransBound.setMessage("10");
		textTransBound.setEnabled(symbolicButton.getSelection() | portfolioButton.getSelection());
		textTransBound.setEditable(symbolicButton.getSelection() | portfolioButton.getSelection());
		GridDataFactory.generate(textTransBound, 5, 1);
		textTransBound.addModifyListener(e -> updateLaunchConfigurationDialog());
	}

	private void makeTimeoutLabel(Composite comp) {
		Label label4 = new Label(comp, SWT.NONE);
		label4.setText("Timeout :");
		GridDataFactory.swtDefaults().applyTo(label4);
	}

	private void makeTimeoutText(Composite comp) {
		textTimeout = new Text(comp, SWT.BORDER);
		textTimeout.setMessage("seconds");
		GridDataFactory.generate(textTimeout, 2, 1);
		textTimeout.addModifyListener(e -> updateLaunchConfigurationDialog());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("edu.postech.analysis.launcher.loopBound", "10");
		configuration.setAttribute("edu.postech.analysis.launcher.transBound", "10");
		configuration.setAttribute("edu.postech.analysis.launcher.pspcPath", "");
		configuration.setAttribute("edu.postech.analysis.launcher.randomAnalysis", "false");
		configuration.setAttribute("edu.postech.analysis.launcher.symbolicAnalysis", "false");
		configuration.setAttribute("edu.postech.analysis.launcher.portfolioAnalysis", "false");
		configuration.setAttribute("edu.postech.analysis.launcher.randomSeed", "0");
		configuration.setAttribute("edu.postech.analysis.launcher.minParamValue", "0");
		configuration.setAttribute("edu.postech.analysis.launcher.maxParamValue", "0");
		configuration.setAttribute("edu.postech.analysis.launcher.timeout", "infinity");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			textLoopBound.setText(configuration.getAttribute("edu.postech.analysis.launcher.loopBound", "10"));
			textTransBound.setText(configuration.getAttribute("edu.postech.analysis.launcher.transBound", "10"));
			targetPSPCFile.setStringValue(configuration.getAttribute("edu.postech.analysis.launcher.pspcPath", ""));
			randomButton.setSelection(Boolean
					.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.randomAnalysis", "false")));
			symbolicButton.setSelection(Boolean
					.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.symbolicAnalysis", "false")));
			portfolioButton.setSelection(Boolean
					.valueOf(configuration.getAttribute("edu.postech.analysis.launcher.portfolioAnalysis", "false")));


			textRandomSeed.setEnabled(randomButton.getSelection() | portfolioButton.getSelection());
			textRandomSeed.setEditable(randomButton.getSelection() | portfolioButton.getSelection());
			textRandomSeed.setText(configuration.getAttribute("edu.postech.analysis.launcher.randomSeed", "0"));
			textMinParam.setEnabled(randomButton.getSelection() | portfolioButton.getSelection());
			textMinParam.setEditable(randomButton.getSelection() | portfolioButton.getSelection());
			textMinParam.setText(configuration.getAttribute("edu.postech.analysis.launcher.minParamValue", "0"));
			textMaxParam.setEnabled(randomButton.getSelection() | portfolioButton.getSelection());
			textMaxParam.setEditable(randomButton.getSelection() | portfolioButton.getSelection());
			textMaxParam.setText(configuration.getAttribute("edu.postech.analysis.launcher.maxParamValue", "0"));
			textTimeout
					.setText(configuration.getAttribute("edu.postech.analysis.launcher.timeout", "infinity"));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("edu.postech.analysis.launcher.loopBound", textLoopBound.getText());
		configuration.setAttribute("edu.postech.analysis.launcher.transBound", textTransBound.getText());
		configuration.setAttribute("edu.postech.analysis.launcher.pspcPath", targetPSPCFile.getStringValue());
		configuration.setAttribute("edu.postech.analysis.launcher.randomAnalysis",
				String.valueOf(randomButton.getSelection()));
		configuration.setAttribute("edu.postech.analysis.launcher.symbolicAnalysis",
				String.valueOf(symbolicButton.getSelection()));
		configuration.setAttribute("edu.postech.analysis.launcher.portfolioAnalysis",
				String.valueOf(portfolioButton.getSelection()));
		configuration.setAttribute("edu.postech.analysis.launcher.randomSeed", textRandomSeed.getText());
		configuration.setAttribute("edu.postech.analysis.launcher.minParamValue", textMinParam.getText());
		configuration.setAttribute("edu.postech.analysis.launcher.maxParamValue", textMaxParam.getText());
		configuration.setAttribute("edu.postech.analysis.launcher.timeout", textTimeout.getText());

	}

	public void setMethod(String method) {
		boolean symbolic = false;
		boolean random = false;
		boolean portfolio = false;
		if (method.contains("Symbolic")) {
			symbolic = true;
		} else if (method.contains("Random")) {
			random = true;
		} else {
			portfolio = true;
		}
		randomButton.setSelection(random);
		symbolicButton.setSelection(symbolic);
		portfolioButton.setSelection(portfolio);
	}

	@Override
	public String getName() {
		return "Anlaysis";
	}

	private boolean checkEmptyText(ILaunchConfiguration launchConfig) throws CoreException {
		String pspcPath = launchConfig.getAttribute("edu.postech.analysis.launcher.pspcPath", "");
		String timeout = launchConfig.getAttribute("edu.postech.analysis.launcher.timeout", "");

		if (pspcPath.length() == 0 || timeout.length() == 0) {
			return false;
		}

		Boolean random = Boolean.valueOf(
				launchConfig.getAttribute("edu.postech.analysis.launcher.randomAnalysis", "false"));
		Boolean symbolic = Boolean
				.valueOf(launchConfig.getAttribute("edu.postech.analysis.launcher.symbolicAnalysis", "false"));
		Boolean profile = Boolean
				.valueOf(launchConfig.getAttribute("edu.postech.analysis.launcher.profileAnalysis", "false"));

		if (random || profile) {
			String randomSeed = launchConfig.getAttribute("edu.postech.analysis.launcher.randomSeed", "");
			String minParamValue = launchConfig.getAttribute("edu.postech.analysis.launcher.minParamValue", "");
			String maxParamValue = launchConfig.getAttribute("edu.postech.analysis.launcher.maxParamValue", "");
			if (randomSeed.length() == 0 || minParamValue.length() == 0 || maxParamValue.length() == 0) {
				return false;
			}
		}

		if (symbolic || profile) {
			String loopBound = launchConfig.getAttribute("edu.postech.analysis.launcher.randomSeed", "");
			String transBound = launchConfig.getAttribute("edu.postech.analysis.launcher.minParamValue", "");
			if (loopBound.length() == 0 || transBound.length() == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			int randomSeed = 0;
			long timeoutVal = 0;
			float minParamValue = 0;
			float maxParamValue = 0;

			if (!checkEmptyText(launchConfig)) {
				setErrorMessage("Fill an empty text");
				return false;
			}

			try {
				randomSeed = Integer
						.valueOf(launchConfig.getAttribute("edu.postech.analysis.launcher.randomSeed", "0"));
			} catch (NumberFormatException e) {
				setErrorMessage("Random seed must be positive integer value");
				return false;
			}

			try {
				minParamValue = Float
						.valueOf(launchConfig.getAttribute("edu.postech.analysis.launcher.minParamValue", "0.0"));
			} catch (NumberFormatException e) {
				setErrorMessage("Minimum parameterized value should be float value");
				return false;
			}

			try {
				maxParamValue = Float
						.valueOf(launchConfig.getAttribute("edu.postech.analysis.launcher.maxParamValue", "0.0"));
			} catch (NumberFormatException e) {
				setErrorMessage("Maximum parameterized value should be float value");
				return false;
			}

			String timeoutStr = launchConfig.getAttribute("edu.postech.analysis.launcher.timeout", "empty");
			if (!timeoutStr.equals("infinity")) {
				try {
					timeoutVal = Long.valueOf(timeoutStr);
				} catch (NumberFormatException e) {
					setErrorMessage("Timeout value can be \"infinity\" or positive integer value");
					return false;
				}
			}

			if (randomSeed < 0) {
				setErrorMessage("Random seed must be positive integer value");
				return false;
			}

			if (minParamValue > maxParamValue) {
				setErrorMessage("Minimum parameter value must be less than maximum parameter value");
				return false;
			}

			if (timeoutVal < 0) {
				setErrorMessage("Timeout value can be \"infinity\" or positive integer value");
				return false;
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
		setErrorMessage(null);

		return true;
	}

}