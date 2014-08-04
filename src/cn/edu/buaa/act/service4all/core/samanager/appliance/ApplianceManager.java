/*
*
* Service4All: A Service-oriented Cloud Platform for All about Software Development
* Copyright (C) Institute of Advanced Computing Technology, Beihang University
* Contact: service4all@act.buaa.edu.cn
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3.0 of the License, or any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*
*/
package cn.edu.buaa.act.service4all.core.samanager.appliance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerComponent;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceRegisterEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceUndeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceUnregisterEvent;

/**
 * This class manages all the registered appliances
 * 
 * @author Huangyj
 * 
 */
public class ApplianceManager implements ApplianceListener {

	private final Log logger = LogFactory.getLog( ApplianceManager.class );

	/**
	 * applianceID -----> appName List
	 * 
	 */
	protected Map<String, List<String>> deployedApps = new HashMap<String, List<String>>();

	protected Map<String, Appliance> appliances = new HashMap<String, Appliance>();// ¼üÊÇID

	protected Map<String, Calendar> updates = new HashMap<String, Calendar>();

	protected AppManager appManager;

	protected SAManagerComponent cmp;


	public SAManagerComponent getCmp() {
		return cmp;
	}


	public void setCmp( SAManagerComponent cmp ) {
		this.cmp = cmp;
	}


	public ApplianceManager() {
		super();
	}


	// ///////////////////The Appliance Listener's operations////////////////

	public void setAppManager( AppManager appManager ) {
		this.appManager = appManager;
	}


	public void logAppliances() {

		synchronized (SAManagerComponent.lock) {
			logger.info( "************* Log all the "
					+ this.getClass().getSimpleName()
					+ " appliances ************************" );
			Iterator<String> appNames = appliances.keySet().iterator();

			while (appNames.hasNext()) {
				String appName = appNames.next();
				Appliance app = appliances.get( appName );
				if (app != null) {
					logger.info( "The App[" );
					logger.info( "\t the appliance id: "
							+ app.getDesp().getId() );

					// List<AppRepetition> rs = app.getBackups();
					// logger.info("\t------ " + rs.size() +
					// "AppRepetitions---------");
					// for(AppRepetition r : rs){
					// String applianceId = r.getContainerId();
					// logger.info("\t one AppRepetition : " + applianceId);
					// }
					// logger.info("\t----------------------------");
				}
			}
			logger.info( "*************************************" );
		}
	}


	@Override
	public void registerAppliance( ApplianceRegisterEvent event )
			throws ApplianceException {

		logger.warn( "Just do nothing for appliance manager except host manager" );

		//
		// Host host = event.getTargetAppliance();
		// logger.info("Receiving the Host Register Event : " + host);
		// //update the hostedApps
		//
		//
		// List<AppRepetition> reps = (List<AppRepetition>) event.getHosteds();
		//
		//
		// }else{
		// logger.warn("The registered appliance type is unsupported: "
		// + appliance.getClass().getSimpleName());
		// }

	}


	protected void updateAppliance( Appliance appliance, Calendar now ) {
		String applianceId = appliance.getDesp().getId();
		// addHostedApps(applianceId, appliance.getStatus());

		synchronized (SAManagerComponent.lock) {
			appliances.put( applianceId, appliance );
			updateTimestamp( applianceId, now );
		}

	}


	public void updateAppsFromAppliance( String applianceId,
			List<AppReplica> reps ) {
		synchronized (SAManagerComponent.lock) {
			// List<String> appIds =
			// Collections.synchronizedList(deployedApps.get(applianceId));
			logger.info( "Recover the app list for the appliance: "
					+ applianceId );
			List<String> appIds = new ArrayList<String>();
			for (AppReplica r : reps) {
				appIds.add( r.getAppId() );
			}
			deployedApps.put( applianceId, appIds );
		}
	}


	@Override
	public void unregisterAppliance( ApplianceUnregisterEvent event )
			throws ApplianceException {
	}


