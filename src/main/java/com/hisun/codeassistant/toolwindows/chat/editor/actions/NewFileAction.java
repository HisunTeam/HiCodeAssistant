package com.hisun.codeassistant.toolwindows.chat.editor.actions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.utils.file.FileUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;
import static java.util.Objects.requireNonNull;

public class NewFileAction extends AbstractAction {
    private final String fileExtension;
    private final EditorEx editor;

    public NewFileAction(@NotNull EditorEx editor, String fileExtension) {
        super(HiCodeAssistantBundle.get("toolwindow.chat.editor.action.new.description"), AllIcons.Actions.AddFile);
        this.fileExtension = fileExtension;
        this.editor = editor;
    }

    @Override
    public void actionPerformed(@NotNull ActionEvent event) {
        var project = requireNonNull(editor.getProject());
        var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
        var textFieldWithBrowseButton = new TextFieldWithBrowseButton();
        textFieldWithBrowseButton.setText(project.getBasePath());
        textFieldWithBrowseButton.addBrowseFolderListener(
                new TextBrowseFolderListener(fileChooserDescriptor, project));
        var fileNameTextField = new JBTextField("Untitled" + fileExtension);
        fileNameTextField.setColumns(30);

        if (showDialog(project, textFieldWithBrowseButton, fileNameTextField) == OK_EXIT_CODE) {
            var file = FileUtil.createFile(
                    textFieldWithBrowseButton.getText(),
                    fileNameTextField.getText(),
                    editor.getDocument().getText());
            var virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            if (virtualFile == null) {
                throw new RuntimeException("Couldn't find the saved virtual file");
            }
            var psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) {
                throw new RuntimeException("Couldn't find the saved psi file");
            }

            EditorHelper.openInEditor(psiFile);
        }
    }

    private int showDialog(
            Project project,
            TextFieldWithBrowseButton textFieldWithBrowseButton,
            JBTextField fileNameTextField) {
        var dialogBuilder = new DialogBuilder(project)
                .title(HiCodeAssistantBundle.get("toolwindow.chat.editor.action.new.title"))
                .centerPanel(FormBuilder.createFormBuilder()
                        .addLabeledComponent(HiCodeAssistantBundle.get("toolwindow.chat.editor.action.new.file.name"), fileNameTextField)
                        .addLabeledComponent(HiCodeAssistantBundle.get("toolwindow.chat.editor.action.new.file.desc"), textFieldWithBrowseButton)
                        .getPanel());
        dialogBuilder.addOkAction();
        dialogBuilder.addCancelAction();
        return dialogBuilder.show();
    }
}
