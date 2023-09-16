package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Date: 2023/9/11
 * Time: 8:23 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StageDepend {

    @JsonIgnore
    private Stage dependStage;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    public StageDepend() {
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getId() {
        if (StringUtils.isNotBlank(id)) {
            return id;
        }
        if (Objects.nonNull(dependStage)) {
            return dependStage.getId();
        }
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stage getDependStage() {
        return dependStage;
    }

    private void setDependStage(Stage dependStage) {
        this.dependStage = dependStage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StageDepend that = (StageDepend) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "StageDepend{" +
                "id='" + getId() + '\'' +
                '}';
    }

    public static final class Builder {
        private Stage dependStage;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder dependStage(Stage dependStage) {
            this.dependStage = dependStage;
            return this;
        }

        public StageDepend build() {
            StageDepend stageDependS = new StageDepend();
            stageDependS.setDependStage(dependStage);
            return stageDependS;
        }
    }
}
