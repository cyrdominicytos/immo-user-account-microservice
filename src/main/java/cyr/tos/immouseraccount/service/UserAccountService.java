package cyr.tos.immouseraccount.service;

import cyr.tos.immouseraccount.config.AuthDetails;
import cyr.tos.immouseraccount.config.DefaultUserAccountMapper;
import cyr.tos.immouseraccount.config.UserAccountMapper;
import cyr.tos.immouseraccount.dao.UserAccountRepository;
import cyr.tos.immouseraccount.dto.DefaultUserAccountDto;
import cyr.tos.immouseraccount.dto.UserAccountDto;
import cyr.tos.immouseraccount.model.UserAccount;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper = UserAccountMapper.INSTANCE;
    private final DefaultUserAccountMapper defaultUserAccountMapper = DefaultUserAccountMapper.INSTANCE;
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public List<UserAccountDto> getAllUserAccount(){
        List<UserAccount> userAccounts = userAccountRepository.findAll();
        return userAccounts.stream().map(userAccountMapper::toDto).toList();
    }

    public UserAccountDto getUserAccountById(Long id){
        UserAccount userAccount = userAccountRepository.findById(id).orElse(null);
        return userAccountMapper.toDto(userAccount);
    }

    public UserAccountDto createUserAccount(DefaultUserAccountDto defaultUserAccountDto){
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(defaultUserAccountDto.getUserId());
        userAccountRepository.save(userAccount);
        return userAccountMapper.toDto(userAccount);
    }

    public UserAccountDto createUserAccountOld(UserAccountDto userAccountDto){
        UserAccount userAccount = userAccountMapper.toEntity(userAccountDto);
        Long userId = AuthDetails.getAuthUserId();
        userAccount.setUserId(userId);
        userAccountRepository.save(userAccount);
        return userAccountMapper.toDto(userAccount);
    }

    public UserAccountDto updateUserAccount(Long id, UserAccountDto userAccountDto){
        UserAccount userAccount = userAccountRepository.findById(id).orElse(null);
        if(userAccount == null){
            throw  new RuntimeException("User not found");
        }
        userAccount.setFirstName(userAccountDto.getFirstName());
        userAccount.setLastName(userAccountDto.getLastName());
        return userAccountMapper.toDto(userAccountRepository.save(userAccount));

    }

}
