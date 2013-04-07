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
			// dump of params
			params?.each { key, value -> println( "params: $key = $value" ) }
			
			vmanager = new VmManager()
	
			if ( params?.event == "CreateVm" ) // NAME OF ACTION
			{
				// MAY REFACTOR TO MAKE THE LOGIC MORE EFFICIENT TO SELECT THE INITIAL HOST FOR VM
				// virtual machine is assign to a host/resource pool based off first character of name 
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
					 println "Unable to create virtual machine from template: " + vmname
				}
			}
			
			if ( params?.event == "deployVm" ) {
				boolean deployOnCurrHost = true
				boolean coldMigrate = false
				
				def vmname = params?.vmname
				def currHost = vmanager.getRPoolNameFromVMInfo(vmname)
				def otherHost = currHost == VmConfig.getHost01() ? VmConfig.getHost02() : VmConfig.getHost01()
				
				def reservedCPU = vmanager.getCPUReservationVMInfo(vmname)
				def reservedMem = vmanager.getMEMReservationVMInfo(vmname)
				
				if( reservedCPU > vmanager.getReservationAvailFromHost(currHost, "cpu") ) {
					println "Not enought cpu resource on host " + currHost
					deployOnCurrHost = false
				}		
				if( reservedMem > vmanager.getReservationAvailFromHost(currHost, "memory") ) {
					println "Not enough memory resource on host " + currHost
					deployOnCurrHost = false
				}
				
				if(!deployOnCurrHost) {
					coldMigrate = true
					if(reservedCPU > vmanager.getReservationAvailFromHost(otherHost, "cpu") ) {
						println "Not enought cpu resource on host " + otherHost
						coldMigrate = false
					} 
					if(reservedMem > vmanager.getReservationAvailFromHost(otherHost, "memory")) {
						println "Not enough memory resource on host " + otherHost
						coldMigrate = false
					}
					
					if(coldMigrate) {
						println "Begin cold migrating ... "
						def success = vmanager.coldMigrateVM(vmname, otherHost)
					}
				}
				
				// power up machine 
				if( deployOnCurrHost || coldMigrate) { 
					vmanager.powerOnVM(vmname) 
				}
				else { 
					println "Unable to provision resource to power on virtual machine " + vmname 
				}
			}
			
			/*
			 * Template below to create events
			 */
/*
			if ( params?.event == "[ACTION_NAME]" )
			{	
				// [CODE TO DO ACTION]
			}		
 */
			vmanager.finalize();
			render(view: "index")
		}
		else { render(view: "/error") }
	}
}
