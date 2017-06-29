package com.cz.convert.ui;


import com.cz.convert.model.ActionItem;
import com.cz.convert.model.ActionType;
import com.cz.model.plugin.delegate.MessageDelegate;
import com.intellij.openapi.ui.DialogBuilder;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by cz on 2017/6/27.
 */
public class NewActionPanel {
    private JPanel rootComponent;
    private JTextField classField;
    private JTextField classKey;
    private JList<ActionType> actionList;
    private JButton createButton;
    private JButton cancelButton;
    private JTextField classNameField;
    private Function1<ActionItem, Unit> listener;

    public NewActionPanel(DialogBuilder dialogBuilder) {
        classField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                createButton.setEnabled(true);
                String text = classField.getText();
                int lastIndex = text.lastIndexOf(".");
                if(0>lastIndex){
                    classNameField.setText(text);
                } else {
                    classNameField.setText(text.substring(lastIndex+1));
                }
            }
        });
        DefaultListModel<ActionType> listModel=new DefaultListModel<>();
        for(ActionType type:ActionType.values()){
            listModel.addElement(type);
        }
        actionList.setModel(listModel);
        actionList.setSelectedIndex(0);
        createButton.addActionListener(e->createAction(dialogBuilder));
        cancelButton.addActionListener(e->dialogBuilder.getWindow().dispose());
    }

    /**
     * 创建一个actionItem
     */
    private void createAction(DialogBuilder dialogBuilder){
        String classFieldText = classField.getText();
        String classKeyText = classKey.getText();
        if(TextUtils.isEmpty(classFieldText)){
            MessageDelegate.INSTANCE.showMessage("Action name is null!","Create Error!");
        } else if(TextUtils.isEmpty(classKeyText)){
            MessageDelegate.INSTANCE.showMessage("Annotation class value is null!","Create Error!");
        } else if(null!=listener){
            dialogBuilder.getWindow().dispose();
            ActionItem item=new ActionItem();
            item.name=classNameField.getText();
            item.clazz=classFieldText;
            item.setKey(classKeyText);
            item.action=actionList.getSelectedValue();
            listener.invoke(item);
        }
    }

    public void setOnCreateNewActionListener(Function1<ActionItem,Unit> listener){
        this.listener=listener;
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
