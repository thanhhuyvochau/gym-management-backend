package spring.project.base.dto.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResultEntry {
    @JsonProperty("_label")
    private String label;

    @JsonProperty("_distance")
    private double distance;
}
