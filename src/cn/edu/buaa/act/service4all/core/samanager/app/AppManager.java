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
package cn.edu.buaa.act.service4all.core.samanager.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerComponent;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.App;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppInvocationEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppUndeploymentEvent;

public abstract class AppManager implements AppListener {

	private final Log logger = LogFactory.getLog( AppManager.class );

	protected ApplianceManager applianceManager;
	protected SAManagerComponent cmp;
	// private static float CPURATELOAD_LIMIT = (float) 70.0;
	// private static float MEMORYLOAD_LIMIT = (float) 128000.0;
	// private static int DEPLOYAMOUNT_LIMIT = 25;
	/**
	 * appID ----> ApplianceID List
	 */
	protected Map<String, List<String>> hostedAppliances = new HashMap<String, List<String>>();

	/**
	 * appID ----> App instance
	 */
	protected Map<String, App> apps = new HashMap<String, App>();

	// ////////////////////////////////////////////
	/**
	 * appID ----> App instance
	 */
	protected Map<String, App> toBeDeployed = new HashMap<String, App>();

	/**
	 * appID ----> App instance
	 */
	// protected Map<String, App> toBeUndeployed = new HashMap<String, App>();

	protected Map<String, App> toBeRetryUndeployed = new HashMap<String, App>();


	// /////////////////////////////////////////////

	public List<App> getAllApps() {
		synchronized (SAManagerComponent.lock) {
			return new ArrayList<App>( apps.values() );
		}
	}


	public AppManager() {
		hostedAppliances = new HashMap<String, List<String>>();
		apps = new HashMap<String, App>();
	}


	public SAManagerComponent getCmp() {
		return cmp;
	}


	public void setCmp( SAManagerComponent cmp ) {
		this.cmp = cmp;
	}


	public void setApplianceManager( ApplianceManager applianceManager ) {
		this.applianceManager = applianceManager;
	}


	/**
	 * receive the App deployment feedback and change the toBeDeployed to
	 * deployed
	 * 
	 */
	@Override
	public void deployAppFeedback( AppDeploymentEvent event )
			throws AppException {

		List<AppReplica> results = event.getDeployResults();
		String serviceID = event.getDeployedServiceId();
		String serviceName = event.getDeployedServiceName();

		logger.info( "Handle the app deployment feedback: " + serviceID );

		if (results == null || results.size() <= 0) {
			logger.info( "The deployment for the service: " + serviceID + "("
					+ serviceName + ") is unsuccessful!" );

			// remove the deploy info from toBeDeployed list
			synchronized (SAManagerComponent.lock) {
				toBeDeployed.remove( serviceID );
			}

			return;
		}

		synchronized (SAManagerComponent.lock) {

			App toBeDeployedApp = toBeDeployed.get( serviceID );
			toBeDeployedApp.setName( serviceName );

//			if (toBeDeployedApp == null) {
//				logger.warn( "The app is not ready to be deployed: "
//						+ serviceID );
//				return;
//			}
			// the deployment is successful, then add the app repetitions
			toBeDeployedApp.setBackups( results );
			toBeDeployed.remove( serviceID );

			logger.info( "Add a new app to AppManager: " + serviceID + "with "
					+ results.size() + " AppRepetitions!" );
			synchronized (apps) {
				App app;
				app = apps.get( serviceID );

				if (app == null) {
					apps.put( serviceID, toBeDeployedApp );
				} else {
					app.addBackups( results );
					apps.put( serviceID, app );
				}
			}

		}

		// add to AppManager's data repository
		List<String> applianceIds = new ArrayList<String>();
		for (AppReplica rep : results) {
			String applianceID = rep.getContainerId();
			applianceIds.add( applianceID );
		}

		synchronized (SAManagerComponent.lock) {
			List<String> oldapplianceIds = hostedAppliances.get( serviceID );
			if (oldapplianceIds != null)
				applianceIds.addAll( oldapplianceIds );
			hostedAppliances.put( serviceID, applianceIds );
		}

		synchronized (SAManagerComponent.lock) {
			// update the index data in ApplianceManager
			applianceManager.addNewApp( serviceID, results );
		}

		logApps();
	}

	

