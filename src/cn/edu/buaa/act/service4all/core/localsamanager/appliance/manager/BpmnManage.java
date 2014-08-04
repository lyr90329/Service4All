package cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.registry.SADeploymentMsg;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileManipulationFactory;

public class BpmnManage extends SAManager {

	public static OMElement bpmnDeploymentOMElement = null;
	private static Logger logger = Logger.getLogger(BpmnManage.class);
	private static final String bpmnSourcePath = Constants.BPMN_SOURCE_PATH;
	private final static long statusCheckTimeout = Constants.STATUS_CHECK_TIMEOUT;

	public BpmnManage() {
		System.getProperty("user.dir").replace('\\', '/');
		PropertyConfigurator.configure(System.getProperty("user.dir").replace(
				'\\', '/')
				+ "/log/" + "log4j2.properties");
		logger.debug("[System Information][BpmnEngine Manage]Begin Logging");
	}

	/**
	 * bpmn引擎部署模块，根据SA Manager的bpmn引擎部署请求，在注册机上新部署一个bpmn引擎
	 * 消息处理模块判断消息是bpmn引擎部署消息后，调用该模块完成bpmn引擎部署操作
	 * 若机器上存在已激活部署有axis2容器但尚未部署bpmn引擎的tomcat环境，
	 * 则将bpmn引擎原子服务文件拷贝至该tomcat的axis2相应目录中，tomcat可实现bpmn引擎的热部署
	 * 
	 * 若存在未部署bpmn引擎和axis2容器的激活态tomcat
	 * 则先Axis2Manage.existTomcatWithNoAxis2()部署axis2,再部署bpmn引擎
	 * 
	 * 若以上情况均不存在，则先Axis2Manage.createTomcatWithAxis2()部署axis2原子服务容器，再部署bpmn引擎
	 * 
	 */
	@Override
	public OMElement deploy() {
		logger.info("prepare to deploy BPMNEngine");
		// String result = "";
		// 机器上存在已激活且部署有axis2原子服务容器但尚未部署bpmn引擎的tomcat环境
		// if (!existTomcatWithAxis2(0))
		// // 机器上不存在已激活但尚未部署axis2原子服务容器的tomcat环境
		// if (!existTomcat())
		// // 新搭建tomcat/axis2 环境，再部署BPMN引擎
		long beginTime = System.currentTimeMillis();
		TomcatInfo tomcatInfo = createTomcatWithBpmn();
		if (tomcatInfo==null){
			logger.warn("[System Infomation][BpmnEngine Manage]"
					+ "BpmnEngine deployment failed");
		}
		// System.out.println("部署失败");
		
		String deployPort = String.valueOf(tomcatInfo.getPort());
		long saDeployTime = System.currentTimeMillis() - beginTime;
		logger.info( "[System Information][Management Operation][BpmnEngine Deploy]"
						+ "BpmnEngine deployment successful,information feedback:"
						+ BpmnManage.bpmnDeploymentOMElement.toString() );
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String hostIP = addr.getHostAddress().toString();
			OMFactory omf = OMAbstractFactory.getOMFactory();
			OMElement saUpdateMsg = omf.createOMElement( "update", null );
			OMAttribute hostIPAttr = omf.createOMAttribute( "ip", null, hostIP );
			saUpdateMsg.addAttribute( hostIPAttr );
			OMElement deployElement = omf.createOMElement( "deploy", null );
			OMAttribute applianceType = omf.createOMAttribute( "type", null,
					"bpmnengine" );
			OMAttribute appliancePort = omf.createOMAttribute( "port", null,
					deployPort );
			OMAttribute deployTime = omf.createOMAttribute( "startTime", null,
					Long.toString( saDeployTime ) );
			deployElement.addAttribute( applianceType );
			deployElement.addAttribute( appliancePort );
			deployElement.addAttribute( deployTime );
			saUpdateMsg.addChild( deployElement );
			// sendUpdateMsg(sendURL, saUpdateMsg, "saDeployUpdate");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return BpmnManage.bpmnDeploymentOMElement;
	}

