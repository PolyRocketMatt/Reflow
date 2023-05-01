package com.github.polyrocketmatt.flowfuscate.gui;

import com.github.polyrocketmatt.flowfuscate.gui.icon.FlowWindowCloseIcon;
import com.github.polyrocketmatt.flowfuscate.gui.item.MenuActionItem;
import com.github.polyrocketmatt.flowfuscate.handler.ClassHandler;
import com.github.polyrocketmatt.flowfuscate.transformer.TransformerType;
import com.github.polyrocketmatt.flowfuscate.wrapper.ClassWrapper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.polyrocketmatt.flowfuscate.gui.GuiConstants.DIM_16;

public class FlowUI extends FrameUI {

    private final JTabbedPane transformerTabs;
    private final JTabbedPane explorerTabs;
    private final JTabbedPane logTabs;

    private final List<JPanel> explorerPanels = new ArrayList<>();
    private final List<JPanel> transformerPanels = new ArrayList<>();
    
    public FlowUI() {
        //  Swing GUI setup
        super("Flowfuscate", true);

        this.transformerTabs = new JTabbedPane();
        this.explorerTabs = new JTabbedPane();
        this.logTabs = new JTabbedPane();

        ImageIcon icon = new ImageIcon("src/main/resources/gui/flow_dark.png");

        frame.setIconImage(icon.getImage());
        frame.setJMenuBar(getMenuBar());

        //  General Layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(explorerTabs);
        mainPanel.add(transformerTabs);
        mainPanel.setVisible(true);

        explorerTabs.setVisible(true);
        transformerTabs.setVisible(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, logTabs);

        splitPane.setDividerLocation(600);
        frame.add(splitPane, BorderLayout.CENTER);

        //  Initialise tabs
        initExplorerTab();
        initTransformerTab();
        initLogTab();

        //  Finish frame
        finish();
    }

    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //  File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new MenuActionItem("Add Input").with(event -> {
            //  Create file chooser dialog
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle("Select JAR Input");

            //  Show dialog and process result
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                //  TODO: Initialize Processing...
                ClassHandler handler = new ClassHandler(selectedFile);

                //  Add the JTree to the explorer tab
                JScrollPane pane = constructClassTree(selectedFile.getName(), handler.getClasses().keySet());

                //  Get class explorer pane
                JPanel explorerPanel = explorerPanels.get(0);

                pane.setPreferredSize(new Dimension(200, 500));
                explorerPanel.add(pane, BorderLayout.WEST);
            }
        }).get());
        fileMenu.add(new MenuActionItem("Exit")
                .with(event -> System.exit(0))
                .icon(new FlowWindowCloseIcon(DIM_16))
                .get()
        );

        //  Explorer Menu
        JMenu explorerMenu = new JMenu("Explorer");
        explorerMenu.add(new MenuActionItem("Class Search").with(event -> {
            setExplorerVisible();
            explorerTabs.setSelectedIndex(0);
        }).get());

        //  Transformer Menu
        JMenu transformerMenu = new JMenu("Transformations");
        transformerMenu.add(new MenuActionItem("Input").with(event -> {
            setTransformersVisible();
            transformerTabs.setSelectedIndex(0);
        }).get());
        transformerMenu.add(new MenuActionItem("Flow Obfuscation").with(event -> {
            setTransformersVisible();
            transformerTabs.setSelectedIndex(1);
        }).get());
        transformerMenu.add(new MenuActionItem("String Encryption").with(event -> {
            setTransformersVisible();
            transformerTabs.setSelectedIndex(2);
        }).get());

        menuBar.add(fileMenu);
        menuBar.add(explorerMenu);
        menuBar.add(transformerMenu);

        return menuBar;
    }
    
    private void initTransformerTab() {
        Arrays.stream(TransformerType.values()).forEach(type -> {
            JPanel panel = getPanel(type.getName());

            transformerPanels.add(panel);
            transformerTabs.addTab(type.getName(), getPanel(type.getName()));
        });
    }

    private void initExplorerTab() {
        JPanel classExplorerPanel = getPanel("Class Explorer");

        explorerPanels.add(classExplorerPanel);
        explorerTabs.addTab("Class Explorer", classExplorerPanel);
    }

    private JPanel getPanel(String panelName) {
        JPanel panel = new JPanel();
        panel.setName(panelName);

        return panel;
    }

    private void setTransformersVisible() {
        explorerTabs.setVisible(false);
        transformerTabs.setVisible(true);
    }

    private void setExplorerVisible() {
        transformerTabs.setVisible(false);
        explorerTabs.setVisible(true);
    }

    private void initLogTab() {
        // Create a panel with a label in the middle
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("There is currently no log to display!");
        panel.add(label, new GridBagConstraints());

        // Add the panel to the tabbed pane
        logTabs.addTab("Log", panel);

        // Set the padding to center the label
        Insets insets = panel.getInsets();
        int horizontalPadding = (panel.getWidth() - label.getPreferredSize().width) / 2;
        int verticalPadding = (panel.getHeight() - label.getPreferredSize().height) / 2;
        panel.setBorder(BorderFactory.createEmptyBorder(verticalPadding + insets.top, horizontalPadding + insets.left,
                verticalPadding + insets.bottom, horizontalPadding + insets.right));
    }

    private JScrollPane constructClassTree(String name, Set<ClassWrapper> wrappers) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(name);

        //  Add classes to tree
        wrappers.forEach(wrapper -> insertIntoTree(root, wrapper));

        //  Create JTree
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        printTree(root, "");

        // Add the JTree to a scroll pane
        return new JScrollPane(tree);
    }

    private void insertIntoTree(DefaultMutableTreeNode root, ClassWrapper wrapper) {
        String[] path = wrapper.getClassName().split("/");
        DefaultMutableTreeNode current = root;

        for (String pathToken : path) {
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

    private void printTree(DefaultMutableTreeNode root, String prefix) {
        Enumeration<TreeNode> children = root.children();
        while (children.hasMoreElements()) {
            TreeNode child = children.nextElement();
            System.out.println(prefix + child.toString());

            for (int i = 0; i < child.getChildCount(); i++) {
                printTree((DefaultMutableTreeNode) child.getChildAt(i), prefix + "    ");
            }
        }
    }

}
