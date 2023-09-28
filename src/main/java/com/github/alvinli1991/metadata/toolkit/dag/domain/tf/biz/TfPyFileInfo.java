package com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2023/9/20
 * Time: 11:09 AM
 */
public class TfPyFileInfo {
    //默认的action
    public static final Map<String, String> DEFAULT_NAME_TO_CLASS;
    public static final String TF = "tf";
    public static final String OS = "os";
    public static final String TF_COND = "cond";
    public static final String TF_KERNELS = "kernels";
    public static final String TF_PLACEHOLDER = "placeholder";


    public static final String TF_COND_SUFFIX = "_cond";


    public static final String OUTER_MOST_FUNC_TAG = "__";

    public static final String INNER_FUNC_PREFIX = "__";

    static {
        DEFAULT_NAME_TO_CLASS = new HashMap<>();
        DEFAULT_NAME_TO_CLASS.put("default1", "com.sankuai.meituan.waimai.ad.graph.function.virtual.DefaultAction1");
        DEFAULT_NAME_TO_CLASS.put("default2", "com.sankuai.meituan.waimai.ad.graph.function.virtual.DefaultAction2");
        DEFAULT_NAME_TO_CLASS.put("default3", "com.sankuai.meituan.waimai.ad.graph.function.virtual.DefaultAction3");
        DEFAULT_NAME_TO_CLASS.put("default4", "com.sankuai.meituan.waimai.ad.graph.function.virtual.DefaultAction4");
    }
}
