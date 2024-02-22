package net.risesoft.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

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
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        // 在risenet-y9boot-3rd-cas-client工程的AssertionThreadLocalFilter类中，已经往session中保存了以下对象：
        // tenantName、loginName、loginPerson、tenantId、y9User等
        // 同时Y9LoginUserHolder也设置了tenantName、tenantId、person、Map<String, Object>属性。
        try {
            UserInfo loginUser = (UserInfo)session.getAttribute("loginUser");
            if (loginUser == null) {
                loginUser = Y9LoginUserHolder.getUserInfo();
            }
            if (loginUser == null) {
                throw new RuntimeException("No user was found in httpsession !!!");
            } else {
                // Boolean isTenantPerson = loginUser.isGlobalManager();
                // Y9DepartmentPropService y9DepartmentPropService = Y9Context.getBean(Y9DepartmentPropService.class);
                // List<Y9DepartmentProp> list =
                // y9DepartmentPropService.findByOrgBaseIdAndCategory(loginUser.getPersonId(),
                // Y9DepartmentPropCategoryEnum.ADMIN.getCategory());
                // if (!isTenantPerson && list.size() == 0) {
                // throw new RuntimeException("This user is not an administrator, without permission !!!");
                // }
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
    public void init(final FilterConfig filterConfig) throws ServletException {}

}
