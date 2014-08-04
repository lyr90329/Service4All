package cn.edu.buaa.act.service4all.qualifycontrol.common;

public class Constants 
{
	//used for request	
	public static final String register="register";
	public static final String login="login";
	public static final String logout="logout";
	public static final String deployService="deployService";
	public static final String undeployService="undeployService";
	public static final String deployQualification="deployQualification";
	public static final String undeployQualification="undeployQualification";
	public static final String invokeQualification="invokeQualification";
	public static final String userName="userName";
	public static final String password="password";
	public static final String serviceList="serviceList";
	public static final String serviceId="serviceId";
	public static final String type="type";
	public static final String bpmn="bpmn";
	public static final String webservice="webservice";
	
	//------------------------------------
	public static final String APP_SERVER="appserver";
	public static final String WEBAPP="webapp";
	public static final int SERVICE_TYPE_WEBAPP=2;
	//------------------------------------
	
	//used for response	
	public static final String responseRegister="responseRegister";
	public static final String responseLogin="responseLogin";
	public static final String responseLogout="responseLogout";
	public static final String responseDeployService="responseDeployService";
	public static final String responseUndeployService="responseUndeployService";
	public static final String isSuccessful="isSuccessful";
	public static final String statement="statement";		
	
	//used for convert boolean to string
	public static final String boolean_True="true";
	public static final String boolean_false="false";
	
	//used for database column 
	public static final String state="state";
	
	//user state description in the column of state of user table
	public static final int user_Init=0;
	public static final int user_Login=1;
	public static final int user_Logout=2;
	
	//service type in the column of type of service table
	public static final int serviceType_webservice=0;
	public static final int serviceType_BPMN=1;
	
	public static final int service_Deploy=0;
	public static final int service_Undeploy=1;	
	
	//used for qualification control
	public static final String responseUndeployQualification="responseUndeployQualification";
	public static final String responseDeployQualification="responseDeployQualification";
	public static final String responseInvokeQualification="responseInvokeQualification";
	public static final String qualification="qualification";
	public static final String permit="permit";
	public static final String deny="deny";
	
	public static final int qualification_deploy=0;
	public static final int qualification_undeploy=1;
	public static final int qualification_invoke=2;	
	
	//used for unit storage
	public static final String request="requset";
	public static final String response="response";
}
