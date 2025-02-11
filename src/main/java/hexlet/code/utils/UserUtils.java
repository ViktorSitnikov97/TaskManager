package hexlet.code.utils;

import hexlet.code.exception.UserAccessDeniedException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserUtils {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Not Authorised"));
    }

    public boolean isAuthor(long id) {
        var user = Optional.ofNullable(getCurrentUser()).orElse(new User());
        if (!Objects.equals(user.getId(), id)) {
            throw new UserAccessDeniedException("Access denied");
        }
        return Objects.equals(user.getId(), id);
    }
}
