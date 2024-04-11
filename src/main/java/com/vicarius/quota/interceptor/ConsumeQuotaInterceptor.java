package com.vicarius.quota.interceptor;

import com.vicarius.quota.model.User;
import com.vicarius.quota.model.UserQuota;
import com.vicarius.quota.service.UserQuotaService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class ConsumeQuotaInterceptor implements HandlerInterceptor {

    private final static String ROOT_PATH_NAME = "users";
    private final UserQuotaService userQuotaService;

    public ConsumeQuotaInterceptor(UserQuotaService userQuotaService) {
        this.userQuotaService = userQuotaService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();

            if (method.isAnnotationPresent(CheckUserQuota.class)) {
                Long userId = getUserIdFromRequest(request);

                if (userId != null && isUserBlocked(userId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "User with ID " + userId + " not found");
                    return false;
                }
            }
        }

        return true;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String[] pathSegments = path.split("/");

        for (int i = 0; i < pathSegments.length; i++) {
            if (pathSegments[i].equals(ROOT_PATH_NAME)) {
                try {
                    return Long.parseLong(pathSegments[i + 1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isUserBlocked(Long userId) {
        return userQuotaService.getUserQuota(userId)
                .map(UserQuota::getUser)
                .map(User::isLocked)
                .orElse(false);
    }
}


