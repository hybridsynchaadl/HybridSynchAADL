package edu.postech.aadl.synch.maude.template

import org.osate.aadl2.ComponentCategory
import org.osate.aadl2.instance.ComponentInstance
import edu.postech.aadl.utils.PropertyUtil

class RtmAadlSetting {

	public static val SEMANTICS_PATH = "semantics";
	
	static def isSync(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:			true 
			case ComponentCategory::PROCESS_VALUE:			true
			case ComponentCategory::THREAD_VALUE:			true
			case ComponentCategory::DATA_VALUE:				true
			case ComponentCategory::THREAD_GROUP_VALUE:		true
			default: false
		}
	}
	
	static def periodic(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:		true 
			case ComponentCategory::PROCESS_VALUE:		true
			case ComponentCategory::THREAD_VALUE:		true
			case ComponentCategory::THREAD_GROUP_VALUE:	true
			default: false
		}
	}
	
	static def behavioral(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::THREAD_VALUE:		true 
			default: false
		}
	}
	
	static def isData(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::DATA_VALUE:			true
			default: false
		}
	}
	
	static def isEnv(ComponentInstance o) {
		PropertyUtil::isEnvironment(o)
	}
	
	static def compClass(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:		o.isEnv ? "Env" : "System" 
			case ComponentCategory::PROCESS_VALUE:		"Process"
			case ComponentCategory::THREAD_VALUE:		"Thread"
			case ComponentCategory::DATA_VALUE:			"Data"
			case ComponentCategory::THREAD_GROUP_VALUE:	"ThreadGroup"
		}
	}	
}
