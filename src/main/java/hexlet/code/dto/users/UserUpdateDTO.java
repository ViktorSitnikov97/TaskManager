package hexlet.code.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserUpdateDTO {

    @NotBlank
    private JsonNullable<String> firstName;

    @NotBlank
    private JsonNullable<String> lastName;

    @Email
    private JsonNullable<String> email;

    @Pattern(regexp = "\\w{6,}")
    private JsonNullable<String> password;
}
