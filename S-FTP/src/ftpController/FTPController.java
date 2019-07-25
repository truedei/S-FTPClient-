package ftpController;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import util.ZHFileBase64Key;
import util.ZHFileCipherTxst;
 
public class FTPController {
	// ftp服务器地址
	public String hostname = "127.0.0.1";
	// ftp服务器端口号默认为21
	public Integer port = 21;
	// ftp登录账号
	public String username = "anonymous";
	// ftp登录密码
	public String password = "";
 
	public FTPClient ftpClient = null;
	
	public FTPController() {
		super();
		// TODO Auto-generated constructor stub
	}
	//只输入了ftp服务器的地址
	public FTPController(String hostname) {
		super();
		this.hostname = hostname;
	}
	//只输入的ftp服务器的地址和端口号
	public FTPController(String hostname, Integer port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}

	//输入了地址，端口号，账号和密码
	public FTPController(String hostname, Integer port, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 初始化ftp服务器
	 */
	public void initFtpClient() {
		System.out.println(hostname+" ," +port+","+username+","+password);
		ftpClient = new FTPClient();
//		ftpClient.setControlEncoding("utf-8");
		ftpClient.setControlEncoding("GBK");
		try {
			System.out.println("connecting...ftp服务器:" + this.hostname + ":" + this.port);
			ftpClient.connect(hostname, port); // 连接ftp服务器
			ftpClient.login(username, password); // 登录ftp服务器
			
			int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				System.out.println("connect failed...ftp服务器:" + this.hostname + ":" + this.port);
			}
			System.out.println("connect successfu...ftp服务器:" + this.hostname + ":" + this.port);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	/**
	 * 上传文件
	 * 
	 * @param pathname
	 *            ftp服务保存地址
	 * @param fileName
	 *            上传到ftp的文件名
	 * @param originfilename
	 *            待上传文件的名称（绝对地址） *
	 * @return
	 */
	public boolean uploadFile(String pathname, String fileName, String originfilename) {
		InputStream inputStream = null;
		try {
			System.out.println("开始上传文件");
			inputStream = new FileInputStream(new File(originfilename));
			initFtpClient();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			ftpClient.makeDirectory(pathname);
			ftpClient.changeWorkingDirectory(pathname);
			// 每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据
			ftpClient.enterLocalPassiveMode();
			// 观察是否真的上传成功
			boolean storeFlag = ftpClient.storeFile(fileName, inputStream);
			System.err.println("storeFlag==" + storeFlag);
			inputStream.close();
			ftpClient.logout();
			System.out.println("上传文件成功");
		} catch (Exception e) {
			System.out.println("上传文件失败");
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
 
	/**
	 * 上传文件
	 * 
	 * @param pathname
	 *            ftp服务保存地址
	 * @param fileName
	 *            上传到ftp的文件名
	 * @param inputStream
	 *            输入文件流
	 * @return
	 */
	public boolean uploadFile(String pathname, String fileName, InputStream inputStream) {
		try {
			System.out.println("开始上传文件");
			initFtpClient();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			ftpClient.makeDirectory(pathname);
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			ftpClient.logout();
			System.out.println("上传文件成功");
		} catch (Exception e) {
			System.out.println("上传文件失败");
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
 
	// 改变目录路径
	public boolean changeWorkingDirectory(String directory) {
		boolean flag = true;
		try {
			flag = ftpClient.changeWorkingDirectory(directory);
			if (flag) {
				System.out.println("进入文件夹" + directory + " 成功！");
 
			} else {
				System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return flag;
	}
 
	// 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
	public boolean CreateDirecroty(String remote) throws IOException {
		boolean success = true;
		String directory = remote + "/";
		// 如果远程目录不存在，则递归创建远程服务器目录
		if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			String path = "";
			String paths = "";
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
				path = path + "/" + subDirectory;
				if (!existFile(path)) {
					if (makeDirectory(subDirectory)) {
						changeWorkingDirectory(subDirectory);
					} else {
						System.out.println("创建目录[" + subDirectory + "]失败");
						changeWorkingDirectory(subDirectory);
					}
				} else {
					changeWorkingDirectory(subDirectory);
				}
 
				paths = paths + "/" + subDirectory;
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return success;
	}
 
	// 判断ftp服务器文件是否存在
	public boolean existFile(String path) throws IOException {
		boolean flag = false;
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (ftpFileArr.length > 0) {
			flag = true;
		}
		return flag;
	}
 
	// 创建目录
	public boolean makeDirectory(String dir) {
		boolean flag = true;
		try {
			flag = ftpClient.makeDirectory(dir);
			if (flag) {
				System.out.println("创建文件夹" + dir + " 成功！");
 
			} else {
				System.out.println("创建文件夹" + dir + " 失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
 
	/**
	 * * 下载文件 *
	 * 
	 * @param pathname
	 *            FTP服务器文件目录 *
	 * @param filename
	 *            文件名称 *
	 * @param localpath
	 *            下载后的文件路径 *
	 * @return
	 */
	public boolean downloadFile(String pathname, String filename, String localpath) {
		boolean flag = false;
		OutputStream os = null;
		try {
			System.out.println("开始下载文件");
			initFtpClient();
			// 切换FTP目录
			boolean changeFlag = ftpClient.changeWorkingDirectory(pathname);
			System.err.println("changeFlag==" + changeFlag);
 
			ftpClient.enterLocalPassiveMode();
			ftpClient.setRemoteVerificationEnabled(false);
			// 查看有哪些文件夹 以确定切换的ftp路径正确
			String[] a = ftpClient.listNames();
			System.err.println(a[0]);
 
			FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile file : ftpFiles) {
				if (filename.equalsIgnoreCase(file.getName())) {
					System.out.println("下载的文件名："+file.getName());
					
					byte[] base64jiemi = ZHFileBase64Key.base64jiemi(file.getName());
	                String jiemiFileName = new String(base64jiemi);
	                String[] split = jiemiFileName.split("#SDJZDX_zhenghuiagsdahscasdqwFTP");
					
	                //"/"+fileName
	                System.out.println("localpath："+localpath+"/" + split[0]);
	               
					
					
					
//					File localFile1 = new File("/" + split[0]);
//	                os = new FileOutputStream(localFile1);
//					ftpClient.retrieveFile(file.getName(), os);
					
//					ZHFileCipherTxst.decode("/" + split[0], localpath+"/" + split[0], ZHFileCipherTxst.key);
//					localFile1.delete();
					
//	                File localFile = new File(localpath + "/" + split[0]);
	                File localFile = new File("/" + split[0]);
					os = new FileOutputStream(localFile);
					ftpClient.retrieveFile(file.getName(), os);
					os.close();
					
					ZHFileCipherTxst.decode("/" + split[0], localpath+"/" + split[0], ZHFileCipherTxst.key);
					localFile.delete();
				}
			}
			ftpClient.logout();
			flag = true;
			System.out.println("下载文件成功");
		} catch (Exception e) {
			System.out.println("下载文件失败");
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
 
	/**
	 * * 删除文件 *
	 * 
	 * @param pathname
	 *            FTP服务器保存目录 *
	 * @param filename
	 *            要删除的文件名称 *
	 * @return
	 */
	public boolean deleteFile(String pathname, String filename) {
		boolean flag = false;
		try {
			System.out.println("开始删除文件");
			initFtpClient();
			// 切换FTP目录
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.dele(filename);
			ftpClient.logout();
			flag = true;
			System.out.println("删除文件成功");
		} catch (Exception e) {
			System.out.println("删除文件失败");
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
 
	public static void main(String[] args) {
		FTPController ftp = new FTPController();
		// 文件路径写为用户建立时 指定的目录
		 ftp.uploadFile("/", "ceshi.png", "C:\\Users\\zhenghui\\Desktop\\QQ图片20190508230009.jpg");
		// ftp.downloadFile("/home/ftpFile", "123.png", "E://");
//		ftp.deleteFile("/", "11111111111111111111111111.txt");
		System.out.println("ok");
	}
}
