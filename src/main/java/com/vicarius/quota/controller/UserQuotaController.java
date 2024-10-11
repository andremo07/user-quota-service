package com.vicarius.quota.controller;

import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.interceptor.CheckUserQuota;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import com.vicarius.quota.service.UserQuotaService;
import com.vicarius.quota.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserQuotaController {

    private final UserService userService;
    private final UserQuotaService userQuotaService;

    public UserQuotaController(UserService userService, UserQuotaService userQuotaService) {
        this.userService = userService;
        this.userQuotaService = userQuotaService;
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<User> get(@PathVariable("userId") Long userId) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserDto createUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(createUserRequest));
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Void> update(@PathVariable("userId") Long userId, @RequestBody UserDto user)
            throws ResourceNotFoundException {
        userService.updateUser(userId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable("userId") Long userId) throws ResourceNotFoundException {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{userId}/quota")
    @CheckUserQuota
    public ResponseEntity<Void> consumeQuota(@PathVariable("userId") Long userId) throws ResourceNotFoundException {
        userQuotaService.incrementUserRequests(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/quota")
    public ResponseEntity<List<UserQuota>> getUsersQuota() {
        return ResponseEntity.ok(userQuotaService.getUsersQuota());
    }
}
