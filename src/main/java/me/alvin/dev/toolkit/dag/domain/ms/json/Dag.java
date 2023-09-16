package me.alvin.dev.toolkit.dag.domain.ms.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Date: 2023/9/11
 * Time: 7:37 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "DagGraph")
@JsonPropertyOrder({"config", "units", "stages"})
public class Dag {

    @JsonAlias(value = "xmlns:xsi")
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
    private String xmlns;

    @JsonAlias(value = "xsi:noNamespaceSchemaLocation")
    @JacksonXmlProperty(isAttribute = true, localName = "xsi:noNamespaceSchemaLocation")
    private String schemaLocation;

    private Config config;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlElementWrapper(localName = "units")
    @JacksonXmlProperty(localName = "unit")
    private List<Action> units;

    @JacksonXmlElementWrapper(localName = "stages")
    @JacksonXmlProperty(localName = "stage")
    private List<Stage> stages;

    public Dag() {
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Action> getUnits() {
        return units;
    }

    public void setUnits(List<Action> units) {
        this.units = units;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        return "Dag{" +
                "xmlns='" + xmlns + '\'' +
                ", schemaLocation='" + schemaLocation + '\'' +
                ", config=" + config +
                ", id='" + id + '\'' +
                ", units=" + units +
                ", stages=" + stages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dag dag = (Dag) o;
        return Objects.equals(getConfig(), dag.getConfig()) && Objects.equals(getId(), dag.getId()) && Objects.equals(getUnits(), dag.getUnits()) && Objects.equals(getStages(), dag.getStages());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConfig(), getId(), getUnits(), getStages());
    }

    public static final class Builder {
        private String xmlns;
        private String schemaLocation;
        private Config config;
        private String id;
        private List<Action> units;
        private List<Stage> stages;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder xmlns(String xmlns) {
            this.xmlns = xmlns;
            return this;
        }

        public Builder schemaLocation(String schemaLocation) {
            this.schemaLocation = schemaLocation;
            return this;
        }

        public Builder config(Config config) {
            this.config = config;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder units(List<Action> units) {
            this.units = units;
            return this;
        }

        public Builder unit(Action unit) {
            if (null == this.units) {
                this.units = new ArrayList<>();
            }
            this.units.add(unit);
            return this;
        }

        public Builder stages(List<Stage> stages) {
            this.stages = stages;
            return this;
        }

        public Builder stage(Stage stage) {
            if (null == this.stages) {
                this.stages = new ArrayList<>();
            }
            this.stages.add(stage);
            return this;
        }

        public Dag build() {
            Dag dag = new Dag();
            dag.setXmlns(xmlns);
            dag.setSchemaLocation(schemaLocation);
            dag.setConfig(config);
            dag.setId(id);
            dag.setUnits(units);
            dag.setStages(stages);
            return dag;
        }
    }
}
