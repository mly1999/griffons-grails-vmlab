package com.griffons.vmanager;

import java.net.URL;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import java.util.ArrayList;

/**
 * Provides basic tasks to manage a vcenter
 * @author Griffons
 */

public class VmManager
{
    // class instance variables
    private ServiceInstance si;
    private InventoryNavigator rootNav;

    /**
     * Constructor for objects of class VmManager that provides a handle to the 
     * ServiceInstance and an InventoryNavigator to root folder.
     */
    public VmManager()
    {
        try{
            this.si = new ServiceInstance(
                    new URL(VmConfig.getVmwareHostURL()),
                    VmConfig.getVmwareLogin(),
                    VmConfig.getVmwarePassword(),
                    true
            );
            
            Folder rootFolder = si.getRootFolder();
            rootNav = new InventoryNavigator(rootFolder);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        
        if( this.si == null ) {
                System.out.println("Unable to make connection to vm server!");
                this.si.getServerConnection().logout();
        }
    }    
    /**
     * Destructor for objects of class VmManager
     */
    protected void finalize() throws Throwable
    {
       
       if( this.si != null )
       {
           System.out.println("Disconnecting server connection.");
           this.si.getServerConnection().logout();
       }
    }     
    
    /**
     * This methods creates a new virtual machine from a specified template.
     * 
     * @param   vm_name             - string value for new vm name, must be unique
     * @param   template_name       - string value of the template name to be cloned
     * @param   vmfolder            - string value of target folder name
     * @param   hostname            - string value of target host
     * 
     * @return  void
     */
    public boolean cloneVM(String vm_name,
                        String template_name, 
                        String vmfolder,
                        String hostname)
    {        
    	// NOTE TO SELF : need to refactor this to make it cleaner ...
    	
        Folder targetFolder = null;         //reference to target Folder object
        VirtualMachine template = null;     //reference to VirtualMachine object of the template
        VirtualMachine checkName = null;
        
        try{
            template = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine", template_name);
            targetFolder = (Folder) rootNav.searchManagedEntity("Folder",vmfolder);
            checkName = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine", vm_name);
        } catch (Exception e) {
            System.out.println("Error in cloneVM() : code 01 : " + e.toString());
        }
        
        // Performs some checks before proceeding ... may need to clean this up
        if(template==null || targetFolder==null || checkName!=null) {
            if(template==null) { System.out.println("Error: Template name not found!"); }
            if(targetFolder==null) {System.out.println("Error: Target folder for virtual machine not found!"); }
            if(checkName!=null) {System.out.println("Error: The specified virtual machine name is used!"); }
            return false;
        }
        
        // Set properties for VirtualMachineRelocateSpec
        VirtualMachineRelocateSpec relocateSpec = new VirtualMachineRelocateSpec();
        try{           
            HostSystem hs = (HostSystem) rootNav.searchManagedEntity("HostSystem", hostname);
            relocateSpec.setHost( hs.getMOR() );
            // Get the object reference to the target datastore from host system. This code is assuming one datastore for each host.
            relocateSpec.setDatastore( hs.getDatastores()[0].getMOR() );
            
            ManagedEntity[] rps = rootNav.searchManagedEntities("ResourcePool");
            ResourcePool targetRP = null;
            int cnt = 0;
            while(cnt < rps.length && targetRP == null) {
                if( rps[cnt].getName().equals( hostname ) ){
                    targetRP = (ResourcePool) rps[cnt];
                    System.out.println("Target resource found ... ");
                }
                cnt++;
            }
            relocateSpec.setPool( targetRP.getMOR() );
        } catch (Exception e) {
            System.out.println("Error in cloneVM() : code 02 : " + e.toString());
            return false;
        }
        // relocateSpec.setDisk();              // Not used
        // relocateSpec.setDiskMoveType();      // Not used
        
        // Set properties for VirtualMachineCloneSpec
        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(relocateSpec);
        cloneSpec.setPowerOn(false);
        cloneSpec.setTemplate(false);
        //cloneSpec.setSnapshot();              // Property not used
        //cloneSpec.setConfig();                // Property not used
        //cloneSpec.setCustomization();         // Property not used
         
        try{
            Task task = template.cloneVM_Task( targetFolder, vm_name, cloneSpec );
            if(task.waitForTask()==Task.SUCCESS) {
                System.out.println(vm_name + " has been created from template.");
            }
        }catch(Exception ex) {
            System.out.println("Error in cloneVM() : code 03 : " + ex.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Destory/delete/remove the target virtual machine. The virtual machine state must not be powered on.
     * 
     * @param   vm_name       - String value of target virtual machine name
     */
    public boolean destroyVM(String vm_name) {
    	boolean success = true;
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
                System.out.println("The following virtual machine was not found: " + vm_name);
            }
            
            Task task = vm.destroy_Task();
            if(task.waitForTask()==Task.SUCCESS) {
                System.out.println(vm_name + " deleted");
            }
        } catch ( Exception e ) { 
        	System.out.println( e.toString() ) ;
            success = false;
        } 
        
        return success;
    }
    
    /**
     * Power on the target virtual machine. The virtual machine state must be powered off or suspended.
     * 
     * @param   vm_name       - String value of target virtual machine name
     */
    public void powerOnVM(String vm_name) {
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
                System.out.println("The following virtual machine was not found: " + vm_name);
                return;
            }
            
            Task task = vm.powerOnVM_Task(null);
            if(task.waitForTask()==Task.SUCCESS) {
                System.out.println(vm.getName() + " powered on");
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }
      
    /**
     * Power off the target virtual machine. The virtual machine state must be powered on. 
     * If power state is suspended, the virtual machine must be powered on first.
     * 
     * @param   vm_name       - String value of target virtual machine name
     */
    public void powerOffVM(String vm_name) {
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
                System.out.println("The following virtual machine was not found: " + vm_name);
                return;
            }
            
            Task task = vm.powerOffVM_Task();           
            if(task.waitForTask()==Task.SUCCESS) {
                System.out.println(vm.getName() + " powered down");
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }
    
    /**
     * Suspend the target virtual machine. The virtual machine state must be powered on.
     * 
     * @param   vm_name       - String value of target virtual machine name
     */
    public void suspendVM(String vm_name) {
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
                System.out.println("The following virtual machine was not found: " + vm_name);
                return;
            }
            
            Task task = vm.suspendVM_Task();
            if(task.waitForTask()==Task.SUCCESS){
                System.out.println(vm.getName() + " suspended");
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }
    
    /**
     * Reset the target virtual machine. The virtual machine state must be powered on.
     * 
     * @param   vm_name       - String value of target virtual machine name
     */
    public void resetVM(String vm_name) {
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
                System.out.println("The following virtual machine was not found: " + vm_name);
                return;
            }
            
            Task task = vm.resetVM_Task();
            if(task.waitForTask()==Task.SUCCESS){
                System.out.println(vm.getName() + " reset");
            }
        } catch ( Exception e ) 
        { System.out.println( "Error in method resetVM : " + e.toString() ) ; }
    }
    
