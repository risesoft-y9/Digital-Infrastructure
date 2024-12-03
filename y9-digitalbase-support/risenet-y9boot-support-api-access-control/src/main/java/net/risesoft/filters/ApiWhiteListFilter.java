package net.risesoft.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.IpUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.Y9ApiAccessControl;
import net.risesoft.y9public.service.Y9ApiAccessControlService;

/**
 * api白名单过滤器
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@RequiredArgsConstructor
public class ApiWhiteListFilter implements Filter {

    private final Y9ApiAccessControlService y9ApiAccessControlService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;

        String clientIpAddr = Y9Context.getIpAddr(httpServletRequest);
        List<Y9ApiAccessControl> y9ApiAccessControlList =
            y9ApiAccessControlService.listByTypeAndEnabled(ApiAccessControlType.WHITE_LIST);

        if (y9ApiAccessControlList.isEmpty()) {
            chain.doFilter(request, response);
        } else {
            boolean isClientInWhiteList = y9ApiAccessControlList.stream().map(Y9ApiAccessControl::getValue)
                .anyMatch(configIp -> IpUtil.isSubnetContains(configIp, clientIpAddr));

            if (isClientInWhiteList) {
                chain.doFilter(request, response);
            } else {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpServletResponse.getWriter()
                    .write(Y9JsonUtil.writeValueAsString(Y9Result.failure(GlobalErrorCodeEnum.IP_NOT_IN_WHITE_LIST,
                        Y9StringUtil.format(GlobalErrorCodeEnum.IP_NOT_IN_WHITE_LIST.getDescription(), clientIpAddr))));
                return;
            }
        }

    }
}
