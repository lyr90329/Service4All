/**
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
*/
package cn.edu.buaa.act.service4all.core.component;

import javax.jbi.JBIException;
import javax.jbi.component.Bootstrap;
import javax.jbi.component.ComponentContext;
import javax.jbi.component.InstallationContext;
import org.apache.servicemix.common.DefaultComponent;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContextManager;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.AppEngineException;

public class AppEngineComponent extends DefaultComponent implements Bootstrap {

	/**
	 * the manager for business units
	 */
	protected AppEngineContextManager manager;
	protected AppEngineContext engineContext = new AppEngineContext();
	protected InstallationContext installCtx;


	public void init( ComponentContext context ) throws JBIException {
		super.init( context );

		// initiate the engineContext
		engineContext.setComponent( this );
		logger.info( "The component workspace : " + context.getWorkspaceRoot() );
		engineContext.setCmpWorkspace( context.getWorkspaceRoot() );
		engineContext.setRootpath( context.getInstallRoot() );
		if (installCtx != null) {
			logger.info( "The installation root path : "
					+ installCtx.getInstallRoot() );
			engineContext.setRootpath( installCtx.getInstallRoot() );
		}
		initContextManager();
	}

	protected void initContextManager() {
		engineContext.initLoader();
		createNewAppEngineContextManager();
		try {
			manager.init( engineContext );
		} catch (AppEngineException e) {
			logger.error( "Can't initiate the BusinessUnit Manager", e );
		}
	}

	protected void createNewAppEngineContextManager() {
		manager = new AppEngineContextManager();
	}

	public void cleanUp() throws JBIException {

	}

	public void init( InstallationContext context ) throws JBIException {
		this.installCtx = context;
		if (installCtx != null) {
			logger.info( "The installation root path : "
					+ installCtx.getInstallRoot() );
			engineContext.setRootpath( installCtx.getInstallRoot() );
		}
	}
	
	public void onInstall() throws JBIException {

	}

	public void onUninstall() throws JBIException {

	}
}
