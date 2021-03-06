package org.xsnake.remote;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.xsnake.remote.server.XSnakeContext;
 
public class XSnakeServerSocket extends ServerSocket { 

	List<String> trustAddress = new ArrayList<String>();
	boolean trust = false;
	
	XSnakeRMIAuthentication authentication;
	
    public XSnakeServerSocket(int port,List<String> trustAddress,XSnakeRMIAuthentication authentication) throws IOException {
        super(port);
        if(trustAddress!=null){
        	this.trustAddress.addAll(trustAddress);
        	this.trust = true;
        }
        this.authentication = authentication;
    }
    
    public Socket accept() throws IOException {
        Socket s = new Socket();
        implAccept(s);
        System.out.println("accept------"+Thread.currentThread().getId());
        String serverAddress = InetAddress.getLocalHost().getHostAddress();
        String clientAddress = s.getInetAddress().getHostAddress();
        if(trust && !serverAddress.equals(clientAddress)){
        	String socketAddress = s.getInetAddress().getHostAddress();
        	for(String address :trustAddress){
                boolean pass = Pattern.compile("^"+address).matcher(socketAddress).find();
                if(!pass){
                	XSnakeContext.getLogger().log4NotTrustAddress(s);
        			s.close();
        		}
        	}
        }
        InputStream in = s.getInputStream();
        DataInputStream data = new DataInputStream(in);
        int length = data.readInt();
        String username = null;
        String password = null;
        if(length > 0){
	        byte[] datas = new byte[length];
	        //读取验证数据
	        while(true){
	        	if(data.available() >= length){
			        data.read(datas);
			        String usernameAndPassword = new String(datas);
			        String[] up = usernameAndPassword.split(",");
			        username = up[0];
			        password = up[1];
			        break;
		        }
	        }
        }
        
        //如果需要验证，并且验证失败
    	//TODO 记录错误的IP
        if(authentication!=null && (!auth(username,password)) ){
        	XSnakeContext.getLogger().log4AuthenticationFailed(s);
        	s.close();
        }
        return s;
    }

	private boolean auth(String username, String password) {
		return authentication.login(username, password);
	}
    
}
