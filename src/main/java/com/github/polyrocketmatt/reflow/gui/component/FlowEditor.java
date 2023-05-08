package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;

import static com.github.polyrocketmatt.reflow.ReFlow.INTERFACE;

public class FlowEditor extends FlowComponent {

    private final JPanel pipelinePanel;
    private final JPanel logPanel;
    private final JSplitPane panel;
    private final FlowTabbedPanel pipeline;
    private final FlowTabbedPanel log;

    public FlowEditor(int index, int width, int height) {
        super(index);

        this.pipelinePanel = new JPanel();
        this.logPanel = new JPanel();
        this.panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pipelinePanel, logPanel);

        //  Settings
        this.pipelinePanel.setLayout(new BoxLayout(pipelinePanel, BoxLayout.Y_AXIS));
        this.logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        this.panel.setDividerSize(0);
        this.panel.setDividerLocation(height - 300);

        //  Initialize pipeline panel
        this.pipeline = new FlowTabbedPanel(false);
        this.pipelinePanel.add(pipeline.getComponent());

        //  Pipeline panels
        this.pipeline.add("Input");
        this.pipeline.add("Flow Obfuscation");
        this.pipeline.add("Output");

        //  Initialize log panel
        this.log = new FlowTabbedPanel(false);
        this.logPanel.add(log.getComponent());

        //  Log panels
        this.log.add("Log");

        INTERFACE.register(this);
    }

    @Override
    public JSplitPane getComponent() {
        return panel;
    }

    @Override
    public void setVisible(boolean visibility) {
        pipelinePanel.setVisible(visibility);
        logPanel.setVisible(visibility);
        panel.setVisible(visibility);
        pipeline.setVisible(visibility);
        log.setVisible(visibility);
    }

    public JPanel getPipelinePanel() {
        return pipelinePanel;
    }

    public JPanel getLogPanel() {
        return logPanel;
    }

    public FlowTabbedPanel getPipeline() {
        return pipeline;
    }

}