	public List<Appliance> getAllAppliances() {

		logger.info( "Get all the appliances from the appliance manager!" );

		synchronized (SAManagerComponent.lock) {
			List<Appliance> all = new ArrayList<Appliance>( appliances.values() );
			// refresh the app numbers of the appliance
			for (Appliance a : all) {
				String applianceId = a.getDesp().getId();
				// get the apps by the appliance's id
				int appRepetitionSize = this.appManager
						.getAppRepetitionSizesByApplianceId( applianceId );

				a.getStatus().setDeployedAmount( appRepetitionSize );

			}
			return all;
		}

	}


	// @Override
	// public void undeployAppliance(ApplianceUndeploymentEvent event)
	// throws ApplianceException{
	// logger.info("Undeploy an appliance: " + event.getTargetAppliance());
	// String applianceId = event.getTargetAppliance();
	//
	// //delete AppRepetitons from AppManager
	// appManager.deleteAppRepetitionsByAppliance(applianceId);
	//
	// //delete from deployedApps and appliances
	// appliances.remove(applianceId);
	// deployedApps.remove(applianceId);
	// updates.remove(applianceId);
	//
	// }

	// @Override
	// public void deployAppliance(ApplianceDeploymentEvent event)
	// throws ApplianceException {
	//
	// }

	// /////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////

	public void deleteApplianceById( String applianceId ) {
		logger.info( "Delete appliance by ID : " + applianceId );

		synchronized (SAManagerComponent.lock) {
			if (appliances.containsKey( applianceId )) {

				// synchronized(deployedApps){
				deployedApps.remove( applianceId );
				// }

				appliances.remove( applianceId );
				// synchronized(updates){
				updates.remove( applianceId );
				// }

				appManager.deleteAppRepetitionsByAppliance( applianceId );

			} else {
				logger.info( "The appliance to be deleted does not exist!" );
			}
		}
	}


	/**
	 * adding a new app update the indexing data map
	 * 
	 * @param newApp
	 */
	public void addNewApp( String appID, List<AppReplica> repetitions ) {

		// add the deployed apps
		// just update the ApplianceManager's indexing data
		synchronized (SAManagerComponent.lock) {
			for (AppReplica rep : repetitions) {

				String applianceId = rep.getContainerId();

				if (appliances.get( applianceId ) == null) {
					logger.warn( "The target appliance doesn't exist: "
							+ applianceId );
					continue;
				}
				if (deployedApps.get( applianceId ) == null) {
					deployedApps.put( applianceId, new ArrayList<String>() );
				}
				deployedApps.get( applianceId ).add( appID );

			}
		}
	}


	public List<Appliance> queryAvailableAppliances() {
		synchronized (SAManagerComponent.lock) {
			List<Appliance> aplis = new ArrayList<Appliance>(
					appliances.values() );
			recountAppsByAppliances( aplis );
			return aplis;
		}

	}


	public void recountAppsByAppliances( List<Appliance> aplis ) {
		synchronized (SAManagerComponent.lock) {
			for (Appliance ap : aplis) {
				String apId = ap.getDesp().getId();
				List<String> hosteds = deployedApps.get( apId );
				if (hosteds != null) {
					ap.getStatus().setDeployedAmount( hosteds.size() );
				}
			}
		}
	}


	public void deleteAppRepetitonByServiceID( String serviceID,
			List<String> applianceIds ) throws AppException {
		synchronized (SAManagerComponent.lock) {
			for (String applianceId : applianceIds) {

				List<String> apps = deployedApps.get( applianceId );
				if (apps == null || apps.size() <= 0) {
					logger.warn( "The service " + serviceID
							+ "is not deployed in the appliance: "
							+ applianceId );
					// need to consider weather to throw the AppException ?
					continue;
				}

				for (String name : apps) {
					if (name.equals( serviceID )) {
						apps.remove( name );
						break;
					}
				}

			}

		}
	}


	public AppManager getAppManager() {
		return appManager;
	}


	public void scan( long max_internal ) {
		synchronized (SAManagerComponent.lock) {
			Calendar now = Calendar.getInstance();
			scan( max_internal, now );
		}
	}


