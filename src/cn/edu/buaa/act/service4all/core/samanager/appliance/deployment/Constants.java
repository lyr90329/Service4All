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

public class Constants {

	/**
	 * these constants refers to the message type received from SA Manager
	 */
	public static final int AppServerDeploy = 0;
	public static final int AppServerUnDeploy = 1;
	public static final int AppServerRestart = 2;
	public static final int Axis2Deploy = 3;
	public static final int Axis2UnDeploy = 4;
	public static final int Axis2Restart = 5;
	public static final int VmHostRestart = 9;

	public static String iplist = "ipList";
	public static String ip = "ip";
	public static String url = "url";

	// public static final int VMHost= 10;
	// public static final int AppServer = 11;
	// public static final int Axis2= 12;
	// public static final int BPMNEngine= 13;
	//
	// /**
	// * this constant refers to the saving operation
	// */
	// public static final int SAVE_OPERATION = "saveOpr";
	//
	// /**
	// * this constant refers to the deleting operation
	// */
	// public static final int DELETE_OPERATION = "deleteOpr";
	//
	// /**
	// * this constant refers to the getting operation
	// */
	// public static final int GET_OPERATION = "getOpr";
	//
	// /**
	// * this constant refers to the getting-max-element operation
	// */
	// public static final int GET_MAX_OPERATION = "getMaxOpr";
	// /**
	// * this constant refers to the getting-min-element operation
	// */
	// public static final int GET_MIN_OPERATION = "getMinOpr";
	// /**
	// * this constant refers to the getting-by-id operation
	// */
	// public static final int GET_BY_ID_OPERATION = "getByIdOpr";
	// /**
	// * this constant refers to the getting-by-name operation
	// */
	// public static final int GET_BY_NAME_OPERATION = "getByNameOpr";
	// /**
	// * this constant refers to the getting-all operation
	// */
	// public static final int GETALL_OPERATION = "getAllOpr";
	//
	// /**
	// * this constant refers to the getting operation which ignores the job id
	// */
	// ¡¢
	//
	// /**
	// * this constant indicates the local path of the config-file of the sql
	// * manager
	// */
	// public static final int LOCAL_SQL_MANAGE_PATH =
	// "conf\\sql-statements.xml";
}
