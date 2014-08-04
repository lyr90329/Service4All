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
package cn.edu.buaa.act.service4all.core.component.messaging.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.axiom.om.OMElement;

public class AXIOMUtils {

	public static OMElement getChildElementByTagName(OMElement parent,
			String name) {
		Iterator<?> children = parent.getChildElements();
		while (children.hasNext()) {
			Object o = children.next();
			if (o instanceof OMElement) {
				OMElement e = (OMElement) o;
				if (e.getLocalName().equals(name)) {
					return e;
				}
			}
		}
		return null;
	}

	public static List<OMElement> getChildElementsByTagName(OMElement parent,
			String name) {
		List<OMElement> es = new ArrayList<OMElement>();
		Iterator<?> children = parent.getChildElements();
		while (children.hasNext()) {
			Object o = children.next();
			if (o instanceof OMElement) {
				OMElement e = (OMElement) o;
				if (e.getLocalName().equals(name)) {
					es.add(e);
				}
			}
		}
		return es;
	}
}
