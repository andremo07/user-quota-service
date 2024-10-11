package com.vicarius.quota.tests.service;

import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.factory.RepositoryFactory;
import com.vicarius.quota.repository.mysql.DatabaseUserRepository;
import com.vicarius.quota.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private RepositoryFactory<User, Long> repositoryFactory;

    @Mock
    private DatabaseUserRepository mySqlUserRepository;

    @Test
    void givenAnUserWhenCreateUserThenSaveUser() {
        UserDto userDto = createUserDto("Johny", "Bravo");

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);

        userService.createUser(userDto);

        verify(mySqlUserRepository, times(1)).save(
                argThat(argument -> {
                    assertThat(argument.getId()).isNull();
                    assertThat(argument.getFirstName()).isEqualTo(userDto.getFirstName());
                    assertThat(argument.getLastName()).isEqualTo(userDto.getLastName());
                    assertThat(argument.isLocked()).isFalse();
                    assertThat(argument.getLastLoginTimeUtc()).isNull();
                    return true;
                })
        );
    }

    @Test
    void givenAnIdOfAExistentUserWhenGetUserThenReturnUser() throws ResourceNotFoundException {
        Long userId = 1L;
        User experctedUser = createUser();

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);
        when(mySqlUserRepository.findById(userId)).thenReturn(Optional.of(experctedUser));

        User user = userService.getUser(userId);

        assertThat(user).isNotNull();
        assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    void givenAnIdOfANonExistentUserWhenGetUserThenThrowsResourceNotFoundException() {
        Long userId = 1L;

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);
        when(mySqlUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void givenAnIdOfAExistentUserAndUserWhenUpdateUserThenUpdateUser() throws ResourceNotFoundException {
        Long userId = 1L;
        UserDto userToUpdateDto = createUserDto("Johny", "Feliz");;
        User existentUser = createUser();

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);
        when(mySqlUserRepository.findById(userId)).thenReturn(Optional.of(existentUser));

        userService.updateUser(userId, userToUpdateDto);

        verify(mySqlUserRepository, times(1)).save(
                argThat(argument -> {
                    assertThat(argument.getId()).isEqualTo(userId);
                    assertThat(argument.getFirstName()).isEqualTo(existentUser.getFirstName());
                    assertThat(argument.getLastName()).isEqualTo(existentUser.getLastName());
                    return true;
                })
        );
    }

    @Test
    void givenAnExistentUserWhenUpdateUserThenUpdateUser() {
        User existentUser = createUser();

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);

        userService.updateUser(existentUser);

        verify(mySqlUserRepository, times(1)).save(
                argThat(argument -> {
                    assertThat(argument.getId()).isEqualTo(existentUser.getId());
                    assertThat(argument.getFirstName()).isEqualTo(existentUser.getFirstName());
                    assertThat(argument.getLastName()).isEqualTo(existentUser.getLastName());
                    assertThat(argument.isLocked()).isEqualTo(existentUser.isLocked());
                    assertThat(argument.getLastLoginTimeUtc()).isEqualTo(existentUser.getLastLoginTimeUtc());
                    return true;
                })
        );
    }

    @Test
    void givenAnIdOfANonExistentUserAndUserWhenUpdateUserThenThrowsResourceNotFoundException() {
        Long userId = 1L;
        UserDto userToUpdateDto = createUserDto("Johny", "Feliz");;

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);
        when(mySqlUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, userToUpdateDto));
        verify(mySqlUserRepository, times(0)).save(any());
    }

    @Test
    void givenAnIdOfAExistentUserWhenDeleteUserThenDeleteUser() throws ResourceNotFoundException {
        Long userId = 1L;
        User existentUser = createUser();

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);
        when(mySqlUserRepository.findById(userId)).thenReturn(Optional.of(existentUser));

        userService.deleteUser(userId);

        verify(mySqlUserRepository, times(1)).deleteById(
                argThat(argument -> {
                    assertThat(argument).isEqualTo(userId);
                    return true;
                })
        );
    }

    @Test
    void givenAnIdOfANonExistentUserWhenDeleteUserThenThrowsResourceNotFoundException() {
        Long userId = 1L;

        when(repositoryFactory.getRepository(any())).thenReturn(mySqlUserRepository);
        when(mySqlUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
        verify(mySqlUserRepository, times(0)).deleteById(anyLong());
    }

    UserDto createUserDto(String firstName, String lastName) {
        var user = new UserDto();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }

    User createUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Johny");
        user.setLastName("Bravo");

        return user;
    }

}
