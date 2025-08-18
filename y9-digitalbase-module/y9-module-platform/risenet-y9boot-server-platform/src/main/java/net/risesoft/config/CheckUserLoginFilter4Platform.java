package net.risesoft.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Manager;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class CheckUserLoginFilter4Platform implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        // 在risenet-y9boot-starter-sso-oauth2-resource工程的Y9Oauth2ResourceFilter类中，已经往session中保存了以下对象：
        // access_token、userInfo、loginName、positionId、deptId
        // 同时Y9LoginUserHolder也设置了positionId、tenantId、tenantName、tenantShortName、UserInfo
        try {
            UserInfo loginUser = (UserInfo)session.getAttribute("loginUser");
            if (loginUser == null) {
                loginUser = Y9LoginUserHolder.getUserInfo();
            }
            if (loginUser == null) {
                throw new RuntimeException("No user was found in httpsession !!!");
            } else {
                Y9ManagerService y9ManagerService = Y9Context.getBean(Y9ManagerService.class);
                Y9Manager y9Manager = y9ManagerService.getById(loginUser.getPersonId());
                if (null == y9Manager) {
                    throw new RuntimeException("This user is not an administrator, without permission !!!");
                }
            }
        } catch (RuntimeException e) {
            LOGGER.info(e.getMessage(), e);
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json; charset=utf-8");
            response.setStatus(401);
            PrintWriter out = servletResponse.getWriter();
            out.append("该用户不是管理员，没有权限!!");
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}

}
