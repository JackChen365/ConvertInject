package com.cz.convert.ui;

import com.cz.convert.Template;
import com.cz.convert.model.ActionItem;
import com.cz.convert.model.Configuration;
import com.cz.convert.model.InjectItem;
import com.cz.convert.ui.action.AddTemplateAction;
import com.cz.convert.ui.action.EditTemplateAction;
import com.cz.convert.ui.action.RemoveTemplateAction;
import com.cz.model.plugin.delegate.MessageDelegate;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import kotlin.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by cz on 2017/6/17.
 */
public class ConfigurationPanel {
    private final Project project;
    private JPanel rootComponent;
    private JPanel listPanel;
    private JTextField methodNameField;
    private JList classList;
    private final DefaultListModel<InjectItem> listModel=new DefaultListModel<>();
    private final DefaultListModel<ActionItem> classListModel =new DefaultListModel<>();
    private final JList<InjectItem> list = new JBList(listModel);
    private final Configuration configuration;

    private void createUIComponents() {
    }

    public ConfigurationPanel(Project project,Configuration configuration) {
        this.project=project;
        this.configuration=configuration;
        JPanel container = buildTemplatePanel();
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_BOTH);
        methodNameField.setText(configuration.getMethodName());
        classList.setModel(classListModel);
        listPanel.add(container,gridConstraints);
        List<InjectItem> items = configuration.getItems();
        items.forEach(listModel::addElement);
        //选中监听
        list.addListSelectionListener(e -> {
            if(list.getValueIsAdjusting()){
                selectTemplateItem(listModel.getElementAt(list.getSelectedIndex()));
            }
        });
        if(!items.isEmpty()){
            int index = configuration.getIndex();
            list.setSelectedIndex(index);
            selectTemplateItem(items.get(index));
        }
    }

    private JPanel buildTemplatePanel() {
        final ToolbarDecorator pathListDecorator = ToolbarDecorator.createDecorator(list);
        AddTemplateAction addTemplateAction = new AddTemplateAction();
        addTemplateAction.onNewTemplateAdded(this::addNewTemplate);
        pathListDecorator.setAddAction(addTemplateAction);
        RemoveTemplateAction removeTemplateAction = new RemoveTemplateAction();
        removeTemplateAction.onTemplateRemove(()->{
            if(1>=listModel.size()){
                MessageDelegate.INSTANCE.showMessage("You must have at least one item!","Remove Error!");
            } else {
                int selectedIndex = list.getSelectedIndex();
                listModel.remove(selectedIndex);
                list.setSelectedIndex(0);
                updateTemplateItems();
            }
            return null;
        });
        pathListDecorator.setRemoveAction(removeTemplateAction);
        EditTemplateAction editAction=new EditTemplateAction(list);
        editAction.onEditTemplateItem(item->{
            selectTemplateItem(item);
            listModel.set(list.getSelectedIndex(),item);
            return null;
        });
        pathListDecorator.setEditAction(editAction);
        pathListDecorator.setMoveDownAction(null);
        pathListDecorator.setMoveUpAction(null);
        final JPanel container = new JPanel(new BorderLayout());
        container.add(pathListDecorator.createPanel(), BorderLayout.CENTER);
        return container;
    }

    private Unit addNewTemplate(InjectItem injectItem) {
        listModel.addElement(injectItem);
        list.setSelectedIndex(listModel.size()-1);
        selectTemplateItem(injectItem);
        return null;
    }

    /**
     * 更新模块
     */
    public void updateTemplateItems() {
        Configuration configuration = getConfiguration();
        Template.INSTANCE.updateTemplateItem(configuration,project);
    }

    public boolean isModified(){
        Configuration configuration = getConfiguration();
        return !this.configuration.equals(configuration);
    }

    private Configuration getConfiguration() {
        Configuration configuration=new Configuration();
        configuration.setIndex(list.getSelectedIndex());
        configuration.methodName=methodNameField.getText();
        List<InjectItem> items = configuration.getItems();
        Enumeration<InjectItem> elements = listModel.elements();
        while(elements.hasMoreElements()){
            items.add(elements.nextElement());
        }
        return configuration;
    }

    /**
     * 选中一个模块条目
     * @param item
     */
    private void selectTemplateItem(InjectItem item){
        classListModel.clear();
        item.getActionItems().forEach(classListModel::addElement);

    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    private void $$$setupUI$$$() {
        createUIComponents();
    }

}
