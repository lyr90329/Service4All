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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContextManager;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;

public class SAManagerContextManager extends AppEngineContextManager {

	private final Log logger = LogFactory
			.getLog( SAManagerContextManager.class );


	protected BusinessUnit loadBusinessUnit( Node unitNode ) {
		if (unitNode == null) {
			logger.warn( "The business unit document is null" );
			return null;
		}

		NamedNodeMap attrs = unitNode.getAttributes();
		// String unitName = null;
		String className = null;
		String targetNamespace = null;

		// Node nameAtt = attrs.getNamedItem("name");
		// if(nameAtt != null){
		// unitName = nameAtt.getNodeValue();
		// }

		Node classAtt = attrs.getNamedItem( "class" );
		if (classAtt != null) {
			className = classAtt.getNodeValue();
		}

		Node nsAtt = attrs.getNamedItem( "targetNamespace" );
		if (nsAtt != null) {
			targetNamespace = nsAtt.getNodeValue();
		}

		if (className == null) {
			logger.error( "The class name of the business unit is null, so can't create one" );
			return null;
		}

		try {
			ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
			// oldcl.
			ClassLoader cl = context.getClassLoader();
			Thread.currentThread().setContextClassLoader( cl );

			// Class superClass =
			// Class.forName("org.act.sdp.appengine.cmp.BusinessUnit", true,
			// cl);
			// logger.info("Loading the class : " + className);
			Class<?> c = cl.loadClass( className );
			Object subObj = c.newInstance();
			Class<?> superClass = c.getSuperclass();
			logger.info( "The super class : " + superClass.getName() );
			Object superObj = superClass.cast( subObj );

			if (superObj instanceof SAManagerBusinessUnit) {
				SAManagerBusinessUnit u = (SAManagerBusinessUnit) superObj;
				u.setComponent( context.getComponent() );

				// add receiver
				Element unitEle = (Element) unitNode;
				// find the receiver child node
				// NodeList receiverNodes =
				// unitEle.getElementsByTagName("receiver");
				List<Element> receivers = findChildElementByName( unitEle,
						RECEIVER_ELEMENT );
				if (receivers != null && receivers.size() >= 1) {
					Node receNode = receivers.get( 0 );
					addReceiver( u, receNode );
				} else {
					logger.warn( "There is no receiver to be added!" );
				}

				// add invokers
				// NodeList invokersNodes =
				// unitEle.getElementsByTagName("invoker");
				List<Element> invokers = findChildElementByName( unitEle,
						INVOKER_ELEMENT );
				if (invokers != null && invokers.size() >= 1) {
					for (int i = 0; i < invokers.size(); i++) {
						Element invNode = invokers.get( i );
						addInvoker( u, invNode );
					}
				} else {
					logger.info( "There is no invoker to be added!" );
				}

				// setting the name and target namespace
				if (targetNamespace != null) {
					u.setTargetNamespace( targetNamespace );
				}

				// add the applianceManager and appManager
				addManagers( u, (Element) unitNode );

				Thread.currentThread().setContextClassLoader( oldcl );
				return u;
			} else {
				logger.warn( "The loaded class is not a type of BusinessUnit!" );
			}
		} catch (ClassNotFoundException e) {
			logger.error( "Can't find the business unit class", e );
			return null;
		} catch (IllegalAccessException e) {
			logger.error( "Can't instantiate the business unit class", e );
			return null;
		} catch (InstantiationException e) {
			logger.error( "Can't instantiate the business unit class", e );
			return null;
		}

		return null;
	}


	protected void addManagers( SAManagerBusinessUnit bu, Element unitElement ) {
		// logger.info("Add the appliance managers and app managers to SAManager BusinessUnit!");

		String appType = unitElement.getAttribute( "appType" );
		String applianceType = unitElement.getAttribute( "applianceType" );

		logger.info( "Add the App Manager(" + appType
				+ ") and Appliance Manager (" + applianceType
				+ ") to the BusinessUnit: " + bu.getClass().getSimpleName() );

		SAManagerComponent cmp = (SAManagerComponent) this.context
				.getComponent();

		if (appType != null && appType.length() > 0) {
			AppManager apMger = cmp.getAppManager( appType );
			if (apMger == null) {
				logger.warn( "Can't add the type of app manager to bu: "
						+ appType );

			} else {
				bu.setAppManager( apMger );
				// bu.getAppManager().setApplianceManager();
			}
		}

		if (applianceType != null && applianceType.length() > 0) {
			ApplianceManager applianceMger = cmp
					.getApplianceManager( applianceType );
			if (applianceMger == null) {
				logger.warn( "Can't add the type of appliance manager to bu: "
						+ applianceType );

			} else {
				bu.setApplianceManager( applianceMger );

			}
		}

	}

}
