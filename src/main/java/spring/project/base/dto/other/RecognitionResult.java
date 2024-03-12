package spring.project.base.dto.other;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
public class RecognitionResult {

    @JsonProperty("result")
    private List<ResultEntry> result;
}
