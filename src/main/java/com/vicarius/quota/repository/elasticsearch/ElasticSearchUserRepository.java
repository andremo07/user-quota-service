package com.vicarius.quota.repository.elasticsearch;

import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.ElasticSearchRepository;
import com.vicarius.quota.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository("elasticSearchUserRepository")
public class ElasticSearchUserRepository implements UserRepository<User, Long>, ElasticSearchRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public List<User> findByLastName(String lastName) {
        return users.values()
                .stream()
                .filter(user -> lastName.equals(user.getLastName()))
                .collect(Collectors.toList());
    }

    public long generateId() {
        UUID uuid = UUID.randomUUID();
        return Math.abs(uuid.getMostSignificantBits());
    }

}
