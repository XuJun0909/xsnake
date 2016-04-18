package xmouse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestClient {

	//第一种方式
//	public static void main(String[] args) {
//		ClientAccessFactory caf = new ClientAccessFactory("192.168.0.241:2181",10);
//		for(int i =0;i<20;i++){
//			IRemoteTest obj = caf.getService(IRemoteTest.class);
//			System.out.println(obj.sayHello("[Jerry]" + "    "));
//		}
//	}
	
	public static void main(String[] args) {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:client-application-context.xml");
		IRemoteTest remoteTest = (IRemoteTest)ctx.getBean("remoteTest");
//		IWechatService wechatService = (IWechatService)ctx.getBean("wechatService");
		try {
			remoteTest.sayHello(" [Jerry] "); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		wechatService.xxx();
	}
	
}
