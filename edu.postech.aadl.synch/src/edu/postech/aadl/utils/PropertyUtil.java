package edu.postech.aadl.utils;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.ConnectionInstanceEnd;
import org.osate.aadl2.instance.ConnectionKind;
import org.osate.aadl2.instance.FeatureCategory;
import org.osate.aadl2.instance.FeatureInstance;
import org.osate.aadl2.properties.PropertyAcc;
import org.osate.aadl2.properties.PropertyLookupException;
import org.osate.xtext.aadl2.properties.util.AadlProject;
import org.osate.xtext.aadl2.properties.util.CommunicationProperties;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

public class PropertyUtil {

	/*
	 * Data Modeling Annex
	 */
	static public final String DATA_MODEL = "Data_Model";
	static public final String INITIAL_VALUE = "Initial_Value" ;

	/*
	 * Hybrid-AADL property set
	 */
	static public final String HYBRIDSYNCHAADL = "Hybrid_SynchAADL";
	static public final String SYNCHRONOUS = "Synchronous";
	static public final String NONDETERMINISTIC = "Nondeterministic";
	static public final String SAMPLING_TIME = "Sampling_Time";
	static public final String RESPONSE_TIME = "Response_Time";
	static public final String MAX_CLOCK_DEV = "Max_Clock_Deviation";
	static public final String ENVIRONMENT = "isEnvironment";
	static public final String CONTINUOUSDYNAMICS = "ContinuousDynamics";

	public static boolean isSynchronous(ComponentInstance ci) {
		return getSynchBooleanProp(ci, HYBRIDSYNCHAADL, SYNCHRONOUS, false);
	}

	public static boolean isTopComponent(ComponentInstance ci) {
		return ci.getContainingComponentInstance() == null;
	}

	public static boolean isEnvironment(ComponentInstance ci) {
		return getSynchBooleanProp(ci, HYBRIDSYNCHAADL, ENVIRONMENT, false);
	}

