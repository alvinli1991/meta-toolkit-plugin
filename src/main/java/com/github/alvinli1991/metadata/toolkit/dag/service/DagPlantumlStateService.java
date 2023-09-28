package com.github.alvinli1991.metadata.toolkit.dag.service;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.intellij.psi.PsiFile;

/**
 * Date: 2023/9/12
 * Time: 23:41
 */
public interface DagPlantumlStateService extends IPlantumlState {


    /**
     * parse file to DAG
     *
     * @param psiFile
     * @return
     */
    LogicDag parse(PsiFile psiFile);

}
