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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerComponent;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceRegisterEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceUndeploymentEvent;

/**
 * 
 * @author Huangyj
 * 
 */
public class HostManager extends ApplianceManager {

	private final Log logger = LogFactory.getLog( HostManager.class );

	private ApplianceScheduler scheduler = new ApplianceScheduler();

	protected Map<String, ApplianceManager> managers = new HashMap<String, ApplianceManager>();


	public void addManager( String name, ApplianceManager applianceManager ) {
		managers.put( name, applianceManager );
	}


	public ApplianceManager getApplianceManager( String name ) {
		return managers.get( name );
	}


	/**
	 * delete a Host
	 * 
	 */
	public void deleteApplianceById( String applianceId ) {

		logger.info( "Delete host appliance by ID : " + applianceId );

		synchronized (SAManagerComponent.lock) {
			List<String> ids = deployedApps.get( applianceId );
			// synchronized(managers){
			for (String id : ids) {
				Iterator<String> keys = managers.keySet().iterator();
				while (keys.hasNext()) {
					ApplianceManager manager = managers.get( keys.next() );
					manager.deleteApplianceById( id );
				}
			}
			// }

			deployedApps.remove( applianceId );
			appliances.remove( applianceId );

			// synchronized(updates){
			updates.remove( applianceId );
			// }
		}
	}


	/**
	 * 
	 * @param hostId
	 * @param applianceId
	 * @param applianceType
	 * @return
	 */
	public boolean deleteSubAppliance( String hostId, String applianceId,
			String applianceType ) throws ApplianceException {

		logger.info( "Delete the sub appliance(" + applianceId
				+ ") from the host(" + hostId + ")!" );

		synchronized (SAManagerComponent.lock) {

			// 还需要更新deployedApps中的索引信息
			List<String> subApplianceIds = deployedApps.get( hostId );
			if (subApplianceIds == null) {
				logger.warn( "The target host does not have sub appliances : "
						+ hostId );
				throw new ApplianceException(
						"The target host does not have sub appliances : "
								+ hostId );
			}

			boolean exist = false;
			for (String id : subApplianceIds) {
				if (id.equals( applianceId )) {
					logger.info( "Remove the sub appliance(" + applianceId
							+ ") from Host( " + hostId + ")!" );
					subApplianceIds.remove( id );
					exist = true;
					break;
				}
			}

			if (exist) {
				logger.warn( "The sub appliance(" + applianceId
						+ ") does not exist in the Host( " + hostId + ")!" );
			}

			ApplianceManager applianceMger = managers.get( applianceType );
			if (applianceMger == null) {
				logger.warn( "The appliance manager for the type ("
						+ applianceType + ") does not exist!" );
				throw new ApplianceException(
						"The appliance manager for the type (" + applianceType
								+ ") does not exist!" );
			}

			// delete the target appliance from the specific ApplianceManager
			applianceMger.deleteApplianceById( applianceId );
			return true;
		}
	}


	public List<Appliance> getAllHostsAndSubAppliance() {
		synchronized (SAManagerComponent.lock) {
			List<Appliance> hosts = new ArrayList<Appliance>(
					this.appliances.values() );

			// add the sub appliances to the specific host
			for (Appliance a : hosts) {
				if (a instanceof Host) {
					Host h = (Host) a;
					// 但是需要重新计算每个设备下的部署的应用数目
					Iterator<String> types = h.getChildAppliances().keySet()
							.iterator();
					while (types.hasNext()) {

						String t = types.next();
						ApplianceManager applianceManager = managers.get( t );

						if (applianceManager == null) {
							logger.warn( "Can't get the appliance manager of the type : "
									+ t );
							continue;
						}

						applianceManager.recountAppsByAppliances( h
								.getChildAppliances().get( t ) );
					}
				}
			}

			return hosts;
		}
	}


	public List<Appliance> getAllHosts() {

		synchronized (SAManagerComponent.lock) {

			List<Appliance> hosts = new ArrayList<Appliance>(
					this.appliances.values() );
			return hosts;

		}
	}


