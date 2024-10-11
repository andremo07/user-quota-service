package com.vicarius.quota.service;

import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.GenericRepository;
import com.vicarius.quota.repository.factory.RepositoryFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final RepositoryFactory<User, Long> repositoryFactory;

    public UserService(RepositoryFactory<User, Long> genericRepository) {
        this.repositoryFactory = genericRepository;
    }

    public User createUser(UserDto createUserRequest) {
        GenericRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);

        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());

        return userRepository.save(user);
    }

    public User getUser(Long userId) throws ResourceNotFoundException {
        GenericRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    }

    public void updateUser(Long userId, UserDto user) throws ResourceNotFoundException {
        GenericRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);

        User existingUser = getUser(userId);

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        userRepository.save(existingUser);
    }

    public void updateUser(User user) {
        GenericRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) throws ResourceNotFoundException {
        GenericRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);
        User existingUser = getUser(userId);
        userRepository.deleteById(existingUser.getId());
    }
}
