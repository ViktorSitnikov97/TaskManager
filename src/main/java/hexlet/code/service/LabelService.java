package hexlet.code.service;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper mapper;

    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll();

        return labels.stream()
                .map(mapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id = " + id + " not found!"));

        return mapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO data) {
        var label = mapper.map(data);
        labelRepository.save(label);

        return mapper.map(label);
    }

    public LabelDTO update(LabelUpdateDTO data, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id = " + id + " not found!"));
        mapper.update(data, label);
        labelRepository.save(label);

        return mapper.map(label);
    }

    public void destroy(Long id) {
        labelRepository.deleteById(id);
    }
}
