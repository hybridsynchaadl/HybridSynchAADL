package edu.postech.aadl.utils;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;

public class IOUtils {
	

	public static IFile getFile(IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}
	
	public static IFile getFile(Resource resource) {
		return IOUtils.getFile(new Path(resource.getURI().toPlatformString(true)));
	}
	
	public static IResource getResource(IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	}
	
	public static IPath getCodegenPath(IPath context, SystemInstance si) {
		return context.append(si.getName()).addFileExtension("maude");
	}
	
	
	public static void setFileContent(InputStream content, IFile file) throws CoreException {
		if (file.exists()) {
			file.setContents(content, true, true, null);
		} else {
			AadlUtil.makeSureFoldersExist(file.getFullPath());
			file.create(content, true, null);
		}
		file.setDerived(true, null);
		file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
	}

}
