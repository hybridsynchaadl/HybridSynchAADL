package edu.postech.aadl.synch.checker;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.FeatureInstance;
import org.osate.aadl2.instance.util.InstanceSwitch;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.properties.PropertyAcc;
import org.osate.ba.aadlba.DataSubcomponentHolder;
import org.osate.ba.aadlba.ValueVariable;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import edu.postech.aadl.synch.maude.contspec.ContSpec;
import edu.postech.aadl.synch.maude.contspec.ContSpecItem;
import edu.postech.aadl.synch.maude.contspec.ODE;
import edu.postech.aadl.utils.PropertyUtil;

public class SynchAadlConstChecker extends AadlProcessingSwitchWithProgress {

	public SynchAadlConstChecker(IProgressMonitor pm, AnalysisErrorReporterManager errMgr) {
		super(pm, errMgr);
	}

	@Override
	protected void initSwitches() {
		instanceSwitch = new InstanceSwitch<String>() {

			@Override
			public String caseComponentInstance(ComponentInstance ci) {
				checkCompSynch(ci);
				checkCompPeriod(ci);
				checkDataCompProperty(ci);

				checkFeatDataOutInitValue(ci);
				checkFeatDataOutParamValue(ci);

				checkEnvCompDataType(ci);
				checkEnvSubCompType(ci);
				checkEnvCompHasCD(ci);
				checkNonEnvCompHasCD(ci);

				checkEnvFlowsDirectReferPort(ci);
				checkEnvFlowsParseError(ci);
				checkEnvTargetInFlows(ci);
				checkODEParse(ci);

				checkCompSubDataInitValue(ci);
				checkCompSampleTime(ci);
				checkCompResponseTime(ci);
				checkCompMaxClockDev(ci);

				monitor.worked(1);
				return DONE;
			}

			@Override
			public String caseConnectionInstance(ConnectionInstance ci) {
				checkDelayedConnBetweenConts(ci);
				checkDelayedConnFromEnvToCont(ci);
				checkDelayedConnFromContToEnv(ci);
				checkConnBetweenEnvComps(ci);
				checkConnEnvOutPortType(ci);

				monitor.worked(1);
				return DONE;
			}
		};
	}


