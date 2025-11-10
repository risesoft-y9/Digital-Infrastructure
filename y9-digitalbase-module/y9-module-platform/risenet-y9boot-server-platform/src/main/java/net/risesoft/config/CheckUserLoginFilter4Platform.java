package net.risesoft.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.user.UserInfo;
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
    public void init(final FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        // 在risenet-y9boot-starter-sso-oauth2-resource工程的Y9Oauth2ResourceFilter类中，已经往session中保存了以下对象：
        // access_token、userInfo、loginName、positionId、deptId
        // 同时Y9LoginUserHolder也设置了positionId、tenantId、tenantName、tenantShortName、UserInfo
        UserInfo loginUser = (UserInfo)session.getAttribute("loginUser");
        if (loginUser == null) {
            loginUser = Y9LoginUserHolder.getUserInfo();
        }
        if (loginUser.getManagerLevel().isGeneralUser()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            try {
                response.getWriter().write("该用户不是管理员，没有权限!");
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            return;
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}

}
