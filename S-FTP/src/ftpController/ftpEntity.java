package ftpController;


/**
 * 连接ftp账号的实体类
 * @author zhenghui
 * 2019年7月5日10:09:20
 */
public class ftpEntity {

	private String URL;//连接ftp的地址（url）
	
	private String PORT;//连接ftp的端口号
	
	private String USER_NAME;//连接ftp使用的用户名
	
	private String PASSWORD;//连接ftp用户的密码

	//如果用户只输入了连接地址，默认就用21号端口连接，和anonymous账户登录
	public ftpEntity(String uRL) {
		this(uRL, "21", "anonymous", "anonymous");
	}
	
	//如果用户只输入了连接地址和端口，默认就用anonymous账户登录
	public ftpEntity(String uRL,String pORT) {
		this(uRL, pORT, "anonymous", "anonymous");
	}
	
	//如果用户输入全了，就使用自定义的连接
	public ftpEntity(String uRL, String pORT, String uSER_NAME, String pASSWORD) {
		super();
		URL = uRL;
		PORT = pORT;
		USER_NAME = uSER_NAME;
		PASSWORD = pASSWORD;
	}
	
	public ftpEntity() {
		super();
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getPORT() {
		return PORT;
	}

	public void setPORT(String pORT) {
		PORT = pORT;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}
	
	
	
	
}
