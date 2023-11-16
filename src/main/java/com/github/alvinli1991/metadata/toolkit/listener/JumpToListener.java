package com.github.alvinli1991.metadata.toolkit.listener;

import com.github.alvinli1991.metadata.toolkit.message.JumpToNotifier;
import com.github.alvinli1991.metadata.toolkit.notification.MetadataToolkitNotifications;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Date: 2023/11/16
 * Time: 22:33
 */
public class JumpToListener implements JumpToNotifier {
    private final Project project;

    public JumpToListener(Project project) {
        this.project = project;

    }

    @Override
    public void jumpTo(String action) {
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
            PsiClass[] classes = shortNamesCache.getClassesByName(action, GlobalSearchScope.allScope(project));
            // filter class whose short name equals possibleClzName
            Optional<PsiClass> theClassOpt = Arrays.stream(classes)
                    .filter(psiClass -> StringUtils.equals(psiClass.getName(), action))
                    .findFirst();
            if (theClassOpt.isEmpty()) {
                MetadataToolkitNotifications.META_DATA_GROUP
                        .createNotification("Can't find class " + action, NotificationType.ERROR)
                        .notify(project);
                return;
            }
            PsiClass theClassPsi = theClassOpt.get();
            NavigationUtil.activateFileWithPsiElement(theClassPsi, true);
        });

    }
}
