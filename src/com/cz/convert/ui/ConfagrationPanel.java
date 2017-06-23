package com.cz.convert.ui;

import com.cz.convert.ui.action.AddPathAction;
import com.cz.convert.ui.action.RemovePathAction;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cz on 2017/6/17.
 */
public class ConfagrationPanel {
    private JPanel rootComponent;
    private JCheckBox onlyConvertCheckBox;
    private JTree tree1;
    private JPanel listPanel;
    private final JList list = new JBList(new DefaultListModel<String>());

    private void createUIComponents() {
    }

    public ConfagrationPanel() {
        listPanel.add(buildClassPathPanel());
    }

    private JPanel buildClassPathPanel() {

        final ToolbarDecorator pathListDecorator = ToolbarDecorator.createDecorator(list);
        pathListDecorator.setAddAction(new AddPathAction());
        pathListDecorator.setRemoveAction(new RemovePathAction());

        final JPanel container = new JPanel(new BorderLayout());
        container.add(new TitledSeparator("Template list"), BorderLayout.NORTH);
        container.add(pathListDecorator.createPanel(), BorderLayout.CENTER);
        return container;
    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    private void $$$setupUI$$$() {
        createUIComponents();
    }



}