	/**
	 * 
	 * @param applianceId
	 * @param hosteds
	 */
	protected void addHostedApps( List<AppReplica> reps ) {

		// for(Object o : hosteds){
		// AppRepetition rep = (AppRepetition)o;
		// apps.add(rep.getAppName());
		// }
		//
		// //if exists, override them
		// deployedApps.put(applianceId, apps);
		//
		// //update the appManager's data
		// appManager.updateApps(applianceId, hosteds);

		// 需要更新AppManager中的数据
		synchronized (SAManagerComponent.lock) {
			Map<String, List<AppReplica>> rs = new HashMap<String, List<AppReplica>>();
			for (AppReplica r : reps) {
				String applianceId = r.getContainerId();
				if (rs.get( applianceId ) == null) {
					List<AppReplica> ls = new ArrayList<AppReplica>();
					rs.put( applianceId, ls );
				}
				rs.get( applianceId ).add( r );

				// 添加到deployedApps列表中,App添加进入的是服务名
				// if(deployedApps.get(applianceId) == null){
				// List<String> ls = new ArrayList<String>();
				// deployedApps.put(applianceId, ls);
				// }
				// deployedApps.get(applianceId).add(r.getAppName());
			}

			// 更新AppManager中的信息
			Iterator<String> names = rs.keySet().iterator();
			while (names.hasNext()) {
				String id = names.next();
				// appManager.updateApps(id, rs.get(id));
				logger.info( "For the appliance (" + id + ") has " + rs.size()
						+ "apps" );
				updateAppsFromHost( id, rs.get( id ) );
			}
		}

	}


	public synchronized Appliance getApplianceById( String applianceId ) {
		logger.info( "The before the locking....." );
		synchronized (SAManagerComponent.lock) {
			logger.info( "Locked........." );
			Host host = (Host) appliances.get( applianceId );

			List<Appliance> axis2 = host.getChildAppliances().get( "axis2" );
			if (axis2 != null) {
				logger.info( ".........Get the axis2 list: " + axis2.size() );
			} else {
				logger.info( ".........The axis2 list is null!" );
			}

			Host nh = new Host();

			nh.setDesp( new ApplianceDescription() );
			nh.setRecords( new ApplianceRecords() );
			nh.setStatus( new ApplianceStatus() );

			nh.getDesp().setId( applianceId );

			// set the host's children
			Map<String, List<Appliance>> clone = cloneChildren( applianceId,
					host.getChildAppliances() );
			nh.setChildrenAppliances( clone );
			// logger.info("The host(" + applianceId + ") has "+
			// host.get+" children appliances!");
			// List<String> subApplianceIds = deployedApps.get(applianceId);
			// logger.info("Get the " + subApplianceIds.size() +
			// "sub appliances for the host: " + applianceId);
			// for(String id : subApplianceIds){
			// updateSubAppliance(host, id);
			// }
			logger.info( "Release the locking......" );
			return nh;
		}
	}


	private Map<String, List<Appliance>> cloneChildren( String applianceId,
			Map<String, List<Appliance>> children ) {
		logger.info( "Clone the children........." );
		Map<String, List<Appliance>> clone = new HashMap<String, List<Appliance>>();
		Iterator<String> cloneNames = children.keySet().iterator();
		while (cloneNames.hasNext()) {
			String type = new String( cloneNames.next() );

			List<Appliance> cs = cloneApplianceList( children.get( type ) );
			logger.info( "Clone " + cs.size() + " appliance for the type : "
					+ type + " for the host: " + applianceId );
			clone.put( type, cs );
		}
		return clone;
	}


	private List<Appliance> cloneApplianceList( List<Appliance> appliances ) {
		List<Appliance> cloneApplianceList = new ArrayList<Appliance>();
		for (Appliance appliance : appliances) {
			Appliance c = cloneAppliance( appliance );
			cloneApplianceList.add( c );
		}

		return cloneApplianceList;
	}


	private Appliance cloneAppliance( Appliance a ) {

		Appliance c = new Appliance();
		c.setDesp( new ApplianceDescription() );
		c.setRecords( new ApplianceRecords() );
		c.setStatus( new ApplianceStatus() );

		c.getDesp().setId( new String( a.getDesp().getId() ) );
		c.getStatus().setStatus( a.getStatus().getStatus() );

		c.getDesp().setDeployEPR( a.getDesp().getDeployEPR() );
		c.getDesp().setDeployOperation( a.getDesp().getDeployOperation() );

		c.getStatus().setCpuRate( a.getStatus().getCpuRate() );
		c.getStatus().setDeployedAmount( a.getStatus().getDeployedAmount() );
		c.getStatus().setMemoryfloat( a.getStatus().getMemoryfloat() );

		return c;
	}


