package com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Date: 2023/9/11
 * Time: 11:04 AM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StateRelation implements Plantuml {
    private State from;
    private State to;

    @Override
    public String toPlantuml() {
        if (Objects.isNull(from) && Objects.isNull(to)) {
            return StringUtils.EMPTY;
        }
        List<String> tokens = new ArrayList<>(3);
        if (Objects.nonNull(from)) {
            tokens.add(getFrom().getName());
            tokens.add(StatePlantumlConstant.TOKEN_RELATION);
        }
        if (Objects.nonNull(to)) {
            tokens.add(getTo().getName());
        }
        if (Objects.nonNull(from)) {
            from.getDescValue(PlantumlConstant.OUTPUTS_TAG)
                    .ifPresent(output -> {
                        if (StringUtils.isNotBlank(output)) {
                            tokens.add(StatePlantumlConstant.TOKEN_COLON + output);
                        }
                    });
        }
        return String.join(" ", tokens);
    }
}
