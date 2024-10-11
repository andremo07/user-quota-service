package com.vicarius.quota.service;

import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserQuotaService {

    private final UserService userService;
    private final ConcurrentMap<Long, UserQuota> userQuotaMap; // In-memory storage (simulates cache)
    private final int maxRequests;

    public UserQuotaService(UserService userService,
                            ConcurrentMap<Long, UserQuota> userQuotaMap,
                            @Value("${user.max.request}") Integer maxRequests) {
        this.userService = userService;
        this.userQuotaMap = userQuotaMap;
        this.maxRequests = maxRequests;
    }

    public void incrementUserRequests(Long userId) throws ResourceNotFoundException {
        User user = userService.getUser(userId);
        UserQuota userQuota = userQuotaMap.getOrDefault(userId, new UserQuota(user, 0));
        userQuota.incrementRequestNumber();
        userQuotaMap.put(userId, userQuota);

        if (userQuota.getRequestNumber() >= maxRequests) {
            user.setLocked(true);
            userService.updateUser(user);
        }
    }

    public boolean isUserBlocked(Long userId) throws ResourceNotFoundException {
        UserQuota userQuota = userQuotaMap.
                getOrDefault(userId, new UserQuota(userService.getUser(userId), 0));
        return userQuota.getRequestNumber() >= maxRequests;
    }

    public List<UserQuota> getUsersQuota() {
        return new ArrayList<>(userQuotaMap.values());
    }

    public Optional<UserQuota> getUserQuota(Long userId) {
        return Optional.ofNullable(userQuotaMap.get(userId));
    }
}
