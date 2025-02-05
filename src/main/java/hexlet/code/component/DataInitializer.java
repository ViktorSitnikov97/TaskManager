package hexlet.code.component;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.instancio.internal.util.StringUtils;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        var userData = supplyAdmin();
        userRepository.save(userData);

        for (var taskStatus : supplyTaskStatuses()) {
            taskStatusRepository.save(taskStatus);
        }


    }

    private User supplyAdmin() {
        var email = "hexlet@example.com";
        var rawPassword = "qwerty";
        var user = new User();
        user.setEmail(email);
        user.setPasswordDigest(passwordEncoder.encode(rawPassword));
        return user;
    }
    private List<TaskStatus> supplyTaskStatuses() {
        var nameTaskStatuses =  List.of("draft", "to_review", "to_be_fixed", "to_publish", "published");

        return nameTaskStatuses.stream()
                .map(slug -> {
                    var name = Arrays.stream(slug.split("_"))
                            .map(StringUtils::capitalise)
                            .collect(Collectors.joining(""));
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setName(name);
                    taskStatus.setSlug(slug);
                    return taskStatus;
                })
                .collect(Collectors.toList());
    }


}
