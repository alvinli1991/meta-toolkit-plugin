package me.alvin.dev.toolkit.dag.domain.plantuml;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 10:46 AM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class State implements Plantuml {
    private String name;
    private String desc;
    @Singular(value = "child")
    private List<State> children;

    public String buildStart() {
        List<String> tokens = new ArrayList<>();
        tokens.add(StatePlantumlConstant.TOKEN_STATE);
        tokens.add(getName());
        tokens.add(StatePlantumlConstant.TOKEN_START_BRACE);

        return String.join(StringUtils.SPACE, tokens);
    }

    public String buildEnd() {
        if (CollectionUtils.isEmpty(children)) {
            return StatePlantumlConstant.TOKEN_END_BRACE;
        } else {
            return StatePlantumlConstant.TOKEN_END_BRACE + "\n";
        }

    }


    public String toPlantuml() {
        List<String> tokens = new ArrayList<>();
        tokens.add(buildStart());
        if (StringUtils.isNotBlank(getDesc())) {
            tokens.add(getName() + ":" + getDesc());
        }
        tokens.addAll(Optional.ofNullable(children)
                .orElse(Collections.emptyList())
                .stream().map(State::toPlantuml)
                .toList());
        tokens.add(buildEnd());
        return String.join("\n", tokens);
    }
}
