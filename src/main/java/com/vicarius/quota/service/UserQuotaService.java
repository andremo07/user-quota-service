package com.vicarius.quota.service;

import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.exception.UserBlockedException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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

    public void consumeQuota(Long userId) throws ResourceNotFoundException, UserBlockedException {
        User user = userService.getUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        if (!user.isLocked()) {
            UserQuota userQuota = updateUserQuota(user);

            userService.updateUser(userId, user);
        } else {
            throw new UserBlockedException("User with ID " + userId + " is blocked");
        }
    }

    public List<UserQuota> getUsersQuota() {
        return new ArrayList<>(userQuotaMap.values());
    }

    private UserQuota updateUserQuota(User user) {
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
        return userQuota;
    }
}
