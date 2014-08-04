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
package cn.edu.buaa.act.service4all.core.samanager.appliance.deployment;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class ApplianceDeploymentMsgUtil {

	/*
	 * public static OMElement msgEncp(Person man) { OMElement result = null;
	 * javax.xml.stream.XMLStreamReader reader = BeanUtil.getPullParser(man);
	 * StreamWrapper parser = new StreamWrapper(reader); StAXOMBuilder
	 * stAXOMBuilder = OMXMLBuilderFactory.createStAXOMBuilder(
	 * OMAbstractFactory.getOMFactory(), parser); OMElement element =
	 * stAXOMBuilder.getDocumentElement(); System.out.println(element); //
	 * result.addChild(element); return result;
	 * 
	 * }
	 */

	/**
	 * @param msgType
	 * @return
	 */
	public static OMElement deployMsgEncp( int msgType ) {
		/*
		 * <Deploy> <msgType>appServerDeploy </msgType> </Deploy>
		 */
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace( "", "" );
		OMElement saMsg = fac.createOMElement( "Deploy", omNs );
		OMElement messageType = fac.createOMElement( "msgType", omNs );
		messageType.setText( Integer.toString( msgType ) );

		saMsg.addChild( messageType );
		return saMsg;
	}


	public static OMElement unDeployMsgEncp( int msgType, String httpPort ) {
		/*
		 * <Deploy> <msgType>appServerDeploy</msgType> <port> 8080 </port>
		 * </Deploy>
		 */
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace( "", "" );
		OMElement saMsg = fac.createOMElement( "UnDeploy", omNs );
		OMElement messageType = fac.createOMElement( "msgType", omNs );
		messageType.setText( Integer.toString( msgType ) );
		OMElement port = fac.createOMElement( "port", omNs );
		port.setText( httpPort );

		saMsg.addChild( messageType );
		saMsg.addChild( port );
		return saMsg;
	}


	public static OMElement reStartMsgEncp( int msgType, String httpPort ) {
		/*
		 * <Deploy> <msgType>appServerRestart</msgType> <port> 8080 </port>
		 * </Deploy>
		 */
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace( "", "" );
		OMElement saMsg = fac.createOMElement( "Restart", omNs );
		OMElement messageType = fac.createOMElement( "msgType", omNs );
		messageType.setText( Integer.toString( msgType ) );
		OMElement port = fac.createOMElement( "port", omNs );
		port.setText( httpPort );

		saMsg.addChild( messageType );
		saMsg.addChild( port );
		return saMsg;
	}

	// /**
	// * @param args
	// */
	// public static void main(String[] args) {
	// // MsgType msgType = MsgType.appServerDeploy;
	// int msgType = Constants.AppServerDeploy;
	// String httpPort = "8008";
	// OMElement saMsg = deployMsgEncp(msgType);
	// System.out.println(deployMsgEncp(msgType));
	//
	// // MsgDecapsulate.saMsgDecp(saMsg);
	//
	// // Person man = new Person();
	// //
	// // man.setName("zilin");
	// // man.setAge(25);
	// // man.setAddress("beijing ");
	// // man.setPhonenum("13426332379");
	// // msgEncp(man);
	// // System.out.println("\n\n"+ man.getAge()+"\n\n");
	// //
	// // LinkedList <TomcatInfo> tomcatList = new LinkedList <TomcatInfo>();
	// // TomcatInfo tomcatInfo = new TomcatInfo ();
	// // tomcatInfo.setAjpPort( "7000");
	// // tomcatInfo.setHttpPort("5000");
	// // tomcatInfo.setBpmn(true);
	// // tomcatList.add(tomcatInfo);
	// // OMElement omElent = BeanUtil.getOMElement(new QName("registry"),
	// // tomcatList.toArray(), new QName ("tomcatInfo"), false, null);
	// // System.out.println(omElent);
	//
	// }

}
