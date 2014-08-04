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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker;

import java.util.Timer;

import javax.jbi.JBIException;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.database.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.scale.ClearDBTimeTask;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.scale.ReplicaScaleUpDown;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;
import cn.edu.buaa.act.service4all.core.component.AppEngineComponent;


public class WSInvokerComponent extends AppEngineComponent {

	Timer timerScale = new Timer(false);
	Timer timerClearDB = new Timer(false);

	public void stop() throws JBIException {

		ConnectionPool.getInstance().closePool();
		logger.info("WSInvokerComponent Error£¡");
		super.stop();
	}

	public void start() throws JBIException {
		super.start();
		
		if (WSInvokeConstants.getWSInvokeConstants().isScalable()) {
			ReplicaScaleUpDown scaleUpDown = new ReplicaScaleUpDown();
			timerScale.scheduleAtFixedRate(scaleUpDown, 0, WSInvokeConstants
					.getWSInvokeConstants().getPeriod());
			
			long clearPeriod = WSInvokeConstants       
			.getWSInvokeConstants().getClearDBPeriod();
			ClearDBTimeTask clearDB = new ClearDBTimeTask() ;
			timerClearDB.scheduleAtFixedRate(clearDB , clearPeriod, clearPeriod);
		}
		
	}
}