	public static boolean getSynchBooleanProp(final NamedElement ne, final String propertySet,
			final String propertyName, boolean defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getBooleanValue(ne, synchronous);
		}
		catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static List<String> getSynchListProp(final NamedElement ne, final String propertySet,
			final String propertyName,
			List<String> defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			List<String> list = new ArrayList<String>();
			for (PropertyExpression expr : ((ListValue) PropertyUtils.getSimplePropertyValue(ne, synchronous))
					.getOwnedListElements()) {
				list.add(((StringLiteral) expr).getValue());
			}
			return list;
		} catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static String getSynchStringProp(final NamedElement ne, final String propertySet, final String propertyName,
			String defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getStringValue(ne, synchronous);
		} catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static double getSynchRealProp(final NamedElement ne, final String propertySet, final String propertyName,
			double defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getRealValue(ne, synchronous);
		} catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static long getSynchIntegerProp(final NamedElement ne, final String propertySet, final String propertyName,
			long defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getIntegerValue(ne, synchronous);
		} catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static String getSynchRangeProp(final NamedElement ne, final String propertySet, final String propertyName,
			String defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			UnitLiteral ul = GetProperties.findUnitLiteral(synchronous, AadlProject.MS_LITERAL);
			return PropertyUtils.getScaledRangeMaximum(ne, synchronous, ul) + ".."
					+ PropertyUtils.getScaledRangeMinimum(ne, synchronous, ul);
		} catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static PropertyAcc getSynchPropAcc(final NamedElement ne, final String propertySet,
			final String properyName) {
		Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, properyName);
		return ne.getPropertyValue(synchronous);
	}

	public static boolean isParameterized(final NamedElement ne) {
		List<String> prNames = PropertyUtil.getSynchListProp(ne, PropertyUtil.DATA_MODEL, PropertyUtil.INITIAL_VALUE,
				null);
		if (prNames != null) {
			for (String name : prNames) {
				if (name.contains("param")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isDataPortFeature(final FeatureInstance fi) {
		return fi.getCategory() == FeatureCategory.DATA_PORT;
	}

	public static boolean isPortConnection(final ConnectionInstance conn) {
		return conn.getKind() == ConnectionKind.PORT_CONNECTION;
	}

	public static boolean isDataPortConnection(final ConnectionInstance conn) {
		ConnectionInstanceEnd src = conn.getSource();
		if (src instanceof FeatureInstance) {
			return ((FeatureInstance) src).getCategory() == FeatureCategory.DATA_PORT;
		}
		ConnectionInstanceEnd dst = conn.getDestination();
		if (dst instanceof FeatureInstance) {
			return ((FeatureInstance) dst).getCategory() == FeatureCategory.DATA_PORT;
		}
		return false;
	}

	public static boolean isEventDataPortConnection(final ConnectionInstance conn) {
		ConnectionInstanceEnd src = conn.getSource();
		if (src instanceof FeatureInstance) {
			return ((FeatureInstance) src).getCategory() == FeatureCategory.EVENT_DATA_PORT;
		}
		ConnectionInstanceEnd dst = conn.getDestination();
		if (dst instanceof FeatureInstance) {
			return ((FeatureInstance) dst).getCategory() == FeatureCategory.EVENT_DATA_PORT;
		}
		return false;
	}

	public static boolean isEventPortConnection(final ConnectionInstance conn) {
		ConnectionInstanceEnd src = conn.getSource();
		if (src instanceof FeatureInstance) {
			return ((FeatureInstance) src).getCategory() == FeatureCategory.EVENT_PORT;
		}
		ConnectionInstanceEnd dst = conn.getDestination();
		if (dst instanceof FeatureInstance) {
			return ((FeatureInstance) dst).getCategory() == FeatureCategory.EVENT_PORT;
		}
		return false;
	}

	public static EnumerationLiteral getConnectionTiming(final ConnectionInstance conn) {
		try {
			Property timing = GetProperties.lookupPropertyDefinition(conn, CommunicationProperties._NAME,
					CommunicationProperties.TIMING);
			return PropertyUtils.getEnumLiteral(conn, timing);
		}
		catch (PropertyLookupException e) {
			return null;
		}
	}

	public static boolean isDelayedPortConnection(final ConnectionInstance conn) {
		if (isPortConnection(conn)) {
			EnumerationLiteral el = getConnectionTiming(conn);
			final String name = el.getName();
			return name.equalsIgnoreCase(CommunicationProperties.DELAYED);
		}
		return false;
	}

	public static boolean isImmediatePortConnection(final ConnectionInstance conn) {
		if (isPortConnection(conn)) {
			EnumerationLiteral el = getConnectionTiming(conn);
			final String name = el.getName();
			return name.equalsIgnoreCase(CommunicationProperties.IMMEDIATE);
		}
		return false;
	}

	public static ComponentInstance getSrcComponent(final ConnectionInstance conni) {
		ConnectionInstanceEnd srcEnd = conni.getSource();
		if (srcEnd instanceof ComponentInstance) {
			if (((ComponentInstance) srcEnd).getCategory() == ComponentCategory.DATA) {
				return srcEnd.getContainingComponentInstance();
			}
			return (ComponentInstance) srcEnd;
		}
		if (srcEnd instanceof FeatureInstance) {
			return srcEnd.getContainingComponentInstance();
		}
		return null;
	}

	public static FeatureInstance getSrcFeaeture(final ConnectionInstance conni) {
		ConnectionInstanceEnd srcEnd = conni.getSource();
		if (srcEnd instanceof FeatureInstance) {
			return (FeatureInstance) srcEnd;
		}
		return null;
	}

	public static ComponentInstance getDstComponent(final ConnectionInstance conni) {
		ConnectionInstanceEnd dstEnd = conni.getDestination();
		if (dstEnd instanceof ComponentInstance) {
			if (((ComponentInstance) dstEnd).getCategory() == ComponentCategory.DATA) {
				return dstEnd.getContainingComponentInstance();
			}
			return (ComponentInstance) dstEnd;
		}
		if (dstEnd instanceof FeatureInstance) {
			return dstEnd.getContainingComponentInstance();
		}
		return null;
	}

	public static FeatureInstance getDstFeaeture(final ConnectionInstance conni) {
		ConnectionInstanceEnd dstEnd = conni.getSource();
		if (dstEnd instanceof FeatureInstance) {
			return (FeatureInstance) dstEnd;
		}
		return null;
	}
}
