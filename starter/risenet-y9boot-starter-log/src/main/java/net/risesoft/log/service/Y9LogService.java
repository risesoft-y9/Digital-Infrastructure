package net.risesoft.log.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInvocation;

import net.risesoft.model.AccessLog;

/**
 * @author DZJ
 * @date 2022/05/26
 */
public interface Y9LogService {
    public void process(MethodInvocation invocation, AccessLog log, HttpServletRequest request,
        HttpServletResponse response);
}
