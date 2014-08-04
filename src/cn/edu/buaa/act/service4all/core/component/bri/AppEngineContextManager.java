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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContextManager;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import cn.edu.buaa.act.service4all.core.component.EngineConstants;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.AppEngineException;

/**
 * manage all the business units in the AppEngine
 * 
 * @author Enqu
 * 
 */
public class AppEngineContextManager {
	private final Log logger = LogFactory.getLog(AppEngineContextManager.class);
	public static String RECEIVER_ELEMENT = "receiver";
	public static String INVOKER_ELEMENT = "invoker";
	public static String UNIT_ELEMENT = "unit";
	protected AppEngineContext context;
	protected Map<String, BusinessUnit> units;
	protected Map<String, Receiver> receivers;
	protected Map<String, Invoker> invokers;

	public AppEngineContextManager() {
		units = new HashMap<String, BusinessUnit>();
		receivers = new HashMap<String, Receiver>();
		invokers = new HashMap<String, Invoker>();
	}

	/**
	 * when initiate the BusinessUnit Manager, we create the business unit
	 * instances
	 * 
	 */
	public void init(AppEngineContext context) throws AppEngineException {
		this.context = context;
		try {
			logger.info("The component root path : " + context.getRootpath());
			String configPath = context.getRootpath()
					+ EngineConstants.APPENGINE_CONFIG;
			File configFile = new File(configPath);
			if (!configFile.exists()) {
				logger.warn("The AppEngine Configuration file ("
						+ configFile.getAbsolutePath() + ") doesn't exist!");
				throw new IOException("The AppEngine Configuration file ("
						+ configFile.getAbsolutePath() + ") doesn't exist!");
			}
			// loading the appEngine context
			loadContext(configFile);
			// invoke all the business unit's init method
			Collection<BusinessUnit> us = units.values();
			logger.info("Initiate the business units of " + us.size()
					+ " size!");
			for (BusinessUnit u : us) {
				u.init(context);
			}
			// logBusinessUnits();
			// init the filter chain
			// context.getChain().init(context);
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
		} catch (SAXException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	protected void loadContext(File configFile)
			throws ParserConfigurationException, SAXException, IOException {
		// parse the appengine.xml file
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = f.newDocumentBuilder();
		Document doc = builder.parse(configFile);
		// loading and register all the business unit
		if (doc.getElementsByTagName("units") == null
				|| doc.getElementsByTagName("units").getLength() <= 0) {
			logger.info("There is no units to be loaded!");
		} else {
			Element unitsElement = (Element) doc.getElementsByTagName("units")
					.item(0);
			// used to use method : addBusinessUnitsByConfigFile(configFile);
			loadBusinessUnits(unitsElement);
		}
	}

	protected void logBusinessUnits() {
		Set<Entry<String, BusinessUnit>> entries = units.entrySet();
		logger.info("There are " + entries.size() + "BusinessUnits!");
		Iterator<Entry<String, BusinessUnit>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<String, BusinessUnit> u = it.next();
			logger.info("The business unit: " + u.getKey());
			logger.info("\t the receiver: "
					+ u.getValue().getReceiver().getEndpoint());
			Set<Entry<String, Invoker>> is = u.getValue().getInvokers()
					.entrySet();
			Iterator<Entry<String, Invoker>> iterator = is.iterator();
			while (iterator.hasNext()) {
				logger.info("\t the invoker: " + iterator.next().getKey());
			}
		}
	}

	/**
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * 
	 */
	public void addBusinessUnitsByConfigFile(File configFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = f.newDocumentBuilder();
		Document doc = builder.parse(configFile);
		NodeList unitsList = doc.getElementsByTagName("units");
		if (unitsList == null || unitsList.getLength() <= 0) {
			logger.info("There is no units element!");
			return;
		}
		// load all the BusinessUnits
		Element unitsElement = (Element) unitsList.item(0);
		loadBusinessUnits(unitsElement);
	}

	/**
	 * 
	 * @param unitsElement
	 */
	protected void loadBusinessUnits(Element unitsElement) {
		NodeList unitNodes = unitsElement.getElementsByTagName(UNIT_ELEMENT);
		if (unitNodes == null || unitNodes.getLength() <= 0) {
			logger.warn("There is no business unit element configured!");
			return;
		}
		logger.info("There are  " + unitNodes.getLength() + " BusinessUnits");
		for (int i = 0; i < unitNodes.getLength(); i++) {
			Node unitNode = unitNodes.item(i);
			BusinessUnit unit = loadBusinessUnit(unitNode);
			if (unit != null) {
				// register the receivers and invokers when register the
				// business unit
				registerBusinessUnit(unit);
			} else {
				logger.warn("The business unit is null: " + i);
			}
		}
	}

	protected BusinessUnit loadBusinessUnit(Node unitNode) {
		if (unitNode == null) {
			logger.warn("The business unit document is null");
			return null;
		}
		NamedNodeMap attrs = unitNode.getAttributes();
		// String unitName = null;
		String className = null;
		String targetNamespace = null;
		Node classAtt = attrs.getNamedItem("class");
		if (classAtt != null) {
			className = classAtt.getNodeValue();
		}
		Node nsAtt = attrs.getNamedItem("targetNamespace");
		if (nsAtt != null) {
			targetNamespace = nsAtt.getNodeValue();
		}
		if (className == null) {
			logger.error("The class name of the business unit is null, so can't create one");
			return null;
		}

		try {
			ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
			// oldcl.
			ClassLoader cl = context.getClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			// Class superClass =
			// Class.forName("cn.edu.buaa.act.service4all.core.appengine.cmp.BusinessUnit",
			// true, cl);
			// logger.info("Loading the class : " + className);
			Class<?> c = cl.loadClass(className);
			Object subObj = c.newInstance();
			Class<?> superClass = c.getSuperclass();
			logger.info("The super class : " + superClass.getName());
			Object superObj = superClass.cast(subObj);

			if (superObj instanceof BusinessUnit) {
				BusinessUnit u = (BusinessUnit) superObj;
				u.setComponent(context.getComponent());
				// add receiver
				Element unitEle = (Element) unitNode;
				// find the receiver child node
				// NodeList receiverNodes =
				// unitEle.getElementsByTagName("receiver");
				List<Element> receivers = findChildElementByName(unitEle,
						RECEIVER_ELEMENT);
				if (receivers != null && receivers.size() >= 1) {
					Node receNode = receivers.get(0);
					addReceiver(u, receNode);
				} else {
					logger.warn("There is no receiver to be added!");
				}
				// add invokers
				// NodeList invokersNodes =
				// unitEle.getElementsByTagName("invoker");
				List<Element> invokers = findChildElementByName(unitEle,
						INVOKER_ELEMENT);
				if (invokers != null && invokers.size() >= 1) {
					for (int i = 0; i < invokers.size(); i++) {
						Element invNode = invokers.get(i);
						addInvoker(u, invNode);
					}
				} else {
					logger.info("There is no invoker to be added!");
				}
				// setting the name and target namespace
				if (targetNamespace != null) {
					u.setTargetNamespace(targetNamespace);
				}
				Thread.currentThread().setContextClassLoader(oldcl);
				return u;
			} else {
				logger.warn("The loaded class is not a type of BusinessUnit!");
			}
		} catch (ClassNotFoundException e) {
			logger.error("Can't find the business unit class", e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("Can't instantiate the business unit class", e);
			return null;
		} catch (InstantiationException e) {
			logger.error("Can't instantiate the business unit class", e);
			return null;
		}
		return null;
	}

	protected List<Element> findChildElementByName(Element parent,
			String tagName) {
		List<Element> elements = new ArrayList<Element>();
		if (parent.getNodeName().equals(tagName)) {
			elements.add(parent);
			return elements;
		}
		if (!parent.hasChildNodes()) {
			return elements;
		}
		NodeList ns = parent.getChildNodes();
		for (int i = 0; i < ns.getLength(); i++) {
			if (ns.item(i) instanceof Element) {
				Element e = (Element) ns.item(i);
				elements.addAll(findChildElementByName(e, tagName));
			}
		}
		return elements;
	}

	/**
	 * instantiate a receiver instance from the document node
	 * 
	 * @param unit
	 * @param node
	 */
	protected void addReceiver(BusinessUnit unit, Node node) {
		// the configuration way to handle business unit creation
		// should be implemented in future
		logger.info("Adding a receiver to the business unit!");
		NamedNodeMap attrs = node.getAttributes();
		String className = null;
		Node classAtt = attrs.getNamedItem("class");
		if (classAtt != null) {
			className = classAtt.getNodeValue();
		}
		if (className == null) {
			logger.error("The class name of the business unit is null, so can't create one");
			return;
		}
		try {
			ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
			ClassLoader cl = context.getClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			logger.info("Loading the  receiver class: " + className);
			Class<?> c = cl.loadClass(className);
			Object subObj = c.newInstance();
			Class<?> superClass = c.getSuperclass();
			Object o = superClass.cast(subObj);
			if (o instanceof Receiver) {
				Receiver re = (Receiver) o;
				// setting the properties
				re.setBusinessUnit(unit);
				re.configProperties(node);
				unit.setReceiver(re);
			} else {
				logger.warn("The loaded class is not a type of Receiver!");
			}
			Thread.currentThread().setContextClassLoader(oldcl);
		} catch (ClassNotFoundException e) {
			logger.error("Can't find the business unit class", e);
			return;
		} catch (IllegalAccessException e) {
			logger.error("Can't instantiate the business unit class", e);
			return;
		} catch (InstantiationException e) {
			logger.error("Can't instantiate the business unit class", e);
			return;
		}
	}

	/**
	 * instantiate invokers instance from the document node
	 * 
	 * <invoker> <target/> </invoker>
	 * 
	 * @param unit
	 * @param node
	 */
	protected void addInvoker(BusinessUnit unit, Node node) {
		logger.info("Adding a invoker to the business unit!");
		NamedNodeMap attrs = node.getAttributes();
		String className = null;
		Node classAtt = attrs.getNamedItem("class");
		if (classAtt != null) {
			className = classAtt.getNodeValue();
		}

		if (className == null) {
			logger.error("The class name of the business unit is null, so can't create one");
			return;
		}
		try {

			ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
			ClassLoader cl = context.getClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			logger.info("Loading the invoker class: " + className);
			Class<?> c = cl.loadClass(className);
			Object subObj = c.newInstance();
			Class<?> superClass = c.getSuperclass();
			Object o = superClass.cast(subObj);
			if (o instanceof Invoker) {
				Invoker inv = (Invoker) o;
				// setting the properties
				inv.setBuisnessUnit(unit);
				inv.configProperties(node);
				unit.addInvoker(inv);
			} else {
				logger.warn("The loaded class is not a type of Receiver!");
			}
			Thread.currentThread().setContextClassLoader(oldcl);
		} catch (ClassNotFoundException e) {
			logger.error("Can't find the business unit class", e);
			return;
		} catch (IllegalAccessException e) {
			logger.error("Can't instantiate the business unit class", e);
			return;
		} catch (InstantiationException e) {
			logger.error("Can't instantiate the business unit class", e);
			return;
		}
	}

	/**
	 * return all the business units
	 * 
	 * @return
	 */
	public Collection<BusinessUnit> getAllUnits() {
		logger.info("Get all the business whose size is : " + units.size());
		return units.values();
	}

	public BusinessUnit getBusinessUnitByName(String name) {
		if (name == null) {
			logger.error("The business unit's name is null!");
			return null;
		}
		return units.get(name);
	}

	/**
	 * use the BusinessUnit's class name as the map's key such as
	 * java.util.HashMap's map's key is "HashMap"
	 * 
	 * @param unit
	 */
	public void registerBusinessUnit(BusinessUnit unit) {
		if (unit == null) {
			logger.error("The business unit is null!");
			return;
		}
		Class<? extends BusinessUnit> c = unit.getClass();
		String className = c.getSimpleName();
		registerBusinessUnit(className, unit);
	}

	protected void registerBusinessUnit(String name, BusinessUnit unit) {
		if (name == null || unit == null) {
			logger.error("The business unit or its name is null!");
			return;
		}
		logger.info("Register a business unit whose name is " + name);
		units.put(name, unit);
		// register the unit's receiver and invokers
		// use the endpoint's name as the receiver's name
		Receiver receiver = unit.getReceiver();
		String receiverName;
		Class<?> c = receiver.getClass();
		receiverName = c.getSimpleName();
		this.registerReceiver(receiverName, receiver);
		// register the provider
		Set<Entry<String, Invoker>> entries = unit.getInvokers().entrySet();
		Iterator<Entry<String, Invoker>> ens = entries.iterator();
		while (ens.hasNext()) {
			Entry<String, Invoker> e = ens.next();
			String invName = e.getKey();
			Invoker inv = e.getValue();
			registerInvoker(invName, inv);
		}
	}

	protected void registerReceiver(String name, Receiver receiver) {
		if (name == null || receiver == null) {
			logger.error("The receiver or its name is null, so can't be registered");
			return;
		}
		if (receivers.get(name) != null) {
			logger.warn("The receiver of the name( " + name + " ) has existed");
			return;
		}
		logger.info("Put a receiver( " + name + " )");
		receivers.put(name, receiver);
		context.addEndpoint(receiver);
	}

	protected void registerInvoker(String name, Invoker invoker) {
		if (name == null || invoker == null) {
			logger.error("The invoker or its name is null, so can't be registered");
			return;
		}

		if (invokers.get(name) != null) {
			logger.warn("The invoker of the name( " + name + " ) has existed");
			return;
		}
		logger.info("Put a invoker( " + name + " )");
		invokers.put(name, invoker);
		context.addEndpoint(invoker);
	}

	// ////////Just for Test//////////////////////
	public void setAppEngineContext(AppEngineContext context) {
		this.context = context;
	}
}
