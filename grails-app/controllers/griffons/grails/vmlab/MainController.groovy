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
					vmanger.allocateResourceVM(vmname, "cpu", params?.cpu)
					vmanger.allocateResourceVM(vmname, "memory", params?.memory )
				}
				else {
					 // print error message here
				}
			}
			
			if ( params?.event == "deployVm" ) // NAME OF ACTION
			{
				boolean availableResource = true;
				boolean coldMigrate = false;
				
				def currHost = vmanager.getVMRPInfo(vmname)
				def otherHost = currHost == VmConfig.getHost01() ? VmConfig.getHost02: VmConfig.getHost01()
				
				def vmname = params?.vmname
				List configSpec getVMInfo(vmname)
				
	
				if(configSpec[0] > vmanager.getReservationAvailFromHost(currHost, "cpu") )
				{
					println "Not enought cpu resource"
					availableResource = false
				}		

				if(configSpec[1] > vmanager.getReservationAvailFromHost(currHost, "memory") {
					println "Not enough memory resource"
					availableResource = false
				}
				
				
				if(!availableResource) {
					
					availableResource = true
					if(configSpec[0] > vmanager.getReservationAvailFromHost(otherHost, "cpu") ) {
						println "Not enought cpu resource"
						availableResource = false
					} 
					
					if(configSpec[1] > vmanager.getReservationAvailFromHost(otherHost, "memory")) {
						println "Not enough memory resource"
						availableResource = false
					}
					
					if(availableResource) {
				     // cold migrate here
						def success = vmanager.coldMigrateVM(vmname, otherHost)
						vmanager.powerOnVM(vmname)
					}
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
