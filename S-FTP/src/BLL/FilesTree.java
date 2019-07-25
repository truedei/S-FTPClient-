package BLL;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.*;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import UI.MainForm;
 
/**
 * @author Kirill Grouchnikov
 */
public class FilesTree extends JPanel {
    /**
     * File system view.
     */
    protected static FileSystemView fsv = FileSystemView.getFileSystemView();
 
    /**
     * Renderer for the file tree.
     * 
     * @author Kirill Grouchnikov
     */
    public static class FileTreeCellRenderer extends DefaultTreeCellRenderer {
        /**
         * Icon cache to speed the rendering.
         */
        private Map<String, Icon> iconCache = new HashMap<String, Icon>();
 
        /**
         * Root name cache to speed the rendering.
         */
        private Map<File, String> rootNameCache = new HashMap<File, String>();
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
         *      java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Icon[] GetFileIcons(String path){
        	Icon[] icons = null;
        	String[] files = GetFileNames.getFileName(path);
        	for(int i = 0; i < files.length; ++i){
        		icons[i] = this.iconCache.get(files[i]);
        	}
        	return icons;
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            FileTreeNode ftn = (FileTreeNode) value;
            File file = ftn.file;
            String filename = "";
            if (file != null) {
                if (ftn.isFileSystemRoot) {
                    filename = this.rootNameCache.get(file);
                    if (filename == null) {
                        filename = fsv.getSystemDisplayName(file);
                        this.rootNameCache.put(file, filename);
                    }              
                } else {
                    filename = file.getName();
                }
            }
            JLabel result = (JLabel) super.getTreeCellRendererComponent(tree,
                    filename, sel, expanded, leaf, row, hasFocus);
            if (file != null) {
                Icon icon = this.iconCache.get(filename);
                if (icon == null) {
                    icon = fsv.getSystemIcon(file);
                    this.iconCache.put(filename, icon);
                }
                result.setIcon(icon);
            }
            return result;
        }
    }
 
    /**
     * A node in the file tree.
     * 
     * @author Kirill Grouchnikov
     */
    private static class FileTreeNode implements TreeNode {
        /**
         * Node file.
         */
        private File file;
 
        /**
         * Children of the node file.
         */
        private File[] children;
 
        /**
         * Parent node.
         */
        private TreeNode parent;
 
        /**
         * Indication whether this node corresponds to a file system root.
         */
        private boolean isFileSystemRoot;
 
        /**
         * Creates a new file tree node.
         * 
         * @param file
         *            Node file
         * @param isFileSystemRoot
         *            Indicates whether the file is a file system root.
         * @param parent
         *            Parent node.
         */
        public FileTreeNode(File file, boolean isFileSystemRoot, TreeNode parent) {
            this.file = file;
            this.isFileSystemRoot = isFileSystemRoot;
            this.parent = parent;
            this.children = this.file.listFiles();
            if (this.children == null)
                this.children = new File[0];
        }
 
        public String toString(){
        	if(file != null)
        	return file.getAbsolutePath();
        	else
        		return "";
        }
        /**
         * Creates a new file tree node.
         * 
         * @param children
         *            Children files.
         */
        public FileTreeNode(File[] children) {
            this.file = null;
            this.parent = null;
            this.children = children;
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#children()
         */
        public Enumeration<?> children() {
            final int elementCount = this.children.length;
            return new Enumeration<File>() {
                int count = 0;
 
                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Enumeration#hasMoreElements()
                 */
                public boolean hasMoreElements() {
                    return this.count < elementCount;
                }
 
                /*
                 * (non-Javadoc)
                 * 
                 * @see java.util.Enumeration#nextElement()
                 */
                public File nextElement() {
                    if (this.count < elementCount) {
                        return FileTreeNode.this.children[this.count++];
                    }
                    throw new NoSuchElementException("Vector Enumeration");
                }
            };
 
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getAllowsChildren()
         */
        public boolean getAllowsChildren() {
            return true;
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getChildAt(int)
         */
        public TreeNode getChildAt(int childIndex) {
            return new FileTreeNode(this.children[childIndex],
                    this.parent == null, this);
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getChildCount()
         */
        public int getChildCount() {
            return this.children.length;
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
         */
        public int getIndex(TreeNode node) {
            FileTreeNode ftn = (FileTreeNode) node;
            for (int i = 0; i < this.children.length; i++) {
                if (ftn.file.equals(this.children[i]))
                    return i;
            }
            return -1;
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getParent()
         */
        public TreeNode getParent() {
            return this.parent;
        }
 
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#isLeaf()
         */
        public boolean isLeaf() {
            return (this.getChildCount() == 0);
        }
    }
 
    /**
     * The file tree.
     */
    private JTree tree;
 
    /**
     * Creates the file tree panel.
     */
    public FilesTree() {
        this.setLayout(new BorderLayout());
        File[] roots = File.listRoots();
        FileTreeNode rootTreeNode = new FileTreeNode(roots);
        this.tree = new JTree(rootTreeNode);
        this.tree.setCellRenderer(new FileTreeCellRenderer());
        this.tree.setRootVisible(false);
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {
        	   public void valueChanged(TreeSelectionEvent e) {
        		   if(tree.getLastSelectedPathComponent() == null) return;
        		   String chooseUrl = tree.getLastSelectedPathComponent().toString();
        		   File temp_file = new File(chooseUrl);
        		   if(temp_file.isDirectory()){
        			   MainForm._instance.stack.push(MainForm._instance.Cur_URL);
        			   MainForm._instance.Cur_URL = chooseUrl;        			 
        			   MainForm._instance.Go_There();        			   
        		   }
        		  // System.out.println("我点击的节点的值：" + tree.getLastSelectedPathComponent().toString()); 
        	   }
        });    
        final JScrollPane jsp = new JScrollPane(this.tree);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.add(jsp, BorderLayout.CENTER);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("File tree");
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                frame.add(new FilesTree());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
