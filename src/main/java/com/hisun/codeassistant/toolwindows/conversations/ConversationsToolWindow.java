package com.hisun.codeassistant.toolwindows.conversations;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.toolwindow.DeleteAllConversationsAction;
import com.hisun.codeassistant.conversations.ConversationService;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ConversationsToolWindow extends JPanel {
    private final Project project;
    private final ConversationService conversationService;
    private final ScrollablePanel scrollablePanel;
    private final JScrollPane scrollPane;

    public ConversationsToolWindow(@NotNull Project project) {
        this.project = project;
        this.conversationService = ConversationService.getInstance();
        scrollablePanel = new ScrollablePanel();
        scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));

        scrollPane = new JBScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(scrollablePanel);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);
        refresh();
    }

    public JPanel getContent() {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);
        panel.setContent(scrollPane);

        var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
        actionGroup.add(new DeleteAllConversationsAction(this::refresh));

        var toolbar = ActionManager.getInstance()
                .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
        toolbar.setTargetComponent(panel);
        panel.setToolbar(toolbar.getComponent());
        return panel;
    }

    public void refresh() {
        scrollablePanel.removeAll();

        var sortedConversations = conversationService.getSortedConversations();
        if (sortedConversations.isEmpty()) {
            var emptyLabel = new JLabel(HiCodeAssistantBundle.get("toolwindow.chat.panel.none"));
            emptyLabel.setFont(JBFont.h2());
            emptyLabel.setBorder(JBUI.Borders.empty(8));
            scrollablePanel.add(emptyLabel);
        } else {
            sortedConversations.forEach(conversation -> {
                scrollablePanel.add(Box.createVerticalStrut(8));
                scrollablePanel.add(new ConversationPanel(project, conversation, () -> {
                    ConversationService.getInstance().deleteConversation(conversation);
                    refresh();
                }));
            });
        }

        scrollablePanel.revalidate();
        scrollablePanel.repaint();
    }
}
