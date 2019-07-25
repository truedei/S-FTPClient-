package UI;

import BLL.*;
import ftpController.FTPController;
import ftpController.FTPTableModel;
import ftpController.ftpReadSocket;
import util.ZHFileBase64Key;
import util.ZHFileCipherTxst;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import view.ZFTP_JMenu;
import java.awt.Component;
import javax.swing.JTable;

public class MainForm extends JFrame   implements ActionListener{
	
	public static MainForm _instance;
	JPanel SearchPanel, ShowPanel, FunctPanel, TreePanel;
	private JPanel FunctPanel_1;
	JTree BigTree;
	FilesTree filesTree;
	JScrollPane ScrollShow, TreeShow;
    DefaultMutableTreeNode node;
	JButton GoBtn;
	ButtonGroup Classify;
	JTextField GuideText;
	String Sort_Items[] = {"文件大小","修改时间","首字母"};
	String Sort_Type_Items[] = {"升序","降序"};
	public String Cur_URL = "";
	String Pre_URL = "";
	String LatURL = "";
	public Map<String, String> Maps = new HashMap<String,String>();
	
	//文件列表的相关变量
	JList<String> list;
	public DefaultListModel defaultListModel;
	public Stack<String> stack, stack_return;
	JPopupMenu jPopupMenu = null;
	JPopupMenu jPopupMenu2 = null;
	JPopupMenu jPopupMenu3 = null;
	JMenuItem[] JMIs = new JMenuItem[10];
	JMenuItem[] JMIs2 = new JMenuItem[5];
	JMenuItem delete = new JMenuItem("删除");
	public Icon[] AllIcons = new Icon[999999];//存储搜索得到的文件图标
	public int Icon_Counter = 0;
	//保存GB,MB,KB,B对应的字节数，方便换算文件大小及单位
	long[] Sizes = {1073741824,1048576,1024,1};
	String[] Size_Names = {"GB", "MB", "KB", "B"};
	Boolean isSearching = false;
	private JTextField ftp_host;
	private JTextField ftp_port;
	private JTextField ftp_userName;
	private JTextField ftp_password;
	
    public JTextArea ftp_message = new JTextArea();//ftp消息
    private JTable table;
    // 获取窗体的内容面板
    FTPTableModel ftpTableModel;
	
    
    ftpReadSocket frs =null;//ftp连接的socket
    
    JScrollPane scrollPane_1 = null;
    private JButton btnNewButton_1;
    
    public void setFTPMessage(String message) {
    	ftp_message.append(message);
    	ftp_message.append("\r\n");
    }
    
	public MainForm(){//主界面
		this._instance = this;
		this.setTitle("ZH(郑晖)-FTP客户端-v0.6");
		this.setBounds(500, 500, 1513, 921);
		this.getContentPane().setLayout(null);
		Init();//初始化
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setResizable(false);
	}