	public void logApps() {
		synchronized (SAManagerComponent.lock) {
			logger.info( "************* Log all the apps ************************" );
			Iterator<String> appNames = apps.keySet().iterator();
			while (appNames.hasNext()) {
				String appName = appNames.next();
				App app = apps.get( appName );
				if (app != null) {
					logger.info( "The App[" );
					logger.info( "\t the app name: " + app.getName() );
					logger.info( "\t the app id: " + app.getId() );
					List<AppReplica> rs = app.getBackups();
					logger.info( "\t------ " + rs.size()
							+ "AppRepetitions---------" );
					for (AppReplica r : rs) {
						String applianceId = r.getContainerId();
						logger.info( "\t one AppRepetition : " + applianceId );
					}
					logger.info( "\t----------------------------" );
				}
			}
			logger.info( "*************************************" );
		}

	}


	@Override
	public void undeployAppFeedback( AppUndeploymentEvent event )
			throws AppException {

		String serviceID = event.getTargetServiceID();
		logger.info( "Receive the app undeployment feedback : " + serviceID );

		List<String> undepRs = event.getUndeployedResults();
		synchronized (SAManagerComponent.lock) {
			App app = apps.get( serviceID );
			if (app == null) {
				logger.warn( "The target app isn't to be undeployed: "
						+ serviceID );
				throw new AppException(
						"The target app isn't to be undeployed: " + serviceID );
			}

			List<AppReplica> oldReps = app.getBackups();
			// Iterator<String> undIt = undepRs.iterator();
			// while (undIt.hasNext()){
			// String test = (String) undIt.next();
			// }
			if (undepRs == null || undepRs.size() <= 0) {
				logger.warn( "There is no app repetitions undeployed: "
						+ serviceID );
				// //just do nothing
				// toBeRetryUndeployed.put(serviceID, app);
				// toBeUndeployed.remove(app);
				return;

			}
			// the unsuccessful undeployment
			if (oldReps.size() > undepRs.size()) {

				logger.info( "There are " + (oldReps.size() - undepRs.size())
						+ "AppRepetitions to be unsuccessfully undeployed: "
						+ serviceID );

				// get the successfully deployed apps and remove them
				for (String r : undepRs) {
					// String applianceID = r.getContainerId();
					for (AppReplica oldR : oldReps) {
						if (oldR.getContainerId().equalsIgnoreCase( r )) {
							oldReps.remove( oldR );
						}
					}
				}

				// change the app to toBeRetryUndeployed
				// toBeRetryUndeployed.put(serviceID, app);
				// add the app to the app manager
				// this.addApp(app);
			} else {
				logger.info( "Remove the app from the app manager: "
						+ serviceID );
				apps.remove( serviceID );
			}
			app.setBackups( oldReps );
			// toBeUndeployed.remove(app);

			// update the appliance indexing data from appliancemanager
			List<String> applianceIds = new ArrayList<String>();
			for (String r : undepRs) {
				applianceIds.add( r );
			}

			synchronized (SAManagerComponent.lock) {
				applianceManager.deleteAppRepetitonByServiceID( app.getId(),
						applianceIds );
			}

		}

	}


	/**
	 * just do nothing now !!!!!
	 */
	@Override
	public void invokeAppFeedback( AppInvocationEvent event )
			throws AppException {

	}


