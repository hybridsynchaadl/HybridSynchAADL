package edu.postech.aadl.xtext.propspec.linking;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.nodemodel.INode;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AbstractSubcomponent;
import org.osate.aadl2.BehavioredImplementation;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentPrototype;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.FeatureGroupPrototype;
import org.osate.aadl2.FeatureGroupType;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubprogramSubcomponent;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.modelsupport.ResolvePrototypeUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import edu.postech.aadl.xtext.propspec.propSpec.ScopedExpression;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class PropSpecLinkingService extends DefaultLinkingService {

	/**
	 * returns the first linked object
	 */
	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference reference, INode node)
			throws IllegalNodeException {
		final EClass requiredType = reference.getEReferenceType();
		if (requiredType == null) {
			return Collections.<EObject> emptyList();
		}

		final String crossRefString = getCrossRefNodeAsString(node);

		if (Aadl2Package.eINSTANCE.getNamedElement() == requiredType && context instanceof ContainmentPathElement) {
			EObject res = null;

			if (context.eContainer() instanceof ContainmentPathElement) { // inside a path

				ContainmentPathElement el = (ContainmentPathElement) ((ContainmentPathElement) context).getOwner();
				res = findNamedObject(el, crossRefString);
			} else { // inside an expression

				EObject container = context.eContainer().eContainer().eContainer();
				ContainmentPathElement el = getContainingPathElement(container);
				if (el == null) {
					ComponentClassifier ns = getContainingModelClassifier(context);
					if (ns != null) {
						res = ns.findNamedElement(crossRefString);
					}
				} else {
					res = findNamedObject(el, crossRefString);
				}
			}
			if (res != null && res instanceof NamedElement) {
				return Collections.singletonList(res);
			}
		}

		return super.getLinkedObjects(context, reference, node);
	}

	private static ComponentClassifier getContainingModelClassifier(EObject element) {
		EObject container = element;
		while (container != null) {
			if (container instanceof Top) {
				return ((Top) container).getModel();
			}
			container = container.eContainer();
		}
		return null;
	}

	private static ContainmentPathElement getContainingPathElement(EObject element) {
		EObject container = element;
		while (container != null) {
			if (container instanceof ScopedExpression) {
				ContainedNamedElement path = ((ScopedExpression) container).getPath();
				List<ContainmentPathElement> list = path.getContainmentPathElements();
				return list.get(list.size() - 1);
			}
			container = container.eContainer();
		}
		return null;
	}

	// find an element in namespace of a given ContainmentPathElement.
	private EObject findNamedObject(ContainmentPathElement el, String crossRefString) {
		EObject res = null;
		NamedElement ne = el.getNamedElement();

		if (ne instanceof Subcomponent) {
			Subcomponent subcomponent = (Subcomponent) ne;
			while (subcomponent.getSubcomponentType() == null && subcomponent.getRefined() != null) {
				subcomponent = subcomponent.getRefined();
			}
			ComponentClassifier ns = null;
			if (subcomponent.getSubcomponentType() instanceof ComponentClassifier) {
				ns = (ComponentClassifier) subcomponent.getSubcomponentType();
			} else if (subcomponent.getSubcomponentType() instanceof ComponentPrototype) {
				ns = ResolvePrototypeUtil
						.resolveComponentPrototype((ComponentPrototype) subcomponent.getSubcomponentType(), el);
			}
			if (ns != null) {
				res = ns.findNamedElement(crossRefString);
				if (res == null && (ne instanceof ThreadSubcomponent || ne instanceof SubprogramSubcomponent
						|| ne instanceof AbstractSubcomponent) && ns instanceof BehavioredImplementation) {
					res = AadlUtil.findNamedElementInList(((BehavioredImplementation) ns).subprogramCalls(),
							crossRefString);
				}
			}
		} else if (ne instanceof FeatureGroup) {
			FeatureGroup featureGroup = (FeatureGroup) ne;
			while (featureGroup.getFeatureType() == null && featureGroup.getRefined() instanceof FeatureGroup) {
				featureGroup = (FeatureGroup) featureGroup.getRefined();
			}
			FeatureGroupType ns = null;
			if (featureGroup.getFeatureType() instanceof FeatureGroupType) {
				ns = (FeatureGroupType) featureGroup.getFeatureType();
			} else if (featureGroup.getFeatureType() instanceof FeatureGroupPrototype) {
				ns = ResolvePrototypeUtil
						.resolveFeatureGroupPrototype((FeatureGroupPrototype) featureGroup.getFeatureType(), el);
			}
			if (ns != null) {
				res = ns.findNamedElement(crossRefString);
			}
		}
		return res;
	}
}
