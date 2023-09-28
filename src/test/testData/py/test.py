res = Test1(request)


def Test1(req):
    request = tf.placeholder(tf.variant, name='request')
    # init
    context = kernels.init(req)
    abContext = kernels.heAction(context)

    afterPoiFilterList = kernels.f1(context, id='cpm1_0')

    p1 = kernels.f2(abContext)
    p2 = tf.cond(p1, lambda: m4(context),
                 lambda: m3(abContext))
    p3 = kernels.testfilter(context)
    p4 = tf.cond(p3, lambda: kernels.f3(p2),
                 lambda: m2(p2))
    p5 = m1(abContext, context)

    p6 = kernels.f1(context)

    kernels.pL(context)
    kernels.pr(afterPoiFilterList, p4, p5)


def m1(abContext, context):
    request = tf.placeholder(tf.variant, name='request')
    h1 = kernels.f20(abContext)
    h2 = kernels.f21(h1)
    h3 = kernels.f22(context, h2)
    h4 = kernels.f23(h1, h3)
    h5 = kernels.f24(h4)
    h6 = kernels.f25(context, h4)
    h7 = kernels.f26(h4, h5, h6, context)
    h8 = kernels.f27(context, h7, abContext)
    h9 = kernels.f28(h8)
    h10 = kernels.f29(h7, h9)
    h11 = kernels.f30(h10, abContext)
    return h11


def m2(p2):
    return kernels.d1(p2, id='default1_1')


def m3(pathMergePoiList):
    return kernels.d1(pathMergePoiList, id='default1_0')


def m4(pathMergePoiList):
    return kernels.f1(pathMergePoiList, id='default1_2')
