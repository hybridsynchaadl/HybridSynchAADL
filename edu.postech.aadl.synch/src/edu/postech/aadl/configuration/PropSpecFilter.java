package edu.postech.aadl.configuration;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

final public class PropSpecFilter extends ViewerFilter {
	@Override
	public boolean select(Viewer viewer, Object parentElm, Object elm) {
		if (elm instanceof IResource) {
			if (((IResource) elm).getType() == IResource.FILE) {
				return isPropSpecFile((IResource) elm);
			}
			if (elm instanceof IContainer) {
				try {
					if (((IContainer) elm).isAccessible()) {
						for (IResource mem : ((IContainer) elm).members()) {
							if (select(viewer, elm, mem)) {
								return true;
							}
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	// org.osate.workspace.IResourceUtility package is deleted in osate2 version 2.8.0
	private boolean isPropSpecFile(IResource file) {
		if (file instanceof IFile) {
			return (file.getName().endsWith(".pspc"));
		}
		return false;
	}

}


