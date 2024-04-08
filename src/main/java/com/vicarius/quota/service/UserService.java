package com.vicarius.quota.service;

import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.UserRepository;
import com.vicarius.quota.repository.factory.UserRepositoryFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepositoryFactory userRepositoryFactory;

    public UserService(UserRepositoryFactory userRepositoryFactory) {
        this.userRepositoryFactory = userRepositoryFactory;
    }

    public User createUser(UserDto createUserRequest) {
        UserRepository userRepository = userRepositoryFactory.getUserRepository();

        User user = new User();
        user.setId(UUID.randomUUID().getLeastSignificantBits());
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());

        return userRepository.save(user);
    }

    public Optional<User> getUser(Long userId) {
        UserRepository userRepository = userRepositoryFactory.getUserRepository();

        return userRepository.findById(userId);
    }

    public void updateUser(Long userId, User updatedUser) throws ResourceNotFoundException {
        UserRepository userRepository = userRepositoryFactory.getUserRepository();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setLocked(updatedUser.isLocked());

        userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) throws ResourceNotFoundException {
        UserRepository userRepository = userRepositoryFactory.getUserRepository();

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        userRepository.deleteById(userId);
    }

}
