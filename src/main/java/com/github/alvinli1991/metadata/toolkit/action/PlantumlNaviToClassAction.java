package com.github.alvinli1991.metadata.toolkit.action;

import com.github.alvinli1991.metadata.toolkit.notification.MetadataToolkitNotifications;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.plantuml.idea.grammar.psi.impl.PumlItemImpl;
import org.plantuml.idea.lang.PlantUmlLanguage;

import java.util.Arrays;
import java.util.Optional;

/**
 * Date: 2023/9/18
 * Time: 21:18
 */
public class PlantumlNaviToClassAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final PsiFile psiFile = e.getRequiredData(CommonDataKeys.PSI_FILE);
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        final Caret primaryCaret = caretModel.getPrimaryCaret();

        PsiElement caretElement = psiFile.findElementAt(primaryCaret.getOffset());
        if (null == caretElement) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Can't find class name", NotificationType.ERROR)
                    .notify(project);
            return;
        }

        PumlItemImpl pumlItem = PsiTreeUtil.getParentOfType(caretElement, PumlItemImpl.class);

        if (null == pumlItem) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Can't find element", NotificationType.ERROR)
                    .notify(project);
            return;
        }


        if (pumlItem.getTextLength() > 100) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("This may not be a class name, length too long", NotificationType.ERROR)
                    .notify(project);
            return;
        }
        String possibleClzName = pumlItem.getText();
        if (StringUtils.isBlank(possibleClzName)) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Class name empty", NotificationType.ERROR)
                    .notify(project);
            return;
        }


        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiClass[] classes = shortNamesCache.getClassesByName(possibleClzName, GlobalSearchScope.allScope(project));
        // filter class whose short name equals possibleClzName
        Optional<PsiClass> theClassOpt = Arrays.stream(classes)
                .filter(psiClass -> StringUtils.equals(psiClass.getName(), possibleClzName))
                .findFirst();
        if (theClassOpt.isEmpty()) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Can't find class " + possibleClzName, NotificationType.ERROR)
                    .notify(project);
            return;
        }

        PsiClass theClassPsi = theClassOpt.get();
        NavigationUtil.activateFileWithPsiElement(theClassPsi, true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        boolean isEnable = project != null && psiFile != null && editor != null && (psiFile.getLanguage().is(PlantUmlLanguage.INSTANCE));
        e.getPresentation().setEnabledAndVisible(isEnable);
    }
}
