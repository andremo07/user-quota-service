package com.vicarius.quota.tests.service;

import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import com.vicarius.quota.service.UserQuotaService;
import com.vicarius.quota.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQuotaServiceTest {

    private UserQuotaService userQuotaService;

    @Mock
    private ConcurrentHashMap<Long, UserQuota> userQuotaMap;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userQuotaService = new UserQuotaService(userService, userQuotaMap, 5);
    }

    @Test
    void givenUserZeroRequests_whenConsumeQuota_thenConsume() throws Exception {
        Long userId = 1L;
        User user = createUser();

        UserQuota userQuota = new UserQuota(user, 0);

        when(userService.getUser(userId)).thenReturn(user);
        when(userQuotaMap.getOrDefault(any(), any())).thenReturn(userQuota);

        userQuotaService.incrementUserRequests(userId);

        verify(userService, times(1)).getUser(userId);
        verify(userQuotaMap, times(1)).getOrDefault(any(), any());
        verify(userQuotaMap, times(1)).put(any(), any());
        verify(userService, times(1)).updateUser(user);
        assertFalse(user.isLocked());
        assertEquals(userQuota.getRequestNumber(), 1);
    }

    @Test
    void givenUserWithMaxNumberRequests_whenConsumeQuota_thenConsumeAndLockUser() throws Exception {
        Long userId = 1L;
        User user = createUser();

        UserQuota userQuota = new UserQuota(user, 4);

        when(userService.getUser(userId)).thenReturn(user);
        when(userQuotaMap.getOrDefault(any(), any())).thenReturn(userQuota);

        userQuotaService.incrementUserRequests(userId);

        verify(userService, times(1)).getUser(userId);
        verify(userQuotaMap, times(1)).getOrDefault(any(), any());
        verify(userQuotaMap, times(1)).put(any(), any());
        verify(userService, times(1)).updateUser(user);
        assertTrue(user.isLocked());
        assertEquals(userQuota.getRequestNumber(), 5);
    }

    @Test
    void givenNotFoundUser_whenConsumeQuota_thenThrowException() throws Exception {
        Long userId = 1L;

        when(userService.getUser(userId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userQuotaService.incrementUserRequests(userId));
    }

    @Test
    void givenUserIdWhenUserDoesNotHasMoreThanSupportedRequestsThenFalse() throws Exception {
        when(userQuotaMap.getOrDefault(anyLong(), any())).thenReturn(createUserQuota(1));

        assertFalse(userQuotaService.isUserBlocked(anyLong()));
    }

    @Test
    void givenUserIdWhenUserHasMoreThanSupportedRequestsThenTrue() throws Exception {
        when(userQuotaMap.getOrDefault(anyLong(), any())).thenReturn(createUserQuota(6));

        assertTrue(userQuotaService.isUserBlocked(anyLong()));
    }

    @Test
    void whenGetQuotasReturnAllQuotas() {
        when(userQuotaMap.values()).thenReturn(List.of(createUserQuota(1)));

        assertNotNull(userQuotaService.getUsersQuota());
    }

    User createUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Johny");
        user.setLastName("Bravo");

        return user;
    }

    UserQuota createUserQuota(Integer numberOfRequests) {
        return new UserQuota(createUser(), numberOfRequests);
    }
}