	/**
	 * 复制bpmn原子服务文件至该tomcat的axis2相应路径下,完成bpmn引擎的热部署 成功，更新tomcatList列表，返回true
	 * 不成功，返回false
	 * 
	 * @author menglinlin
	 */
	private boolean bpmnSubDeploy(TomcatInfo tomcatInfo) {
		String bpmnHtml = "http://localhost:";
		try {
			logger.info( bpmnSourcePath );
			logger.info( tomcatInfo.getTomcatPath() );
			File bpmnFile = new File(bpmnSourcePath);
			FileManipulationFactory.newFileReproduction().copyFile(
					bpmnFile.getParent(),
					bpmnFile,
					tomcatInfo.getTomcatPath()
							+ "/webapps/axis2/WEB-INF/services" );

			// 状态检测
			bpmnHtml = bpmnHtml + tomcatInfo.getPort()
					+ "/axis2/services/BpmnEngineService?wsdl";
			if (SharedFunction.activeCheckByHtml(
					statusCheckTimeout, bpmnHtml)) {
//				System.out.println("Successfully at deploy bpmn file");
				logger.info( "BPMNEngine deployment successful" );
				return true;
			} else {// BPMN 部署未完成，删除BPMN引擎文件
				FileManipulationFactory.newFileDeletion()
						.delFile(tomcatInfo.getTomcatPath()
								+ "/webapps/axis2/WEB-INF/services/BpmnEngine.aar");
				logger.info( "BPMN deployment failed" );
			}// else BPMN 部署未完成
		} catch (IOException e) {
			logger.error( "error at copying bpmn file" );
			e.printStackTrace();
		}// catch
		return false;
	}

	/**
	 * 以上两种情况不存在
	 * axis2Manage的createTomcatWithAxis2()方法重新搭建一个tomcat/axis2环境，再部署bpmn引擎
	 * 成功部署axis2后，bpmnSubDeploy(TomcatInfo tomcatInfo)部署bpmn引擎
	 * 成功，更新tomcatList列表，返回true 不成功，返回false
	 * 
	 */
	private TomcatInfo createTomcatWithBpmn() {
		synchronized (ShareTomcatList.getNewInstance()) {
			TomcatInfo tomcatInfo = Axis2Manager.createTomcatWithAxis2(0);
			if (tomcatInfo!=null) {// 部署axis2
														// 新添加的tomcat相关信息
				logger.info( "Axis2 deployment successful,prepare to deploy BPMN" );
				if (bpmnSubDeploy(tomcatInfo)) {// 部署bpmn引擎
												// 发送BPMN引擎注册信息

					// 更新tomcat记录tomcatManageService.tomcatList，添加新BPMN引擎信息
					tomcatInfo.setBpmnEngine( true );
					tomcatInfo.setAxis2(true);
					SADeploymentMsg bpmnDeploymentMsg = new SADeploymentMsg();
					bpmnDeploymentMsg.setDeploymentIsSuccessful("true");
					bpmnDeploymentOMElement = bpmnDeploymentMsg
							.createSADeploymentResponse(tomcatInfo,
									"bpmnengine");
					logger.debug("[System Infomation][BpmnEngine Manage]"
							+ "BPMN deployment successful,rebuild tomcat/axis2 environment and bpmnengine");
					// System.out
					// .println("BPMN部署成功，重新搭建一个tomcat/axis2环境，再部署bpmn引擎");
					return tomcatInfo;
				}// if 部署bpmn引擎
				else {// BPMN 部署未完成，删除tomcatList列表的最后项，端口列表的后三项，删除axis2及BPMN引擎文件
//					ShareTomcatList.getNewInstance().removeTomcat(size - 1);
					ShareTomcatList.getNewInstance().removePort( tomcatInfo.getPort() );
					int length = ShareTomcatList.getNewInstance()
							.getPortListSize();
					for (int i = 0; i < 3; i++)
						ShareTomcatList.getNewInstance().removePort(
								length - i - 1);
					SADeploymentMsg bpmnDeploymentMsg = new SADeploymentMsg();
					bpmnDeploymentMsg.setDeploymentIsSuccessful("false");
					bpmnDeploymentMsg
							.setDeploymentDescription("BPMN deployment failed,rebuild tomcat/axis2 environment and bpmnengine");
					bpmnDeploymentOMElement = bpmnDeploymentMsg
							.createSADeploymentResponse(tomcatInfo,
									"bpmnengine");
					logger.warn("[System Infomation][BpmnEngine Manage]"
							+ "Bpmn deployment failed");
				}// else BPMN 部署未完成
			}// if /部署axis2
			return null;
		}
	}

