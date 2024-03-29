package com.hisun.codeassistant.toolwindows.chat.standard;

import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.conversations.ConversationService;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.hisun.codeassistant.settings.GeneralSettingsState;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.service.openai.OpenAISettings;
import com.hisun.codeassistant.settings.service.openai.OpenAISettingsState;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettings;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettingsState;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class ModelComboBoxAction extends ComboBoxAction {
    private final Runnable onAddNewTab;

    private final GeneralSettingsState settings;
    private final OpenAISettingsState openAISettings;
    private final SelfHostedLanguageModelSettingsState selfHostedLanguageModelSettings;

    public ModelComboBoxAction(Runnable onAddNewTab, ServiceType selectedService) {
        this.onAddNewTab = onAddNewTab;
        settings = GeneralSettings.getCurrentState();
        openAISettings = OpenAISettings.getCurrentState();
        selfHostedLanguageModelSettings = SelfHostedLanguageModelSettings.getCurrentState();
        updateTemplatePresentation(selectedService);
    }

    public JComponent createCustomComponent(@NotNull String place) {
        return createCustomComponent(getTemplatePresentation(), place);
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(
            @NotNull Presentation presentation,
            @NotNull String place) {
        ComboBoxButton button = createComboBoxButton(presentation);
        button.setBorder(null);
        return button;
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(@NotNull JComponent button, @NotNull DataContext dataContext) {
        var presentation = ((ComboBoxButton) button).getPresentation();
        var actionGroup = new DefaultActionGroup();
        actionGroup.addSeparator("OpenAI");
        List.of(OpenAIChatCompletionModel.GPT_4_0125_128k, OpenAIChatCompletionModel.GPT_3_5_0125_16k)
                .forEach(model -> actionGroup.add(createOpenAIModelAction(model, presentation)));
        actionGroup.addSeparator("HiCodeAssistant");
        List.of(SelfModelEnum.values()).forEach(modelEnum -> actionGroup.add(createSelfHostedLanguageModelAction(modelEnum, presentation)));
        return actionGroup;
    }

    @Override
    protected boolean shouldShowDisabledActions() {
        return true;
    }

    private void updateTemplatePresentation(ServiceType selectedService) {
        var templatePresentation = getTemplatePresentation();
        switch (selectedService) {
            case OPENAI:
                templatePresentation.setIcon(HiCodeAssistantIcons.OPENAI_ICON);
                templatePresentation.setText(OpenAIChatCompletionModel.findByCode(openAISettings.getModel()).getDescription());
                break;
            case SELF_HOSTED:
                templatePresentation.setIcon(HiCodeAssistantIcons.SYSTEM_ICON);
                templatePresentation.setText(SelfModelEnum.fromName(selfHostedLanguageModelSettings.getModel()).getDisplayName());
                break;
            default:
        }
    }

    private void handleProviderChange(
            ServiceType serviceType,
            String label,
            Icon icon,
            Presentation comboBoxPresentation) {
        settings.setSelectedService(serviceType);

        var currentConversation = ConversationsState.getCurrentConversation();
        if (Objects.isNull(currentConversation) || currentConversation.getMessages().isEmpty()) {
            comboBoxPresentation.setIcon(icon);
            comboBoxPresentation.setText(label);
            if (Objects.isNull(currentConversation)) {
                ConversationService.getInstance().startConversation();
            } else {
                ConversationService.getInstance().updateConversation(currentConversation);
            }
        } else {
            onAddNewTab.run();
        }
    }

    private AnAction createOpenAIModelAction(
            OpenAIChatCompletionModel model,
            Presentation comboBoxPresentation) {
        return new AnAction(model.getDescription(), "", HiCodeAssistantIcons.OPENAI_ICON) {
            @Override
            public void update(@NotNull AnActionEvent event) {
                var presentation = event.getPresentation();
                boolean sameIcon = presentation.getIcon().equals(comboBoxPresentation.getIcon());
                boolean sameText = presentation.getText().equals(comboBoxPresentation.getText());
                presentation.setEnabled(!sameIcon || !sameText);
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                openAISettings.setModel(model.getCode());
                handleProviderChange(
                        ServiceType.OPENAI,
                        model.getDescription(),
                        HiCodeAssistantIcons.OPENAI_ICON,
                        comboBoxPresentation);
            }
        };
    }

    private AnAction createSelfHostedLanguageModelAction(
            SelfModelEnum model,
            Presentation comboBoxPresentation) {
        return new AnAction(model.getDisplayName(), "", HiCodeAssistantIcons.SYSTEM_ICON) {
            @Override
            public void update(@NotNull AnActionEvent event) {
                var presentation = event.getPresentation();
                boolean sameIcon = presentation.getIcon().equals(comboBoxPresentation.getIcon());
                boolean sameText = presentation.getText().equals(comboBoxPresentation.getText());
                presentation.setEnabled(!sameIcon || !sameText);
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                selfHostedLanguageModelSettings.setModel(model.getName());
                handleProviderChange(
                        ServiceType.SELF_HOSTED,
                        model.getDisplayName(),
                        HiCodeAssistantIcons.SYSTEM_ICON,
                        comboBoxPresentation);
            }
        };
    }
}
