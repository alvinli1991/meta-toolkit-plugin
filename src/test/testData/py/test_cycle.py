def L1(req):
    context = kernels.init_action(req)
    abContext = kernels.hw_exp_action(context)

    afterPoiFilterList = kernels.common_poi_filter(context, id='common_poi_filterdefault1_0')

    isWxbPoiFilter = kernels.wxb_poi_filter_condition(abContext)
    afterWxbPoiFilterList = tf.cond(isWxbPoiFilter, lambda: do_wxb_poi_filter(context),
                                    lambda: not_do_wxb_poi_filter(abContext))
    isWxbSpuFilter = kernels.testfilter(context)
    isWxbPoiFilter = tf.cond(isWxbSpuFilter, lambda: kernels.do_wxb_spu_filter(afterWxbPoiFilterList),
                             lambda: not_do_wxb_spu_filter(afterWxbPoiFilterList))


def not_do_wxb_spu_filter(afterWxbPoiFilterList):
    return kernels.default1(afterWxbPoiFilterList, id='default1_1')


def not_do_wxb_poi_filter(pathMergePoiList):
    return kernels.default1(pathMergePoiList, id='default1_0')


def do_wxb_poi_filter(pathMergePoiList):
    return kernels.common_poi_filter(pathMergePoiList, poi_filters='wxbPoiFilter', id='common_poi_filterdefault1_1')
