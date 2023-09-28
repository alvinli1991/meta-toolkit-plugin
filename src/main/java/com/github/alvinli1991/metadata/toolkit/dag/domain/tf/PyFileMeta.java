package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import lombok.*;

import java.util.List;

/**
 * Date: 2023/9/19
 * Time: 2:35 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PyFileMeta {

    private String fileName;

    @Singular("outerAssign")
    private List<PyStatementMeta> outerAssignments;

    @Singular("function")
    private List<PyFuncMeta> functions;

}
