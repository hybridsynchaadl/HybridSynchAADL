package edu.postech.aadl.synch.propspec;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class PropSpecFileResourceManager extends PropSpecResourceManager {

	public void setPSPCFile(IFile file) {
		pspcFile = file;
		updateResource();
	}

	private void updateResource() {
		IProject project = pspcFile.getProject();
		URI uri = URI.createPlatformResourceURI(pspcFile.getFullPath().toString(), true);
		ResourceSet rs = IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(uri)
				.get(IResourceSetProvider.class).get(project);
		Resource r = rs.getResource(uri, true);
		try {
			r.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		content = (Top) ((XtextResource) r).getParseResult().getRootASTElement();
		setModelResource();
	}
}
