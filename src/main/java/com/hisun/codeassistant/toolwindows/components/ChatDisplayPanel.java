package com.hisun.codeassistant.toolwindows.components;

import com.hisun.codeassistant.icons.HiCodeAssistantIcons;
import com.hisun.codeassistant.settings.state.HiCodeAssistantSettingsState;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ChatDisplayPanel extends JPanel {
    private final Title title;

    private final ResponseInfo responseInfo;

    public ChatDisplayPanel() {
        super(new BorderLayout());
        this.title = new Title();
        this.responseInfo = new ResponseInfo();
        this.add(title, BorderLayout.NORTH);
        this.add(responseInfo, BorderLayout.CENTER);
    }

    public ChatDisplayPanel setText(JComponent text) {
        responseInfo.setText(text);
        return this;
    }

    public ChatDisplayPanel setUserLabel() {
        this.title.setUserLabel();
        return this;
    }

    public ChatDisplayPanel setSystemLabel() {
        this.title.setSystemLabel();
        return this;
    }

    static class Title extends JPanel {

        Title() {
            super(new CardLayout());

            setBorder(JBUI.Borders.empty(12, 8, 4, 8));

            add(userLabel(), "user");
            add(systemLabel(), "system");

            JPanel iconsWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            iconsWrapper.setBackground(getBackground());
            add(iconsWrapper, BorderLayout.LINE_END);
        }

        public void setUserLabel() {
            ((CardLayout) this.getLayout()).show(this, "user");
        }

        public void setSystemLabel() {
            ((CardLayout) this.getLayout()).show(this, "system");
        }

        private JBLabel userLabel() {
            return new JBLabel(HiCodeAssistantSettingsState.getInstance().getFullName(), HiCodeAssistantIcons.USER_ICON, SwingConstants.LEADING)
                    .setAllowAutoWrapping(true)
                    .withFont(JBFont.label().asBold());
        }

        private JBLabel systemLabel() {
            return new JBLabel("HiCodeAssistant", HiCodeAssistantIcons.SYSTEM_ICON, SwingConstants.LEADING)
                    .setAllowAutoWrapping(true)
                    .withFont(JBFont.label().asBold());
        }

    }

    static class ResponseInfo extends JPanel {
        private @Nullable JComponent text;

        ResponseInfo() {
            super(new BorderLayout());
            setBorder(JBUI.Borders.compound(
                    JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0),
                    JBUI.Borders.empty(5, 10, 10, 10)));
        }

        public JComponent getText() {
            return text;
        }

        public void setText(JComponent text) {
            this.text = text;
            this.add(text);
        }

    }
}
