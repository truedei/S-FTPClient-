package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;

import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;


public class ZTFP_Main {

	private JFrame frmSftp;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ZTFP_Main window = new ZTFP_Main();
					window.frmSftp.setVisible(true);
					window.frmSftp.setSize(1400, 900);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ZTFP_Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSftp = new JFrame();
		frmSftp.setTitle("S-FTP");
		frmSftp.getContentPane().setLayout(null);
		frmSftp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBounds(14, 78, 887, 49);
		frmSftp.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\u5730\u5740\uFF1A");
		lblNewLabel.setBounds(14, 13, 45, 18);
		panel.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(58, 9, 157, 26);
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel label_2 = new JLabel("\u7AEF\u53E3\uFF1A");
		label_2.setBounds(218, 8, 45, 27);
		panel.add(label_2);
		
		textField_3 = new JTextField();
		textField_3.setBounds(263, 8, 56, 26);
		panel.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel label = new JLabel("\u7528\u6237\u540D\uFF1A");
		label.setBounds(333, 8, 74, 27);
		panel.add(label);
		
		textField_1 = new JTextField();
		textField_1.setBounds(400, 8, 145, 26);
		panel.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel label_1 = new JLabel("\u5BC6\u7801\uFF1A");
		label_1.setBounds(558, 8, 45, 27);
		panel.add(label_1);
		
		textField_2 = new JTextField();
		textField_2.setBounds(604, 8, 145, 26);
		panel.add(textField_2);
		textField_2.setColumns(10);
				
		JButton button = new JButton("\u8FDE\u63A5");
		button.setBounds(775, 9, 82, 27);
		panel.add(button);
		
		JSeparator separator = new JSeparator();
		separator.setToolTipText("");
		separator.setBounds(0, 140, 1382, 20);
		frmSftp.getContentPane().add(separator);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(14, 13, 273, 52);
		frmSftp.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnNewButton = new JButton("\u5237\u65B0");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\shuaxin.png"));
		btnNewButton.setBounds(151, 0, 115, 52);
		setBF(btnNewButton); //设置按钮不显示边框、焦点
		panel_1.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("\u65AD\u5F00");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setIcon(new ImageIcon("E:\\eclipse-workspace\\S-FTP\\image\\duankai.png"));
		btnNewButton_1.setBounds(0, 0, 115, 52);
		setBF(btnNewButton_1); //设置按钮不显示边框、焦点
		panel_1.add(btnNewButton_1);
		
		
		JFileChooser jfc=new JFileChooser();
		jfc.setBounds(835, 228, 617, 313);
		frmSftp.getContentPane().add(jfc);
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
		//JSeparator.VERTICAL  垂直    这两个构造函数使得我们可以创建一个水平或是垂直分隔。如果没有指定方向，则为水平方向。如果我们希望显示式指定方向，我们可以使用JSeparator的常量HORIZONTAL或是VERTICAL。
		
		JSeparator separator_2 = new JSeparator(JSeparator.VERTICAL);
		separator_2.setBounds(820, 150, 22, 577);
		frmSftp.getContentPane().add(separator_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(BorderFactory.createLoweredBevelBorder());
		panel_2.setLayout(null);
		panel_2.setBounds(10, 150, 549, 376);
		frmSftp.getContentPane().add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(BorderFactory.createTitledBorder("本地"));
		panel_3.setBounds(14, 13, 433, 43);
		panel_2.add(panel_3);
		
		panel_3.setLayout(null);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(48, 13, 377, 24);
		panel_3.add(comboBox);
		comboBox.setToolTipText("1\r\n2\r\n3\r\n4\r\n");
		
		File file=jfc.getSelectedFile();
		ZFTP_JMenu p_JMenu = new ZFTP_JMenu();
		frmSftp.setJMenuBar(p_JMenu);
		
		
	}
	
	//设置按钮不显示边框、焦点
	private void setBF(JButton btn) {
		btn.setBorderPainted(false);//不显示边框
		btn.setFocusPainted(false);//不焦点
	}
	
    //定义“编辑”菜单
    private JMenu createEditMenu() {
        JMenu menu=new JMenu("编辑(E)");
        menu.setMnemonic(KeyEvent.VK_E);
        JMenuItem item=new JMenuItem("撤销(U)",KeyEvent.VK_U);
        item.setEnabled(false);
        menu.add(item);
        menu.addSeparator();
        item=new JMenuItem("剪贴(T)",KeyEvent.VK_T);
        menu.add(item);
        item=new JMenuItem("复制(C)",KeyEvent.VK_C);
        menu.add(item);
        menu.addSeparator();
        JCheckBoxMenuItem cbMenuItem=new JCheckBoxMenuItem("自动换行");
        menu.add(cbMenuItem);
        return menu;
    }
    
    
    /////////////////////本地文件显示  开始///////////////////
    
    
    /////////////////////本地文件显示  结束///////////////////
}
