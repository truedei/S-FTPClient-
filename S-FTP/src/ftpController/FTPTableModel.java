package ftpController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import util.ZHFileBase64Key;

/**
 * FTP文件目录的表格
 * @author zhenghui
 *
 */
public class FTPTableModel extends AbstractTableModel {
	// 定义表头数据
	String[] head = {"选择","修改时间","文件类型", "大小", "文件名"};
	
	//定义表格每一列的数据类型  
	Class[] typeArray = {Boolean.class,Object.class, Object.class,  Object.class, Object.class };

	//数据
	Object[][] data = null;
	ArrayList<FileEntity> fileList= null;
	
	 ArrayList<FileEntity> fileList1= null;
	
	//存储文件名和解密秘钥  K:文件名  V:解密秘钥
	Map<String, String> fileNameMap = new HashMap<>();
	
	//FTP用户名
	String ftpName= null;

	 //获取选中的名字
	 public String getSelectFileName(String  string) {
		 return fileNameMap.get(string);
	 }
	
	
	
	public void setData(ArrayList<FileEntity> fileList) {
		this.fileList = fileList;
		fileList1 = new ArrayList<>();
	}
	
	//动态加载数据
	public FTPTableModel(String ftpName) {
		this.ftpName = ftpName;
		
		//加载数据库数据
		if(fileList==null) {
			data=new Object[0][head.length];//动态创建表格
		}else {
			data=new Object[fileList.size()][head.length];//动态创建表格
		}
	}
	
	//动态加载数据
	public void getFTPTableData() {
		
		//加载数据库数据
		if(fileList!=null) {
			int size = 0;
			 //封装数据结果
	        for(int i=0;i<fileList.size();i++){
	        	try {
	        		  String jiaMiQianFileName = fileList.get(i).getFileName();//获取加密后的文件名
	                  byte[] base64jiemi = ZHFileBase64Key.base64jiemi(jiaMiQianFileName);
	                  String jiemiFileName = new String(base64jiemi);
	                  String[] split = jiemiFileName.split("#SDJZDX_zhenghuiagsdahscasdqwFTP");
	                  if (split[1].equals(ftpName)) {
	                  	size++;
	                  	
	                  	String fileSize = "";
	                  	if(Integer.valueOf(fileList.get(i).getFileSise())==-1) {
	                  		fileSize=""; //大小
	  	                }else {
	  	                	fileSize= fileList.get(i).getFileSise(); //大小
	  	                }
	                  	
	                  	//把符合的数据到加载到
	                  	fileList1.add(new FileEntity(split[0], fileSize, fileList.get(i).getFileType(), fileList.get(i).getFileUpdateDate()));
	  				}
				} catch (NullPointerException e) {
					System.out.println(fileList.get(i).getFileName()+"文件不符合要求，已过滤！");
				}
              
	        }
			System.out.println("size:"+size);
			//动态创建表格
			data=new Object[size][head.length];
		}
		
		//加载数据数据到表格
		if (fileList1.size()>0) {
			 //封装数据结果
	        for(int i=0;i<fileList1.size();i++){
            	try {
 	                data[i][0]=new Boolean(false);  //选择
	                data[i][1]=fileList.get(i).getFileUpdateDate(); //修改时间
	                data[i][2]=fileList.get(i).getFileType(); //文件类型
	               
	                String size = fileList.get(i).getFileSise();
	                
	                if(Integer.valueOf(fileList.get(i).getFileSise())==-1) {
	                	 data[i][3]=""; //大小
	                }else {
	                	 data[i][3]= size; //大小
	                }
	                
	                String jiaMiQianFileName = fileList.get(i).getFileName();//获取加密后的文件名
	                byte[] base64jiemi = ZHFileBase64Key.base64jiemi(jiaMiQianFileName);
	                
	                String jiemiFileName = new String(base64jiemi);
	                String[] split = jiemiFileName.split("#SDJZDX_zhenghuiagsdahscasdqwFTP");
                	data[i][4] = split[0] ; //文件名  解密
 	                fileNameMap.put(split[0], split[1]);  //文件名和秘钥对应起来并存储到Map里   文件名  秘钥(用户名)
 	                
            	} catch (Exception e) {
            		e.printStackTrace();
				}
	        }
		}
		
	}
	
	
	// 获得表格的列数
	public int getColumnCount() {
		return head.length;
	}

	// 获得表格的行数
	public int getRowCount() {
		return data.length;
	}

	// 获得表格的列名称
	@Override
	public String getColumnName(int column) {
		return head[column];
	}

	// 获得表格的单元格的数据
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	// 使表格具有可编辑性
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	// 替换单元格的值
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	// 实现了如果是boolean自动转成JCheckbox
	/*
	 * 需要自己的celleditor这么麻烦吧。jtable自动支持Jcheckbox，
	 * 只要覆盖tablemodel的getColumnClass返回一个boolean的class， jtable会自动画一个Jcheckbox给你，
	 * 你的value是true还是false直接读table里那个cell的值就可以
	 */
	public Class getColumnClass(int columnIndex) {
		return typeArray[columnIndex];// 返回每一列的数据类型
	}
	
	//是否选中一个
	public Boolean getSelectOne() {
		int sum = 0;
		
		 for (int i = 0; i < this.getRowCount(); i++) {
			 System.out.println("选中的状态："+Boolean.valueOf((boolean) this.getValueAt(i, 0)));
			 if(Boolean.valueOf((boolean) this.getValueAt(i, 0)) == true) {
				 sum  = sum +1;
			 }
		 }
		 
		return sum==1;
	}
	
	//获取选中的学生ID列的数据
	public String[] getSelectOneInfo() {
		String strID[] = new String[2];
		 for (int i = 0; i < this.getRowCount(); i++) {
			 if(Boolean.valueOf((boolean) this.getValueAt(i, 0)) == true) {
				 strID[0] = (String) this.getValueAt(i, 3);
				 strID[1] = (String) this.getValueAt(i, 4);
			 }
		 }
		return strID;
	}
	
	
	//是否选中的数量
	public int getSelectAllSum() {
		int sum = 0;
		
		 for (int i = 0; i < this.getRowCount(); i++) {
			 if(Boolean.valueOf((boolean) this.getValueAt(i, 0)) == true) {
				 sum  = sum +1;
			 }
		 }
		 
		return sum;
	}
	
	//获取所有选中的文件的名字
	public String[] getSelectAllInfo() {
		String strID[] = new String[getSelectAllSum()];
		int j = 0;
		 for (int i = 0; i < this.getRowCount(); i++) {
			 if(Boolean.valueOf((boolean) this.getValueAt(i, 0)) == true) {
				 strID[j] = (String) this.getValueAt(i, 4);
				 j = j + 1;
			 }
		 }
		return strID;
	}
	
	//获取存储的文件名和秘钥
	public Map<String, String> getFileNameMap() {
		return fileNameMap;
	}

	public void setFileNameMap(Map<String, String> fileNameMap) {
		this.fileNameMap = fileNameMap;
	}
	
}