    /**
     * Cold migrate a target virtual machine.
     * 
     * @param   vm_name             - String value of existing vm to be moved
     * @param   destinationHost     - String value of target host name
     */
    public boolean coldMigrateVM(String vm_name, String destinationHost) {
    	boolean success = true;
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
                System.out.println("The following virtual machine was not found: " + vm_name);
                success = false;
            }
            
            if(vm.getRuntime().getPowerState().toString().equals("poweredOn")) {
                suspendVM(vm_name); // suspend vm if needed  
            }

            HostSystem targetHost = (HostSystem) rootNav.searchManagedEntity("HostSystem", destinationHost);            
            Datastore targetDS = targetHost.getDatastores()[0]; // assume that there is one datastore per host
            
            ManagedEntity[] rps = rootNav.searchManagedEntities("ResourcePool");
            ResourcePool targetRP = null;
            int cnt = 0;
            while(cnt < rps.length && targetRP == null) {
                if( rps[cnt].getName().equals( destinationHost ) ){
                    targetRP = (ResourcePool) rps[cnt];
                    System.out.println("Target resource found ... ");
                }
                cnt++;
            }
            
            
            VirtualMachineRelocateSpec relocateSpec = new VirtualMachineRelocateSpec();
            relocateSpec.setHost( targetHost.getMOR() );
            relocateSpec.setDatastore( targetDS.getMOR() );
            relocateSpec.setPool( targetRP.getMOR() ); // should throw exception if targetRP null
            
