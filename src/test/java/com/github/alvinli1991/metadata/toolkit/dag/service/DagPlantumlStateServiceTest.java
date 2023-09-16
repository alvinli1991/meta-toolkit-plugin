package com.github.alvinli1991.metadata.toolkit.dag.service;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateUml;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.PsiErrorElementUtil;

/**
 * Date: 2023/9/13
 * Time: 00:08
 */
public class DagPlantumlStateServiceTest extends BasePlatformTestCase {

    public void testParse() {
        PsiFile psiFile = myFixture.configureByFile("dag/testDag.xml");
        XmlFile xmlFile = assertInstanceOf(psiFile, XmlFile.class);
        assertFalse(PsiErrorElementUtil.hasErrors(myFixture.getProject(), xmlFile.getVirtualFile()));


        DagPlantumlStateService theService = myFixture.getProject().getService(DagPlantumlStateService.class);
        LogicDag logicDag = theService.parse(xmlFile);
        StateUml stateUml = theService.buildPlantumlState(logicDag);

        assertEquals(53, logicDag.getNodes().size());
        assertEquals(46, logicDag.getEdges().size());
        assertFalse(stateUml.getStates().isEmpty());
        assertFalse(stateUml.getStateRelations().isEmpty());
        assertEquals(10, stateUml.getStates().size());
        assertEquals(41, stateUml.getStateRelations().size());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
}
