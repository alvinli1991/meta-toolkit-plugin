package me.alvin.dev.toolkit.dag.domain.ms.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Date: 2023/9/11
 * Time: 10:43 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreadPool {
    private int coreSize;

    private int maxSize;

    private int queueSize;

    private int scheduledSize;

    public ThreadPool() {

    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getScheduledSize() {
        return scheduledSize;
    }

    public void setScheduledSize(int scheduledSize) {
        this.scheduledSize = scheduledSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadPool that = (ThreadPool) o;
        return getCoreSize() == that.getCoreSize() && getMaxSize() == that.getMaxSize() && getQueueSize() == that.getQueueSize() && getScheduledSize() == that.getScheduledSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCoreSize(), getMaxSize(), getQueueSize(), getScheduledSize());
    }

    @Override
    public String toString() {
        return "ThreadPool{" +
                "coreSize=" + coreSize +
                ", maxSize=" + maxSize +
                ", queueSize=" + queueSize +
                ", scheduledSize=" + scheduledSize +
                '}';
    }

    public static final class Builder {
        private int coreSize;
        private int maxSize;
        private int queueSize;
        private int scheduledSize;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder coreSize(int coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        public Builder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder queueSize(int queueSize) {
            this.queueSize = queueSize;
            return this;
        }

        public Builder scheduledSize(int scheduledSize) {
            this.scheduledSize = scheduledSize;
            return this;
        }

        public ThreadPool build() {
            ThreadPool threadPool = new ThreadPool();
            threadPool.setCoreSize(coreSize);
            threadPool.setMaxSize(maxSize);
            threadPool.setQueueSize(queueSize);
            threadPool.setScheduledSize(scheduledSize);
            return threadPool;
        }
    }
}