            Task task = vm.relocateVM_Task( relocateSpec );
            if(task.waitForTask()==Task.SUCCESS){ 
                System.out.println(vm.getName() + " migrated to the following host: " + destinationHost); 
            }
        } catch(Exception e) {
            System.out.println( e.toString());
            success = false;
        }
        return success;
    }
    
    /**
     * Reconfigure resource allocation for virtual machine. CPU and Memory only
     * *Note: Virtual machine must be in a p
     * 
     * @param   vm_name     - String value of target virtual machine name
     * @param   device      - String value of device. Only 2 options: cpu|memory
     * @param   value       - String value 
     *                          = for CPU: 1, 2, 4
     *                          = for Memory in mb: 512, 1024, 2048, 4096
     * 
     * @return   void
     */
    public void allocateResourceVM(String vm_name, String deviceType, String value) {
        try {
            VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine",vm_name);
            if(vm==null) {
              System.out.println("The following virtual machine was not found: " + vm_name);
              return;
            }
            
            VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();
            
            if("memory".equalsIgnoreCase(deviceType)) {
                /** Test lab has a total mem capacity of about 9698 MB per host **/
                System.out.println("Setting memory for VM [" + vm_name + "] to " + value);
                vmConfigSpec.setMemoryMB( Long.parseLong(value) );
                  
                ResourceAllocationInfo raInfo = new ResourceAllocationInfo();
                raInfo.setReservation(Long.parseLong(value)); // in MB
                //SharesInfo sharesInfo = new SharesInfo();
                //sharesInfo.setLevel(SharesLevel.high); // allocate all resources from virtual hardware
                //raInfo.setShares(sharesInfo);
                vmConfigSpec.setMemoryAllocation( raInfo );  
            }
            else if("cpu".equalsIgnoreCase(deviceType)) {
                /** Test lab has a total cpu capacity of 15853 MHz per host 
                 * 
                 *  Total cpu available is 8 and caculated 1 cpu to be equivalent to 1980 MHz when calculating reservation
                  */
                System.out.println("Setting CPU for VM:  [" + vm_name + "] to " + value + " MHz");
                  
                vmConfigSpec.setNumCPUs( Integer.parseInt(value) );  
                ResourceAllocationInfo raInfo = new ResourceAllocationInfo();
                raInfo.setReservation( Long.parseLong(value) * 1980 );
                vmConfigSpec.setCpuAllocation( raInfo );
            }
            else {
                System.out.println("Incorrect option for " + vm_name);
            }
            
            Task task = vm.reconfigVM_Task(vmConfigSpec);
            if(task.waitForTask()==Task.SUCCESS){ 
                System.out.println("Resource allocated set for " + vm_name); 
            }
        } catch(Exception e) {
            System.out.println( e.toString());
        }
    }
    
    /**
     * Check whether the demanded resourses are available on the server or not.
     * 
     * @param   hostname        - String value containing the name of the server
     * @param   device          - String value for the resource to be checked
     */
    public long getReservationAvailFromHost(String resourcepool, String device)
   {
	   long returnValue = -1;
	   
       try
       {
           ResourcePool resPool = (ResourcePool) rootNav.searchManagedEntity("ResourcePool", resourcepool);
           ResourcePoolRuntimeInfo resInfo = resPool.getRuntime();
           
           if(device.equalsIgnoreCase("cpu"))
           {
               ResourcePoolResourceUsage resCPU = resInfo.getCpu();
//               System.out.println("The Unreserved CPU is : "+ resCPU.getUnreservedForVm());
               returnValue = resCPU.getUnreservedForVm();
           }
           else if(device.equalsIgnoreCase("memory"))
           {
               ResourcePoolResourceUsage resMem = resInfo.getMemory();
//               System.out.println("The Unreserved Memory is : "+ resMem.getUnreservedForVm());
               returnValue = resMem.getUnreservedForVm();
           }
       }
       catch ( Exception e ) 
       { System.out.println( "Error in method getReservationAvailFromHost : " + e.toString() ) ; }
       
       return returnValue;
   }
       
    /**
     * Gets the list of VMs currently present on both the hosts
     * 
     * @return   String[]
     */    
    public ArrayList<String> GetVMList() 
    {
        ArrayList<String> VMList = new ArrayList<String>();
        try {
             //Get the list of all Virtual Machines
             ManagedEntity[] vmList = rootNav.searchManagedEntities("VirtualMachine");
             
             int len=0;
             while(len<vmList.length)
             {
            	 // Display the inventory selectively. We purposely did this for testing ...
                 if(vmList[len].getName().endsWith("-vm")) {
                     VMList.add(vmList[len].getName());
                 }
                 
                 len++;
             }
             
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
        
        return VMList;
    }
        
    public long getCPUReservationVMInfo(String vm_name) {
    	long value = 0;
    	try {
    		VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine", vm_name);
    		value = vm.getConfig().getCpuAllocation().getReservation();
    	} catch(Exception e){
    		System.out.println( e.toString() );
    	}
    	return value;
    }
    
    public long getMEMReservationVMInfo(String vm_name) {
    	long value = 0;
    	try {
    		VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine", vm_name);
    		value = vm.getConfig().getMemoryAllocation().getReservation();
    	} catch(Exception e){
    		System.out.println( e.toString() );
    	}
    	return value;
    }
    
    public String getRPoolNameFromVMInfo(String vm_name) {
    	String resourcepool = "";
    	try {
    		VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine", vm_name);
    		resourcepool = vm.getResourcePool().getSummary().getName();
    	} catch(Exception e){
    		System.out.println( e.toString() );
    	}
    	return resourcepool;
    }
    
    public String getVmPowerState(String vm_name) {
    	String powerstate = "";
    	
    	try {
    	VirtualMachine vm = (VirtualMachine) rootNav.searchManagedEntity("VirtualMachine", vm_name);
    	powerstate = vm.getRuntime().getPowerState().toString();
    	} catch(Exception e) {
    		System.out.println( e.toString() );
    	}
    	
    	return powerstate;
    }
}