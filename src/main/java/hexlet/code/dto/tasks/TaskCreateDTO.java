package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskCreateDTO {
    private JsonNullable<Integer> index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotBlank
    private String title;

    private JsonNullable<String> content;

    @NotBlank
    private String status;
}
