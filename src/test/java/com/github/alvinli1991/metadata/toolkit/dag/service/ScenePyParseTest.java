package com.github.alvinli1991.metadata.toolkit.dag.service;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateUml;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.PyFileMeta;
import com.github.alvinli1991.metadata.toolkit.utils.JacksonUtils;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.python.psi.PyFile;

import java.util.Collections;

/**
 * Date: 2023/9/19
 * Time: 3:01 PM
 */
public class ScenePyParseTest extends BasePlatformTestCase {

    public void testPyParse() {
        PsiFile psiFile = myFixture.configureByFile("py/test.py");
        PyFile pyFile = assertInstanceOf(psiFile, PyFile.class);

        TfPlantumlStateService theService = myFixture.getProject().getService(TfPlantumlStateService.class);
        PyFileMeta pyMeta = theService.parse(pyFile);
        System.out.println(JacksonUtils.toJson(pyMeta));
        LogicDag logicDag = theService.trans(pyMeta, Collections.emptyList());
        System.out.println(JacksonUtils.toJson(logicDag));


        StateUml stateUml = theService.buildPlantumlState(logicDag);
        System.out.println(JacksonUtils.toJson(stateUml));
        System.out.println(stateUml.toPlantuml() + "\n");

    }

    public void testPyCycle() {
        PsiFile psiFile = myFixture.configureByFile("py/test_cycle.py");
        PyFile pyFile = assertInstanceOf(psiFile, PyFile.class);

        TfPlantumlStateService theService = myFixture.getProject().getService(TfPlantumlStateService.class);
        PyFileMeta pyMeta = theService.parse(pyFile);
        System.out.println(JacksonUtils.toJson(pyMeta));
        LogicDag logicDag = theService.trans(pyMeta, Collections.emptyList());
        System.out.println(JacksonUtils.toJson(logicDag));

        System.out.println("has cycle:" + theService.hasCycle(logicDag));
        StateUml stateUml = theService.buildPlantumlState(logicDag);
        System.out.println(JacksonUtils.toJson(stateUml));
        System.out.println(stateUml.toPlantuml() + "\n");

    }


    @Override
    protected String getTestDataPath() {
        return "src/test/testData";

    }
}
