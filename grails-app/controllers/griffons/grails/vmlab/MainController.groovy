package griffons.grails.vmlab
import com.griffons.vmanager.*

class MainController {	
	def VmManager vmanager

    def index() { 
		
		String VCAP_SERVICES = System.getenv('VCAP_SERVICES')
		
		if (request.method == "GET") {
			// Creates a VmManager instance
			vmanager = new VmManager()
			
			
			render(view: "index")
		}
		else if (request.method == "POST") {
			vmanager = new VmManager()
			
			
			// dump of params
			params?.each { key, value -> println( "params: $key = $value" ) }
			
			if ( params?.event == "CreateVm" ) // NAME OF ACTION
			{	
				
				println "testing"
				
				//def range = 'a'..'m'
				def vmname = params?.vmname
				//def targetHost = range.contains(vmname[0].toLowerCase()) ? VmConfig.getHost01() : VmConfig.getHost02()  
				
				// DO ACTION
				//def clonesuccess = 
				vmanager.cloneVM(vmname, params?.temp, VmConfig.getVmFolder(), VmConfig.getHost02())
				
				// set resource reservation
				if(clonesuccess) {
					
				}
				else {
					// Let the user know it was not created successfully 
				}
				
				
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
//			if ( params?.event == "CreateVM" ) // NAME OF ACTION
//			{

	
//				// DO ACTION
//			}		
			
			render(view: "index") // MAY NEED TO UPDATE DEPENDING NAME OF PAGE IF DIFFERENT
		}
		
		else { render(view: "/error") }
	}
}
