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
package cn.edu.buaa.act.service4all.core.component.bri;

import javax.jbi.JBIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.common.endpoints.SimpleEndpoint;
import org.apache.servicemix.jbi.container.JBIContainer;
import org.apache.servicemix.jbi.framework.ComponentContextImpl;
import org.apache.servicemix.jbi.framework.Registry;
import cn.edu.buaa.act.service4all.core.component.AppEngineComponent;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;

/**
 * provide basic information of AppEngine such as workspace directory and some
 * other important reference
 * 
 * 
 * The AppEngineContext holds the class loader for the loading the BusienessUnit
 * 
 * @author Enqu
 * 
 */
public class AppEngineContext {
	private final Log logger = LogFactory.getLog(AppEngineContext.class);
	protected String rootpath;
	protected String cmpWorkspace;
	protected AppEngineComponent component;
	protected org.apache.servicemix.common.Registry cmpRegistry;
	protected ComponentContextImpl cmpCtx;
	protected Registry jbiRegistry;
	protected JBIContainer container;
	protected ClassLoader loader;

	public AppEngineContext() {

	}

	public AppEngineContext(AppEngineComponent component) {

	}

	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	public void initLoader() {
		this.loader = this.component.getClass().getClassLoader();
	}

	public ClassLoader getClassLoader() {
		if (this.component != null) {
			logger.info("The component is not null!");
			loader = this.component.getClass().getClassLoader();
		} else {
			logger.warn("There is available class loader!");
		}
		return loader;
	}

	public void setClassLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public String getCmpWorkspace() {
		return cmpWorkspace;
	}

	public void setCmpWorkspace(String cmpWorkspace) {
		this.cmpWorkspace = cmpWorkspace;
	}

	public AppEngineComponent getComponent() {
		return component;
	}

	public void setComponent(AppEngineComponent component) {
		this.component = component;
		this.cmpCtx = (ComponentContextImpl) this.component
				.getComponentContext();
		this.cmpRegistry = this.component.getRegistry();
		// this.cmpCtx =
		// (ComponentContextImpl)this.component.getComponentContext();
		if (this.cmpCtx == null) {
			logger.error("Can't get the component context implementation");
			return;
		}
		this.jbiRegistry = this.cmpCtx.getContainer().getRegistry();
		this.container = this.cmpCtx.getContainer();
	}

	public void addEndpoint(SimpleEndpoint endpoint) {
		cmpRegistry.registerEndpoint(endpoint);
		try {
			jbiRegistry.activateEndpoint(cmpCtx, endpoint.getService(),
					endpoint.getEndpoint());
			jbiRegistry.registerInterfaceConnection(
					endpoint.getInterfaceName(), endpoint.getService(),
					endpoint.getEndpoint());
		} catch (JBIException e) {
			logger.warn(
					"Can't register the endpoint: " + endpoint.getEndpoint(), e);
			// The next step to be adopted should be considered future
		}
	}
}