	/**
	 * update the appliance's list
	 * 
	 * @param host
	 * @param type
	 * @param newIds
	 * @param oldIds
	 */
	private void updateSubAppliance( Host host, String type,
			List<Appliance> news, List<Appliance> olds ) {
		synchronized (SAManagerComponent.lock) {
			try {
				ApplianceManager manager = this.getApplianceManager( type );
				// Appliance sub = manager.getApplianceById(applianceId);
				//
				// if(sub == null){
				// logger.warn("The sub appliance is null: " + applianceId);
				// return;
				// }
				// logger.info("Update a sub appliance(" + applianceId +
				// ") for the host: " + host.getDesp().getId());
				// host.addChildAppliance(type, sub);
				List<Appliance> addeds = new ArrayList<Appliance>();
				List<Appliance> removeds = new ArrayList<Appliance>();

				// search the new added appliances
				for (Appliance na : news) {
					String newId = na.getDesp().getId();
					boolean isNew = true;
					for (Appliance oa : olds) {
						String oldId = oa.getDesp().getId();
						if (newId.equals( oldId )) {
							isNew = false;
							break;
						}
					}
					if (isNew) {
						addeds.add( na );
						manager.addAppliance( na );
					}
				}

				// search the removed appliances
				for (Appliance oa : olds) {
					String oldId = oa.getDesp().getId();
					boolean isRemoved = true;
					for (Appliance na : news) {
						String newId = na.getDesp().getId();
						if (newId.equals( oldId )) {
							isRemoved = false;
							break;
						}
					}
					if (isRemoved) {
						removeds.add( oa );
						manager.deleteApplianceById( oa.getDesp().getId() );
					}
				}

			} catch (Exception e) {
				logger.warn( e.getMessage() );

			}
		}

	}


	protected void updateAppsFromHost( String applianceId,
			List<AppReplica> rs ) {

		synchronized (SAManagerComponent.lock) {
			if (applianceId.startsWith( SAManagerComponent.APPLIANCE_AXIS2 )) {

				ApplianceManager mger = managers
						.get( SAManagerComponent.APPLIANCE_AXIS2 );
				// mger.updateAppsFromAppliance(applianceId, rs);
				AppManager appMger = mger.getAppManager();
				appMger.updateApps( applianceId, rs );

			} else if (applianceId
					.startsWith( SAManagerComponent.APPLIANCE_APPSERVER )) {
				ApplianceManager mger = managers
						.get( SAManagerComponent.APPLIANCE_APPSERVER );
				AppManager appMger = mger.getAppManager();
				if (appMger == null) {
					logger.info( "AppManager is null" );
				}
				appMger.updateApps( applianceId, rs );
			} else {
				logger.warn( "The appliance type currently is not supported!" );
			}
		}
	}


	/**
	 * just Query the appliance, not delete one!
	 */
	@Override
	public void undeployAppliance( ApplianceUndeploymentEvent event )
			throws ApplianceException {

		// String undeployType = event.getType();
		String targetUndeployApplianceId = event.getTargetAppliance();
		// logger.info("Undeploy ")
		logger.info( "Undeploy the target appliance : "
				+ targetUndeployApplianceId );

		String hostId = getHostIdFromApplianceId( targetUndeployApplianceId );

		synchronized (SAManagerComponent.lock) {
			Host host = (Host) appliances.get( hostId );
			if (host == null) {
				event.setResult( false );
				event.setDesp( "Can't the host" + hostId
						+ " by the applianceId: " + targetUndeployApplianceId );
				return;
			}
			event.setResult( true );
			event.setHost( host );
		}

		// synchronized(managers){
		// ApplianceManager mger = managers.get(event.getType());
		// if(mger == null){
		// logger.warn("The target appliance manager type is not supported : " +
		// undeployType);
		// event.setResult(false);
		// event.setDesp("The target appliance manager type is not supported : "
		// + undeployType);
		// return;
		// }
		//
		// Appliance appliance =
		// mger.getApplianceById(targetUndeployApplianceId);
		// if(appliance == null){
		// logger.warn("The target appliance manager type is not supported : " +
		// undeployType);
		// event.setResult(false);
		// event.setDesp("The target appliance manager type is not supported : "
		// + undeployType);
		// return;
		// }
		// event.setHost(appliance);
		// Iterator<String> hostIds = this.deployedApps.keySet().iterator();
		// while(hostIds.hasNext()){
		//
		// }
		// }

		// mger.deleteApplianceById(event.getTargetAppliance());
	}