	@Override
	public synchronized void queryApplianceForDeployment(
			AppDeploymentEvent event ) throws AppException {

		// String serviceName = event.getDeployedServiceName();

		String defaultSerName = "default service name";
		String serviceId = null;
		String deployedServiceName = event.getDeployedServiceName();

		if (event.getDeployedServiceId() != null) {
			//好像没有程序会执行该分支
			//there is no sign that the program will execute this branch
			serviceId = event.getDeployedServiceId();
			logger.info( "There is aready an event service id =" + serviceId );
		} else {
			serviceId = generateServiceId( defaultSerName );
			logger.info( "generate a service id=" + serviceId );
		}

		logger.info( "Query the appliances for deployment: " + serviceId );
		synchronized (SAManagerComponent.lock) {
			// query the available appliances
			logger.info( "Query the appliance from the appliance manager : "
					+ applianceManager.getClass().getSimpleName() );
			List<Appliance> results = applianceManager
					.queryAvailableAppliances();
			logger.info( "the whole container size:" + results.size() );
			logger.info( "the service name to be judged:" + deployedServiceName );
			AppManager appManager = (AppManager) applianceManager
					.getAppManager();
			List<Appliance> applianceToDelete = new LinkedList<Appliance>();
			Iterator<Appliance> applianceIt = results.iterator();
			while (applianceIt.hasNext()) {
				Appliance applianceForCheck = applianceIt.next();
				List<AppReplica> appList = appManager
						.getAppRepetitionsByApplianceId( applianceForCheck
								.getDesp().getId() );
				Iterator<AppReplica> appIt = appList.iterator();
				while (appIt.hasNext()) {
					AppReplica appRep = (AppReplica) appIt.next();
					if (appRep.getAppName().equalsIgnoreCase(
							deployedServiceName )) {//若要部署的应用名与容器内的同名，则排除该容器
						applianceToDelete.add( applianceForCheck );
					}
				}
				// if (applianceForCheck.getStatus().getCpuRate() >
				// CPURATELOAD_LIMIT
				// || applianceForCheck.getStatus().getMemoryfloat() <
				// MEMORYLOAD_LIMIT
				// || appList.size() > DEPLOYAMOUNT_LIMIT) {
				// applianceToDelete.add(applianceForCheck);
				// }
			}
			int i, size;
			size = applianceToDelete.size();
			for (i = 0; i < size; i++)
				results.remove( applianceToDelete.get( i ) );
			logger.info( "available server size:" + results.size() );
			logger.info( "Get the " + results.size()
					+ "appliances for deployment" );
			event.setQueryResults( results );
		}
		// create a new App instance
		App targetApp = new App();
		targetApp.setId( serviceId );
		targetApp.setName( deployedServiceName );

		synchronized (SAManagerComponent.lock) {
			// adding to the toBeDeployed List
			toBeDeployed.put( serviceId, targetApp );
			event.setDeployedServiceId( serviceId );
		}

		// log all the to be deployed Apps
		logToBeDeploedApps();

	}

	@Override
	public synchronized void queryApplianceForScaleout(AppDeploymentEvent event) throws AppException{
		logger.info("Step into function of queryApplianceForScaleout");
		String serviceId = event.getDeployedServiceId();
		String deployedServiceName = event.getDeployedServiceName();

		logger.info( "Query the appliances for scale out: " + serviceId );
		synchronized (SAManagerComponent.lock) {
			// query the available appliances
			logger.info( "Query the appliance from the appliance manager : "
					+ applianceManager.getClass().getSimpleName() );
			List<Appliance> results = applianceManager
					.queryAvailableAppliances();
			logger.info( "the whole container size:" + results.size() );
			logger.info( "the service name to be judged:" + deployedServiceName );
			AppManager appManager = (AppManager) applianceManager
					.getAppManager();
			List<Appliance> applianceToDelete = new LinkedList<Appliance>();
			Iterator<Appliance> applianceIt = results.iterator();
			while (applianceIt.hasNext()) {
				Appliance applianceForCheck = applianceIt.next();
				List<AppReplica> appList = appManager
						.getAppRepetitionsByApplianceId( applianceForCheck
								.getDesp().getId() );
				Iterator<AppReplica> appIt = appList.iterator();
				while (appIt.hasNext()) {
					AppReplica appRep = (AppReplica) appIt.next();
					if (appRep.getAppName().equalsIgnoreCase(
							deployedServiceName )) {//若要部署的应用名与容器内的同名，则排除该容器
						applianceToDelete.add( applianceForCheck );
					}
				}
				// if (applianceForCheck.getStatus().getCpuRate() >
				// CPURATELOAD_LIMIT
				// || applianceForCheck.getStatus().getMemoryfloat() <
				// MEMORYLOAD_LIMIT
				// || appList.size() > DEPLOYAMOUNT_LIMIT) {
				// applianceToDelete.add(applianceForCheck);
				// }
			}
			int i, size;
			size = applianceToDelete.size();
			for (i = 0; i < size; i++)
				results.remove( applianceToDelete.get( i ) );
			logger.info( "available server size:" + results.size() );
			logger.info( "Get the " + results.size()
					+ "appliances for deployment" );
			event.setQueryResults( results );
		}
		// create a new App instance
		App targetApp = new App();
		targetApp.setId( serviceId );
		targetApp.setName( deployedServiceName );

		synchronized (SAManagerComponent.lock) {
			// adding to the toBeDeployed List
			toBeDeployed.put( serviceId, targetApp );
			event.setDeployedServiceId( serviceId );
		}

		// log all the to be deployed Apps
		logToBeDeploedApps();
	}

