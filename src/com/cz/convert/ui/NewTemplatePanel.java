package com.cz.convert.ui;

import com.cz.convert.model.ActionItem;
import com.cz.convert.model.InjectItem;
import com.cz.convert.ui.action.AddClassAction;
import com.cz.convert.ui.action.RemoveClassAction;
import com.cz.model.plugin.delegate.MessageDelegate;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import org.apache.http.util.TextUtils;
import sun.plugin2.message.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by cz on 2017/6/23.
 */
public class NewTemplatePanel {
    private JTextField nameField;
    private JButton createButton;
    private JButton cancelButton;
    private JPanel rootComponent;
    private JPanel actionList;
    private JTextField classField;
    private JTextField classKeyField;
    private JTextField actionField;
    private final DefaultListModel<ActionItem> listModel=new DefaultListModel<>();
    private final JList<ActionItem> list = new JBList(listModel);
    private Function1<InjectItem, Unit> listener;

    public NewTemplatePanel(DialogBuilder dialogBuilder,InjectItem item) {
        this.createButton.addActionListener(e->createNewTemplateItem(dialogBuilder));
        this.cancelButton.addActionListener(e -> dialogBuilder.getWindow().dispose());
        JPanel container = buildTemplatePanel();
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_BOTH);
        actionList.add(container,gridConstraints);

        //初始化默认的模块条目
        this.createButton.setText(null==item?"Create":"Update");
        if(null!=item){
            nameField.setText(item.getName());
            List<ActionItem> actionItems = item.getActionItems();
            for(ActionItem actionItem:actionItems){
                listModel.addElement(actionItem);
            }
            list.addListSelectionListener(e -> {
                if(list.getValueIsAdjusting()){
                    selectActionItem(list.getSelectedValue());
                }
            });
            if(!actionItems.isEmpty()){
                list.setSelectedIndex(0);
                selectActionItem(list.getSelectedValue());
            }
        }
    }

    private void selectActionItem(ActionItem item) {
        classField.setText(item.clazz);
        classKeyField.setText(item.getKey());
        actionField.setText(item.action.getValue());
    }


    private void createUIComponents() {
    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    private void $$$setupUI$$$() {
        createUIComponents();
    }

    private JPanel buildTemplatePanel() {
        final ToolbarDecorator pathListDecorator = ToolbarDecorator.createDecorator(list);
        AddClassAction addClassAction = new AddClassAction();
        addClassAction.onNewActionAdded(item->{
            createButton.setEnabled(true);
            listModel.addElement(item);
            return null;
        });
        pathListDecorator.setAddAction(addClassAction);
        RemoveClassAction removeClassAction = new RemoveClassAction();
        removeClassAction.onRemoveAction(()->{
            if(1>=listModel.size()){
                MessageDelegate.INSTANCE.showMessage("You must have at least one item!","Remove Error!");
            } else {
                listModel.remove(list.getSelectedIndex());
            }
            return null;
        });
        pathListDecorator.setRemoveAction(removeClassAction);
        pathListDecorator.setMoveDownAction(null);
        pathListDecorator.setMoveUpAction(null);
        final JPanel container = new JPanel(new BorderLayout());
        container.add(pathListDecorator.createPanel(), BorderLayout.CENTER);
        return container;
    }


    /**
     * 创建一个新的模板
     * @param dialogBuilder
     */
    private void createNewTemplateItem(DialogBuilder dialogBuilder) {
        String name = nameField.getText();
        if(TextUtils.isEmpty(name)){
            MessageDelegate.INSTANCE.showMessage("Template name is null!","Create Error!");
        } else {
            dialogBuilder.getWindow().dispose();
            //create a new template item
            InjectItem item=new InjectItem();
            item.setName(name);
            java.util.List<ActionItem> actionItems = item.getActionItems();
            Enumeration<ActionItem> elements = listModel.elements();
            while(elements.hasMoreElements()){
                actionItems.add(elements.nextElement());
            }
            if(null!=listener){
                listener.invoke(item);
            }
        }
    }

    public void setOnTemplateModifyListener(Function1<InjectItem, Unit> listener){
        this.listener=listener;
    }

}
