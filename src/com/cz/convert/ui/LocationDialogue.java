package com.cz.convert.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;


/**
 * Allows selection of the location of the CheckStyle file.
 */
public class LocationDialogue extends JDialog {

    private static final Insets COMPONENT_INSETS = JBUI.insets(4);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;


    private final Project project;

    private JButton commitButton;
    private JButton previousButton;
    private boolean committed = true;


    public LocationDialogue(@NotNull final Project project) {
        super(WindowManager.getInstance().getFrame(project));
        this.project = project;
        initialise();
    }

    public void initialise() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        setLocation((toolkit.getScreenSize().width - getSize().width) / 2,
                (toolkit.getScreenSize().height - getSize().height) / 2);
    }

    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            this.committed = false;
        }
        super.setVisible(visible);
    }


}
