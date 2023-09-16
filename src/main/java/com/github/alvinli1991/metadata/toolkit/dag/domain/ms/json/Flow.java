package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Date: 2023/9/11
 * Time: 7:42 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Flow {
    @JsonIgnore
    private Action fromAction;
    @JsonIgnore
    private Action toAction;

    private String from;

    private String to;

    public Flow() {
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    private Action getFromAction() {
        return fromAction;
    }

    private void setFromAction(Action fromAction) {
        this.fromAction = fromAction;
    }

    private Action getToAction() {
        return toAction;
    }

    private void setToAction(Action toAction) {
        this.toAction = toAction;
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getFrom() {
        if (StringUtils.isNotBlank(this.from)) {
            return this.from;
        }
        if (Objects.nonNull(fromAction)) {
            return fromAction.getId();
        }
        return StringUtils.EMPTY;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getTo() {
        if (StringUtils.isNotBlank(this.to)) {
            return this.to;
        }
        if (Objects.nonNull(toAction)) {
            return toAction.getId();
        }
        return StringUtils.EMPTY;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flow flow = (Flow) o;
        return Objects.equals(getFrom(), flow.getFrom()) && Objects.equals(getTo(), flow.getTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo());
    }

    @Override
    public String toString() {
        return "Flow{" +
                "from='" + getFrom() + '\'' +
                ", to='" + getTo() + '\'' +
                '}';
    }

    public static final class Builder {
        private Action fromAction;
        private Action toAction;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder fromAction(Action fromAction) {
            this.fromAction = fromAction;
            return this;
        }

        public Builder toAction(Action toAction) {
            this.toAction = toAction;
            return this;
        }

        public Flow build() {
            Flow flow = new Flow();
            flow.toAction = this.toAction;
            flow.fromAction = this.fromAction;
            return flow;
        }
    }
}
