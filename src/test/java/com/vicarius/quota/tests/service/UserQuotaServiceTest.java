package com.vicarius.quota.tests.service;

import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.exception.UserBlockedException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import com.vicarius.quota.service.UserQuotaService;
import com.vicarius.quota.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {"user.max.request=5"})
class UserQuotaServiceTest {

    @MockBean
    private ConcurrentHashMap<Long, UserQuota> userQuotaMap;

    @MockBean
    private UserService userService;

    @Autowired
    private UserQuotaService userQuotaService;


    @Test
    void givenUserZeroRequests_whenConsumeQuota_thenConsume() throws Exception {
        Long userId = 1L;
        User user = createUser();

        UserQuota userQuota = new UserQuota(user, 0);

        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(userQuotaMap.getOrDefault(any(), any())).thenReturn(userQuota);

        userQuotaService.consumeQuota(userId);

        verify(userService, times(1)).getUser(userId);
        verify(userQuotaMap, times(1)).getOrDefault(any(), any());
        verify(userQuotaMap, times(1)).put(any(), any());
        verify(userService, times(1)).updateUser(userId, user);
        assertFalse(user.isLocked());
        assertEquals(userQuota.getRequestNumber(), 1);
    }

    @Test
    void givenUserWithMaxNumberRequests_whenConsumeQuota_thenConsumeAndLockUser() throws Exception {
        Long userId = 1L;
        User user = createUser();

        UserQuota userQuota = new UserQuota(user, 4);

        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(userQuotaMap.getOrDefault(any(), any())).thenReturn(userQuota);

        userQuotaService.consumeQuota(userId);

        verify(userService, times(1)).getUser(userId);
        verify(userQuotaMap, times(1)).getOrDefault(any(), any());
        verify(userQuotaMap, times(1)).put(any(), any());
        verify(userService, times(1)).updateUser(userId, user);
        assertTrue(user.isLocked());
        assertEquals(userQuota.getRequestNumber(), 5);
    }

    @Test
    void givenLockedUser_whenConsumeQuota_thenThrowException() throws Exception {
        Long userId = 1L;
        User user = createUser();
        user.setLocked(true);

        when(userService.getUser(userId)).thenReturn(Optional.of(user));

        assertThrows(UserBlockedException.class, () -> userQuotaService.consumeQuota(userId));
    }

    @Test
    void givenNotFoundUser_whenConsumeQuota_thenThrowException() throws Exception {
        Long userId = 1L;

        when(userService.getUser(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userQuotaService.consumeQuota(userId));
    }

    User createUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Johny");
        user.setLastName("Bravo");

        return user;
    }

}
