package com.vicarius.quota.service;

import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.UserRepository;
import com.vicarius.quota.repository.factory.RepositoryFactory;
import com.vicarius.quota.repository.mysql.DatabaseUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final RepositoryFactory<User, Long> repositoryFactory;

    public UserService(RepositoryFactory<User, Long> genericRepository, DatabaseUserRepository databaseUserRepository) {
        this.repositoryFactory = genericRepository;
    }

    public User createUser(UserDto createUserRequest) {
        UserRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);

        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());

        return userRepository.save(user);
    }

    public User getUser(Long userId) throws ResourceNotFoundException {
        UserRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);
        List<User> users = userRepository.findByLastName("Martin");
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    }

    public void updateUser(Long userId, UserDto user) throws ResourceNotFoundException {
        UserRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);

        User existingUser = getUser(userId);

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        userRepository.save(existingUser);
    }

    public void updateUser(User user) {
        UserRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) throws ResourceNotFoundException {
        UserRepository<User, Long> userRepository = repositoryFactory.getRepository(User.class);
        User existingUser = getUser(userId);
        userRepository.deleteById(existingUser.getId());
    }
}
