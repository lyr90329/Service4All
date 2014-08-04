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
package cn.edu.buaa.act.service4all.core.localsamanager.messagehandler;

import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public class SAMsgDecapsulate {
	
	private static Logger logger = Logger.getLogger( SAMsgDecapsulate.class );
	
	public int deployMsgDecp( OMElement saMsg ) {
		OMElement messageType = null;
		if (saMsg.getLocalName().equalsIgnoreCase( "Deploy" )) {
			for (Iterator iterator = saMsg.getChildElements(); iterator
					.hasNext();) {
				OMElement childElement = (OMElement) iterator.next();
				if (childElement.getLocalName().equalsIgnoreCase( "msgType" ))
					messageType = childElement;
			}
			if (messageType != null) {
				logger.error( "msgType is: " + messageType.getText()
						+ "\n" );
				return Integer.parseInt( messageType.getText() );
			}
		}
		return -1;
	}


	public int unDeployMsgDecp( OMElement saMsg ) {
		int messageType = -1;
//		OMElement port = null;
		if (saMsg.getLocalName().equalsIgnoreCase( "UnDeploy" )) {
			for (Iterator iterator = saMsg.getChildElements(); iterator
					.hasNext();) {
				OMElement childElement = (OMElement) iterator.next();
				if (childElement.getLocalName().equalsIgnoreCase( "msgType" ))
					messageType = Integer.parseInt(childElement.getText());
//				if (childElement.getLocalName().equalsIgnoreCase( "port" ))
//					port = childElement;
			}
			
		}
		return messageType;
	}


	public int reStartMsgDecp( OMElement saMsg ) {
		int messageType = -1;
//		OMElement port = null;
		if (saMsg.getLocalName().equalsIgnoreCase( "Restart" )) {
			for (Iterator iterator = saMsg.getChildElements(); iterator
					.hasNext();) {
				OMElement childElement = (OMElement) iterator.next();
				if (childElement.getLocalName().equalsIgnoreCase( "msgType" ))
					messageType = Integer.parseInt(childElement.getText());
//				if (childElement.getLocalName().equalsIgnoreCase( "port" ))
//					port = childElement;
			}
			
		}
		return messageType;
	}

}
