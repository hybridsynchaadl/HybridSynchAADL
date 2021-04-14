package edu.postech.aadl.xtext.propspec.validation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.xtext.validation.Check;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import edu.postech.aadl.xtext.propspec.propSpec.PropSpecPackage;
import edu.postech.aadl.xtext.propspec.propSpec.Top;
import edu.postech.aadl.xtext.propspec.validation.AbstractPropSpecJavaValidator;


public class PropSpecJavaValidator extends AbstractPropSpecJavaValidator {

	@Check
	public void checkTheInstanceModel(final Top top)
	{
		String path = top.getPath();
		if (path == null || path.isEmpty())
		{
			error("No instance model provided", PropSpecPackage.Literals.TOP__PATH);
			return;
		}
		IResource fr = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (fr == null)
		{
			error("Could't find the instance model", PropSpecPackage.Literals.TOP__PATH);
			return;
		}
		org.osate.aadl2.Element model = AadlUtil.getElement(fr);
		if (model == null || ! (model instanceof SystemInstance))
		{
			error("Invalied instance model provided", PropSpecPackage.Literals.TOP__PATH);
			return;
		}
	}

	@Check
	public void checkIfTheTopInstance(final Top top)
	{
		if ( ! (top.getModel()  instanceof ComponentImplementation) ) {
			error("Not a implementation", PropSpecPackage.Literals.TOP__MODEL);
		}
	}

}