	public void scan( long max_internal, Calendar now ) {
		Map<String, Calendar> us;
		synchronized (SAManagerComponent.lock) {
			us = new HashMap<String, Calendar>( updates );
			logger.info( "There are " + updates.size() + "appliance registered" );

			logAppliances();

			Iterator<String> keys = us.keySet().iterator();
			while (keys.hasNext()) {
				String id = keys.next();
				Calendar c = us.get( id );
				if (now.getTimeInMillis() - c.getTimeInMillis() > max_internal) {
					logger.info( "The appliance is expired : " + id );
					// the appliance is expired, so delete it
					deleteApplianceById( id );
				}
			}

			if (appManager != null) {

				appManager.logApps();
				appManager.logToBeDeploedApps();
				appManager.logToBeUndeployedApps();
			}

		}

	}


	protected void updateTimestamp( String applianceId, Calendar c ) {
		synchronized (SAManagerComponent.lock) {
			updates.put( applianceId, c );
		}
	}


	@Override
	public void deployAppliance( ApplianceDeploymentEvent event )
			throws ApplianceException {
		logger.info( "The appliance doesn't support deployment" );
	}


	@Override
	public void undeployAppliance( ApplianceUndeploymentEvent event )
			throws ApplianceException {

	}


	public Appliance getApplianceById( String applianceId ) {
		synchronized (SAManagerComponent.lock) {
			return appliances.get( applianceId );
		}

	}


	/**
	 * update appliances
	 */
	public void addAppliance( Appliance appliance ) {

		synchronized (SAManagerComponent.lock) {
			logger.info( "Add a new appliance to the appliance manager: "
					+ this.getClass().getSimpleName() );
			appliances.put( appliance.getDesp().getId(), appliance );
			deployedApps.put( appliance.getDesp().getId(),
					new ArrayList<String>() );
		}

	}


	public List<String> getAppIdsByApplianceId( String applianceId ) {
		synchronized (SAManagerComponent.lock) {
			return Collections
					.synchronizedList( deployedApps.get( applianceId ) );
		}
	}


	public boolean increateApplianceReqLoad( String applianceId ) {
		synchronized (appliances) {
			Appliance appliance = appliances.get( applianceId );
			if (appliance == null) {
				logger.warn( "The target appliance is null: " + applianceId );
				return false;
			}
			synchronized (appliance) {
				appliance.getStatus().increaseReq();
			}
			return true;
		}
	}


	public boolean decreaseApplianceReqLoad( String applianceId ) {
		synchronized (appliances) {
			Appliance appliance = appliances.get( applianceId );
			if (appliance == null) {
				logger.warn( "The target appliance is null: " + applianceId );
				return false;
			}
			synchronized (appliance) {
				appliance.getStatus().decreaseReq();
			}
			return true;
		}
	}

	// public void displayAppliance(){
	// Iterator<>appliances.keySet().iterator();
	// for(){
	//
	// }
	// }

