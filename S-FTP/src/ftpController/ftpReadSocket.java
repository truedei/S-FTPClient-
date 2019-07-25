package ftpController;

import java.awt.List;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.InflaterInputStream;

import javax.swing.JFrame;

import UI.MainForm;

import ftpController.*;
import util.ZHFileBase64Key;

/**
 * 
 * @author zhenghui
 * FTP客户端
 */
public class ftpReadSocket {

	private Socket socket = null;//
	
	private BufferedReader reader = null;
	
//	private BufferedWriter writer = null;
	
	public PrintWriter ctrlWriter; //控制输出用的流 
	
	MainForm mainForm = null;

	public void setMain(MainForm mainForm) {
		this.mainForm = mainForm;
	}

	//如果用户只输入远程地址
	public void connect(String host) {
		connect(host,21);
	}
	
	public void connect(String host, int port) {
		connect(host, port, "anonymous", "");
	}

	//连接ftp服务器
	public void connect(String host, int port, String userName, String password){
		try {
			//与ftp建立连接
			socket = new Socket(host,port);
			//读
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//写
//			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			System.out.println("正在连接" + host+"....");
			mainForm.setFTPMessage("正在连接" + host+"....");
			
			ctrlWriter = new PrintWriter(socket.getOutputStream()); 
			
			String line = null;
			line = reader.readLine();
			System.out.println("响应："+line);
			mainForm.setFTPMessage("响应："+line);
			
			//发送命令，输入用户名：
			System.out.println("命令：USER "+userName);
			mainForm.setFTPMessage("命令：USER "+userName);
			
			sendCommand("USER "+userName);
			line = reader.readLine();
			System.out.println("响应："+line);
			mainForm.setFTPMessage("响应："+line);

			//发送命令，输入密码
			sendCommand("PASS "+password);
			line = reader.readLine();
			System.out.println("命令：PASS "+password);
			System.out.println("响应："+line);
			mainForm.setFTPMessage("命令：PASS "+password);
			mainForm.setFTPMessage("响应："+line);
					
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//输入操作FTP的命令
	public void sendCommand(String com) throws IOException {
		//先判断是否连接了
		if(socket == null) {
			throw new IOException("FTP未连接");
		}
		//如果发送失败
		try {
			//发送命令
//			writer.write(com+"\r\n");
//			writer.flush();
			ctrlWriter.println(com);
			ctrlWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//查询所有的文件
	public void  selectAll() throws IOException {
		if(socket == null) {
			throw new IOException("FTP未连接");
		}
		
		//如果发送失败
		try {
			//发送命令
			sendCommand("PWD");
			sendCommand("PASV");
			sendCommand("TYPE A");
			sendCommand("LIST");
			
			String line = null;
			while ((line=reader.readLine())!=null) {
				System.out.println(line);
			}
			
		} catch (Exception e) {
			socket = null;
			throw e;
		}
		
		
	}
	
  	//发送指令
  	public void sendOrder(String order) throws IOException {
  		sendCommand("PASV");//开启一个被动连接的
//  		sendCommand("PORT");//开启一个主动连接的
        //取得被动连接模式的端口等信息
        String  response = reader.readLine();
        System.out.println(response);
        
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        String ip_port = response.substring(opening+1,closing);

        //        System.out.println(closing);
        //提取227 Entering Passive Mode (127,0,0,1,250,80).  中的IP地址和端口号
        //IP：127.0.0.1
        //端口：250*254+80
        String ip = null;
        int port = -1;
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256  + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("FTP接收到错误的数据链接信息: "
                        + response);
            }
        }
        
        System.out.println("上传文件的IP："+ip + "  端口号：" + port);
        mainForm.setFTPMessage("上传文件的IP："+ip + "  端口号：" + port);
        sendCommand("PORT "+ip_port);//发送命令

        System.out.println(reader.readLine());
        sendCommand(order);//发送命令

        String str1 = null;
        while ((str1=reader.readLine())!=null) {
        	System.out.println("str1:"+str1);
        }
  	}
 
 
//dataConnection方法 
//构造与服务器交换数据用的Socket 
//再用PORT命令将端口通知服务器 
public Socket dataConnection(String ctrlcmd,int port) { 
	 String cmd = "PORT "; // PORT存放用PORT命令传递数据的变量 
	 int i; 
	 Socket dataSocket = null;// 传送数据用Socket 
	 try { 
	  // 得到自己的地址 
	  byte[] address = InetAddress.getLocalHost().getAddress(); 
	  // 用适当的端口号构造服务器 
	  ServerSocket serverDataSocket = new ServerSocket(port,1); 
	 
	  // 利用控制用的流传送PORT命令 
	  ctrlWriter.println(cmd+""+port); 
	  ctrlWriter.flush(); 
	  // 向服务器发送处理对象命令(LIST,RETR,及STOR) 
	  ctrlWriter.println(ctrlcmd); 
	  ctrlWriter.flush(); 
	 
	  // 接受与服务器的连接 
	         
	  dataSocket = serverDataSocket.accept(); 
	  serverDataSocket.close(); 
	 } catch (Exception e) { 
	  e.printStackTrace(); 
	  System.exit(1); 
	 } 
 return dataSocket; 
}

  	//获取FTP的数据
	public void listFiles(String string, FTPTableModel ftpTableModel) throws IOException {
		    sendCommand("cwd  " + string);
	        String string1 = reader.readLine();
	        System.out.println("响应：" + string1);
	        mainForm.setFTPMessage("响应："+ string1);
	        sendCommand("PASV");
	        
	        //取得目录信息 
	        String  response = reader.readLine();
	        String[] ip_PORT = getIP_PORT(response);
	        String ip =ip_PORT[0];
	        int port = Integer.valueOf(ip_PORT[1]);
	        
	        
	        System.out.println("listFilesIP："+ip + "  listFiles端口号：" + port);
	        sendCommand("LIST" );
	        mainForm.setFTPMessage("listFilesIP："+ip + "  listFiles端口号：" + port);

	        Socket dataSocket = new Socket(ip, port);
	        
	        DataInputStream dis = new DataInputStream(dataSocket.getInputStream());
	        
	        String s = "";
	        ArrayList<FileEntity> fileList = new ArrayList<FileEntity>();
	        
	        //遍历目录
	        while ((s = dis.readLine()) != null) {
	            String l = new String(s.getBytes("ISO-8859-1"), "GB2312");//转换字符类型，解决乱码问题
	            //如果是目录
	            if(l.indexOf("<DIR>")!=-1) {
	            	 //是目录的时候的字符串分割，拿到文件名的
	            	 String[] split = l.split("       <DIR>");
	            	 String string2 = "日期："+split[0]+",文件夹,文件名："+split[1].split("          ")[1];
	            	 String fileName = split[1].split("          ")[1];
	            	 String fileSise = "-1";
	            	 String fileType = "文件夹";
	            	 String fileUpdateDate = split[0];
	            	 //存到List里
	            	 fileList.add(new FileEntity(fileName, fileSise, fileType, fileUpdateDate));
	            }else{//如果不是目录
	           	 	String[] split = l.split("             ");
	           	    String[] split2 = split[1].trim().split(" ");
		           	 String string2 ="日期："+split[0]+",文件,大小："+split2[0]+",文件名："+split2[1];
//		           	 mainForm.setFTPMessage(string2);
		           	 String fileName = split2[1];
	            	 String fileSise = String.valueOf(split2[0]);
	            	 String fileType = "文件";
	            	 String fileUpdateDate = split[0];
	            	 
		           	 //存到List里
	            	 fileList.add(new FileEntity(fileName, fileSise, fileType, fileUpdateDate));
	            }
	        }
	        
	        ftpTableModel.setData(fileList);
	        
	        
	        
	        
	        dis.close();
	        dataSocket.close();
	        System.out.println(reader.readLine());
	        System.out.println(reader.readLine());
//	        String str1 = null;
//	        while ((str1=reader.readLine())!=null) {
//			}
	        
	}
	
	
	 //提取IP地址和端口号
	 public String[] getIP_PORT(String  response) throws IOException {
		 
		 String ip = null;
	     int port = -1;
	     int opening = response.indexOf('(');
	     int closing = response.indexOf(')', opening + 1);

	     if (closing > 0) {
	         String dataLink = response.substring(opening + 1, closing);
	         StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
	         try {
	             ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
	                     + tokenizer.nextToken() + "." + tokenizer.nextToken();
	             port = Integer.parseInt(tokenizer.nextToken()) * 256
	                     + Integer.parseInt(tokenizer.nextToken());
	         } catch (Exception e) {
	             throw new IOException("FTP接收到错误的数据链接信息: " + response);
	         }
	     }
	     
	     String [] str= {ip,String.valueOf(port)};
		 return str;
	 }
	
	//关闭所有的连接
	public void closeAll() {
		if(socket!=null) {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("关闭socket失败");
				e.printStackTrace();
			}
		}

		if(reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(ctrlWriter!=null) {
			ctrlWriter.close();
		}
	}
	
	
}
