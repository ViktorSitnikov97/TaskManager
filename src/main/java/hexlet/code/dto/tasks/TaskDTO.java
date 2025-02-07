package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskDTO {
    private long id;
    private int index;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @JsonProperty("assignee_id")
    private long assigneeId;

    private String title;
    private String content;
    private String status;
}
