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
package cn.edu.buaa.act.service4all.core.samanager;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.AppEngineException;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceListener;

public abstract class SAManagerBusinessUnit extends BusinessUnit {

	private final Log logger = LogFactory.getLog( SAManagerBusinessUnit.class );

	protected List<ApplianceListener> applianceListeners = new ArrayList<ApplianceListener>();
	protected List<AppListener> appListeners = new ArrayList<AppListener>();

	protected AppManager appManager;
	protected ApplianceManager applianceManager;


	@SuppressWarnings("unused")
	public void init( AppEngineContext context ) throws AppEngineException {

		super.init( context );
		 SAManagerComponent cmp = (SAManagerComponent)context.getComponent();

		if (appManager == null && applianceManager == null) {
			logger.warn( "The Appliance Manager and App Manager are null for "
					+ this.getClass().getSimpleName() );
			return;
		}

		logger.info( "Get the business unit's managers: "
				+ this.getClass().getSimpleName() );

		if (appManager != null) {

			logger.info( "Set the appManager("
					+ appManager.getClass().getSimpleName() + ")" );
			appManager.setApplianceManager( applianceManager );

		}

		if (applianceManager != null) {

			logger.info( "Set the applianceManager("
					+ applianceManager.getClass().getSimpleName() + ")" );
			applianceManager.setAppManager( appManager );

		}

//		 applianceListeners.add(applianceManager);
//		 appListeners.add(appManager);

	}


	public void addApplianceListener( ApplianceListener listener ) {
		this.applianceListeners.add( listener );
	}


	public void addAppListener( AppListener listener ) {
		this.appListeners.add( listener );
	}


	public List<ApplianceListener> getApplianceListeners() {
		return applianceListeners;
	}


	public void setApplianceListeners(
			List<ApplianceListener> applianceListeners ) {
		this.applianceListeners = applianceListeners;
	}


	public List<AppListener> getAppListeners() {
		return appListeners;
	}


	public void setAppListeners( List<AppListener> appListeners ) {
		this.appListeners = appListeners;
	}


	public AppManager getAppManager() {
		return appManager;
	}


	public ApplianceManager getApplianceManager() {
		return applianceManager;
	}


	public void setApplianceManager( ApplianceManager applianceManager ) {

		if (applianceManager == null) {
			return;
		}
		this.applianceManager = applianceManager;
		logger.info( "Add applianceManager to "
				+ this.getClass().getSimpleName() );
		this.applianceListeners.add( applianceManager );
	}


	public void setAppManager( AppManager appManager ) {
		if (appManager == null) {
			return;
		}else{
			this.appManager = appManager;
			logger.info( "Add appManager to " + this.getClass().getSimpleName() );
			this.appListeners.add( appManager );
		}
	}


	/**
	 * this is important debug information
	 * 
	 */
	public String toString() {
		String logStr = "{The Business Unit: "
				+ this.getClass().getSimpleName() + ";\n";
		logStr += "\tThe Receiver: "
				+ this.getReceiver().getClass().getSimpleName() + ";\n";

		// the invokers

		logStr += "\tThe ApplianceListeners: [\n";
		for (ApplianceListener applianceListener : applianceListeners) {
			logStr += "\t\t" + applianceListener.getClass().getSimpleName()
					+ ";\n";
		}

		logStr += "]\n The AppListener:[\n";
		for (AppListener appListener : appListeners) {
			logStr += "\t\t" + appListener.getClass().getSimpleName() + ";\n";
		}
		logStr += "]\n";
		return logStr;
	}
}
