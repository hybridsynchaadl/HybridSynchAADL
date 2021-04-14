package edu.postech.aadl.synch.maude.template

import com.google.common.collect.HashMultimap
import com.google.common.collect.SetMultimap
import edu.postech.aadl.synch.maude.contspec.ContFunc
import edu.postech.aadl.synch.maude.contspec.ContSpec
import edu.postech.aadl.synch.maude.contspec.ContSpecItem
import edu.postech.aadl.utils.PropertyUtil
import java.util.HashSet
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.emf.common.util.EList
import org.osate.aadl2.ConnectedElement
import org.osate.aadl2.DefaultAnnexSubclause
import org.osate.aadl2.DirectionType
import org.osate.aadl2.ModalPropertyValue
import org.osate.aadl2.NamedElement
import org.osate.aadl2.ParameterConnection
import org.osate.aadl2.Port
import org.osate.aadl2.PortConnection
import org.osate.aadl2.PropertyAssociation
import org.osate.aadl2.StringLiteral
import org.osate.aadl2.instance.ComponentInstance
import org.osate.aadl2.instance.ConnectionInstance
import org.osate.aadl2.instance.ConnectionReference
import org.osate.aadl2.instance.FeatureInstance
import org.osate.aadl2.instance.ModeInstance
import org.osate.aadl2.instance.ModeTransitionInstance
import org.osate.aadl2.instance.SystemInstance
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager
import org.osate.ba.aadlba.BehaviorAnnex
import org.osate.ba.aadlba.BehaviorVariable
import org.osate.ba.aadlba.BehaviorVariableHolder

import static extension edu.postech.aadl.synch.maude.template.RtmAadlSetting.*
import static extension edu.postech.aadl.utils.PropertyUtil.*
import java.util.ArrayList
import org.osate.aadl2.Mode

class RtmAadlModel extends RtmAadlIdentifier {

	val RtmAadlBehaviorLanguage bc;
	val RtmAadlProperty pc;
	val IProgressMonitor monitor;
	val HashMultimap<ComponentInstance, ConnectionReference> conxTable = HashMultimap::create() ;


	new(IProgressMonitor pm, AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		super(errMgr, opTable)
		this.monitor = pm;
		this.bc = new RtmAadlBehaviorLanguage(errMgr, opTable);
		this.pc = new RtmAadlProperty(errMgr, opTable);
	}

	def doGenerate(SystemInstance model) {
		val initialState = model.compileComponent
		'''
			mod «model.name.toUpperCase»-MODEL is
				including MODEL-TRANSITION-SYSTEM .
				
				--- AADL identifiers
				«generateIds»
				--- the initial state
				op initial : -> Object .
				eq initial = «initialState» .
			endm
		'''
	}
	
	def doGenerateForSymbolic(SystemInstance model)
	'''
		load «model.name».maude
		mod «model.name.toUpperCase»-MODEL-SYMBOLIC is
			including «model.name.toUpperCase»-MODEL .
			including BEHAVIOR-SYMBOLIC-LOCATION .
			
			«generateLocConst»
			
			«generateLocName»
		endm
	'''
	
	def generateLocConst()
	'''
		«var idx=0»
		«FOR String name : getIdsOfType("Location")»
		eq «name» = loc(real(«idx++»)) .
		«ENDFOR»
		eq @@default@loc@@ = loc(real(«idx++»)) .
	''' 
	
	def generateLocName()
	'''
		«var idx=0»
		«FOR String name : getIdsOfType("Location")»
		eq locName(loc([(«idx++»/1).Real])) = '«name» .
		«ENDFOR»
		eq locName(loc([(«idx++»/1).Real])) = '@@default@loc@@ .
	'''

	
	private def CharSequence compileComponent(ComponentInstance o) {
		if (monitor.isCanceled()) // when canceled
			throw new OperationCanceledException
		else {
			
			val anxSub = o.componentClassifier.ownedAnnexSubclauses.filter(typeof(DefaultAnnexSubclause)) => [
				o.check((! o.behavioral) || ! it.empty,
					"No behavior annex definition in thread: " + o.category.getName() + " " + o.name)
			]
			val behAnx = if(o.behavioral && ! (anxSub.empty)) anxSub.get(0).parsedAnnexSubclause as BehaviorAnnex

			
			o.connectionInstances.forEach[connectionReferences.forEach[conxTable.put(context, it)]]	

			'''
			< «o.id("ComponentId")» : «o.compClass» |
				features : (
					«o.featureInstances.map[compileFeature].filterNull.join('\n', "none")»),
				subcomponents : (
					«o.componentInstances.filter[isSync].map[compileComponent].filterNull.join('\n',"none")»),
				«IF o.isData»	
				value : «o.compileValue»,
				«ENDIF»
				«IF o.behavioral && ! (behAnx === null)»
				currState : (
					«behAnx.states.filter[isInitial].get(0).id("Location")»),
				completeStates : (
					«behAnx.states.filter[isComplete].map[id("Location")].join(' ', "empty")»),
				variables : (
					«behAnx.variables.map[compileVariables].join(' ; ', "empty")»),
				transitions : (
					«behAnx.transitions.map[bc.compileTransition(it)].filterNull.join(' ;\n', "empty")»),
				loopBound : (
					loopBoundNum),
				transBound : (
					transBoundNum),
				varGen : (
					< "«o.compileVarGenName»" >
					),
				«ENDIF»
				«IF o.isEnv»
				currMode : (
					«o.modeInstances.compileCurrentMode»
					),
				jumps : (
					«o.modeTransitionInstances.map[compileJumps].filterNull.join(' ;\n', 'empty')»
					),
				flows : (
					«o.compileModalPropertyValue.map[compileFlows(o)].filterNull.join(" ;\n", "empty")»
					),
				sampling : (
					«o.compileTargetInstances.map[compileSamplingTime].filterNull.join(" ,\n", "empty")»
					),
				response : (
					«o.compileTargetInstances.map[compileResponseTime].filterNull.join(" ,\n", "empty")»
					),
				varGen : (
					< "«o.compileVarGenName»" >
					),
				«ENDIF»
				properties : (
					«o.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")»),
				connections : (
					«conxTable.get(o).map[compileConnection(o)].filterNull.join(' ;\n', "empty")») 
				> ''' => [
				monitor.worked(1)
			]	
		}
	}

