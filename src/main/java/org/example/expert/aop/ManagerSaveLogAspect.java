package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ManagerSaveLogAspect {

    private final LogService logService;

    @After("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..)) && args(authUser, todoId, request)")
    public void logAfterSaveManager(JoinPoint joinPoint, AuthUser authUser, long todoId, ManagerSaveRequest request) {

        try {
            logService.saveLog(authUser.getId(), todoId, request.getManagerUserId());
            log.info("Manager Save Log - Auth User ID: {}, Todo Id: {}, Manager Id: {}, Method: {}",
                    authUser.getId(), todoId, request.getManagerUserId(), joinPoint.getSignature().getName());
        } catch (Exception e) {
            log.error("Failed to save manager log", e);
        }
    }
}