	private void checkCompSynch(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA && !PropertyUtil.isEnvironment(ci)
				&& !PropertyUtil.isSynchronous(ci)) {
			getErrorManager().error(ci, ci.getName() + " is not a synchronous " + ci.getCategory().getLiteral(),
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkCompPeriod(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA) {
			double period = GetProperties.getPeriodinMS(ci);
			ComponentInstance parent = ci.getContainingComponentInstance();
			if (parent != null) {
				double parentPeriod = GetProperties.getPeriodinMS(parent);
				if (parentPeriod != period) {
					getErrorManager().error(ci, ci.getName() + " have different period with others",
							new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
				}
			}
		}
	}

	private void checkFeatDataOutInitValue(ComponentInstance ci) {
		if (PropertyUtil.isTopComponent(ci)) {
			for (ComponentInstance sub : ci.getComponentInstances()) {
				if (!PropertyUtil.isEnvironment(sub)) {
					for (FeatureInstance fi : sub.getFeatureInstances()) {
						if (fi.getDirection().outgoing() && PropertyUtil.isDataPortFeature(fi)
								&& PropertyUtil.getSynchListProp(fi,
								PropertyUtil.DATA_MODEL,
								PropertyUtil.INITIAL_VALUE, null) == null) {
							getErrorManager().error(fi,
									fi.getName() + " does not have a Data_Model::Initial_Value property",
									new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
						}
					}
				}
			}
		}
	}

	private void checkDataCompProperty(ComponentInstance ci) {
		List<String> prNames = PropertyUtil.getSynchListProp(ci, PropertyUtil.DATA_MODEL, PropertyUtil.INITIAL_VALUE,
				null);
		if (prNames != null && !prNames.get(0).matches("-?\\d+(\\.\\d+)?") && !prNames.get(0).matches("false|true")
				&& !prNames.get(0).equals("param")) {
			getErrorManager().error(ci,
					"Data Component: initial value property permits only the integer, boolean and \'param\' string value",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}

	}

	private void checkFeatDataOutParamValue(ComponentInstance ci) {
		if (PropertyUtil.isTopComponent(ci)) {
			for (ComponentInstance sub : ci.getComponentInstances()) {
				if (!PropertyUtil.isEnvironment(sub)) {
					for (FeatureInstance fi : sub.getFeatureInstances()) {
						if (fi.getDirection().outgoing() && PropertyUtil.isParameterized(fi)) {
							getErrorManager().error(fi, fi.getName() + " is not allowed to use parameterized value",
									new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
						}
					}
				}
			}
		}
	}

	private void checkEnvCompDataType(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			for (ComponentInstance sub : ci.getComponentInstances()) {
				if (sub.getCategory() == ComponentCategory.DATA && !sub.getClassifier().getName().equals("Float")) {
					getErrorManager().error(ci, ci.getName() + " must be float type data component",
							new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
				}
			}
		}
	}

	private void checkEnvSubCompType(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			for (ComponentInstance sub : ci.getComponentInstances()) {
				if (sub.getCategory() != ComponentCategory.DATA) {
					getErrorManager().error(ci, ci.getName() + " can not have subcomponent except data component",
							new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
				}
			}
		}
	}

	private void checkEnvCompHasCD(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci) && PropertyUtil.getSynchStringProp(ci, PropertyUtil.HYBRIDSYNCHAADL,
				PropertyUtil.CONTINUOUSDYNAMICS, null) == null) {
			getErrorManager().error(ci, ci.getName() + " must have Continuous Dynamics property",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkNonEnvCompHasCD(ComponentInstance ci) {
		if (!PropertyUtil.isEnvironment(ci) && PropertyUtil.getSynchStringProp(ci, PropertyUtil.HYBRIDSYNCHAADL,
				PropertyUtil.CONTINUOUSDYNAMICS, null) != null) {
			getErrorManager().error(ci, ci.getName() + " must not have Continuous Dynamics property",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkEnvFlowsDirectReferPort(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			PropertyAcc pacc = PropertyUtil.getSynchPropAcc(ci, PropertyUtil.HYBRIDSYNCHAADL,
					PropertyUtil.CONTINUOUSDYNAMICS);
			for(ModalPropertyValue mpv : pacc.getAssociations().get(0).getOwnedValues()) {
				String cont = ((StringLiteral) mpv.getOwnedValue()).getValue();
				ContSpec spec = ContSpec.parse(cont, ci);
				for (FeatureInstance fi : ci.getFeatureInstances()) {
					if (spec.findValueVariable(fi.getFeature().getName())) {
						getErrorManager().error(ci,
								ci.getName()
										+ " should have continuous dynamics without using directly a port component",
								new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
					}
				}
			}
		}
	}

	private void checkEnvFlowsParseError(ComponentInstance ci) {
		if(PropertyUtil.isEnvironment(ci)) {
			PropertyAcc pacc = PropertyUtil.getSynchPropAcc(ci, PropertyUtil.HYBRIDSYNCHAADL,
					PropertyUtil.CONTINUOUSDYNAMICS);
			for(ModalPropertyValue mpv : pacc.getAssociations().get(0).getOwnedValues()) {
				String cont = ((StringLiteral) mpv.getOwnedValue()).getValue();
				ContSpec spec = ContSpec.parse(cont,  ci);
				if(spec.hasError()) {
					getErrorManager().error(ci, "ContinuousDynamics: " + spec.getErrorMessage(),
							new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
				}
			}
		}
	}

	private void checkEnvTargetInFlows(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			PropertyAcc pacc = PropertyUtil.getSynchPropAcc(ci, PropertyUtil.HYBRIDSYNCHAADL,
					PropertyUtil.CONTINUOUSDYNAMICS);
			for (ModalPropertyValue mpv : pacc.getAssociations().get(0).getOwnedValues()) {
				String cont = ((StringLiteral) mpv.getOwnedValue()).getValue();
				ContSpec spec = ContSpec.parse(cont, ci);
				for (ContSpecItem item : spec.getItems()) {
					ValueVariable target = item.getTarget();
					String name = ((DataSubcomponentHolder) target).getDataSubcomponent().getName();
					if (!hasName(ci.getComponentInstances(), name)) {
						getErrorManager().error(ci, "ContinuousDynmaics Flow: " + "The target is not a data component",
								new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
					}
				}
			}
		}
	}

	private boolean hasName(EList<ComponentInstance> eci, String name) {
		for (ComponentInstance ci : eci) {
			if (ci.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void checkODEParse(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			PropertyAcc pacc = PropertyUtil.getSynchPropAcc(ci, PropertyUtil.HYBRIDSYNCHAADL,
					PropertyUtil.CONTINUOUSDYNAMICS);
			for (ModalPropertyValue mpv : pacc.getAssociations().get(0).getOwnedValues()) {
				String cont = ((StringLiteral) mpv.getOwnedValue()).getValue();
				ContSpec spec = ContSpec.parse(cont, ci);
				for (ContSpecItem item : spec.getItems()) {
					if (item instanceof ODE) {
						if (item.getExpression().getBinaryAddingOperators().size() > 0) {
							getErrorManager().error(ci,
									"ContinuousDynmaics Flow: " + "Not supported ODE format",
									new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
						}
					}
				}
			}
		}
	}


	private void checkCompSubDataInitValue(ComponentInstance ci) {
		if (ci.getCategory() == ComponentCategory.DATA && PropertyUtil.getSynchListProp(ci,
				PropertyUtil.DATA_MODEL, PropertyUtil.INITIAL_VALUE, null) == null) {
			getErrorManager().error(ci, ci.getName() + " does not have a Data_Model::Initial_Value property",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkCompSampleTime(ComponentInstance ci) {
		if (ci.getCategory() == ComponentCategory.THREAD
				&& PropertyUtil.getSynchRangeProp(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.SAMPLING_TIME,
						null) == null) {
			getErrorManager().error(ci, ci.getName() + " does not have a Hybrid_SynchAADL::Sampling_Time",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkCompResponseTime(ComponentInstance ci) {
		if (ci.getCategory() == ComponentCategory.THREAD
				&& PropertyUtil.getSynchRangeProp(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.RESPONSE_TIME,
						null) == null) {
			getErrorManager().error(ci, ci.getName() + " does not have a Hybrid_SynchAADL::Response_Time",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkCompMaxClockDev(ComponentInstance ci) {
		if ((ci.getCategory() == ComponentCategory.THREAD)
				&& PropertyUtil.getSynchIntegerProp(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.MAX_CLOCK_DEV,
						0) == 0) {
			getErrorManager().error(ci, ci.getName() + " does not have a Hybrid_SynchAADL::Max_Clock_Deviation",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkDelayedConnBetweenConts(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (!PropertyUtil.isEnvironment(dst) && !PropertyUtil.isEnvironment(src)
				&& !PropertyUtil.isDelayedPortConnection(ci)) {
			getErrorManager().error(ci, ci.getName() + " should be delayed connection",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}


	private void checkDelayedConnFromEnvToCont(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (!PropertyUtil.isEnvironment(dst) && PropertyUtil.isEnvironment(src)
				&& PropertyUtil.isDelayedPortConnection(ci)) {
			getErrorManager().error(ci, ci.getName() + " should not be delayed connection",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkDelayedConnFromContToEnv(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (PropertyUtil.isEnvironment(dst) && !PropertyUtil.isEnvironment(src)
				&& PropertyUtil.isDelayedPortConnection(ci)) {
			getErrorManager().error(ci, ci.getName() + " should not be delayed connection",
					new String[] { IMarker.LOCATION }, new String[] { ci.getName() });
		}
	}

	private void checkConnBetweenEnvComps(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (PropertyUtil.isEnvironment(src) && PropertyUtil.isEnvironment(dst)) {
			getErrorManager().error(ci, ci.getName() + " is not allowed connection", new String[] { IMarker.LOCATION },
					new String[] { ci.getName() });
		}
	}

	private void checkConnEnvOutPortType(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (PropertyUtil.isEnvironment(src) && !PropertyUtil.isEnvironment(dst)
				&& !PropertyUtil.isDataPortConnection(ci)) {
			getErrorManager().error(ci, ci.getName() + " should be data connection", new String[] { IMarker.LOCATION },
					new String[] { ci.getName() });
		}
	}



}