package me.alvin.dev.toolkit.dag.service.impl;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import me.alvin.dev.toolkit.dag.domain.common.LogicDag;
import me.alvin.dev.toolkit.dag.domain.common.Node;
import me.alvin.dev.toolkit.dag.domain.ms.MsKey;
import me.alvin.dev.toolkit.dag.domain.ms.MsNodeType;
import me.alvin.dev.toolkit.dag.domain.ms.xml.DagGraph;
import me.alvin.dev.toolkit.dag.domain.ms.xml.Depend;
import me.alvin.dev.toolkit.dag.domain.ms.xml.Stage;
import me.alvin.dev.toolkit.dag.domain.ms.xml.Unit;
import me.alvin.dev.toolkit.dag.service.DagPlantumlStateService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author: Alvin Li
 * Date: 2023/9/12
 * Time: 23:44
 */
public class MsDagPlantumlStateService implements DagPlantumlStateService {
    private final Project project;

    public MsDagPlantumlStateService(Project project) {
        this.project = project;
    }


    @Override
    public boolean canProcess(PsiFile psiFile) {
        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(XMLLanguage.INSTANCE));
        if (isEnable) {
            //是否是匹配的xml文档
            XmlFile xmlFile = (XmlFile) psiFile;
            DomManager domManager = DomManager.getDomManager(project);
            DomFileElement<DagGraph> dagFile = domManager.getFileElement(xmlFile, DagGraph.class);
            if (Objects.isNull(dagFile)) {
                isEnable = false;
            }
        }
        return isEnable;
    }

    @Override
    public LogicDag parse(PsiFile psiFile) {
        XmlFile xmlFile = (XmlFile) psiFile;
        DomManager domManager = DomManager.getDomManager(project);
        DomFileElement<DagGraph> dagFile = domManager.getFileElement(xmlFile, DagGraph.class);
        if (Objects.isNull(dagFile)) {
            return null;
        }
        DagGraph dagGraph = dagFile.getRootElement();
        List<Unit> units = dagGraph.getUnits().getUnits();
        List<Stage> stages = dagGraph.getStages().getStages();
        if (CollectionUtils.isEmpty(units) || CollectionUtils.isEmpty(stages)) {
            return null;
        }

        //解析node&edge
        LogicDag logicDag = new LogicDag(dagGraph.getDagId().getValue());
        //add node
        units.stream()
                .map(unit -> {
                    //创建action node
                    Node actionNode = new Node(unit.getId().getValue(), MsNodeType.Action.name(), unit.getDescription().getValue());
                    actionNode.putData(MsKey.clz.name(), unit.getClz().getClzName());
                    return actionNode;
                })
                .forEach(logicDag::addNode);
        stages.stream()
                .map(stage -> {
                    //创建stage node
                    Node stageNode = new Node(stage.getStageId(), MsNodeType.Stage.name(), stage.getStageDesc().getValue());
                    //设置node所属的stage
                    stage.getFlows().getFlows().forEach(flow -> {
                        logicDag.getNodeById(flow.getFrom().getValue())
                                .ifPresent(node -> node.putData(MsKey.stage.name(), stage.getStageId()));
                        logicDag.getNodeById(flow.getTo().getValue())
                                .ifPresent(node -> node.putData(MsKey.stage.name(), stage.getStageId()));
                    });
                    return stageNode;
                })
                .forEach(logicDag::addNode);

        //add edge
        stages.forEach(stage -> {
            //add stage edge
            List<Depend> depends = stage.getDepends().getDepends();
            if (CollectionUtils.isNotEmpty(depends)) {
                logicDag.getNodeById(stage.getStageId())
                        .ifPresent(to -> depends.forEach(depend -> {
                            Node from = logicDag.getNodeById(depend.getDependId().getValue()).orElse(null);
                            logicDag.addEdge(from, to);
                        }));
            } else {
                logicDag.getNodeById(stage.getStageId())
                        .ifPresent(from -> logicDag.addEdge(from, null));
            }
            //add node edge
            stage.getFlows().getFlows()
                    .forEach(flow -> logicDag.getNodeById(flow.getFrom().getValue())
                            .ifPresent(from -> {
                                        Node to = logicDag.getNodeById(flow.getTo().getValue()).orElse(null);
                                        logicDag.addEdge(from, to);
                                    }
                            ));
        });

        return logicDag;
    }
}