	private def compileFeature(FeatureInstance fi) {
		switch fi.feature {
			Port: '''
			< «fi.feature.id("FeatureId")» : «fi.compilePortType» | 
				«IF fi.isEnvContainer»
					«fi.compileEnvFeatureAttrs»
				«ELSE»
					«fi.compileDataFeatureAttrs»
				«ENDIF»
				content : «fi.compileOutFeature» ,
				properties : «fi.ownedPropertyAssociations.map[compilePropertyAssociation(fi)].filterNull.join(' ;\n', "none")» >'''
			default:
				null => [fi.check(false, "Unsupported feature: " + fi.category.getName() + " " + fi.name)]
		}
	}
	
	private def isEnvContainer(FeatureInstance fi){
		fi.containingComponentInstance.isEnv
	}
	
	private def compileDataFeatureAttrs(FeatureInstance fi){
		switch (fi.feature as Port).direction {
			case IN: '''cache : null(Real),'''
			case OUT: ''''''
			default:
			 null => [fi.check(false, "Unsupported feature: " + fi.category.getName() + " " + fi.name)]
		}
	}
	
	private def compileEnvFeatureAttrs(FeatureInstance fi){
		var co = fi.containingComponentInstance 
		'''
		target : «fi.compileTarget(co)»,
		envCache : «fi.compileOutFeature»,
		'''
	}
	
	private def compileTarget(FeatureInstance fi, ComponentInstance ci){
		for(ConnectionInstance conn : ci.getAllEnclosingConnectionInstances()){
			if(fi.direction.incoming && PropertyUtil::getSrcComponent(conn).equals(ci)){
				return PropertyUtil::getDstComponent(conn).name
			} else if(fi.direction.outgoing && PropertyUtil::getDstComponent(conn).equals(ci)){
				return PropertyUtil::getSrcComponent(conn).name
			}
		}
		null => [ci.check(false, "Unknown target: " + ci.category.getName() + " " + ci.name)]
	}
	
	private def compileOutFeature(FeatureInstance fi){
		switch fi.category {
			case DATA_PORT: 	 	"null(Real)"
			case EVENT_DATA_PORT: 	"null(Real)"
			case EVENT_PORT:  		"null(Unit)"
			default :  				null => [fi.check(false, "Unsupported feature: " + fi.category.name() + " " + fi.name)]
		}
	}
	
	private def compilePortType(FeatureInstance fi){
		'''«fi.containingComponentInstance.isEnv ? "Env" : "Data"»«(fi.feature as Port).direction.compileDirection(fi)»Port'''
	}

	private def compileDirection(DirectionType type, FeatureInstance o) {
		o.check(! (type.incoming && type.outgoing), "'in out' features are not supported")
		'''«IF type.incoming»In«ENDIF»«IF type.outgoing»Out«ENDIF»'''
	}
	
	private def compileValue(ComponentInstance o){
		switch o.subcomponent.subcomponentType.name {
			case "Float":		o.isParameterized ? "param(Real)" : "null(Real)"
			case "Real":		o.isParameterized ? "param(Real)" : "null(Real)"
			case "Boolean":		o.isParameterized ? "param(Boolean)" : "null(Boolean)"
			default : 			null => [o.check(false, "Unsupported data type: " + o.category.name() + " " + o.name)]
		}
	}
	
	private def compileVariables(BehaviorVariable bv){
		'''( «bv.id("VarId")» : Real )'''
	}
	
