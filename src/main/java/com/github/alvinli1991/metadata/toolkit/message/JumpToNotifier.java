package com.github.alvinli1991.metadata.toolkit.message;

import com.intellij.util.messages.Topic;

/**
 * Date: 2023/11/16
 * Time: 22:10
 */
public interface JumpToNotifier {

    Topic<JumpToNotifier> JUMP_TO_TOPIC = Topic.create("JumpToNotifier", JumpToNotifier.class);

    void jumpTo(String action);
}
