package UI;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MyCellRenderer extends JLabel implements ListCellRenderer {
	 Icon[] icons;
	//ArrayList<Icon> icons;

	public MyCellRenderer() {
	};

	public MyCellRenderer(Icon[] icons) {
		// TODO Auto-generated constructor stub
		this.icons = icons;
		//System.out.println("初始化icons的icons大小:"+icons.size());
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		String s = value.toString();
		setText(s);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// 加入宽度为5的空白边框
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		//System.out.println("CellRender用到的index:" + index);
		setIcon(icons[index]);// 设置图片
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		return this;
	}

}