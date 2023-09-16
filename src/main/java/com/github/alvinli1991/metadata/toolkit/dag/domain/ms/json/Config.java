package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Date: 2023/9/11
 * Time: 10:42 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Config {
    private ThreadPool threadPool;

    public Config() {
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(getThreadPool(), config.getThreadPool());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getThreadPool());
    }

    @Override
    public String toString() {
        return "Config{" +
                "threadPool=" + threadPool +
                '}';
    }

    public static final class Builder {
        private ThreadPool threadPool;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder threadPool(ThreadPool threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setThreadPool(threadPool);
            return config;
        }
    }
}
