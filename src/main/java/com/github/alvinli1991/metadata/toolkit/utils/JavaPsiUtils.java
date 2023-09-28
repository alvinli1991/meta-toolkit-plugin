package com.github.alvinli1991.metadata.toolkit.utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Date: 2023/9/19
 * Time: 7:28 PM
 */
public class JavaPsiUtils {
    public static String getAnnoAttributeValue(JavaPsiFacade javaPsiFacade, PsiAnnotationMemberValue annoAttributeValue) {
        if (Objects.isNull(annoAttributeValue)) {
            return "";
        }
        PsiElement valueElement = annoAttributeValue.getOriginalElement();
        if (valueElement instanceof PsiLiteralExpression || valueElement instanceof PsiReferenceExpression) {
            Object valueHolder = javaPsiFacade.getConstantEvaluationHelper().computeConstantExpression(annoAttributeValue);
            if (Objects.nonNull(valueHolder)) {
                return String.valueOf(valueHolder);
            } else {
                PsiElement thePsiElement = ((PsiReferenceExpressionImpl) valueElement).resolve();
                if (Objects.nonNull(thePsiElement)) {
                    if (thePsiElement instanceof PsiEnumConstant) {
                        return Arrays.stream(thePsiElement.getChildren())
                                .filter(ele -> ele instanceof PsiIdentifier || ele instanceof PsiExpressionList)
                                .map(PsiElement::getText)
                                .collect(Collectors.joining());
                    }
                }
            }
        }

        if (valueElement instanceof PsiArrayInitializerMemberValue arrayValue) {
            return Arrays.stream(arrayValue.getInitializers())
                    .map(initValue -> getAnnoAttributeValue(javaPsiFacade, initValue))
                    .collect(Collectors.joining(","));
        }

        if (valueElement instanceof PsiClassObjectAccessExpression) {
            return valueElement.getText();
        }
        return "";
    }
}
