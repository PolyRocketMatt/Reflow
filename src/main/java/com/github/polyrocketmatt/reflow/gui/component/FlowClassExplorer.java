package com.github.polyrocketmatt.reflow.gui.component;

import com.formdev.flatlaf.icons.FlatFileViewFileIcon;
import com.github.polyrocketmatt.reflow.utils.Decompiler;
import com.github.polyrocketmatt.reflow.utils.SortedTreeModel;
import com.github.polyrocketmatt.reflow.wrapper.ClassWrapper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Set;
import java.util.Stack;

import static com.github.polyrocketmatt.reflow.ReFlow.TEMP_DIR;

public class FlowClassExplorer implements FlowComponent {

    private final JPanel parentPanel;
    private final JScrollPane classTree;
    private final FlowTabbedPanel decompiledTabs;

    public FlowClassExplorer(String name, Set<ClassWrapper> wrappers) {
        this.parentPanel = new JPanel(new BorderLayout());
        this.parentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.classTree = constructClassTree(name, wrappers);
        this.classTree.setPreferredSize(new Dimension(325, 0));
        this.decompiledTabs = new FlowTabbedPanel(true);

        this.parentPanel.add(classTree, BorderLayout.WEST);
        this.parentPanel.add(decompiledTabs.getComponent(), BorderLayout.CENTER);
    }

    @Override
    public JPanel getComponent() {
        return parentPanel;
    }

    @Override
    public void setVisibile(boolean visibility) {
        classTree.setVisible(visibility);
        decompiledTabs.setVisibile(visibility);
        parentPanel.setVisible(visibility);
    }

    public JScrollPane getClassTree() {
        return classTree;
    }

    public FlowTabbedPanel getDecompiledTabs() {
        return decompiledTabs;
    }

    private JScrollPane constructClassTree(String name, Set<ClassWrapper> wrappers) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(name);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (leaf)
                    setIcon(new FlatFileViewFileIcon());
                return this;
            }
        };

        //  Add classes to tree
        wrappers.forEach(wrapper -> insertIntoTree(root, wrapper));

        //  Create JTree
        SortedTreeModel treeModel = new SortedTreeModel(root);
        treeModel.sortChildren(root);

        JTree tree = new JTree(treeModel);
        tree.setCellRenderer(renderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(event -> {
            //  Get the selected node
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            if (node.isLeaf()) {
                //  Get the path the node is pointing to
                Stack<String> path = new Stack<>();

                while (node != null) {
                    path.push(node.getUserObject().toString());
                    node = (DefaultMutableTreeNode) node.getParent();
                }

                //  We don't need the original .jar file name
                path.pop();

                StringBuilder builder = new StringBuilder();
                while (!path.isEmpty()) {
                    builder.append(path.pop());
                    if (!path.isEmpty())
                        builder.append("/");
                }

                String classPath = builder.toString();
                String className = classPath.split("/")[classPath.split("/").length - 1];

                //  TODO: Decompile the class based on the class name
                decompileClass(classPath, className, wrappers);
            }
        });

        return new JScrollPane(tree);
    }

    private void insertIntoTree(DefaultMutableTreeNode root, ClassWrapper wrapper) {
        String[] primaryPaths = wrapper.getClassName().split("/");
        DefaultMutableTreeNode current = root;

        for (String pathToken : primaryPaths) {
            //  See if the current node has a child with the same name
            Enumeration<TreeNode> children = current.children();

            boolean found = false;
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();

                //  If a child was found, we can continue down the tree
                if (child.getUserObject().equals(pathToken)) {
                    current = child;
                    found = true;
                    break;
                }
            }

            //  If we found a child, we can continue down the tree
            if (found)
                continue;

            //  If no child was found, we need to create a new node
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(pathToken);

            //  Add the new node to the current node
            current.add(newNode);

            //  Set the new node as the current node
            current = newNode;
        }
    }

    private void decompileClass(String classPath, String className, Set<ClassWrapper> wrappers) {
        //  First we check if there already is a tab with the same name
        if (decompiledTabs.getTabByName(className) != null) {
            decompiledTabs.setSelectedTab(decompiledTabs.getIndexOf(decompiledTabs.getTabByName(className)));
            return;
        }

        //  The .class file is located in the tmp directory with the class path appended
        File selectedFile = new File(TEMP_DIR, classPath + ".class");

        //  Check if the file exists
        String absolutePath = selectedFile.getAbsolutePath();

        //  Decompile
        Decompiler decompiler = new Decompiler(absolutePath);
        String decompiledSource = decompiler.getDecompiledSource();

        //  Create syntax-highlighted text area
        FlowSyntaxHighlightedTextView decompiledTextPane = new FlowSyntaxHighlightedTextView(decompiledSource, wrappers);
        FlowTab decompiledTab = new FlowTab(className);

        //  Finally, we add the tab to the tabbed pane and set it as the selected tab
        decompiledTab.getComponent().add(decompiledTextPane.getComponent(), BorderLayout.CENTER);
        decompiledTabs.add(decompiledTab);
        decompiledTabs.setSelectedTab(decompiledTab);
    }

}
