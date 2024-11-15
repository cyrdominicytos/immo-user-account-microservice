package cyr.tos.immouseraccount.config;

import cyr.tos.immouseraccount.dto.DefaultUserAccountDto;
import cyr.tos.immouseraccount.dto.UserAccountDto;
import cyr.tos.immouseraccount.model.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DefaultUserAccountMapper {

    DefaultUserAccountMapper  INSTANCE = Mappers.getMapper(DefaultUserAccountMapper.class);
    DefaultUserAccountDto toDto(DefaultUserAccountDto defaultUserAccount);
    UserAccount toEntity(DefaultUserAccountDto defaultUserAccountDto);
}
