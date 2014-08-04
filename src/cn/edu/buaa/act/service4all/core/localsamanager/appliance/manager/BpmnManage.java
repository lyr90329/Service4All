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
	 * bpmn���沿��ģ�飬����SA Manager��bpmn���沿��������ע������²���һ��bpmn����
	 * ��Ϣ����ģ���ж���Ϣ��bpmn���沿����Ϣ�󣬵��ø�ģ�����bpmn���沿�����
	 * �������ϴ����Ѽ������axis2��������δ����bpmn�����tomcat������
	 * ��bpmn����ԭ�ӷ����ļ���������tomcat��axis2��ӦĿ¼�У�tomcat��ʵ��bpmn������Ȳ���
	 * 
	 * ������δ����bpmn�����axis2�����ļ���̬tomcat
	 * ����Axis2Manage.existTomcatWithNoAxis2()����axis2,�ٲ���bpmn����
	 * 
	 * ����������������ڣ�����Axis2Manage.createTomcatWithAxis2()����axis2ԭ�ӷ����������ٲ���bpmn����
	 * 
	 */
	@Override
	public OMElement deploy() {
		logger.info("prepare to deploy BPMNEngine");
		// String result = "";
		// �����ϴ����Ѽ����Ҳ�����axis2ԭ�ӷ�����������δ����bpmn�����tomcat����
		// if (!existTomcatWithAxis2(0))
		// // �����ϲ������Ѽ����δ����axis2ԭ�ӷ���������tomcat����
		// if (!existTomcat())
		// // �´tomcat/axis2 �������ٲ���BPMN����
		long beginTime = System.currentTimeMillis();
		TomcatInfo tomcatInfo = createTomcatWithBpmn();
		if (tomcatInfo==null){
			logger.warn("[System Infomation][BpmnEngine Manage]"
					+ "BpmnEngine deployment failed");
		}
		// System.out.println("����ʧ��");
		
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
	 * ����bpmnԭ�ӷ����ļ�����tomcat��axis2��Ӧ·����,���bpmn������Ȳ��� �ɹ�������tomcatList�б�����true
	 * ���ɹ�������false
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

			// ״̬���
			bpmnHtml = bpmnHtml + tomcatInfo.getPort()
					+ "/axis2/services/BpmnEngineService?wsdl";
			if (SharedFunction.activeCheckByHtml(
					statusCheckTimeout, bpmnHtml)) {
//				System.out.println("Successfully at deploy bpmn file");
				logger.info( "BPMNEngine deployment successful" );
				return true;
			} else {// BPMN ����δ��ɣ�ɾ��BPMN�����ļ�
				FileManipulationFactory.newFileDeletion()
						.delFile(tomcatInfo.getTomcatPath()
								+ "/webapps/axis2/WEB-INF/services/BpmnEngine.aar");
				logger.info( "BPMN deployment failed" );
			}// else BPMN ����δ���
		} catch (IOException e) {
			logger.error( "error at copying bpmn file" );
			e.printStackTrace();
		}// catch
		return false;
	}

	/**
	 * �����������������
	 * axis2Manage��createTomcatWithAxis2()�������´һ��tomcat/axis2�������ٲ���bpmn����
	 * �ɹ�����axis2��bpmnSubDeploy(TomcatInfo tomcatInfo)����bpmn����
	 * �ɹ�������tomcatList�б�����true ���ɹ�������false
	 * 
	 */
	private TomcatInfo createTomcatWithBpmn() {
		synchronized (ShareTomcatList.getNewInstance()) {
			TomcatInfo tomcatInfo = Axis2Manager.createTomcatWithAxis2(0);
			if (tomcatInfo!=null) {// ����axis2
														// ����ӵ�tomcat�����Ϣ
				logger.info( "Axis2 deployment successful,prepare to deploy BPMN" );
				if (bpmnSubDeploy(tomcatInfo)) {// ����bpmn����
												// ����BPMN����ע����Ϣ

					// ����tomcat��¼tomcatManageService.tomcatList�������BPMN������Ϣ
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
					// .println("BPMN����ɹ������´һ��tomcat/axis2�������ٲ���bpmn����");
					return tomcatInfo;
				}// if ����bpmn����
				else {// BPMN ����δ��ɣ�ɾ��tomcatList�б�������˿��б�ĺ����ɾ��axis2��BPMN�����ļ�
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
				}// else BPMN ����δ���
			}// if /����axis2
			return null;
		}
	}

	/**
	 * bpmn���淴����ģ�飬����SA Manager��bpmn���淴����������ע����Ϸ�����һ��bpmn����
	 * ��Ϣ����ģ���ж���Ϣ��bpmn���淴������Ϣ�󣬵��ø�ģ�����bpmn���淴�������
	 * ���ݷ�����������Ϣ�в�����bpmn����˿ںţ���tomcat�б�����ȡ��������·�� ֱ��ɾ��ɾ��bpmn���������ļ����޸�tomcat�б�
	 * ���ɹ������� ���򣬷���
	 * 
	 * @author menglinlin
	 */
	@Override
	public OMElement undeploy(String port) {
//		synchronized (ShareTomcatList.getNewInstance()) {
//			String bpmnHtml = "http://localhost:";
//			String result = "";
//			// ���ݷ�����������Ϣ������tomcat�˿ںţ���tomcat�б�����ȡ��tomcat��·��
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
//						&& tomcatInfo.getStatus().equalsIgnoreCase("active")) {// ������axis2
//					// ������bpmn����
//					// ɾ��bpmn�����aar�ļ�
//					FileManipulationFactory.newFileDeletion()
//							.delFile(tomcatInfo.getTomcatPath()
//									+ "/webapps/axis2/WEB-INF/services/BpmnEngine.aar");
//					bpmnHtml = bpmnHtml + tomcatInfo.getHttpPort()
//							+ "/axis2/services/BpmnEngine?wsdl";
//					if (SharedFunction.inActiveCheckByHtml(
//							SAManageService.satusCheckTimeout, bpmnHtml)) {// inActive״̬���\\webapps\\axis2\\WEB-INF\\services
//																			// ����BPMNע����Ϣ
//
//						// �޸�tomcat�б�
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
//								+ "BPMN������ɹ����˿�Ϊ" + SAManageService.httpPort
//								+ "Ŀ¼Ϊ" + tomcatInfo.getTomcatPath());
//						// System.out.println("BPMN������ɹ����˿�Ϊ"
//						// + SAManageService.httpPort + "Ŀ¼Ϊ"
//						// + tomcatInfo.getTomcatPath());
//						return true;
//					}// if inActive״̬��⣬BPMNֹͣ
//				}// tomcat�в�����axis2���� ��bpmn����
//			}// if ( tomcatInfo!= null)
//				// ���ͷ�����ʧ����Ϣ
//			SADeploymentMsg bpmnUndeploymentMsg = new SADeploymentMsg();
//			bpmnUndeploymentMsg.setDeploymentIsSuccessful("false");
//			bpmnUndeploymentMsg.setUnDeploymentDescription("BPMN������ʧ��");
//			bpmnDeploymentOMElement = bpmnUndeploymentMsg
//					.createSAUndeploymentResponse(tomcatInfo, "bpmnengine");
//			logger.warn("[System Infomation][BpmnEngine Manage]"
//					+ "BPMN������ʧ�ܣ��˿�Ϊ" + SAManageService.httpPort + "Ŀ¼Ϊ"
//					+ tomcatInfo.getTomcatPath());
//			// System.out.println("BPMN������ʧ�ܣ��˿�Ϊ" + SAManageService.httpPort
//			// + "Ŀ¼Ϊ" + tomcatInfo.getTomcatPath());
//			return false;
//		}
		return null;
	}

	/**
	 * bpmn��������ģ�飬����SA Manager��bpmn��������������ע���������һ��bpmn����
	 * ��Ϣ����ģ���ж���Ϣ��bpmn����������Ϣ�󣬵��ø�ģ�����bpmn������������
	 * ��������������Ϣ�в�����bpmn����˿ںţ���tomcat�б�����ȡ��������·�� ����״̬��⣬ ��BPMN��������������������ʱ����״̬��Ϣ
	 * ��BPMN�����쳣������tomcat����������󣬷��������ɹ���Ϣ ���ɹ������� ���򣬷���
	 * 
	 * @author menglinlin
	 */
	@Override
	public OMElement restart(String port) {
//		synchronized (ShareTomcatList.getNewInstance()) {
//			String result = "";
//			String bpmnHtml = "http://localhost:";
//			// ���ݷ�����������Ϣ������tomcat�˿ںţ���tomcat�б�����ȡ��tomcat��·��T
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
//																				// ״̬���,bpmn����������쳣����������tomcat
//					String startupPath = tomcatInfo.getTomcatPath()
//							+ "/bin/startup.sh";
//					new TomcatControler().startTomcat(startupPath);
//					if (SharedFunction.activeCheckByHtml(
//							SAManageService.satusCheckTimeout, bpmnHtml)) {// active
//																			// ״̬��⣬BPMN���������ɹ�
//																			// ����tomcat�б�tomcatManageService.tomcatList
//						ShareTomcatList.getNewInstance()
//								.getTomcatInfo(tomcatInfo.getTomcatIndex())
//								.setStatus("Active");
//						System.out.println("BPMN restart success�� port at��"
//								+ SAManageService.httpPort);
//						result = "true";
//
//						// �������������Ϣ
//
//						return true;
//					}// if active״̬���,bpmn���������ɹ�
//				}// if inActive״̬���,bpmn����ȷʵ�ǳ������쳣
//				else {// ��·��ʱ�����״̬��Ϣ���䶪ʧ
//						// �ط�״̬��Ϣ��
//					System.out.println("�ط�״̬��Ϣ��BPMN restart success�� port at��"
//							+ SAManageService.httpPort);
//					return true;
//				}// else
//			}// if tomcatInfo !=null
//				// ��������ʧ����Ϣ
//			result = "����ʧ�ܣ�bpmn does not exist failed";
//			System.out.println(result);
//			return false;
//		}
		return null;
	}

}
