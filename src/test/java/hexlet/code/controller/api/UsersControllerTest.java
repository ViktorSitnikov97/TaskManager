package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.users.UserCreateDTO;

import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/{id}", testUser.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = new UserCreateDTO();
        dto.setFirstName("first name");
        dto.setLastName("last name");
        dto.setPassword("asdfAndQwerty");
        dto.setEmail("qwerty@mail.com");

        var request = post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByFirstNameAndLastName(
                dto.getFirstName(), dto.getLastName()).orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    public void testCreateWithNotValidName() throws Exception {
        var dto = mapper.mapToCreateDTO(testUser);
        dto.setLastName("");

        var request = post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = mapper.mapToCreateDTO(testUser);
        dto.setFirstName("new name");
        dto.setLastName("new last name");
        dto.setPassword("qwerty");
        dto.setEmail("new@mail.com");

        var request = put("/api/users/{id}", testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userAfterUpdate = userRepository.findById(testUser.getId()).orElse(null);

        assertThat(userAfterUpdate).isNotNull();
        assertThat(userAfterUpdate.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(userAfterUpdate.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var dto = mapper.mapToCreateDTO(testUser);
        dto.setFirstName("new name");
        dto.setLastName("new last name");
        dto.setEmail(null);
        dto.setPassword(null);

        var request = put("/api/users/{id}", testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userAfterPartialUpdate = userRepository.findById(testUser.getId()).orElse(null);

        assertThat(userAfterPartialUpdate).isNotNull();
        assertThat(userAfterPartialUpdate.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userAfterPartialUpdate.getFirstName()).isEqualTo(dto.getFirstName());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/users/{id}", testUser.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }

    @Test
    public void testUpdateAnotherUser() throws Exception {
        User secondUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(secondUser);
        var dto = mapper.mapToCreateDTO(secondUser);

        var request = put("/api/users/{id}", secondUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDestroyAnotherUser() throws Exception {
        User secondUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(secondUser);

        var request = delete("/api/users/{id}", secondUser.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        assertThat(userRepository.existsById(secondUser.getId())).isTrue();
    }
}
