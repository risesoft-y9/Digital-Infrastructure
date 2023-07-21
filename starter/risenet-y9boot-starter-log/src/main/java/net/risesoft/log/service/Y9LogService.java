package net.risesoft.log.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInvocation;

import net.risesoft.model.AccessLog;

/**
 * @author DZJ
 * @date 2022/05/26
 */
public interface Y9LogService {
    public void process(MethodInvocation invocation, AccessLog log, HttpServletRequest request, HttpServletResponse response);
}
