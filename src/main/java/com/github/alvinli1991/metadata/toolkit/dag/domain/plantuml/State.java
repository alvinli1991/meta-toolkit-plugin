package com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 2023/9/11
 * Time: 10:46 AM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class State implements Plantuml {
    private String name;

    @Singular(value = "description")
    private Map<String, String> descriptions;

    @Singular(value = "child")
    private List<State> children;

    public List<String> getDesc() {
        return Optional.ofNullable(descriptions)
                .map(Map::entrySet)
                .orElse(Collections.emptySet())
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.toList());
    }

    public Optional<String> getDescValue(String key) {
        return Optional.ofNullable(descriptions)
                .map(desc -> desc.get(key));
    }

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

        if (CollectionUtils.isNotEmpty(getDesc())) {
            getDesc().forEach(desc -> {
                tokens.add(getName() + ":" + desc);
            });

        }
        tokens.addAll(Optional.ofNullable(children)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(State::toPlantuml)
                .toList());
        tokens.add(buildEnd());
        return String.join("\n", tokens);
    }
}