	/**
	 * bpmn引擎反部署模块，根据SA Manager的bpmn引擎反部署请求，在注册机上反部署一个bpmn引擎
	 * 消息处理模块判断消息是bpmn引擎反部署消息后，调用该模块完成bpmn引擎反部署操作
	 * 根据反部署请求消息中参数的bpmn引擎端口号，从tomcat列表中提取出容器的路径 直接删除删除bpmn引擎的相关文件，修改tomcat列表
	 * 若成功，返回 否则，返回
	 * 
	 * @author menglinlin
	 */
	@Override
	public OMElement undeploy(String port) {
//		synchronized (ShareTomcatList.getNewInstance()) {
//			String bpmnHtml = "http://localhost:";
//			String result = "";
//			// 根据反部署请求消息参数的tomcat端口号，从tomcat列表中提取出tomcat的路径
//			TomcatInfo tomcatInfo = null;
//			if (SharedFunction.getTomcatPath(SAManageService.httpPort) != null) {
//				tomcatInfo = new TomcatInfo();
//				tomcatInfo = new TomcatInfo(
//						SharedFunction.getTomcatPath(SAManageService.httpPort));
//				for (int i = 0; i < ShareTomcatList.getNewInstance()
//						.getTomcatListSize(); i++) {
//					if (ShareTomcatList.getNewInstance().getTomcatInfo(i)
//							.getHttpPort()
//							.equalsIgnoreCase(SAManageService.httpPort))
//						tomcatInfo.setTomcatIndex(i);
//				}
//			}
//			if (tomcatInfo != null) {
//				if (tomcatInfo.isAxis2() && tomcatInfo.isBpmn()
//						&& tomcatInfo.getStatus().equalsIgnoreCase("active")) {// 部署有axis2
//					// 容器及bpmn引擎
//					// 删除bpmn引擎的aar文件
//					FileManipulationFactory.newFileDeletion()
//							.delFile(tomcatInfo.getTomcatPath()
//									+ "/webapps/axis2/WEB-INF/services/BpmnEngine.aar");
//					bpmnHtml = bpmnHtml + tomcatInfo.getHttpPort()
//							+ "/axis2/services/BpmnEngine?wsdl";
//					if (SharedFunction.inActiveCheckByHtml(
//							SAManageService.satusCheckTimeout, bpmnHtml)) {// inActive状态检测\\webapps\\axis2\\WEB-INF\\services
//																			// 发送BPMN注销信息
//
//						// 修改tomcat列表
//						tomcatInfo.setBpmn(false);
//						ShareTomcatList.getNewInstance()
//								.getTomcatInfo(tomcatInfo.getTomcatIndex())
//								.setBpmn(false);
//						ShareTomcatList.getNewInstance().setTomcatInfo(
//								tomcatInfo.getTomcatIndex(), tomcatInfo);
//						SADeploymentMsg bpmnUndeploymentMsg = new SADeploymentMsg();
//						bpmnUndeploymentMsg.setDeploymentIsSuccessful("true");
//						bpmnDeploymentOMElement = bpmnUndeploymentMsg
//								.createSAUndeploymentResponse(tomcatInfo,
//										"bpmnengine");
//						logger.debug("[System Infomation][BpmnEngine Manage]"
//								+ "BPMN反部署成功，端口为" + SAManageService.httpPort
//								+ "目录为" + tomcatInfo.getTomcatPath());
//						// System.out.println("BPMN反部署成功，端口为"
//						// + SAManageService.httpPort + "目录为"
//						// + tomcatInfo.getTomcatPath());
//						return true;
//					}// if inActive状态检测，BPMN停止
//				}// tomcat中部署有axis2容器 及bpmn引擎
//			}// if ( tomcatInfo!= null)
//				// 发送反部署失败信息
//			SADeploymentMsg bpmnUndeploymentMsg = new SADeploymentMsg();
//			bpmnUndeploymentMsg.setDeploymentIsSuccessful("false");
//			bpmnUndeploymentMsg.setUnDeploymentDescription("BPMN反部署失败");
//			bpmnDeploymentOMElement = bpmnUndeploymentMsg
//					.createSAUndeploymentResponse(tomcatInfo, "bpmnengine");
//			logger.warn("[System Infomation][BpmnEngine Manage]"
//					+ "BPMN反部署失败，端口为" + SAManageService.httpPort + "目录为"
//					+ tomcatInfo.getTomcatPath());
//			// System.out.println("BPMN反部署失败，端口为" + SAManageService.httpPort
//			// + "目录为" + tomcatInfo.getTomcatPath());
//			return false;
//		}
		return null;
	}

