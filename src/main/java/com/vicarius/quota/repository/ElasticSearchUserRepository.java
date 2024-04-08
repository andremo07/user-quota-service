package com.vicarius.quota.repository;

import com.vicarius.quota.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository("elasticSearchUserRepository")
public class ElasticSearchUserRepository implements UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    public ElasticSearchUserRepository() {
        //Loading users
/*        var user1 = new User(1L, "Andre", "Oliveira", null);
        var user2 = new User(2L, "Jane", "Smith", null);
        var user3 = new User(3L, "John", "Kayne", null);

        users.put(user1.getUserId(), user1);
        users.put(user2.getUserId(), user2);
        users.put(user3.getUserId(), user3);*/
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            long id = generateId();
            user.setId(id);
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

    public long generateId() {
        UUID uuid = UUID.randomUUID();
        return Math.abs(uuid.getMostSignificantBits());
    }

}
