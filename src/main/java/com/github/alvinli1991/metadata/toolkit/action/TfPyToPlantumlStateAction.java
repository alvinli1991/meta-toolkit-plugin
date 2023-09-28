package com.github.alvinli1991.metadata.toolkit.action;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateUml;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.PyFileMeta;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.PyNodeClass;
import com.github.alvinli1991.metadata.toolkit.dag.service.TfPlantumlStateService;
import com.github.alvinli1991.metadata.toolkit.notification.MetadataToolkitNotifications;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.psi.PyFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Date: 2023/9/19
 * Time: 21:43
 */
public class TfPyToPlantumlStateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //读取选中的python文件
        Project project = e.getProject();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PyFile pyFile = (PyFile) psiFile;

        TfPlantumlStateService pyStateService = project.getService(TfPlantumlStateService.class);
        PyFileMeta pyFileMeta = pyStateService.parse(pyFile);
        LogicDag logicDag = null;
        try {
            List<PyNodeClass> pyNodeClasses = Collections.emptyList();
            logicDag = pyStateService.trans(pyFileMeta, pyNodeClasses);
        } catch (Exception ex) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification(ex.getMessage(), NotificationType.ERROR)
                    .notify(project);
            return;
        }


        if (null == logicDag) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("File data can't be parsed", NotificationType.ERROR)
                    .notify(project);
            return;
        }

        if (pyStateService.hasCycle(logicDag)) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("DAG存在圆环", NotificationType.ERROR)
                    .notify(project);
            return;
        }

        StateUml stateUml;
        try {
            stateUml = pyStateService.buildPlantumlState(logicDag);
        } catch (Exception ex) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification(ex.getMessage(), NotificationType.ERROR)
                    .notify(project);
            return;
        }

        if (null == stateUml) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Plantuml build fail", NotificationType.ERROR)
                    .notify(project);
            return;
        }


        String statePlantUml = stateUml.toPlantuml();
        LogicDag finalLogicDag = logicDag;
        ApplicationManager.getApplication().runWriteAction(() -> {
            PsiDirectory xmlFileDirectory = pyFile.getContainingDirectory();
            String fileName = finalLogicDag.getId() + ".puml";
            VirtualFile virtualFile = VfsUtil.findRelativeFile(xmlFileDirectory.getVirtualFile(), fileName);
            if (Objects.isNull(virtualFile)) {
                PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
                PsiFile statePlantUmlFile = psiFileFactory.createFileFromText(fileName
                        , PlainTextFileType.INSTANCE, statePlantUml);

                xmlFileDirectory.add(statePlantUmlFile);
            } else {
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                document.setText(statePlantUml);
            }
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("PlantUML state generated", NotificationType.INFORMATION)
                    .notify(project);
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(PythonLanguage.INSTANCE));
        e.getPresentation().setEnabledAndVisible(isEnable);

    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