	public String getHostIdFromApplianceId( String applianceId )
			throws ApplianceException {

		int indexPrefix = applianceId.indexOf( "_" );
		int indexPort = applianceId.lastIndexOf( ":" );
		int indexHttp = applianceId.indexOf( ":" );

		if (indexPrefix == -1 || indexPort == indexHttp) {
			throw new ApplianceException( "The appliance id is invalidated : "
					+ applianceId );
		}

		String mid = applianceId.substring( indexPrefix, indexPort );
		return "host" + mid;
	}


	@Override
	public void registerAppliance( ApplianceRegisterEvent event )
			throws ApplianceException {

		Host host = event.getTargetAppliance();

		@SuppressWarnings("unchecked")
		List<AppReplica> reps = (List<AppReplica>) event.getHosteds();
		logger.info( "Receiving the Host Register Event : " + host );
		// update the hostedApps
		synchronized (SAManagerComponent.lock) {
			updateHost( host );
			addHostedApps( reps );
		}

		// }else{
		// logger.warn("The registered appliance type is unsupported: "
		// + appliance.getClass().getSimpleName());
		// }

	}


	// /**
	// *
	// * @param applianceId
	// * @param hosteds
	// */
	// protected void addHostedApps(List<AppRepetition> reps){
	//
	// logger.info("Update the application repetition information while host register");
	//
	// //需要更新AppManager中的数据
	// Map<String, List<AppRepetition>> rs = new HashMap<String,
	// List<AppRepetition>>();
	// for(AppRepetition r : reps){
	// String applianceId = r.getContainerId();
	// if(rs.get(applianceId) == null){
	// List<AppRepetition> ls = new ArrayList<AppRepetition>();
	// rs.put(applianceId, ls);
	// }
	// rs.get(applianceId).add(r);
	//
	// //添加到deployedApps列表中,App添加进入的是服务名
	// if(deployedApps.get(applianceId) == null){
	// List<String> ls = new ArrayList<String>();
	// deployedApps.put(applianceId, ls);
	// }
	// deployedApps.get(applianceId).add(r.getAppName());
	// }
	//
	// //更新AppManager中的信息
	// Iterator<String> names = rs.keySet().iterator();
	// while(names.hasNext()){
	// String id = names.next();
	// appManager.updateApps(id, rs.get(id));
	//
	// }
	// }

	protected void updateHost( Host host ) {

		Calendar now = Calendar.getInstance();
		String applianceId = host.getDesp().getId();
		// addHosteds(applianceId, event.getHosteds());

		logger.info( "Before Locking host register: " + applianceId );

		synchronized (SAManagerComponent.lock) {

			if (appliances.get( applianceId ) == null) {// the newly registered
														// host
				logger.info( "The first register host: " + applianceId );
				appliances.put( applianceId, host );

				// deployedApps.put(applianceId, new ArrayList<String>());
				Map<String, List<Appliance>> childAppliances = host
						.getChildAppliances();
				List<String> applianceIds = new ArrayList<String>();

				logger.info( "Add " + applianceIds
						+ "sub appliances to the Host's indexing" );
				for (String applianceType : childAppliances.keySet()) {
					// updateAppliance(appliance, now);
					List<Appliance> childByType = childAppliances
							.get( applianceType );
					// applianceIds.add(appliance.getDesp().getId());
					// add new child appliance timestamp
					for (Appliance a : childByType) {
						applianceIds.add( a.getDesp().getId() );
						addSubAppliance( applianceType, host, a, now );
					}

				}
				// synchronized(deployedApps){

				// }
				deployedApps.put( applianceId, applianceIds );
				// update the timestamp
			} else {
				logger.info( "Locked Update the host" );
				// the host has been registered
				Host old = (Host) appliances.get( applianceId );

				// deployedApps.put(applianceId, new ArrayList<String>());
				Map<String, List<Appliance>> newChildren = host
						.getChildAppliances();
				Map<String, List<Appliance>> oldChildren = old
						.getChildAppliances();
				List<String> applianceIds = new ArrayList<String>();

				for (String applianceType : newChildren.keySet()) {
					// updateAppliance(appliance, now);
					// List<Appliance> childByType =
					// newChildren.get(applianceType);
					// applianceIds.add(appliance.getDesp().getId());
					// add new child appliance timestamp
					// for(Appliance a : childByType){
					// // applianceIds.add(a.getDesp().getId());
					// // addSubAppliance(applianceType, old, a, now);
					// updateSubAppliance(host);
					// }
					List<Appliance> news = newChildren.get( applianceType );
					List<Appliance> olds = oldChildren.get( applianceType );
					updateSubAppliance( host, applianceType, news, olds );

					for (Appliance a : news) {
						applianceIds.add( a.getDesp().getId() );
					}
				}
				// update the deployed sub appliance id list

				deployedApps.put( applianceId, applianceIds );
				// update the timestamp
			}
			appliances.put( applianceId, host );

			updateTimestamp( applianceId, now );
		}
	}