	// ////////////////The Listener Interface operations ///////////////////
	//
	// @Override
	// public void pollingAppliance(AppliancePollingEvent event) {
	// logger.info("Receiving the appliance polliing message!");
	// Appliance appliance = event.getTargetAppliance();
	//
	// }
	//
	// @Override
	// public void registerAppliance(ApplianceRegisterEvent event) throws
	// ApplianceException{
	//
	// //add a new appliance to its repository
	// int registeredType = event.getRegisterType();
	// switch(registeredType){
	// case ApplianceRegisterEvent.HOST_REGISTER:
	// logger.info("Register a host!");
	// Host host = (Host)event.getAppliance();
	// //register the host and its child appliances
	// registerHost(host);
	// break;
	// case ApplianceRegisterEvent.AXIS2_REGISTER:
	// logger.info("Register a axis2!");
	// Axis2Server axis2 = (Axis2Server)event.getAppliance();
	// axis2Repository.addNewAppliance(axis2);
	// break;
	// case ApplianceRegisterEvent.BPMN_ENGINE_REGISTER:
	// logger.info("Register a BPMN Engine!");
	// BPMNEngine bpmnEngine = (BPMNEngine)event.getAppliance();
	// bpmnEngineRepository.addNewAppliance(bpmnEngine);
	// break;
	// case ApplianceRegisterEvent.TOMCAT_REGISTER:
	// logger.info("Register a tomcat!");
	// TomcatServer tomcat = (TomcatServer)event.getAppliance();
	// tomcatRepository.addNewAppliance(tomcat);
	// break;
	// default:
	// logger.warn("This is a unknown registered type: " + registeredType);
	// }
	// }
	//
	// /**
	// * If some exceptions happen when invoking hostRepository's operation,
	// * we should throw out the exception.
	// * Other case just do nothing
	// *
	// * @param host
	// * @throws ApplianceException
	// */
	// private void registerHost(Host host) throws ApplianceException{
	//
	// List<Appliance> children = host.getChildAppliances();
	//
	// //adding the host
	// hostRepository.addNewAppliance(host);
	//
	// for(Appliance a : children){
	// a.getDesp().setParent(host);
	// if(a instanceof Axis2Server){
	// logger.info("Register a new Axis2 Server");
	// try{
	// axis2Repository.addNewAppliance(a);
	// continue;
	// }catch(ApplianceException e){
	// //do nothing
	// logger.warn("Failed to add an axis2 server to repository: "
	// + a.getDesp().getId());
	// }
	// }
	//
	// if(a instanceof TomcatServer){
	// logger.info("Register a new Tomcat Server!");
	// try{
	// tomcatRepository.addNewAppliance(a);
	// continue;
	// }catch(ApplianceException e){
	// //do nothing
	// logger.warn("Failed to add an Tomcat server to repository: "
	// + a.getDesp().getId());
	// }
	//
	// continue;
	// }
	// if(a instanceof BPMNEngine){
	// logger.info("Register a new BPMN Engine");
	// try{
	// bpmnEngineRepository.addNewAppliance(a);
	// continue;
	// }catch(ApplianceException e){
	// //do nothing
	// logger.warn("Failed to add an BPMNEngine server to repository: "
	// + a.getDesp().getId());
	// }
	//
	// continue;
	// }
	// }
	//
	// @Override
	// public void unregisterAppliance(ApplianceUnregisterEvent event) {
	//
	// }
	//
	// @Override
	// public void queryServers(ServerQueryEvent event) throws
	// ApplianceException{
	//
	// }
	//
	// @Override
	// public void queryAvailableServers(AvailableServerQueryEvent event)
	// throws ApplianceException {
	//
	// logger.info("Query the available servers by the application id: " +
	// event.getTargetAppId());
	//
	// int queryType = event.getQueryType();
	//
	// switch(queryType){
	// case AvailableServerQueryEvent.AVAILABLE_AXIS2:
	// logger.info("Query all the available axis2 servers!");
	// List<Appliance> startedAxis2s =
	// this.axis2Repository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_STARTED);
	// List<Appliance> workingAxis2s =
	// this.axis2Repository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_REGISTERED);
	// List<Appliance> availableAxis2s = new ArrayList<Appliance>();
	// availableAxis2s.addAll(startedAxis2s);
	// availableAxis2s.addAll(workingAxis2s);
	// logger.info("Get " + availableAxis2s.size() +
	// " available axis2 servers!");
	// event.setQueryResult(availableAxis2s);
	// break;
	//
	// case AvailableServerQueryEvent.AVAILABLE_BPMNENGINE:
	// logger.info("Query all the available BPMNEngines");
	// List<Appliance> startedBPMNs =
	// this.bpmnEngineRepository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_STARTED);
	// List<Appliance> workingBPMNs =
	// this.bpmnEngineRepository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_WORKING);
	// List<Appliance> availableBPMNs = new ArrayList<Appliance>();
	// availableBPMNs.addAll(startedBPMNs);
	// availableBPMNs.addAll(workingBPMNs);
	// event.setQueryResult(availableBPMNs);
	// break;
	//
	// case AvailableServerQueryEvent.AVAILABLE_TOMCAT:
	// logger.info("Query all the available Tomcats");
	// List<Appliance> startedTomcats =
	// this.tomcatRepository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_STARTED);
	// List<Appliance> workingTomcats =
	// this.tomcatRepository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_WORKING);
	// List<Appliance> availableTomcats = new ArrayList<Appliance>();
	// availableTomcats.addAll(startedTomcats);
	// availableTomcats.addAll(workingTomcats);
	// event.setQueryResult(availableTomcats);
	// break;
	// case AvailableServerQueryEvent.AVAILABLE_HOST:
	// logger.info("Query all the available hosts");
	// List<Appliance> startedHosts =
	// this.hostRepository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_STARTED);
	// List<Appliance> workingHosts =
	// this.hostRepository.queryAppliancesByStatus(ApplianceStatus.APPLIANCE_WORKING);
	// List<Appliance> availableHosts = new ArrayList<Appliance>();
	// availableHosts.addAll(startedHosts);
	// availableHosts.addAll(workingHosts);
	// event.setQueryResult(availableHosts);
	// break;
	//
	// default:
	// logger.warn("The query type is unknown, so do nothing");
	// }
	// }
	//
	// public Appliance getHostById(String id){
	// try {
	// return this.hostRepository.queryApplianceById(id);
	// } catch (ApplianceException e) {
	// logger.warn(e.getMessage(),e);
	// return null;
	// }
	// }
	//
	// public Appliance getAxis2ServerById(String id){
	// try {
	// return this.axis2Repository.queryApplianceById(id);
	//
	// } catch (ApplianceException e) {
	// logger.warn(e.getMessage(),e);
	// return null;
	// }
	// }
	//
	// public Appliance getTomcatServerById(String id){
	// try {
	// return this.tomcatRepository.queryApplianceById(id);
	// } catch (ApplianceException e) {
	// logger.warn(e.getMessage(),e);
	// return null;
	// }
	// }
	//
	// public Appliance getBPMNEngineById(String id){
	// try {
	// return this.bpmnEngineRepository.queryApplianceById(id);
	// } catch (ApplianceException e) {
	// logger.warn(e.getMessage(),e);
	// return null;
	// }
	// }
	//
	// @Override
	// public void queryDeployedServersByAppId(DeployedServerQueryEvent event)
	// throws ApplianceException {
	// //the appliance manager do nothing
	// //this job would be done by AppManager
	//
	// }
	//
	// @Override
	// public void deployApp(AppDeploymentEvent event) {
	//
	// }
	//
	// @Override
	// public void invokeAppFeedback(AppInvocationEvent event) {
	// logger.info("Receive the invoke feed back!");
	//
	// }
	//
	// @Override
	// public void deployAppFeedback(AppDeploymentEvent event) throws
	// AppException {
	// logger.info("Receive the deployment feedback!");
	//
	// }
	//
	// @Override
	// public void queryApp(AppQueryEvent event) {
	//
	// }
	//
	// @Override
	// public void undeployApp(AppUndeploymentEvent event) {
	//
	// }
	// //////////////////////////////////////////////////////////////////////////
	//
	// @Override
	// public void undeployAppFeedback(AppDeploymentEvent event)
	// throws AppException {
	// logger.info("Deleting the repetiton from the appliance manager");
	//
	// String serviceID = event.getDeployedServiceId();
	// //String serviceName = event.getDeployedServiceName();
	// Map<String, String> maps = (Map<String, String>)event.getDeployResults();
	//
	// //remove the repetitions
	// // App app = new App();
	// // app.setId(serviceID);
	// // app.setName(serviceName);
	//
	// Iterator<String> keys = maps.keySet().iterator();
	// logger.info("There are " + maps.size() +
	// " undeployed results from the undeployment feedback!");
	// //List<AppRepetition> reps = new ArrayList<AppRepetition>();
	//
	// while(keys.hasNext()){
	//
	// String containerId = keys.next();
	//
	// boolean is = Boolean.valueOf(maps.get(containerId));
	// if(is){
	// logger.info("Remove the repetition from the container: " + containerId);
	// try {
	// axis2Repository.deleteRepetitionByAppId(containerId, serviceID);
	// } catch (ApplianceException e) {
	// logger.info(e.getMessage());
	// }
	// }
	// }
	//
	//
	// }
	//
	// @Override
	// public void undeployAppQuery(UndeployAppQueryEvent event)
	// throws AppException {
	//
	// }

}
