package com.vicarius.quota.service;

import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserQuotaService {

    private final UserService userService;
    private final ConcurrentHashMap<Long, UserQuota> userQuotaMap; // In-memory storage (simulates cache)
    private final int maxRequests;

    public UserQuotaService(UserService userService,
                            ConcurrentHashMap<Long, UserQuota> userQuotaMap,
                            @Value("${user.max.request}") Integer maxRequests) {
        this.userService = userService;
        this.userQuotaMap = userQuotaMap;
        this.maxRequests = maxRequests;
    }

    public void consumeQuota(Long userId) throws ResourceNotFoundException {
        User user = userService.getUser(userId);
        updateUserQuota(user);
        userService.updateUser(userId, user);
    }

    public List<UserQuota> getUsersQuota() {
        return new ArrayList<>(userQuotaMap.values());
    }

    private void updateUserQuota(User user) {
        Long userId = user.getId();

        UserQuota userQuota = userQuotaMap.getOrDefault(userId,
                new UserQuota(user, 0));
        userQuota.incrementRequestNumber();

        user.setLastLoginTimeUtc(LocalDateTime.now(ZoneId.of("UTC")));
        if (userQuota.getRequestNumber() >= maxRequests) {
            user.setLocked(true);
        }

        userQuota.setUser(user);
        userQuotaMap.put(userId, userQuota);
    }

    public Optional<UserQuota> getUserQuota(Long userId) {
        return Optional.ofNullable(userQuotaMap.get(userId));
    }
}
