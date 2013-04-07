package griffons.grails.vmlab
import com.griffons.vmanager.*

class MainController {	
	def VmManager vmanager

    def index() { 
		
		String VCAP_SERVICES = System.getenv('VCAP_SERVICES')
		
		if (request.method == "GET") {
			
			/// get list of vm in inventory
			
			
			render(view: "index")
		}
		else if (request.method == "POST") {
			vmanager = new VmManager()
			
			
			// dump of params
			params?.each { key, value -> println( "params: $key = $value" ) }
			
			if ( params?.event == "CreateVm" ) // NAME OF ACTION
			{
				def range = 'a'..'l'
				def vmname = params?.vmname
				def targetHost = range.contains(vmname[0].toLowerCase()) ? VmConfig.getHost01() : VmConfig.getHost02()  
				
				def clonesuccess = vmanager.cloneVM(vmname, params?.temp, VmConfig.getVmFolder(), targetHost)
				
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
			
			
			
			
			
			vmanager.finalize();
			
			render(view: "index")
		}
		
		else { render(view: "/error") }
	}
}
