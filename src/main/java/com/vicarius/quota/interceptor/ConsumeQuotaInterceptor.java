package com.vicarius.quota.interceptor;

import com.vicarius.quota.exception.UserBlockedException;
import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import com.vicarius.quota.service.UserQuotaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class ConsumeQuotaInterceptor implements HandlerInterceptor {

    private final UserQuotaService userQuotaService;

    public ConsumeQuotaInterceptor(UserQuotaService userQuotaService) {
        this.userQuotaService = userQuotaService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Long userId = extractUserIdFromRequest(request);

        if (userQuotaService.isUserBlocked(userId)) {
            throw new UserBlockedException("User has exceeded the maximum number of requests.");
        }

        userQuotaService.incrementUserRequests(userId);
        return true;
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String[] pathSegments = path.split("/");

        if (pathSegments.length >= 3) {
            try {
                return Long.parseLong(pathSegments[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid userId in path");
            }
        }

        throw new IllegalArgumentException("Invalid path format, expected /users/{userId}/quota");
    }
}


