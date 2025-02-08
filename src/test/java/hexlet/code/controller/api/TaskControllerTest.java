package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    private User testUser;
    private TaskStatus testTaskStatus;
    private Task testTask;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();

        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
        Set<Label> labels = new HashSet<>();
        labels.add(testLabel);
        testTask.setLabels(labels);

        taskRepository.save(testTask);
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/{id}", testTask.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("taskLabelsIds").isEqualTo(
                        testTask.getLabels().stream().map(Label::getId).collect(Collectors.toSet())
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatusNew = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(taskStatusNew);

        var labelNew = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(labelNew);
        Set<Label> labels = new HashSet<>();
        labels.add(labelNew);
        Set<Long> taskLabelsIds = labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());

        var dto = mapper.mapToCreateDTO(testTask);
        dto.setStatus(taskStatusNew.getSlug());
        dto.setTaskLabelsIds(JsonNullable.of(taskLabelsIds));
        dto.setTitle("newTask");

        var request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByNameAndIndex(dto.getTitle(), dto.getIndex()).orElse(null);

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(dto.getTitle());
        assertThat(task.getIndex()).isEqualTo(dto.getIndex());
        assertThat(task.getDescription()).isEqualTo(dto.getContent());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(dto.getStatus());
        assertThat(
                task.getLabels().stream()
                        .map(Label::getId)
                        .collect(Collectors.toSet())
        ).isEqualTo(dto.getTaskLabelsIds().get());
    }

    @Test
    public void testCreateWithNotValidName() throws Exception {
        var dto = mapper.mapToCreateDTO(testTask);
        dto.setTitle("");

        var request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var newTestUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(newTestUser);

        var newTestTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(newTestTaskStatus);

        var labelNew = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(labelNew);
        Set<Label> labels = new HashSet<>();
        labels.add(labelNew);
        Set<Long> taskLabelsIds = labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());

        var dto = new TaskUpdateDTO();
        dto.setTitle(JsonNullable.of("Tasks Title"));
        dto.setContent(JsonNullable.of("Tasks Content"));
        dto.setAssigneeId(JsonNullable.of(newTestUser.getId()));
        dto.setStatus(newTestTaskStatus.getSlug());
        dto.setTaskLabelsIds(JsonNullable.of(taskLabelsIds));

        var request = put("/api/tasks/{id}", testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var taskAfterUpdate = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(taskAfterUpdate).isNotNull();
        assertThat(taskAfterUpdate.getName()).isEqualTo(dto.getTitle().get());
        assertThat(taskAfterUpdate.getDescription()).isEqualTo(dto.getContent().get());
        assertThat(taskAfterUpdate.getAssignee().getId()).isEqualTo(dto.getAssigneeId().get());
        assertThat(taskAfterUpdate.getTaskStatus().getSlug()).isEqualTo(dto.getStatus());
        assertThat(
                taskAfterUpdate.getLabels().stream()
                        .map(Label::getId)
                        .collect(Collectors.toSet())
        ).isEqualTo(dto.getTaskLabelsIds().get());
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var dto = new TaskUpdateDTO();
        dto.setTitle(JsonNullable.of("Only new Title"));

        var request = put("/api/tasks/{id}", testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var taskAfterPartialUpdate = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(taskAfterPartialUpdate).isNotNull();
        assertThat(taskAfterPartialUpdate.getName()).isEqualTo(dto.getTitle().get());
        assertThat(taskAfterPartialUpdate.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(taskAfterPartialUpdate.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(taskAfterPartialUpdate.getTaskStatus().getSlug()).isEqualTo(testTask.getTaskStatus().getSlug());
        assertThat(taskAfterPartialUpdate.getAssignee().getId()).isEqualTo(testTask.getAssignee().getId());
        assertThat(
                taskAfterPartialUpdate.getLabels().stream()
                        .map(Label::getId)
                        .collect(Collectors.toSet())
        ).isEqualTo(
                testTask.getLabels().stream()
                        .map(Label::getId)
                        .collect(Collectors.toSet()));
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/tasks/{id}", testTask.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }
}
