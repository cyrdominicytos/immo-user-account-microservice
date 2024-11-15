package cyr.tos.immouseraccount.config;

import cyr.tos.immouseraccount.dto.UserAccountDto;
import cyr.tos.immouseraccount.model.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAccountMapper {
    UserAccountMapper INSTANCE = Mappers.getMapper(UserAccountMapper.class);
    UserAccountDto toDto(UserAccount userAccount);
    UserAccount toEntity(UserAccountDto userAccountDto);
}
