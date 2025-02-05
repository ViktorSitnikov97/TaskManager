package hexlet.code.service;

import hexlet.code.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatuses.TaskStatusDTO;
import hexlet.code.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper mapper;

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task_Status with id = " + id + " not found"));

        return mapper.map(taskStatus);
    }

    public List<TaskStatusDTO> getAll() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(mapper::map)
                .toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var taskStatus = mapper.map(data);
        taskStatusRepository.save(taskStatus);

        return mapper.map(taskStatus);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task_Status with id = " + id + " not found"));
        mapper.update(data, taskStatus);
        taskStatusRepository.save(taskStatus);

        return mapper.map(taskStatus);
    }

    public void destroy(long id) {
        taskStatusRepository.deleteById(id);
    }
}
