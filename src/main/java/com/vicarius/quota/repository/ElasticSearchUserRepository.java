package com.vicarius.quota.repository;

import com.vicarius.quota.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository("elasticSearchUserRepository")
public class ElasticSearchUserRepository implements GenericRepository<User, Long> {

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

    public long generateId() {
        UUID uuid = UUID.randomUUID();
        return Math.abs(uuid.getMostSignificantBits());
    }

}
