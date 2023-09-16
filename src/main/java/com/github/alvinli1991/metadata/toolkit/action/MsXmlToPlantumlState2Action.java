package com.github.alvinli1991.metadata.toolkit.action;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateUml;
import com.github.alvinli1991.metadata.toolkit.dag.service.DagPlantumlStateService;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Date: 2023/9/8
 * Time: 2:38 PM
 */
public class MsXmlToPlantumlState2Action extends AnAction {

    private static final Logger LOG = Logger.getInstance(MsXmlToPlantumlState2Action.class);

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        //读取选中的xml文件
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        XmlFile xmlFile = (XmlFile) psiFile;
        DagPlantumlStateService dagPlantumlStateService = project.getService(DagPlantumlStateService.class);
        LogicDag logicDag = dagPlantumlStateService.parse(psiFile);
        if (null == logicDag) {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Meta Data Toolkit")
                    .createNotification("File data can't be parsed", NotificationType.ERROR)
                    .notify(project);
            return;
        }

        StateUml stateUml = dagPlantumlStateService.buildPlantumlState(logicDag);

        //save to file
        String statePlantUml = stateUml.toPlantuml();
        ApplicationManager.getApplication().runWriteAction(() -> {
            PsiDirectory xmlFileDirectory = xmlFile.getContainingDirectory();
            String fileName = logicDag.getId() + ".puml";
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

            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Meta Data Toolkit")
                    .createNotification("PlantUML state generated", NotificationType.INFORMATION)
                    .notify(project);
        });
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean canProcess = project.getService(DagPlantumlStateService.class).canProcess(psiFile);
        event.getPresentation().setEnabledAndVisible(canProcess);
    }
}
