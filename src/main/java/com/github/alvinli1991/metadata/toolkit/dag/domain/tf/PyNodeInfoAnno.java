package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Date: 2023/9/19
 * Time: 7:24 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PyNodeInfoAnno {
    public static final String PY_NODE_INFO_ANNOTATION = "";

    private String name;
    private String input;
    private String configKeys;

    private String output;
    private String des;

}
