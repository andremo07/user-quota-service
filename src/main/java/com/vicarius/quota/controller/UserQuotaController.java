package com.vicarius.quota.controller;

import com.vicarius.quota.dto.UserDto;
import com.vicarius.quota.exception.ResourceNotFoundException;
import com.vicarius.quota.interceptor.CheckUserQuota;
import com.vicarius.quota.model.User;
import com.vicarius.quota.service.UserQuotaService;
import com.vicarius.quota.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserQuotaController {

    private final UserService userService;
    private final UserQuotaService userQuotaService;

    public UserQuotaController(UserService userService, UserQuotaService userQuotaService) {
        this.userService = userService;
        this.userQuotaService = userQuotaService;
    }

    @ResponseBody
    @GetMapping(value = "/{userId}")
    public ResponseEntity<?> get(@PathVariable("userId") Long userId) {
        try {
            return userService.getUser(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDto createUserRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userService.createUser(createUserRequest));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseBody
    @PutMapping(value = "/{userId}")
    public ResponseEntity<?> update(@PathVariable("userId") Long userId,
                                    @RequestBody User updatedUser) {
        try {
            userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseBody
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<?> delete(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseBody
    @GetMapping(value = "/{userId}/quota")
    @CheckUserQuota
    public ResponseEntity<?> consumeQuota(@PathVariable("userId") Long userId) {
        try {
            userQuotaService.consumeQuota(userId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseBody
    @GetMapping(value = "/quota")
    public ResponseEntity<?> getUsersQuota() {
        try {
            return ResponseEntity.ok(userQuotaService.getUsersQuota());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
