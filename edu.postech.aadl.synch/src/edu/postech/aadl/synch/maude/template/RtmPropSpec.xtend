
package edu.postech.aadl.synch.maude.template

import edu.postech.aadl.xtext.propspec.propSpec.BinaryExpression
import edu.postech.aadl.xtext.propspec.propSpec.Top
import edu.postech.aadl.xtext.propspec.propSpec.Property
import edu.postech.aadl.xtext.propspec.propSpec.UnaryExpression
import edu.postech.aadl.xtext.propspec.propSpec.Value
import org.osate.aadl2.ContainedNamedElement
import org.osate.aadl2.PropertyValue

import static extension edu.postech.aadl.synch.maude.template.RtmAadlIdentifier.*
import edu.postech.aadl.xtext.propspec.propSpec.Reachability
import edu.postech.aadl.xtext.propspec.propSpec.Invariant
import edu.postech.aadl.xtext.propspec.propSpec.ScopedExpression
import edu.postech.aadl.xtext.propspec.propSpec.Proposition
import edu.postech.aadl.xtext.propspec.propSpec.PropRef
import org.osate.aadl2.NamedElement
import org.osate.aadl2.DataPort
import org.osate.aadl2.DataSubcomponent
import org.osate.ba.aadlba.BehaviorVariable
import org.osate.ba.aadlba.ParameterHolder
import edu.postech.aadl.synch.maude.action.mode.SymbolicMode
import edu.postech.aadl.synch.maude.action.mode.RandomMode
import edu.postech.aadl.synch.maude.action.mode.Mode

class RtmPropSpec {

	static def compilePropertyCommand(Top top, Property prop, String maudeDirPath, Mode mode)'''
	«top.compileLoadFiles(maudeDirPath, mode)»
	mod TEST-«top.name.toUpperCase» is
	  «top.compileIncludedModel(mode)»
	  «mode.compileIncludedAnalysisWay»
	  including SPECIFICATION-LANGUAGE-SEMANTICS .
	
	  op initState : -> Object .
	  eq initState = initialize(collapse(initial)) .
	  
	  eq loopBoundNum = «mode.compileLoopBound» .
	  eq transBoundNum = «mode.compileTransBound» .
	  
	  «mode.compileDefaultParam»
	  
	  «top.compileMerge(mode)»
	  
	  «top.compileProposition»
	  
	endm
		  
	«top.compileProperty(prop, mode)»	  
	
	quit
	'''
	
	static def compileLoadFiles(Top top, String maudeDirPath, Mode mode)'''
	load «maudeDirPath»/prelude.maude
	load «maudeDirPath»/smt.maude
	load ../semantics/«mode.compileInterpreter»
	load ../instance/«top.compileInstanceFile(mode)»
	'''
	
	static def compileInstanceFile(Top top, Mode mode){
		switch (mode){
		RandomMode:				'''«top.name».maude'''
		SymbolicMode:			'''«top.name»-symbolic.maude'''
		default:			''''''
		}
	}
	
	static def compileIncludedModel(Top top, Mode mode){
		switch (mode){
		RandomMode:			'''including «top.name.toUpperCase»-MODEL .'''
		SymbolicMode:		'''including «top.name.toUpperCase»-MODEL-SYMBOLIC .'''
		default:			''''''
		}
	}
	
	static def compileIncludedAnalysisWay(Mode mode){
		switch (mode){
		RandomMode:				'''including RANDOMZIE-OBJECT . 
								   including RANDOM-SIMULATION .'''
		SymbolicMode:			'''including REACHABILITY-ANALYSIS .'''
		default:				''''''
		}
	}
	
	static def compileInterpreter(Mode mode){
		switch (mode){
		RandomMode:				'''interpreter-random.maude'''
		SymbolicMode:			'''interpreter-symbolic2-merge.maude'''
		default:			''''''
		}
	}
	
	static def compileLoopBound(Mode mode){
		switch (mode){
		RandomMode:				'''unbounded'''
		SymbolicMode:			'''«mode.getloopBound»'''
		default:			    ''''''
		}
	}
	
