package com.github.polyrocketmatt.reflow.gui;

import com.github.polyrocketmatt.reflow.gui.component.FlowClassExplorer;
import com.github.polyrocketmatt.reflow.gui.component.FlowComponent;
import com.github.polyrocketmatt.reflow.gui.component.FlowEditor;
import com.github.polyrocketmatt.reflow.gui.component.FlowMenu;
import com.github.polyrocketmatt.reflow.gui.component.FlowMenuBar;
import com.github.polyrocketmatt.reflow.gui.component.FlowMenuItem;
import com.github.polyrocketmatt.reflow.gui.component.FlowPanel;
import com.github.polyrocketmatt.reflow.gui.component.FlowTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowInterface extends JFrame {

    private final FlowInterfaceManager manager;
    private final FlowActions actions;
    private final Set<FlowComponent> panels = new HashSet<>();
    private final Logger logger = LoggerFactory.getLogger("FlowInterface");

    public FlowInterface() {
        //  Initializes the look & feel
        this.manager = new FlowInterfaceManager();

        //  Initialize actions
        this.actions = new FlowActions(this);

        //  Initialize interface stuff that needs to be done before the main window is created

        //  General window settings
        setTitle("ReFlow");
        setJMenuBar(getFlowBar());
        setIconImage(new ImageIcon("src/main/resources/logo/flow_dark.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        //  Initialize main components
        initDragAndDropPanel();
    }

    private JMenuBar getFlowBar() {
        FlowMenuBar flowMenuBar = new FlowMenuBar();

        //  Menus
        FlowMenu fileMenu = new FlowMenu("File");
        FlowMenu editMenu = new FlowMenu("Edit");
        FlowMenu transformersMenu = new FlowMenu("Transformers");

        //  Menu Items
        fileMenu.add(new FlowMenuItem("Open").withListener(event -> {
            actions.openFileAction(event);

            updateEditorPanel();
        }));
        fileMenu.add(new FlowMenuItem("Exit").withListener(actions::exitAction));

        //  Add menus to menu bar
        flowMenuBar.add(fileMenu);
        flowMenuBar.add(editMenu);
        flowMenuBar.add(transformersMenu);

        return flowMenuBar.getComponent();
    }

    @SuppressWarnings("unchecked")
    private void initDragAndDropPanel() {
        FlowPanel flowPanel = new FlowPanel(getNextIndex(), new GridBagLayout());
        JPanel panel = flowPanel.getComponent();
        JLabel label = new JLabel("Drag and drop a .jar file here");

        panel.add(label, new GridBagConstraints());
        panel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support))
                    return false;


                Transferable transferable = support.getTransferable();

                try {
                    List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (fileList.size() != 1)
                        throw new IllegalArgumentException("Only one file can be imported at a time");

                    logger.info("Importing file: " + fileList.get(0).getName());
                    actions.dragFileAction(fileList.get(0));

                    initEditorPanel();
                } catch (UnsupportedFlavorException | IOException | IllegalArgumentException ex) {
                    logger.error("Error importing file", ex);

                    return false;
                }

                return true;
            }
        });

        register(flowPanel);
    }

    private void initEditorPanel() {
        //  Initialize the class tree
        actions.initializeClassTree();

        //  Create the flow editor
        createFlowEditor();
    }

    private void updateEditorPanel() {
        //  Initialize the class tree
        actions.initializeClassTree();

        //  Unregister the old editor
        if (get(FlowEditor.class) != null)
            unregister(FlowEditor.class);

        //  Create the flow editor
        createFlowEditor();
        revalidate();
        repaint();
    }

    private void createFlowEditor() {
        //  Create the flow editor
        FlowEditor flowEditor = new FlowEditor(getNextIndex(), getWidth(), getHeight());
        FlowTab inputTab = flowEditor.getPipeline().getTabByIndex(0);
        FlowClassExplorer explorer = new FlowClassExplorer(actions.getCurrentFile().getName(), actions.getCurrentClassHandler().getClasses().keySet());

        //  Add class exploration to the input tab
        inputTab.getComponent().add(explorer.getComponent());

        register(flowEditor);
        setVisibility(flowEditor.getIndex());
    }

    public void register(FlowComponent component) {
        if (component.getIndex() != -1)
            panels.add(component);
        add(component.getComponent());
    }

    private void unregister(Class<?> clazz) {
        FlowComponent component = get(clazz);

        component.setVisibile(false);
        panels.removeIf(panel -> panel.getClass().equals(clazz));
    }

    private FlowComponent get(Class<?> clazz) {
        return panels.stream().filter(panel -> panel.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public void setVisibility(int index) {
        if (index < 0 || index >= panels.size())
            throw new IllegalArgumentException("Index out of bounds");
        panels.forEach(panel -> panel.getComponent().setVisible(panel.getIndex() == index));
    }

    public void setVisibility(FlowComponent flowComponent) {
        int index = flowComponent.getIndex();
        if (index < 0 || index >= panels.size())
            throw new IllegalArgumentException("Index out of bounds");
        panels.forEach(panel -> panel.getComponent().setVisible(panel.getIndex() == index));
    }

    public int getNextIndex() {
        return panels.size();
    }

}