	private def compileVarGenName(ComponentInstance o){
		if(o.getContainingComponentInstance === null)
			return o.name.escape
		return compileVarGenName(o.getContainingComponentInstance) +"."+ o.name.escape
	}
	
	private def compileCurrentMode(EList<ModeInstance> mi){
		mi.forEach[ element | element.name.id("Location")]
		if(mi.isEmpty){
			return "@@default@loc@@"
		}
		for(ModeInstance value : mi){
			if(value.initial==true){
				return value.name.escape
			}
		}
		return mi.get(0).name.escape
	}
	
	private def compileJumps(ModeTransitionInstance mti){
		'''(«mti.source.name.escape» -[ («mti.modeTransition.ownedTriggers.map[it.triggerPort.name.escape].filterNull.join(" , ", "[[true]]")») ]-> «mti.destination.name.escape»)'''
	}
	
	private def compileModalPropertyValue(ComponentInstance o){
		for(PropertyAssociation pa : o.ownedPropertyAssociations){
			if(pa.property.qualifiedName().contains(PropertyUtil::CONTINUOUSDYNAMICS)){
				return pa.ownedValues
			}
		}
	}
	
	private def compileFlows(ModalPropertyValue mpv, ComponentInstance o){
		var mode = o.compileMode(mpv)
		var expr = (mpv.ownedValue as StringLiteral).value
		var spec = ContSpec::parse(expr, o)
		for(ContSpecItem item : spec.items){
			if(item instanceof ContFunc){
				(item.param as BehaviorVariableHolder).behaviorVariable.name.id("VarId")
			}
		}
		
		return "((" + mode.escape + ")" + "[" + bc.compileContSpec(spec)+" ] )"
	}
	
	private def compileMode(ComponentInstance o, ModalPropertyValue mpv){
		if(!mpv.inModes.isEmpty){
			for(Mode mode : mpv.inModes){
				
				for(String name : mode.toString.split("#")){
					if(!o.containingComponentInstance.isTopComponent && name.contains(o.containingComponentInstance.name + "." + o.name)){
						return name.split("\\.").last
					}
					if(o.containingComponentInstance.isTopComponent && name.contains(o.name)){
						return name.split("\\.").last;
					}
				}
			}

		} else {
			return "@@default@loc@@"
		}
	}
	
	private def compileTargetInstances(ComponentInstance ci){
		var targetInstances = new HashSet<ComponentInstance>();
		for(ConnectionInstance conn : ci.getAllEnclosingConnectionInstances()){
			if(PropertyUtil::getSrcComponent(conn).equals(ci)){
				targetInstances.add(PropertyUtil::getDstComponent(conn))
			}
		}
		return targetInstances
	}
	
	private def compileSamplingTime(ComponentInstance o) {
		var value = "(" + o.name + " : ("
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.contains(PropertyUtil::SAMPLING_TIME)){
				var time = pc.compilePropertyValue(p.property, o).toString
				return value += "rat("+time.split(" ").get(0)+"),rat("+time.split(" ").get(2)+")))"
			}
		}
		return null;
	}
	
	private def compileResponseTime(ComponentInstance o) {
		var value = "(" + o.name + " : ("
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.equals(PropertyUtil::RESPONSE_TIME)){
				var time = pc.compilePropertyValue(p.property, o).toString
				return value += "rat("+time.split(" ").get(0)+"),rat("+time.split(" ").get(2)+")))"
			}
		}
		return null;
	}

	private def CharSequence compileConnection(ConnectionReference cr, ComponentInstance ci){
		val c = cr.connection => [
			cr.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type")
		]
		'''(«c.source.compileConnectionEndName(cr)» «cr.compileConnectionArrow(ci)» «c.destination.compileConnectionEndName(cr)»)'''
	}
	
	private def compileConnectionArrow(ConnectionReference cr, ComponentInstance ci){
		ci.isEnv ? '''«IF cr.destination instanceof ComponentInstance»=>>«ELSE»==>«ENDIF»''' : '''-->'''
	}

	private def compileConnectionEndName(ConnectedElement end, ConnectionReference o) {
		switch end {
			ConnectedElement: '''«IF end.context !== null»«end.context.name.escape» .. «ENDIF»«end.connectionEnd.name.escape»'''
			default:
				null => [o.check(false, "Unsupported connection end")]
		}
	}
	
	private def compilePropertyAssociation(PropertyAssociation p, NamedElement ne) {
		val value = pc.compilePropertyValue(p.property, ne)
		if (value !== null && !value.equals("param")) '''(«p.property.qualifiedName().escape» => {{«value.toString.replace("\n", "")»}})'''
	}


	private def compileInitialValue(NamedElement ne, String none) {
		val iv = ne.getSynchStringProp(PropertyUtil::DATA_MODEL, PropertyUtil::INITIAL_VALUE, null)
		if(! iv.nullOrEmpty) "[[" + iv + "]]" else none 
	}
}