	/**
	 * bpmn引擎重启模块，根据SA Manager的bpmn引擎重启请求，在注册机上重启一个bpmn引擎
	 * 消息处理模块判断消息是bpmn引擎重启消息后，调用该模块完成bpmn引擎重启操作
	 * 根据重启请求消息中参数的bpmn引擎端口号，从tomcat列表中提取出容器的路径 先做状态检测， 若BPMN引擎正常，不做处理，即时反馈状态信息
	 * 若BPMN引擎异常，重启tomcat，检测正常后，返回重启成功信息 若成功，返回 否则，返回
	 * 
	 * @author menglinlin
	 */
	@Override
	public OMElement restart(String port) {
//		synchronized (ShareTomcatList.getNewInstance()) {
//			String result = "";
//			String bpmnHtml = "http://localhost:";
//			// 根据反部署请求消息参数的tomcat端口号，从tomcat列表中提取出tomcat的路径T
//			TomcatInfo tomcatInfo = null;
//			if (SharedFunction.getTomcatPath(SAManageService.httpPort) != null) {
//				tomcatInfo = new TomcatInfo();
//				tomcatInfo = new TomcatInfo(
//						SharedFunction.getTomcatPath(SAManageService.httpPort));
//			}
//			if (tomcatInfo != null) {
//				bpmnHtml = bpmnHtml + tomcatInfo.getHttpPort()
//						+ "/axis2/services/BpmnEngine?wsdl";
//				if (tomcatInfo.getStatus().equalsIgnoreCase("Active")
//						&& SharedFunction.inActiveCheckByHtml(
//								SAManageService.satusCheckTimeout, bpmnHtml)) {// inActive
//																				// 状态检测,bpmn引擎出现了异常，重新启动tomcat
//					String startupPath = tomcatInfo.getTomcatPath()
//							+ "/bin/startup.sh";
//					new TomcatControler().startTomcat(startupPath);
//					if (SharedFunction.activeCheckByHtml(
//							SAManageService.satusCheckTimeout, bpmnHtml)) {// active
//																			// 状态检测，BPMN引擎重启成功
//																			// 更新tomcat列表tomcatManageService.tomcatList
//						ShareTomcatList.getNewInstance()
//								.getTomcatInfo(tomcatInfo.getTomcatIndex())
//								.setStatus("Active");
//						System.out.println("BPMN restart success， port at："
//								+ SAManageService.httpPort);
//						result = "true";
//
//						// 返回重启结果信息
//
//						return true;
//					}// if active状态检测,bpmn引擎重启成功
//				}// if inActive状态检测,bpmn引擎确实是出现了异常
//				else {// 网路延时等造成状态信息传输丢失
//						// 重发状态信息？
//					System.out.println("重发状态信息，BPMN restart success， port at："
//							+ SAManageService.httpPort);
//					return true;
//				}// else
//			}// if tomcatInfo !=null
//				// 发送重启失败信息
//			result = "重启失败，bpmn does not exist failed";
//			System.out.println(result);
//			return false;
//		}
		return null;
	}

}