	static def compileTransBound(Mode mode){
		switch (mode){
		RandomMode:				'''unbounded'''
		SymbolicMode:			'''«mode.gettransBound»'''
		default:			    ''''''
		}
	}
	
	
	static def compileDefaultParam(Mode mode){
		if (mode instanceof RandomMode)'''
		  op minParam : -> PropSpec .
		  eq minParam = ParamCompRef | (c[ParamCompId] >= «mode.getMinParamSignedValue.compileParamSign»([[«mode.getMinParamValue»]])) .
		  op maxParam : -> PropSpec .
		  eq maxParam = ParamCompRef | (c[ParamCompId] <= «mode.getMaxParamSignedValue.compileParamSign»([[«mode.getMaxParamValue»]])) .
		'''
		else'''
		'''
	}
	
	static def compileParamSign(float value){
		if(value < 0){
			return "minus"
		} 
		return ""
	}
	
	static def compileMerge(Top top, Mode mode){
		switch (mode){
		SymbolicMode:			'''eq @m@ = ['TEST-«top.name.toUpperCase»] .'''
		default:			''''''
		}
	}
	
	static def compileProposition(Top top)'''
	«FOR Proposition prop : top.getProposition»
	    op «prop.name» : -> PropSpec .
	    eq «prop.name» = «prop.expression.compileExp» .
	«ENDFOR»
	'''	
	
	static def dispatch compileProperty(Top top, Property prop, SymbolicMode mode)'''
    red reachAnalysis(
        initState,
        «top.compileInitConst(prop)»,
        «top.compileGoalConst(prop)»,
        «prop.bound»
        ) .
	'''

	static def dispatch compileProperty(Top top, Property prop, RandomMode mode)'''
	rew repeat({initState,«mode.randomSeed»}, «prop.bound», («top.compileInitConst(prop)» and (minParam and maxParam)), «top.compileGoalConst(prop)») .
	'''
	
	static def compileInitConst(Top top, Property pr)'''
	(«top.name.escape» | «pr.initCond === null ? "[[true]]" : pr.initCond.compileExp»)'''
	
	static def compileGoalConst(Top top, Property pr){
		switch(pr){
			Invariant:			'''«top.name.escape» | not(«pr.goalCond.compileExp»)'''
			Reachability:		'''«top.name.escape» | «pr.goalCond.compileExp»'''
			default:			''''''
		}
	}

	/**
	 *  translate BA expressions
	 */
	private static def dispatch CharSequence compileExp(BinaryExpression e) '''
		(«e.left.compileExp» «e.op» «e.right.compileExp»)'''
		
	private static def dispatch CharSequence compileExp(UnaryExpression e) '''
		«e.op.translateUnaryOp»(«e.child.compileExp»)'''
	
	private static def dispatch CharSequence compileExp(ScopedExpression e)'''
		(«e.path.compileScopedPath» | ( «e.expression.compileExp» ))'''

	private static def dispatch CharSequence compileExp(PropRef e)'''
		«e.def.name»'''

	private static def dispatch CharSequence compileExp(Value e) {
		var v = e.value
		switch v {
			ContainedNamedElement:		'''(«IF v.containmentPathElements.length > 1»«v.compilePath» | «ENDIF»«v.containmentPathElements.last.namedElement.compilePrefix» )'''
			default:					'''[[«RtmAadlProperty::compilePropertyValue(v as PropertyValue)»]]'''
		}
	}
	
	private static def compilePrefix(NamedElement ne){
		switch ne {
			DataPort:					'''f[«ne.name»]'''
			DataSubcomponent:			'''c[«ne.name»]'''
			BehaviorVariable:			'''v[«ne.name»]'''
			ParameterHolder:			'''p[«ne.name»]'''
			default:					null
		}
	}
	
	private static def translateUnaryOp(String op) {
		switch op {
			case "+":	"plus"
			case "-":	"minus"
			default:	op
		}
	}
	
	// an component path except component
	private static def CharSequence compilePath(ContainedNamedElement path){
		path.containmentPathElements.subList(0, path.containmentPathElements.length-1).map[namedElement.name.escape].join(' . ')
	}
	
	private static def CharSequence compileScopedPath(ContainedNamedElement path){
		path.containmentPathElements.subList(0, path.containmentPathElements.length).map[namedElement.name.escape].join(' . ')
	}
}