	@Override
	public void deployAppliance( ApplianceDeploymentEvent event )
			throws ApplianceException {

		logger.info( "Query available Host for deploy " + event.getDeployType() );

		// return the target Host's url and port
		// return the first one appliance
		// Iterator<Appliance> as = appliances.values().iterator();
		synchronized (SAManagerComponent.lock) {

			logger.info( "There are " + appliances.size() + "Host registered!" );

			// List<Appliance> aps = cloneApplianceList(new
			// ArrayList<Appliance>(appliances.values()));
			List<Appliance> aps = new ArrayList<Appliance>( appliances.values() );

			logger.info( "**Deploying** deployType:" + event.getDeployType() );
			// sort the appliances by its sub appliance
			PriorityQueue<Appliance> sorteds = scheduler.sortAppliance( aps,
					event.getDeployType() );

			logAllSortedAppliance( sorteds );

			event.setTargetAppliances( sorteds );

		}
	}


	protected void logAllSortedAppliance( PriorityQueue<Appliance> aps ) {
		Iterator<Appliance> it = aps.iterator();
		while (it.hasNext()) {
			Host a = (Host) it.next();
			List<Appliance> axis2s = a.getChildAppliances().get( "axis2" );
			int size;
			if (axis2s == null) {
				size = 0;
			} else {
				size = axis2s.size();
			}
			logger.info( "The appliance ID(" + a.getDesp().getId() + ") with "
					+ size + " children" );
		}
	}


	public void addSubAppliance( String applianceType, Appliance host,
			Appliance appliance, Calendar now ) {

		synchronized (SAManagerComponent.lock) {
			String subApplianceId = appliance.getDesp().getId();
			String hostId = host.getDesp().getId();

			// Host h = (Host)host;
			// Host h = (Host)appliances.get(hostId);
			// h.addChildAppliance(applianceType, appliance);

			logger.info( "Add a new Appliance to the host: " + subApplianceId );
			ApplianceManager applianceMger = managers.get( applianceType );

			if ("tomcat".equals( applianceType )) {
				applianceMger = managers.get( "appserver" );
			}
			if (applianceMger == null) {
				logger.warn( "The appliance manager for the type ("
						+ applianceType + ") does not exist!" );
				return;
			}

			// applianceMger.appliances.put(subApplianceId, appliance);
			applianceMger.addAppliance( appliance );

			// synchronized(deployedApps){

			// update the apps for the appliance
			List<String> deployeds = this.deployedApps.get( hostId );
			if (deployeds == null) {
				deployeds = new ArrayList<String>();
			}

			logger.info( "Deploy a new appliance : " + subApplianceId );
			deployeds.add( subApplianceId );
			this.deployedApps.put( hostId, deployeds );
			// }

			applianceMger.updateTimestamp( subApplianceId, now );
		}

	}


	/**
	 * Scan the Host
	 */
	public void scan( long max_internal ) {
		synchronized (SAManagerComponent.lock) {
			logger.info( "Scan the hosts Status!" );
			Calendar now = Calendar.getInstance();
			logger.info( "There are " + updates.size()
					+ "hosts registered now!" );
			Iterator<String> keys = updates.keySet().iterator();
			while (keys.hasNext()) {
				String id = keys.next();
				Calendar c = updates.get( id );
				if (now.getTimeInMillis() - c.getTimeInMillis() > max_internal) {
					logger.warn( "The appliance is expired : " + id
							+ ", so delete it!" );
					// the appliance is expired, so delete it,meantime,delete
					// the subappliances
					deleteApplianceById( id );
				}
			}

			// //heart beat
			// Iterator<String> ids = managers.keySet().iterator();
			// while(ids.hasNext()){
			// String type = ids.next();
			// logger.info("Scanning for the type of applianceManager: " +
			// type);
			// ApplianceManager mger = managers.get(type);
			// mger.scan(max_internal, now);
			// }

		}
	}
}