	public void logToBeDeploedApps() {
		synchronized (SAManagerComponent.lock) {
			logger.info( "************* Log all the ToBeDeploed apps ************************" );
			Iterator<String> appNames = toBeDeployed.keySet().iterator();
			while (appNames.hasNext()) {
				String appName = appNames.next();
				App app = toBeDeployed.get( appName );
				if (app != null) {
					logger.info( "The App[" );
					logger.info( "\t the app name: " + app.getName() );
					logger.info( "\t the app id: " + app.getId() );
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


	public void logToBeUndeployedApps() {
		synchronized (SAManagerComponent.lock) {
			logger.info( "************* Log all the ToBeUndeploed apps ************************" );
			Iterator<String> appNames = toBeDeployed.keySet().iterator();
			while (appNames.hasNext()) {
				// String appName = appNames.next();
				// App app = toBeUndeployed.get(appName);
				// if (app != null) {
				// logger.info("The App[");
				// logger.info("\t the app name: " + app.getName());
				// logger.info("\t the app id: " + app.getId());
				// List<AppRepetition> rs = app.getBackups();
				// logger.info("\t------ " + rs.size()
				// + "AppRepetitions---------");
				// for (AppRepetition r : rs) {
				// String applianceId = r.getContainerId();
				// logger.info("\t one AppRepetition : " + applianceId);
				// }
				// logger.info("\t----------------------------");
				// }
			}
			logger.info( "*************************************" );
		}
	}


	@Override
	public void queryAppRepetitionForExecution( AppInvocationEvent event )
			throws AppException {
		String serviceID = event.getTargetServiceID();

		synchronized (SAManagerComponent.lock) {
			App app = apps.get( serviceID );
			if (app == null) {
				logger.warn( "The target service is not deployed: " + serviceID );
				throw new AppException( "The target service is not deployed: "
						+ serviceID );
			}

			List<AppReplica> reps = app.getBackups();
			logger.info( "Get the " + reps.size()
					+ "app repetitions for execution: " + serviceID );

			resetParentAppliances( reps );
			event.setRepetitions( reps );
			// It's possible to change the app's status but now just do nothing
		}

	}


	protected void resetParentAppliances( List<AppReplica> reps ) {

		synchronized (SAManagerComponent.lock) {

			applianceManager.logAppliances();

			for (AppReplica r : reps) {
				String applianceId = r.getContainerId();

				if (applianceManager.getAppIdsByApplianceId( applianceId ) == null) {
					logger.warn( "The repetition's(" + r.getAppName()
							+ ") parent appliance is null: " + applianceId );

				} else {
					r.setContainer( applianceManager
							.getApplianceById( applianceId ) );
				}
			}
		}
	}


	@Override
	public void queryAppRepetitionForUndeployment( AppUndeploymentEvent event )
			throws AppException {

		String serviceID = event.getTargetServiceID();
		logger.info( "Query app repetition for undeployment: " + serviceID );

		synchronized (SAManagerComponent.lock) {
			// get the app repetition list
			App app = apps.get( serviceID );
			if (app == null) {
				logger.warn( "The target app has not been deployed!" );
				throw new AppException( "The target app has not been deployed!" );
			}
			List<AppReplica> reps = app.getBackups();
			logger.info( "Get " + reps.size()
					+ "app repetitions for undeployment : " + app.getId() );
			// reset the parent appliance
			resetAppliance2AppRepetition( reps );
			event.setDeployedRepetitions( reps );
			event.setTargetServiceName( app.getName() );

			// synchronized(toBeUndeployed){
			// add the app to the toBeUndeployed list
			// toBeUndeployed.put(serviceID, app);
			//
			// // set the app to be unavailable
			// //apps.remove(serviceID);
			// logger.info("There are " + toBeUndeployed.size()
			// + "apps to be undeployed!");
			//
			// for (String serID : toBeUndeployed.keySet()) {
			// logger.info("\t---------> " + serID);
			// }
			// }

		}

	}


	/**
	 * 
	 * @param reps
	 */
	private void resetAppliance2AppRepetition( List<AppReplica> reps ) {
		synchronized (SAManagerComponent.lock) {
			for (AppReplica rep : reps) {
				String applianceId = rep.getContainerId();
				Appliance parent = applianceManager
						.getApplianceById( applianceId );

				if (parent == null) {
					logger.warn( "The target parent appliance(" + applianceId
							+ ") does not exist for the app "
							+ rep.getAppName() );

				} else {
					rep.setContainer( parent );
				}

			}
		}

	}


	// ///////////////////////////////////////////////////////////

	protected abstract String generateServiceId( String appName );


	protected void addApp( App app ) {

	}


	public void deleteApp( String serviceName, String applianceID ) {

	}


	/**
	 * just return the app repetition list's size
	 * 
	 * @param applianceId
	 * @return
	 */
	public int getAppRepetitionSizesByApplianceId( String applianceId ) {
		synchronized (SAManagerComponent.lock) {
			// List<AppRepetition> reps = new ArrayList<AppRepetition>();
			int size = 0;
			List<App> as = new ArrayList<App>( apps.values() );
			for (App a : as) {
				List<AppReplica> repsByapp = a.getBackups();
				for (AppReplica r : repsByapp) {
					if (r.getContainerId().equalsIgnoreCase( applianceId )) {
						size++;
					}
				}
			}
			return size;
		}
	}


	/**
	 * update the App Repetitions when polling the appliance update replica
	 * information accroding to appliance ID
	 * 
	 * @param applianceId
	 * @param appNames
	 */
	public void updateApps( String applianceId,
			List<AppReplica> appRepetitions ) {

		logger.info( "Update the apps by the applianceId when appliance register: "
				+ applianceId );
		synchronized (SAManagerComponent.lock) {
			// change the algorithm for app update
			List<String> oldAppIds = this.applianceManager
					.getAppIdsByApplianceId( applianceId );
			List<String> removeds = new ArrayList<String>();
			List<String> addeds = new ArrayList<String>();

			for (AppReplica rep : appRepetitions) {
				logger.info( "Find one AppRepetition : " + rep.getAppId()
						+ " in the appliance: " + applianceId );

			}
			for (String id : oldAppIds) {

				logger.info( "Find one Old App : " + id + " in the appliance: "
						+ applianceId );
			}

			for (AppReplica rep : appRepetitions) {
				boolean has = false;
				for (String hostedId : oldAppIds) {
					if (hostedId.equals( rep.getAppId() )) {
						has = true;
						break;
					}
				}
				if (!has) {
					// it is the new added repetition
					logger.info( "Find one new repetition: " + rep.getAppId()
							+ " in the appliance: " + applianceId );
					addeds.add( rep.getAppId() );
				}
			}
			for (String oldId : oldAppIds) {
				boolean isRetained = false;
				for (AppReplica rep : appRepetitions) {
					if (rep.getAppId().equals( oldId )) {
						isRetained = true;
						break;
					}
				}
				if (!isRetained) {
					// the repetition has been removed from the host
					logger.info( "Find one deleted repetition: " + oldId
							+ " in the appliance: " + applianceId );
					removeds.add( oldId );
				}
			}

			// process the removed app repetition list
			for (String removedId : removeds) {
				App app = apps.get( removedId );
				List<AppReplica> bs = Collections.synchronizedList( app
						.getBackups() );

				// for(int i = 0;i<bs.size();i++){
				// if(applianceId.equals(bs.get(i).getContainerId())){
				// //remove this appliance repetition from the backup list
				// bs.remove(i);
				// }
				// }
				for (int i = 0; i < bs.size(); i++) {
					if (applianceId.equals( bs.get( i ).getContainerId() )) {
						// remove this appliance repetition from the backup list
						bs.remove( i );
					}
				}
				// update the backups
				app.setBackups( bs );
				List<String> applianceIds = Collections
						.synchronizedList( hostedAppliances.get( removedId ) );
				// for(String id : applianceIds){
				// if(id.equals(applianceId)){
				// applianceIds.remove(id);
				// break;
				// }
				// }
				for (int i = 0; i < applianceIds.size(); i++) {
					if (applianceId.equals( applianceIds.get( i ) )) {
						applianceIds.remove( i );
						break;
					}
				}
				// update the hostedAppliances list
				hostedAppliances.put( removedId, applianceIds );

			}

			// process the added app repetition list
			for (String addedId : addeds) {
				App app = apps.get( addedId );
				String appId = app.getId();
				List<AppReplica> bs = Collections.synchronizedList( app
						.getBackups() );
				for (AppReplica rep : appRepetitions) {
					if (rep.getAppId().equals( appId )) {
						// remove this appliance repetition from the backup list
						logger.info( "Add a new app repetition: "
								+ rep.getAppId() + " in the appliance( "
								+ rep.getContainerId() + ")" );
						// add a new repetition but if already exist one ,ignore
						boolean has = false;
						for (AppReplica r : bs) {
							if (r.getContainerId().equals( applianceId )) {
								has = true;
								break;
							}
						}
						if (!has)
							bs.add( rep );
					}
				}
				app.setBackups( bs );
				List<String> applianceIds = Collections
						.synchronizedList( hostedAppliances.get( addedId ) );
				applianceIds.add( applianceId );
				// update the hostedAppliances list
				hostedAppliances.put( addedId, applianceIds );
			}

			this.applianceManager.updateAppsFromAppliance( applianceId,
					appRepetitions );

		}

		// List<String> targetAppIds = new ArrayList<String>();
		//
		// Iterator<String> ids = hostedAppliances.keySet().iterator();
		//
		// while(ids.hasNext()){
		// String appId = ids.next();
		// List<String> applianceIds = hostedAppliances.get(appId);
		// if(applianceIds == null){
		// logger.warn("The app's(" + appId + ") parent appliance" + applianceId
		// + " is null when update apps!");
		//
		// }else{
		//
		// for(String appliaId : applianceIds){
		// if(appliaId.equals(applianceId)){
		// targetAppIds.add(appId);
		// break;
		// }
		// }
		// }
		//
		// }
		//
		// for(String appId : targetAppIds){
		// App app = apps.get(appId);
		// List<AppRepetition> reps = app.getBackups();
		//
		// for(AppRepetition r : reps){
		// boolean exists = false;
		// for(Object o : appRepetitions){
		// AppRepetition newR = (AppRepetition)o;
		// if(r.getAppName().equals(newR.getAppName())){
		// exists = true;
		// break;
		// }
		// }
		//
		// //remove the unUpadated repetitions
		// if(!exists){
		// reps.remove(r);
		// }
		// }
		//
		// //update the global indexing data
		// List<String> toUpdates = new ArrayList<String>();
		// for(AppRepetition r : reps){
		// toUpdates.add(r.getContainerId());
		// }
		// //override the existed data
		// hostedAppliances.put(appId, toUpdates);

		// }
	}


	/**
	 * @param appliance
	 */
	public void deleteAppRepetitionsByAppliance( String appliance ) {

		synchronized (SAManagerComponent.lock) {
			Iterator<App> apps = this.apps.values().iterator();

			while (apps.hasNext()) {
				App app = apps.next();
				List<AppReplica> reps = app.getBackups();
				// for (AppRepetition r : reps) {
				// if (r.getContainerId().equals(appliance)) {
				// reps.remove(r);
				// synchronized (hostedAppliances) {
				// List<String> as = hostedAppliances.get(app.getId());
				// for (String a : as) {
				// if (a.equals(appliance)) {
				// as.remove(a);
				// }
				// }
				// hostedAppliances.put(app.getId(), as);
				// }
				//
				// }
				// }
				for (int i = 0; i < reps.size(); i++) {
					if (reps.get( i ).getContainerId().equals( appliance )) {
						reps.remove( i );
						synchronized (hostedAppliances) {
							List<String> as = hostedAppliances
									.get( app.getId() );
							for (int j = 0; j < as.size(); i++) {
								if (as.get( j ).equals( appliance )) {
									as.remove( j );
								}
							}
							hostedAppliances.put( app.getId(), as );
						}
					}
				}
			}
		}
	}


	public List<AppReplica> getAppRepetitionsByApplianceId(
			String applianceId ) {

		synchronized (SAManagerComponent.lock) {
			List<AppReplica> results = new ArrayList<AppReplica>();
			Iterator<App> apps = this.apps.values().iterator();
			while (apps.hasNext()) {
				App app = apps.next();
				List<AppReplica> reps = app.getBackups();
				for (AppReplica r : reps) {
					if (r.getContainerId().equals( applianceId )) {
						// add the app repetition to the result list
						AppReplica rr = new AppReplica();

						rr.setAppName( r.getAppName() );
						rr.setContainerId( r.getContainerId() );
						rr.setInvocationUrl( r.getInvocationUrl() );
						rr.setAppId( app.getId() );

						results.add( rr );
					}
				}
			}

			return results;
		}

	}


	public App getAppById( String appId ) {
		synchronized (SAManagerComponent.lock) {
			return apps.get( appId );
		}
	}

	// /////////////////////////////////////////////////////////////////
	// @Override
	// public void registerAppliance(ApplianceRegisterEvent event) throws
	// ApplianceException {
	// logger.info("Update the applications while receiving appliance register request");
	// int registeredType = event.getRegisterType();
	// switch(registeredType){
	// case ApplianceRegisterEvent.HOST_REGISTER:
	// Host host = (Host)event.getAppliance();
	// //register the host and its child appliances
	// try {
	// registerHost(host);
	// } catch (AppException e) {
	// throw new ApplianceException(e.getMessage());
	// }
	// break;
	// case ApplianceRegisterEvent.AXIS2_REGISTER:
	// logger.info("Updating the web service information");
	// Axis2Server axis2 = (Axis2Server)event.getAppliance();
	// try {
	// updateWebService(axis2);
	// } catch (AppException e) {
	// throw new ApplianceException(e.getMessage());
	// }
	// break;
	// case ApplianceRegisterEvent.BPMN_ENGINE_REGISTER:
	// logger.info("Updating the BPMN information");
	// BPMNEngine bpmnEngine = (BPMNEngine)event.getAppliance();
	// //do nothing now
	// break;
	// case ApplianceRegisterEvent.TOMCAT_REGISTER:;
	// logger.info("Updating the Web App information");
	// TomcatServer tomcat = (TomcatServer)event.getAppliance();
	// //do nothing now
	// default:
	// logger.warn("This is a unknown registered type: " + registeredType);
	// }
	//
	// }
	//
	// private void registerHost(Host host) throws AppException{
	// List<Appliance> children = host.getChildAppliances();
	//
	// //adding the host
	// for(Appliance a : children){
	// a.getDesp().setParent(host);
	// if(a instanceof Axis2Server){
	// logger.info("Register a new Axis2 Server");
	// try{
	// updateWebService(a);
	// continue;
	// }catch(AppException e){
	// //do nothing
	// logger.warn("Failed to add an axis2 server to repository: "
	// + a.getDesp().getId());
	// }
	// }
	//
	// if(a instanceof TomcatServer){
	// logger.info("Register a new Tomcat Server!");
	// // try{
	// //
	// // continue;
	// // }catch(AppException e){
	// // //do nothing
	// // logger.warn("Failed to add an Tomcat server to repository: "
	// // + a.getDesp().getId());
	// // }
	// //
	// continue;
	// }
	// if(a instanceof BPMNEngine){
	// logger.info("Register a new BPMN Engine");
	// // try{
	// //
	// // continue;
	// // }catch(AppException e){
	// // //do nothing
	// // logger.warn("Failed to add an BPMNEngine server to repository: "
	// // + a.getDesp().getId());
	// // }
	//
	// continue;
	// }
	// }
	// }
	//
	//
	// /**
	// * update the web service information in the target axis2 appliance
	// *
	// * @param appliance
	// * @throws AppException
	// */
	//
	// private void updateWebService(Appliance appliance) throws AppException{
	// List<AppRepetition> apps = appliance.getStatus().getApps();
	// String applianceId = appliance.getDesp().getId();
	//
	// for(AppRepetition app : apps){
	// wsRepository.updateRepetitions(applianceId, app.getDesription().getId(),
	// app);
	// }
	// }
	//
	// /**
	// * update the repetition information
	// */
	// @Override
	// public void pollingAppliance(AppliancePollingEvent event) {
	// logger.info("update the repetition information while polling!");
	//
	// }
	//
	//
	//
	// @Override
	// public void deployApp(AppDeploymentEvent event) {
	// //this method may have nothing to do
	//
	// }
	//
	// @Override
	// public void invokeAppFeedback(AppInvocationEvent event) {
	// logger.info("Receive the invoke feedback!");
	// //do nothing recently
	// }
	//
	// @Override
	// public void queryApp(AppQueryEvent event) throws AppException {
	// String queryType = event.getQueryType();
	//
	// if(queryType.equalsIgnoreCase(AppQueryEvent.WEB_SERVICE_QUERY)){
	//
	// queryWebService(event);
	//
	// }else if(queryType.equalsIgnoreCase(AppQueryEvent.BPMN_QUERY)){
	//
	// queryBPMNService(event);
	//
	// }else{
	// logger.warn("There is an unidentified query type :" + queryType);
	// }
	// }
	//
	// private void queryWebService(AppQueryEvent event) throws AppException{
	//
	// String id = event.getTargetServiceID();
	// logger.info("Query available Web Services for the service id: " + id);
	//
	// List<AppRepetition> wss = wsRepository.queryAppRepetitionByAppId(id);
	//
	// if(wss == null){
	// logger.info("There is matched web services for the id : " + id);
	// event.setApps(null);
	// return;
	// }
	//
	// //event.setApps(event.getApps());
	// event.setApps(wss);
	//
	// }
	//
	// private void queryBPMNService(AppQueryEvent event){
	// logger.info("Query BPMN Services!");
	//
	// }
	// @Override
	// public void undeployApp(AppUndeploymentEvent event) {
	//
	// }
	//
	// @Override
	// public void deployAppFeedback(AppDeploymentEvent event) throws
	// AppException {
	// int type = event.getEventType();
	// switch(type){
	// case AppDeploymentEvent.APP_WS_DEPLOYMENT_FEEDBACK:
	// logger.info("handle web service deployment feed back!");
	// String serviceID = event.getDeployedServiceId();
	// String serviceName = event.getDeployedServiceName();
	// Map<String, String> deployedResults = event.getDeployResults();
	//
	// App app = new App();
	// app.setId(serviceID);
	// app.setName(serviceName);
	//
	// Iterator<String> keys = deployedResults.keySet().iterator();
	// logger.info("There are " + deployedResults.size() +
	// " deployed results from the deployment feedback!");
	// List<AppRepetition> reps = new ArrayList<AppRepetition>();
	// while(keys.hasNext()){
	//
	// String containerId = keys.next();
	//
	// String invokeUrl = deployedResults.get(containerId);
	//
	// AppRepetition repetition = new AppRepetition();
	// logger.info("Query the appliance manager!");
	// Appliance axis2Server =
	// this.applianceManager.getAxis2ServerById(containerId);
	//
	// repetition.setDesription(app);
	// repetition.setInvocationUrl(invokeUrl);
	// repetition.setContainer(axis2Server);
	// AppRecords records = new AppRecords();
	// repetition.setRecords(records);
	//
	// reps.add(repetition);
	// }
	//
	// app.setBackups(reps);
	// wsRepository.addNewApp(app);
	// break;
	// default:
	// logger.info("Unkown deploymment feedback");
	// }
	// }
	//
	//
	//
	//
	// @Override
	// public void queryServers(ServerQueryEvent event) {
	//
	// }
	//
	//
	//
	// @Override
	// public void unregisterAppliance(ApplianceUnregisterEvent event) {
	//
	// }
	// ///////////////////////////////////////////////////
	//
	// @Override
	// public void undeployAppQuery(UndeployAppQueryEvent event)
	// throws AppException {
	//
	// logger.info("Query the deployed appliance for application undpeloyment");
	// String serviceID = event.getServiceID();
	// List<AppRepetition> reps =
	// wsRepository.queryAppRepetitionByAppId(serviceID);
	// logger.info("Get repetitions from " + reps.size() + "appliances!");
	// List<Appliance> appliances = new ArrayList<Appliance>();
	// for(AppRepetition r : reps){
	// appliances.add(r.getContainer());
	// }
	//
	// event.setQueryResults(appliances);
	// }
	//
	// @Override
	// public void undeployAppFeedback(AppDeploymentEvent event)
	// throws AppException {
	// String serviceID = event.getDeployedServiceId();
	// String serviceName = event.getDeployedServiceName();
	// Map<String, String> maps = (Map<String, String>)event.getDeployResults();
	//
	// //remove the repetitions
	// // App app = new App();
	// // app.setId(serviceID);
	// // app.setName(serviceName);
	// boolean isWhole = true;
	// Iterator<String> keys = maps.keySet().iterator();
	// logger.info("There are " + maps.size() +
	// " undeployed results from the undeployment feedback!");
	// List<AppRepetition> reps = new ArrayList<AppRepetition>();
	// while(keys.hasNext()){
	//
	// String containerId = keys.next();
	//
	// boolean is = Boolean.valueOf(maps.get(containerId));
	// if(is){
	// logger.info("Remove the repetition from the container: " + containerId);
	// wsRepository.deleteAppRepetition(serviceID, containerId);
	// }else{
	// isWhole = false;
	// }
	// }
	//
	// if(isWhole){
	// logger.info("Undeploy the whole application from the repository!");
	// wsRepository.deleteApp(serviceID);
	// }
	// }
	//
	// @Override
	// public void queryAvailableServers(AvailableServerQueryEvent event)
	// throws ApplianceException {
	// logger.info("Query all available server for application deployment");
	// int queryType = event.getQueryType();
	// String appId = event.getTargetAppId();
	//
	// }
	//
	// @Override
	// public void queryDeployedServersByAppId(DeployedServerQueryEvent event)
	// throws ApplianceException {
	//
	// }
	// */
	// /////////////////////////////////////////////////////////////////////////////
}
