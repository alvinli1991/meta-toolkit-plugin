package com.github.alvinli1991.metadata.toolkit.api.http;

import com.github.alvinli1991.metadata.toolkit.message.JumpToNotifier;
import com.github.alvinli1991.metadata.toolkit.notification.MetadataToolkitNotifications;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;

import java.io.IOException;


/**
 * Date: 2023/11/16
 * Time: 21:43
 */
public class JumpToHttpService extends RestService {
    public JumpToHttpService() {

        MetadataToolkitNotifications.META_DATA_GROUP
                .createNotification("JumpToHttpService init", NotificationType.INFORMATION)
                .notify(getLastFocusedOrOpenedProject());
        //get http port


    }

    @Nullable
    @Override
    public String execute(@NotNull QueryStringDecoder queryStringDecoder, @NotNull FullHttpRequest fullHttpRequest, @NotNull ChannelHandlerContext channelHandlerContext) throws IOException {
        String actionName = getStringParameter("action", queryStringDecoder);
        if (StringUtils.isNotBlank(actionName)) {
            Project theProject = getLastFocusedOrOpenedProject();
            if (null != theProject) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    theProject.getMessageBus().syncPublisher(JumpToNotifier.JUMP_TO_TOPIC).jumpTo(actionName);
                });
            }
        }

        sendOk(fullHttpRequest, channelHandlerContext);
        return "ok";
    }

    @NotNull
    @Override
    protected String getServiceName() {
        return "metaJump";
    }

    @Override
    protected boolean isMethodSupported(@NotNull HttpMethod method) {
        return method == HttpMethod.POST || method == HttpMethod.GET;
    }
}
