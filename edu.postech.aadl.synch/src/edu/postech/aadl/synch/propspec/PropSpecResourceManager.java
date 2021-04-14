package edu.postech.aadl.synch.propspec;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class PropSpecResourceManager {

	protected Top content = null;
	protected IFile pspcFile = null;
	protected IResource modelRes = null;


	public Top getContent() {
		return this.content;
	}

	public IFile getPSPCFile() {
		return pspcFile;
	}

	public IPath getCodegenFilePath() {
		if (pspcFile != null && getModelResource() != null) {
			SystemInstance si = (SystemInstance) AadlUtil.getElement(getModelResource());
			IPath context = pspcFile.getFullPath().removeLastSegments(2).append("verification").append("instance");
			return IOUtils.getCodegenPath(context, si);
		}
		return null;
	}

	public IResource getModelResource() {
		return modelRes;
	}

	protected void setModelResource() {
		String path = content.getPath();
		modelRes = (path != null) ? IOUtils.getResource(new Path(path)) : null;
	}

}
