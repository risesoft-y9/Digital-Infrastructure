package net.risesoft.config;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import net.risesoft.entity.Y9Person;
import net.risesoft.model.Person;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class CheckUserLoginFilterSkipSSO implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        try {
            String loginName = "admin";
            String tenantId = "c425281829dc4d4496ddddf7fc0198d0";
            String tenantName = "北京有生博大软件股份有限公司";
            String tenantShortName = "risesoft";
            String personId = "4c106ab010ef47858e0acc84f98ea2a1";

            session.setAttribute("tenantName", tenantName);
            session.setAttribute("loginName", loginName);

            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setTenantName(tenantName);
            Y9LoginUserHolder.setTenantShortName(tenantShortName);

            Y9PersonService pm = Y9Context.getBean(Y9PersonService.class);
            Y9Person y9Person = pm.getById(personId);
            Person person = ModelConvertUtil.convert(y9Person, Person.class);

            session.setAttribute("loginUser", person);
            Y9LoginUserHolder.setPerson(person);

            chain.doFilter(servletRequest, servletResponse);

        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    @Override
    public void destroy() {}

}
