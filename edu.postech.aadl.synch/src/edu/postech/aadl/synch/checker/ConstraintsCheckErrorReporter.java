package edu.postech.aadl.synch.checker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.AadlConstants;
import org.osate.aadl2.modelsupport.Activator;
import org.osate.aadl2.modelsupport.errorreporting.AbstractAnalysisErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterFactory;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;

public class ConstraintsCheckErrorReporter extends AbstractAnalysisErrorReporter {
	/** The name of the marker type to use. */
	private final String markerType;

	/** The IResource to attach the markers to. */
	private final IResource iResource;

	public ConstraintsCheckErrorReporter(final Resource rsrc, final IResource irsrc, final String mType) {
		super(rsrc);
		iResource = irsrc;
		markerType = mType;
	}

	private void createMarker(final Element where, final String message, final int severity, final String[] attrs,
			final Object[] values) {
		if (iResource.exists()) {
			try {
				IMarker marker_p = iResource.createMarker(markerType);
				marker_p.setAttribute(IMarker.SEVERITY, severity);
				marker_p.setAttribute(IMarker.MESSAGE, message);
				marker_p.setAttribute(AadlConstants.AADLURI, EcoreUtil.getURI(where).toString());

				for (int i = 0; i < attrs.length; i++) {
					marker_p.setAttribute(attrs[i], values[i]);
				}
			} catch (CoreException e1) {
				Activator.logThrowable(e1);
			}
			return;
		} else {
			Activator.logErrorMessage("Couldn't find IResource.");
		}
	}

	@Override
	protected void errorImpl(final Element where, final String message, final String[] attrs, final Object[] values) {
		createMarker(where, message, IMarker.SEVERITY_ERROR, attrs, values);
	}

	@Override
	protected void warningImpl(final Element where, final String message, final String[] attrs, final Object[] values) {
		createMarker(where, message, IMarker.SEVERITY_WARNING, attrs, values);
	}

	@Override
	protected void infoImpl(final Element where, final String message, final String[] attrs, final Object[] values) {
		createMarker(where, message, IMarker.SEVERITY_INFO, attrs, values);
	}

	@Override
	protected void deleteMessagesImpl() {
		if (iResource.exists()) {
			try {
				iResource.deleteMarkers(markerType, false, IResource.DEPTH_INFINITE);
			} catch (final CoreException e1) {
				Activator.logThrowable(e1);
			}
		}
	}

	public static final class Factory implements AnalysisErrorReporterFactory {
		/** The name of the marker type to use. */
		private final String markerType;

		/**
		 * Secondary factory to use in case an IResource cannot be found.
		 * Allowed to be <code>null</code>.
		 */
		private final AnalysisErrorReporterFactory secondaryFactory;

		public Factory(final String mt, final AnalysisErrorReporterFactory sndFact) {
			markerType = mt;
			secondaryFactory = sndFact;
		}

		public Factory(final String mt) {
			this(mt, null);
		}

		@Override
		public AnalysisErrorReporter getReporterFor(final Resource rsrc) {
			if (rsrc == null) {
				throw new IllegalArgumentException(
						"Cannot create a MarkerAnalysisErrorReporter when the Resource is null");
			}
			final IFile irsrc = OsateResourceUtil.toIFile(rsrc.getURI());
			if (irsrc.exists()) {
				return new ConstraintsCheckErrorReporter(rsrc, irsrc, markerType);
			} else {
				// Try the secondary factory
				if (secondaryFactory != null) {
					return secondaryFactory.getReporterFor(rsrc);
				} else {
					throw new IllegalArgumentException("Couldn't find IResource");
				}
			}
		}
	}

}
