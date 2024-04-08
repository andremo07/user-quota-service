package com.vicarius.quota.tests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.exception.UserBlockedException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import com.vicarius.quota.service.UserQuotaService;
import com.vicarius.quota.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserQuotaControllerTest {

    private final String API_RESOURCE = "/users";

    private ObjectMapper mapper;

    @MockBean
    UserQuotaService userQuotaService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mapper = new ObjectMapper();
    }

    @Test
    void whenGetUserRequestIsFound_thenReturnOkStatus() throws Exception {
        var expectedUser = createUser();

        when(userService.getUser(anyLong())).thenReturn(Optional.of(expectedUser));

        var response = mockMvc.perform(get(String.format("%s/%s", API_RESOURCE, expectedUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var userResponse = mapper.readValue(response.getResponse().getContentAsString(), User.class);

        assertNotNull(userResponse);
        assertEquals(expectedUser.getId(), userResponse.getId());
        assertEquals(expectedUser.getFirstName(), userResponse.getFirstName());
        assertEquals(expectedUser.getLastName(), userResponse.getLastName());
    }

    @Test
    void whenGetUserRequestIsNotFound_thenReturnNotFoundStatus() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("%s/%s", API_RESOURCE, 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetUserRequestThrowException_thenReturnInternalServerErrorStatus() throws Exception {
        when(userService.getUser(anyLong())).thenThrow(RuntimeException.class);

        mockMvc.perform(get(String.format("%s/%s", API_RESOURCE, 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenValidCreateUserRequest_thenReturnCreatedStatus() throws Exception {
        UserDto createUserRequest = createUserRequest();

        when(userService.createUser(any())).thenReturn(createUser());

        var response = mockMvc.perform(post(API_RESOURCE)
                        .content(mapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        var userResponse = mapper.readValue(response.getResponse().getContentAsString(), User.class);

        assertNotNull(userResponse);
        assertEquals(createUserRequest.getFirstName(), userResponse.getFirstName());
        assertEquals(createUserRequest.getLastName(), userResponse.getLastName());
        assertFalse(userResponse.isLocked());
        assertNull(userResponse.getLastLoginTimeUtc());
    }

    @Test
    void whenValidCreateUserRequestAndThrowsException_thenReturnInternalServerErrorStatus() throws Exception {
        UserDto createUserRequest = createUserRequest();

        when(userService.createUser(any())).thenThrow(RuntimeException.class);

        mockMvc.perform(post(API_RESOURCE)
                        .content(mapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenUpdateUserRequestIsFound_thenReturnOkStatus() throws Exception {
        var user = createUser();

        doNothing().when(userService).updateUser(anyLong(), any());

        mockMvc.perform(put(String.format("%s/%s", API_RESOURCE, user.getId()))
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenUpdateUserRequestIsNotFound_thenReturnNotFoundStatus() throws Exception {
        var user = createUser();

        doThrow(ResourceNotFoundException.class).when(userService).updateUser(anyLong(), any());

        mockMvc.perform(put(String.format("%s/%s", API_RESOURCE, user.getId()))
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateUserRequestThrowException_thenReturnInternalServerError() throws Exception {
        var user = createUser();

        doThrow(RuntimeException.class).when(userService).updateUser(anyLong(), any());

        mockMvc.perform(put(String.format("%s/%s", API_RESOURCE, user.getId()))
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenDeleteUserRequestIsFound_thenReturnNoContentStatus() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(String.format("%s/%s", API_RESOURCE, 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteUserRequestIsNotFound_thenReturnNotFoundStatus() throws Exception {
        doThrow(ResourceNotFoundException.class).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(String.format("%s/%s", API_RESOURCE, 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteUserRequestThrowException_thenReturnInternalServerErrorStatus() throws Exception {
        doThrow(RuntimeException.class).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(String.format("%s/%s", API_RESOURCE, 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenConsumeQuotaIsSuccess_thenReturnNoContentStatus() throws Exception {
        doNothing().when(userQuotaService).consumeQuota(anyLong());

        mockMvc.perform(get(String.format("%s/%s/%s", API_RESOURCE, 1L, "quota"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenConsumeQuotaWithUserNotFound_thenReturnNotFoundStatus() throws Exception {
        doThrow(ResourceNotFoundException.class).when(userQuotaService).consumeQuota(anyLong());

        mockMvc.perform(get(String.format("%s/%s/%s", API_RESOURCE, 1L, "quota"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenConsumeQuotaThrowUserBlockedException_thenReturnForbiddenStatus() throws Exception {
        doThrow(UserBlockedException.class).when(userQuotaService).consumeQuota(anyLong());

        mockMvc.perform(get(String.format("%s/%s/%s", API_RESOURCE, 1L, "quota"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenConsumeQuotaThrowException_thenReturnInternalServerErrorStatus() throws Exception {
        doThrow(RuntimeException.class).when(userQuotaService).consumeQuota(anyLong());

        mockMvc.perform(get(String.format("%s/%s/%s", API_RESOURCE, 1L, "quota"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenGetAllUserQuotas_thenReturnOkStatus() throws Exception {
        when(userQuotaService.getUsersQuota()).thenReturn(createUserQuotaList());

        var response = mockMvc.perform(get(String.format("%s/%s", API_RESOURCE, "quota"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var userQuotaResponse = mapper.readValue(response.getResponse().getContentAsString(), List.class);
        assertNotNull(userQuotaResponse);
        assertNotNull(userQuotaResponse.get(0));
    }

    @Test
    void whenGetAllUserQuotas_thenReturnInternalServerErrorStatus() throws Exception {
        when(userQuotaService.getUsersQuota()).thenThrow(RuntimeException.class);

        mockMvc.perform(get(String.format("%s/%s", API_RESOURCE, "quota"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    UserDto createUserRequest() {
        var createUserRequest = new UserDto();
        createUserRequest.setFirstName("John");
        createUserRequest.setLastName("Doe");

        return createUserRequest;
    }

    User createUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        return user;
    }

    List<UserQuota> createUserQuotaList() {
        var user = createUser();
        var userQuota = new UserQuota(user, 1);

        return List.of(userQuota);
    }

}