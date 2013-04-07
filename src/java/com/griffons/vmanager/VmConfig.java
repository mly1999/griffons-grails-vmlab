package com.griffons.vmanager;

public class VmConfig
{
    public static String getVmwareHostURL() { return "https://cmpe-admin-console.engr.sjsu.edu/sdk" ; }
    public static String getVmwareLogin() { return "student" ; }
    public static String getVmwarePassword() { return "cmpe281@cloud" ; }
    public static String getVmwareVM() { return "" ; } // not used
    
    public static String getDataCenter() { return "CMPE281"; }
    public static String getVmFolder() { return "CMPE281"; }
    
    public static String getHost01(){ return "130.65.157.132"; }
    //public static String getResourcePool01(){ return "130.65.157.132"; }
    public static String getHost02(){ return "130.65.157.137"; }
    //public static String getResourcePool02(){ return "130.65.157.137"; }
    
    public static String getTemplate01(){ return "griffons_ubuntu_12.04_desktop"; }
    public static String getTemplate02(){ return "griffons_linuxmint_14.1_cinnamon_desktop"; }
    public static String getTemplate03(){ return "griffons_peppermint_3_20120722_desktop"; }
}