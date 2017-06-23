package com.cz.convert.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;

import javax.swing.*;

/**
 * Created by cz on 2017/6/23.
 */
public class NewTemplatePanel {
    private JTextField nameField;
    private JTextField injectAnnotationField;
    private JTextField methodNameField;
    private JCheckBox activityCheckBox;
    private JCheckBox fragmentCheckBox;
    private JCheckBox dialogCheckBox;
    private JCheckBox viewHolderCheckBox;
    private JButton createButton;
    private JButton cancelButton;
    private JPanel rootComponent;
    private Project project;

    public NewTemplatePanel(DialogBuilder dialogBuilder) {
        this.cancelButton.addActionListener(e -> dialogBuilder.getWindow().dispose());
    }

    private void createUIComponents() {
    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    private void $$$setupUI$$$() {
        createUIComponents();
    }
}