	//初始化
	public void Init(){
		ftp_userName = new JTextField();
		 
	   scrollPane_1 = new JScrollPane((Component) null);
       scrollPane_1.setBounds(5, 0, 914, 491);
		
		SearchPanel = new JPanel();
		ShowPanel = new JPanel();
		FunctPanel = new JPanel();
		TreePanel = new JPanel();
		
		Classify = new ButtonGroup();
	   
		 //中上导航栏35
        FunctPanel_1 = new JPanel();
        FunctPanel_1.setBounds(5, 114, 516, 45);
        FunctPanel_1.setLayout(null);
        GuideText = new JTextField();
        GuideText.setBounds(64, 13, 362, 25);
        GuideText.addActionListener(this);
        GoBtn = new JButton("Go!");
        GoBtn.setFont(new Font("Serif", Font.PLAIN, 15));
        GoBtn.setBounds(440, 13, 65, 25);
        GoBtn.addActionListener(this);
        FunctPanel_1.add(GuideText);
        FunctPanel_1.add(GoBtn);
        this.getContentPane().add(FunctPanel_1);
        
        JLabel label = new JLabel("\u672C\u5730:");
        label.setFont(new Font("微软雅黑", Font.PLAIN, 19));
        label.setBounds(14, 13, 52, 18);
        FunctPanel_1.add(label);
        
		//中部文件列表
        stack = new Stack<String>();
        stack_return = new Stack<String>();
        ShowPanel.setSize(291, 491);
        ShowPanel.setLocation(198, 172);
        ShowPanel.setLayout(null);    
        list = new JList<String>();
        list.setValueIsAdjusting(true);
        list.setSize(10, 50);
        jPopupMenu = new JPopupMenu();//文件/文件夹的属性菜单
        jPopupMenu2 = new JPopupMenu();//磁盘的属性菜单
        JMIs[0] = new JMenuItem("打开");
        JMIs[1] = new JMenuItem("删除");
        JMIs[2] = new JMenuItem("重命名");
        JMIs[3] = new JMenuItem("属性");
        JMIs[4] = new JMenuItem("上传");
        for(int k = 0; k < 5; ++k){//文件/文件夹的属性菜单初始化
        	JMIs[k].addActionListener(this);
        	jPopupMenu.add(JMIs[k]);            	
        }        
        JMIs2[0] = new JMenuItem("打开");
        JMIs2[1] = new JMenuItem("属性");
        for(int k = 0; k < 2; ++k){//磁盘的属性菜单初始化
        	JMIs2[k].addActionListener(this);
        	jPopupMenu2.add(JMIs2[k]);            	
        }    
        jPopupMenu3 = new JPopupMenu();
        delete.addActionListener(this);
        jPopupMenu3.add(delete);
        list.add(jPopupMenu3);
        list.add(jPopupMenu2);
        list.add(jPopupMenu);
        
        Home_List();//显示磁盘根目录
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(list.getSelectedIndex() != -1){
					if(e.getClickCount() == 1){//单击list时，暂无事件
					
					}else if(e.getClickCount() == 2){//双击list时，打开文件或进入该子目录
						System.out.println(list.getSelectedValue());
						twoClick(list.getSelectedValue());												
					}
					if(e.getButton() == 3){//右击list时，打开菜单栏
						if(Cur_URL != ""){
							if(list.getSelectedValuesList().size() == 1){
								jPopupMenu.show(list,e.getX(),e.getY()); //如果右击的是单个文件夹和文件，则应打开一个功能齐全的菜单栏
							}else if(list.getSelectedValuesList().size() > 1){//如果选中多个文件夹和文件，则只支持删除功能
								jPopupMenu3.show(list, e.getX(), e.getY());
							}
						}		                 
						else if(Cur_URL == "" && list.getSelectedValuesList().size() == 1){
							jPopupMenu2.show(list, e.getX(), e.getY()); //如果右击的是磁盘，菜单栏中只含有“打开”和“属性”功能
						}						
					}
				}
			}
		});	
	        
		ScrollShow = new JScrollPane(list);
		ShowPanel.add(ScrollShow);
		ScrollShow.setSize(277, 478);
		ScrollShow.setLocation(5, 13);
		this.getContentPane().add(ShowPanel);
		
		//左侧目录树状图
        TreePanel.setSize(190,491);
        TreePanel.setLocation(5, 172);
        TreePanel.setLayout(null); 
        filesTree = new FilesTree();
        TreeShow = new JScrollPane(filesTree);
        TreeShow.setBounds(5, 13, 185, 478);
        TreePanel.add(TreeShow);
        this.getContentPane().add(TreePanel);
        
      
        
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(BorderFactory.createLoweredBevelBorder());  
        panel_2.setLayout(null);
        panel_2.setBounds(0, 13, 1507, 77);
        this.getContentPane().add(panel_2);
        
        JLabel label_ftp_host = new JLabel("\u5730\u5740\uFF1A");
        label_ftp_host.setBounds(14, 30, 45, 18);
        panel_2.add(label_ftp_host);
        
        ftp_host = new JTextField();
        ftp_host.setColumns(10);
        ftp_host.setBounds(58, 26, 157, 26);
        panel_2.add(ftp_host);
        
        JLabel label_ftp_port = new JLabel("\u7AEF\u53E3\uFF1A");
        label_ftp_port.setBounds(218, 25, 45, 27);
        panel_2.add(label_ftp_port);
        
        ftp_port = new JTextField();
        ftp_port.setColumns(10);
        ftp_port.setBounds(263, 25, 56, 26);
        panel_2.add(ftp_port);
        
        JLabel label_ftp_userName = new JLabel("\u7528\u6237\u540D\uFF1A");
        label_ftp_userName.setBounds(333, 25, 74, 27);
        panel_2.add(label_ftp_userName);
        
       
        ftp_userName.setColumns(10);
        ftp_userName.setBounds(400, 25, 145, 26);
        panel_2.add(ftp_userName);
        
        JLabel label_ftp_password = new JLabel("\u5BC6\u7801\uFF1A");
        label_ftp_password.setBounds(558, 25, 45, 27);
        panel_2.add(label_ftp_password);
        
        ftp_password = new JTextField();
        ftp_password.setColumns(10);
        ftp_password.setBounds(604, 25, 145, 26);
        panel_2.add(ftp_password);
        
        JButton lianjie = new JButton("\u8FDE\u63A5");
        lianjie.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\lianjie.png"));
        lianjie.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String host = ftp_host.getText(); //ftp地址
        		String port = ftp_port.getText();//端口号
        		String userName = ftp_userName.getText();//用户名
        		String password = ftp_password.getText();//密码
        		
        		//如果当前没有连接，就重新连接
        		if(frs==null) {
	        		frs = new ftpReadSocket();
	        		frs.setMain(_instance);
	        		
	        		if(host.equals("")) {
	        			 // 消息对话框无返回, 仅做通知作用
	        			JOptionPane.showMessageDialog(null, "请输入ftp地址", "【出错啦】", JOptionPane.ERROR_MESSAGE);
	        		}else {
        			    String name = ftp_userName.getText();
        		        if (name.equals("")) {
        					name = "anonymous";
        				}
        		        
        		        ftpTableModel = new FTPTableModel(name);
	        		        
	        			System.out.println(host+","+port+","+userName+","+password);
	        			//地址不为空  端口为空 账号为空  密码为空
	        			if(!host.equals("") && port.equals("") && userName.equals("")  && password.equals("")) {
	        				System.out.println("走的1路");
        					frs.connect(host);
        				//地址不为空  端口不为空 账号为空  密码为空
	        			}else if(!host.equals("") && !port.equals("") && userName.equals("")  && password.equals("")) {
	        				System.out.println("走的2路");
	        				if(isNumeric(port)) {//判断端口号是不是数字
	        					frs.connect(host, Integer.valueOf(port));
	        				}else {
	        					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
	        				}
        				//地址不为空  端口不为空 账号不为空  密码不为空
	        			}else if(!host.equals("") && !port.equals("") && !userName.equals("")  && !password.equals("")) {
	        				System.out.println("走的3路");
	        				if(isNumeric(port)) {//判断端口号是不是数字
	        					frs.connect(host, Integer.valueOf(port),userName,password);
	        				}else {
	        					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
	        				}
	        			}
	        			
	        			
	        			try {
							frs.listFiles("/",ftpTableModel);
							ftpTableModel.getFTPTableData();
							// JTable
					        table = new JTable(ftpTableModel);
					        scrollPane_1.setViewportView(table);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
	        		}
        		}else {
        			//如果当前有连接，就先断开再重新连接
        			frs.closeAll();
	        		frs = new ftpReadSocket();
	        		frs.setMain(_instance);
	        		
	        		if(host.equals("")) {
	        			 // 消息对话框无返回, 仅做通知作用
	        			JOptionPane.showMessageDialog(null, "请输入ftp地址", "【出错啦】", JOptionPane.ERROR_MESSAGE);
	        		}else {
        			    String name = ftp_userName.getText();
        		        if (name.equals("")) {
        					name = "anonymous";
        				}
        		        
        		        ftpTableModel = new FTPTableModel(name);
	        		        
        		        System.out.println(host+","+port+","+userName+","+password);
	        			//地址不为空  端口为空 账号为空  密码为空
	        			if(!host.equals("") && port.equals("") && userName.equals("")  && password.equals("")) {
	        				System.out.println("走的1路");
        					frs.connect(host);
        				//地址不为空  端口不为空 账号为空  密码为空
	        			}else if(!host.equals("") && !port.equals("") && userName.equals("")  && password.equals("")) {
	        				System.out.println("走的2路");
	        				if(isNumeric(port)) {//判断端口号是不是数字
	        					frs.connect(host, Integer.valueOf(port));
	        				}else {
	        					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
	        				}
        				//地址不为空  端口不为空 账号不为空  密码不为空
	        			}else if(!host.equals("") && !port.equals("") && !userName.equals("")  && !password.equals("")) {
	        				System.out.println("走的3路");
	        				if(isNumeric(port)) {//判断端口号是不是数字
	        					frs.connect(host, Integer.valueOf(port),userName,password);
	        				}else {
	        					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
	        				}
	        			}
	        			
	        			
	        			try {
							frs.listFiles("/",ftpTableModel);
							ftpTableModel.getFTPTableData();
							// JTable
					        table = new JTable(ftpTableModel);
					        scrollPane_1.setViewportView(table);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
	        		}
	        		
        		}
        	}
        });
        lianjie.setBounds(776, 13, 115, 51);
        panel_2.add(lianjie);
        
        JButton duankai = new JButton("\u65AD\u5F00");
        duankai.setBounds(905, 13, 115, 52);
        panel_2.add(duankai);
        duankai.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		frs.closeAll();
        	}
        });
        duankai.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\duankai.png"));
        
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setBounds(528, 70, 22, 607);
        this.getContentPane().add(separator);
        
        JSeparator separator_1 = new JSeparator();
        separator_1.setToolTipText("");
        separator_1.setBounds(-148, 676, 1655, 20);
        this.getContentPane().add(separator_1);
        
        JPanel lianjie_ftp_file_pan = new JPanel();
        lianjie_ftp_file_pan.setLayout(null);
        lianjie_ftp_file_pan.setBounds(574, 177, 919, 491);
        this.getContentPane().add(lianjie_ftp_file_pan);
        
       
        lianjie_ftp_file_pan.add(scrollPane_1);
        
        JPanel panel_6 = new JPanel();
        panel_6.setLayout(null);
        panel_6.setBounds(5, 686, 1175, 187);
        getContentPane().add(panel_6);
        
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(0, 0, 1175, 187);
        panel_6.add(scrollPane_2);
        
       
        scrollPane_2.setViewportView(ftp_message);
        
        JButton qingkong = new JButton("\u6E05\u7A7A");
        qingkong.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ftp_message.setText(" ");
        	}
        });
        qingkong.setBounds(1194, 681, 92, 46);
        getContentPane().add(qingkong);
        
        JPanel panel_3 = new JPanel();
        panel_3.setLayout(null);
        panel_3.setBounds(574, 103, 933, 68);
        getContentPane().add(panel_3);
        
        JButton btnNewButton = new JButton("\u4E0B\u8F7D");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {

        		if(ftpTableModel.getSelectAllSum()==0) {
        			JOptionPane.showMessageDialog(null, "请选择您要下载的文件进行下载", "【不对呦】", JOptionPane.ERROR_MESSAGE);
        		}else {
        			JPanel p = new JPanel();
        			JFileChooser chooser;
        			String choosertitle = "选择下载位置";
        			
        			int result;
    				chooser = new JFileChooser();
    				chooser.setCurrentDirectory(new java.io.File("."));
    				chooser.setDialogTitle(choosertitle);
//    				System.out.println("---" + choosertitle);
    				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    				chooser.setAcceptAllFileFilterUsed(false);
    				
    				if (chooser.showOpenDialog(p) == JFileChooser.APPROVE_OPTION) {
    					FTPController ftp = getFtpControoler();
            			String[] selectAllInfo = ftpTableModel.getSelectAllInfo();
            			for (int i = 0; i < selectAllInfo.length; i++) {
            				//linux学习路线.docx
            				//这里以#SDJZDX_zhenghui作为分隔符
            				String userName = ftp_userName.getText();
        					if(userName.equals("")) {
        						userName = "anonymous";
        					}
        					String jiamiqian = selectAllInfo[i]+"#SDJZDX_zhenghuiagsdahscasdqwFTP"+userName;
        					String base64jiami_fileName = ZHFileBase64Key.base64jiami(jiamiqian.getBytes());
            				//一直在根目录
    						ftp.downloadFile("/", base64jiami_fileName, String.valueOf(chooser.getSelectedFile()));
    					}
    				} else {
    					System.out.println("No Selection ");
    				}
    				
        		}
        		
        	}
        });
        btnNewButton.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\xiazai.png"));
        btnNewButton.setBounds(277, 13, 115, 51);
        panel_3.add(btnNewButton);
        
        JButton shuaxin = new JButton("\u5237\u65B0");
        shuaxin.setBounds(406, 12, 115, 52);
        panel_3.add(shuaxin);
        shuaxin.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
		         //刷新ftp列表文件
        		shuaxin();
        		
        	}
        });
        shuaxin.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\shuaxin.png"));
        shuaxin.setFocusPainted(false);
        shuaxin.setBorderPainted(false);
        
        btnNewButton_1 = new JButton("\u5220\u9664");
        btnNewButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(ftpTableModel == null) {
        			JOptionPane.showMessageDialog(null, "请先登录", "【不对呦】", JOptionPane.ERROR_MESSAGE);
        			return;
        		}
        		if(ftpTableModel.getSelectAllSum()==0) {
        			JOptionPane.showMessageDialog(null, "请选择您要删除的文件进行删除", "【不对呦】", JOptionPane.ERROR_MESSAGE);
        		}else {
        		
    				FTPController ftp = getFtpControoler();
        			String[] selectAllInfo = ftpTableModel.getSelectAllInfo();
        			//循环删除所有选中的文件
        			for (int i = 0; i < selectAllInfo.length; i++) {
        				//获取选中的文件名 
        				String selectName = selectAllInfo[i];
        				String selectNameKey = ftpTableModel.getSelectFileName(selectName);
        				
        				String userName = ftp_userName.getText();
    					if(userName.equals("")) {
    						userName = "anonymous";
    					}
        				
        				/************加密过程******************/
    					//这里以#SDJZDX_zhenghui作为分隔符
    					String jiamiqian = selectName+"#SDJZDX_zhenghuiagsdahscasdqwFTP"+userName;
    					String base64jiami_fileName = ZHFileBase64Key.base64jiami(jiamiqian.getBytes());
        				
        				//删除根目录下的文件
        				boolean deleteFile = ftp.deleteFile( "/",base64jiami_fileName);
        			}
        			shuaxin();
        		}
        		
        	}
        });
        btnNewButton_1.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\shanchu.png"));
        btnNewButton_1.setBounds(535, 13, 115, 51);
        panel_3.add(btnNewButton_1);
	}
	
	
	//刷新ftp的文件列表
	public void shuaxin() {
		//如果当前有连接，就先关闭然后，重新连接
		if(frs!=null) {
			frs.closeAll();
			
			String host = ftp_host.getText(); //ftp地址
    		String port = ftp_port.getText();//端口号
    		String userName = ftp_userName.getText();//用户名
    		String password = ftp_password.getText();//密码
    		
    		frs = new ftpReadSocket();
    		frs.setMain(_instance);
    		
    		if(host.equals("")) {
    			 // 消息对话框无返回, 仅做通知作用
    			JOptionPane.showMessageDialog(null, "请输入ftp地址", "【出错啦】", JOptionPane.ERROR_MESSAGE);
    		}else {
    			String name = ftp_userName.getText();
		        if (name.equals("")) {
					name = "anonymous";
				}
		        
		        ftpTableModel = new FTPTableModel(name);
    		        
    			System.out.println(host+","+port+","+userName+","+password);
    			//地址不为空  端口为空 账号为空  密码为空
    			if(!host.equals("") && port.equals("") && userName.equals("")  && password.equals("")) {
    				System.out.println("走的1路");
					frs.connect(host);
				//地址不为空  端口不为空 账号为空  密码为空
    			}else if(!host.equals("") && !port.equals("") && userName.equals("")  && password.equals("")) {
    				System.out.println("走的2路");
    				if(isNumeric(port)) {//判断端口号是不是数字
    					frs.connect(host, Integer.valueOf(port));
    				}else {
    					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
    				}
				//地址不为空  端口不为空 账号不为空  密码不为空
    			}else if(!host.equals("") && !port.equals("") && !userName.equals("")  && !password.equals("")) {
    				System.out.println("走的3路");
    				if(isNumeric(port)) {//判断端口号是不是数字
    					frs.connect(host, Integer.valueOf(port),userName,password);
    				}else {
    					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
    				}
    			}
    			
    			
    			try {
					frs.listFiles("/",ftpTableModel);
					// JTable
					ftpTableModel.getFTPTableData();
			        table = new JTable(ftpTableModel);
			        scrollPane_1.setViewportView(table);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
		}
		
	}

	//点击两次时的事件
	public void twoClick(String choice){
		if(!isSearching){//如果此时不在搜索状态，就是正常的点击处理
			choice += "\\";		
			File file = new File(Cur_URL + choice);
			if(file.isDirectory()){
				Cur_URL += choice;	
				stack.push(Cur_URL);
				Go_There();
			}else{
				OpenIt(file);
			}
		}else{//如果是在搜索状态，那就要从map里提取我们的URL，因为搜索把顺序都打乱了，无法用一个URL对应
			File file = new File(Maps.get(choice));
			OpenIt(file);
		}
	}
	
	//回到初始磁盘界面
	public void Home_List(){
		List<String> Disks = MemoryInfo.getDisk();
		defaultListModel = new DefaultListModel();
		for(int i = 0; i < Disks.size(); ++i){
			defaultListModel.addElement(Disks.get(i));
		}
		Icon[] icons = GetFileIcon.getSmallIcon("HOME");
		list.setCellRenderer(new MyCellRenderer(icons));
		GuideText.setText("");
		Cur_URL = "";
		stack.push(Cur_URL);
	}
	
	//调用电脑中的程序“打开”文件的方法
	public void OpenIt(File file){
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//想去哪，就去哪（核心跳转函数）
	public void Go_There(){
			GuideText.setText(Cur_URL);
			if(Cur_URL != ""){//Cur_URL非空，就跳入目标目录
				defaultListModel.clear();
				String[] getString = GetFileNames.getFileName(Cur_URL);		
				for(int i = 0; i < getString.length; ++i){
					defaultListModel.addElement(getString[i]);		
				}	
				Icon[] icons = GetFileIcon.getSmallIcon(Cur_URL);
				list.setModel(defaultListModel);
				list.setCellRenderer(new MyCellRenderer(icons));
				
			}else{//Cur_URL为空时，就跳转回根目录
				Home_List();
			}
	}
	
	//搜索功能核心函数
//	public void GetAllResults(String path){
//		  if(path != ""){		    	
//				String[] getString = GetFileNames.getFileName(path);
//				for(int i = 0; i < getString.length; ++i){
//					File file = new File(path + getString[i] + "\\");						
//					if(file.isDirectory()){//遍历子文件夹下						
//						GetAllResults(path + getString[i] + "\\");
//					}else{
//						String prefix = getString[i].substring(getString[i].lastIndexOf('.') + 1);					
//						if(VideoType.contains(prefix) && Videos.isSelected()){//判断是否为视频文件且视频按钮被选中，是则加入我们的显示目录里
//							System.out.println(getString[i]);
//							Maps.put(getString[i], path + getString[i]);//用Maps存储文件名与路径的对应关系
//							defaultListModel.addElement(getString[i]);
//							AllIcons[Icon_Counter++] = GetFileIcon.getSingleSmallIcon(path + getString[i]);
//						}else if(GraphType.contains(prefix) && Picture.isSelected()){//判断是否为图片文件且图片按钮被选中，是则加入我们的显示目录里
//							Maps.put(getString[i], path + getString[i]);//用Maps存储文件名与路径的对应关系
//							defaultListModel.addElement(getString[i]);
//							AllIcons[Icon_Counter++] = GetFileIcon.getSingleSmallIcon(path + getString[i]);
//						}else if(TxtType.contains(prefix) && Text.isSelected()){//判断是否为文档文件且文档按钮被选中，是则加入我们的显示目录里
//							Maps.put(getString[i], path + getString[i]);//用Maps存储文件名与路径的对应关系
//							defaultListModel.addElement(getString[i]);
//							AllIcons[Icon_Counter++] = GetFileIcon.getSingleSmallIcon(path + getString[i]);
//						}else if(MusicType.contains(prefix) && Music.isSelected()){
//							Maps.put(getString[i], path + getString[i]);//用Maps存储文件名与路径的对应关系
//							defaultListModel.addElement(getString[i]);
//							AllIcons[Icon_Counter++] = GetFileIcon.getSingleSmallIcon(path + getString[i]);
//						}
//					}				
//				}
//		    }
//	}		
			
	public static void main(String[] args) {
        MainForm m = new MainForm();     
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		if(e.getSource() == PreBtn){//向左走
//			LatURL = Cur_URL;			
//			if(!stack.isEmpty()){
//				stack.pop();//每从当前一个目录跳回之前的目录时，stack就要出栈		
//				stack_return.push(Cur_URL);//把跳之前的目录放入返回栈stack_return
//				if(!stack.isEmpty()){					
//					Cur_URL = stack.peek();//从栈中得到上一个访问的目录，赋给当前目录					
//				}
//				else{
//					Cur_URL = "";//如果栈为空，则说明前面是根目录，则直接置空
//				}
//				Go_There();
//			}
////			if(isSearching){//如果正在搜索状态，那此时应该结束
////				isSearching = false;
////				AllFiles.setSelected(true);
////			}
//		}
//		
//		else if(e.getSource() == LatBtn){//向右走
//			if(!stack_return.isEmpty()){//向右走，则从返回栈里拿URL
//				Cur_URL = stack_return.peek();
//				stack_return.pop();
//				stack.push(Cur_URL);
//				Go_There();
//			}
////			if(isSearching){//如果正在搜索状态，那此时应该结束
////				isSearching = false;
////				AllFiles.setSelected(true);
////			}
//		}
		
//		else 
			if(e.getSource() == JMIs[0] || e.getSource() == JMIs2[0]){	//打开文件/文件夹/磁盘
				if(!isSearching){
					String url = Cur_URL + list.getSelectedValue();
					if(Cur_URL != ""){
						url += "\\";
					}
					File file = new File(url);
					if(file.isDirectory()){
						twoClick(url);
					}else{
						OpenIt(file);				
					}
				}else{
					File file = new File(Maps.get(list.getSelectedValue()));
					OpenIt(file);
				}			
			} else if(e.getSource() == JMIs[1]){//删除
				File file = new File(Cur_URL + "/" + list.getSelectedValue());
				int n;
				if(file.isFile()){
					n = JOptionPane.showConfirmDialog(null, "确定要删除文件 " + file.getName() + " 么?", "文件删除",JOptionPane.YES_NO_OPTION);
				}else{
					n = JOptionPane.showConfirmDialog(null, "确定要删除 " + file.getName() + " 及其目录下的文件么?", "文件夹删除",JOptionPane.YES_NO_OPTION);
				}
				if(n == 0){
					FileDelete.delete(Cur_URL + list.getSelectedValue() +  "\\");
					Go_There();
				}			
			} else if(e.getSource() == delete){//多选下的删除
				List<String> selected_str = list.getSelectedValuesList();
				File file;
				int num = selected_str.size();
				int n = JOptionPane.showConfirmDialog(null, "确定要删除 " + selected_str.get(0) + " 等" + num + "项么?", "文件删除",JOptionPane.YES_NO_OPTION);
				if(n == 0){
					if(isSearching){//如果是正在搜索，从Maps取URL
						for(int i = 0; i < selected_str.size(); ++i){
							file = new File(Maps.get(selected_str.get(i)));
							FileDelete.delete(file.getAbsolutePath());
						}				
					}else{//否则就用Cur_URL拼接获得
						for(int i = 0; i < selected_str.size(); ++i){
							FileDelete.delete(Cur_URL + selected_str.get(i) +  "\\");
						}		
						Go_There();
					}
				}						
			}
		
			else if(e.getSource() == JMIs[2]){//重命名
				String before = list.getSelectedValue();
				File file = new File(Cur_URL + before + "\\");
				String after = "";
				if(file.isDirectory()){
					after = (String) JOptionPane.showInputDialog(null, "请输入新文件夹名:\n", "重命名", JOptionPane.PLAIN_MESSAGE, null, null,
			                list.getSelectedValue());
				}else{
					after = (String) JOptionPane.showInputDialog(null, "请输入新文件名:\n", "重命名", JOptionPane.PLAIN_MESSAGE, null, null,
			                list.getSelectedValue());
				}			
				if(before != after && after != null){
					new File(Cur_URL + before + "\\").renameTo(new File(Cur_URL + after + "\\"));
					Go_There();
				}else{
					Go_There();
				}
			}
		
			else if(e.getSource() == JMIs[3]){//打开文件/文件夹属性窗口
//				String temp_url = Cur_URL + list.getSelectedValue() + "\\";
//				File file = new File(temp_url);
//				Icon icon = GetFileIcon.getSingleSmallIcon(temp_url);			
//				String name = list.getSelectedValue();
//				long size;
//				double final_size;
//			    long File_Num = 0, Directory_Num = 0;
//			    int flag = 0;
//				String file_size = "";
//				
//				String Create_Time = FileTime.getCreateTime(temp_url);
//				String Modify_Time = FileTime.getModifiedTime(temp_url);
//				String Last_Access = FileTime.getLatestAccessTime(temp_url);
//				
//				if(file.isDirectory()){//目录属性初始化所需参数
//					DirectoryInfo DInfo = new DirectoryInfo();
//					size = DInfo._instance.getDirSize(file);
//					File_Num = DInfo.File_Num;
//					Directory_Num = DInfo.Directory_Num;
//					flag = 1;
//				}else{//文件属性初始化所需参数
//					size = file.length();				
//				}			 
//				final_size = 0;				
//				for(int i = 0; i < Sizes.length; ++i){
//					if(size / Sizes[i] > 0){
//						final_size = size * 1.0 / Sizes[i];
//						DecimalFormat fnum = new DecimalFormat("##0.00");  
//						file_size = fnum.format(final_size) + Size_Names[i];
//						break;
//					}
//				}
//				if(flag == 1){
//					FileProperties properties = new FileProperties(icon, name, file_size, File_Num, Directory_Num-1, temp_url, Create_Time);
//				}else{
//					FileProperties properties = new FileProperties(icon, name, file_size, temp_url, Create_Time, Modify_Time, Last_Access);
//				}		
			}else if(e.getSource() == JMIs[4]) {//如果是上传
				
			    if(!isSearching){
					String url = Cur_URL + list.getSelectedValue();
					if(Cur_URL != ""){
						url += "\\";
					}
				    
				    //上传该文件  向ftp发送命令
				    //上传路径
				    String uploadFile = url.substring(0, url.length()-1);
				    System.out.println("上传文件位置:"+uploadFile);
				    JOptionPane.showMessageDialog(this,"正在上传："+uploadFile,"提示 ",3);
				    
				   
				    
				    FTPController ftp = getFtpControoler();
				    
					//构造File类，方便拿到文件名
					File file = new File(uploadFile);
					String fileName = file.getName();
					String userName = ftp_userName.getText();
					if(userName.equals("")) {
						userName = "anonymous";
					}
					
					 //对文件进行 加密，并暂时存放在一个位置
				    try {
						ZHFileCipherTxst.encode(uploadFile, "/"+fileName, ZHFileCipherTxst.key);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				    
					
					
					System.out.println("用户名："+ userName);
					System.out.println("上传文件名："+ fileName);
					System.out.println("上传路径2："+ file.getPath());
					
					/************加密过程******************/
					//这里以#SDJZDX_zhenghui作为分隔符
					String jiamiqian = fileName+"#SDJZDX_zhenghuiagsdahscasdqwFTP"+userName;
					System.out.println("未加密后的文件名："+jiamiqian);
					
					String base64jiami_fileName = ZHFileBase64Key.base64jiami(jiamiqian.getBytes());
					System.out.println("加密后的文件名："+base64jiami_fileName);
					
					//上传文件  "/"+fileName
//					boolean uploadFile2 = ftp.uploadFile("/", base64jiami_fileName, uploadFile);
					boolean uploadFile2 = ftp.uploadFile("/", base64jiami_fileName, "/"+fileName);
					if (uploadFile2==true) {
						JOptionPane.showMessageDialog(null, "上传成功！", "提示", 1);
						//刷新ftp的文件列表
						shuaxin();
					}else {
						JOptionPane.showMessageDialog(null, "上传失败！", "【提示】", JOptionPane.ERROR_MESSAGE);
					}
					
					//删除暂存的文件
					File deleFile = new File("/"+fileName);
					deleFile.delete();
					
				}
			    
			}
		
			
		else if(e.getSource() == JMIs2[1]){//磁盘属性查看
			String temp_url = list.getSelectedValue() + "\\";
			Icon icon = GetFileIcon.getSingleSmallIcon(temp_url);	
			File file = new File(temp_url);			
			FileSystemView fileSys=FileSystemView.getFileSystemView();
			String name = fileSys.getSystemDisplayName(file);
			double Available = file.getFreeSpace() * 1.0 / Sizes[0];		
			double Used = file.getTotalSpace()* 1.0 / Sizes[0] - Available;
			FileProperties properties = new FileProperties(icon, name, Used, Available);
		}
		
		else if(e.getSource() == GoBtn || e.getSource() == GuideText){//通过地址栏进行文件地址跳转
			String url = GuideText.getText();
			if(url.length() > 0){
			File file = new File(url);
			if(file.exists()){
				stack.push(Cur_URL);
				Cur_URL = url;
				Go_There();
			}else{
				JOptionPane.showConfirmDialog(null, "没有找到该目录", "确认对话框", JOptionPane.YES_OPTION);
			}
			}else{
				Home_List();
			}
		}
		

	}
	
	/**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
           Pattern pattern = Pattern.compile("[0-9]*");
           Matcher isNum = pattern.matcher(str);
           if( !isNum.matches() ){
               return false;
           }
           return true;
    }
    
    /**
     * 构造返回ftp控制器的方法
     * @return
     */
    public FTPController getFtpControoler() {
    		FTPController ftp = null;
    		String host = ftp_host.getText(); //ftp地址
			String port = ftp_port.getText();//端口号
			String userName = ftp_userName.getText();//用户名
			String password = ftp_password.getText();//密码
			
			if(!host.equals("") && port.equals("") && userName.equals("") && password.equals("")) {
				ftp = new FTPController(host);
				System.out.println(1);
			}else if (!host.equals("") && !port.equals("") && userName.equals("") && password.equals("")) {
				if(isNumeric(port)) {//判断端口号是不是数字
					ftp = new FTPController(host, Integer.valueOf(port));
				}else {
					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
				}
				System.out.println(2);
			}else if (!host.equals("") && !port.equals("") && !userName.equals("") && !password.equals("")) {
				if(isNumeric(port)) {//判断端口号是不是数字
					ftp = new FTPController(host, Integer.valueOf(port),userName,password);
				}else {
					JOptionPane.showMessageDialog(null, "端口输入的不对", "【出错啦】", JOptionPane.ERROR_MESSAGE);
				}
				System.out.println(3);
			}
    	
    	return ftp;
    }
}
