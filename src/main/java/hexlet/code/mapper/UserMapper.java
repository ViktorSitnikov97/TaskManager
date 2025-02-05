package hexlet.code.mapper;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE

)
public abstract class UserMapper {
    public abstract UserDTO map(User model);

    @Mapping(source = "password", target = "passwordDigest")
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    @Mapping(target = "password", source = "passwordDigest")
    public abstract UserCreateDTO mapToCreateDTO(User model);

    @Mapping(source = "password", target = "passwordDigest")
    public abstract User map(UserCreateDTO userDTO);
}
