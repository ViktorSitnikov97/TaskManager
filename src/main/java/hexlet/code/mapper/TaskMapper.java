package hexlet.code.mapper;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "name", source = "title")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getTaskStatusByStatusSlug")
    @Mapping(target = "labels", source = "taskLabelsIds", qualifiedByName = "getLabelsBySetIds")
    public abstract Task map(TaskCreateDTO data);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "taskLabelsIds", source = "labels", qualifiedByName = "getIdsBySetLabels")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getTaskStatusByStatusSlug")
    @Mapping(target = "labels", source = "taskLabelsIds", qualifiedByName = "getLabelsBySetIds")
    public abstract void update(TaskUpdateDTO data, @MappingTarget Task model);


    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "taskLabelsIds", source = "labels", qualifiedByName = "getIdsBySetLabels")
    public abstract TaskCreateDTO mapToCreateDTO(Task model);


    @Named("getTaskStatusByStatusSlug") // slug статуса однозначно определяет TaskStatus(status)
    public TaskStatus getTaskStatusByStatusSlug(String status) {
        return taskStatusRepository.findBySlug(status)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task_Status with slug = " + status + " not found")
                );
    }

    @Named("getLabelsBySetIds")
    public Set<Label> getLabelsBSetIds(Set<Long> ids) {
        return ids == null
                ?
                new HashSet<>()
                :
                labelRepository.findByIdIn(ids);
    }

    @Named("getIdsBySetLabels")
    public Set<Long> getIdsBySetLabels(Set<Label> labels) {
        return labels == null
                ?
                new HashSet<>()
                :
                labels.stream()
                        .map(Label::getId)
                        .collect(Collectors.toSet());
    }

}
