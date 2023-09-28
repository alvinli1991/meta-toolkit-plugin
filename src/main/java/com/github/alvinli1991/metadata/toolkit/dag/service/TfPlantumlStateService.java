package com.github.alvinli1991.metadata.toolkit.dag.service;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.PyFileMeta;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.PyNodeClass;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * Date: 2023/9/19
 * Time: 21:29
 */
public interface TfPlantumlStateService extends IPlantumlState {
    PyFileMeta parse(PsiFile psiFile);

    LogicDag trans(PyFileMeta dagGraph, List<PyNodeClass> pyNodeClasses);


    List<PyNodeClass> find(PsiFile psiFile);
}
