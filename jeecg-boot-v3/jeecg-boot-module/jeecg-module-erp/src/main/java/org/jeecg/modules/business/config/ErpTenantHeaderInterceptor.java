package org.jeecg.modules.business.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 校验请求头 X-Tenant-Id 属于当前登录用户。
 * 上游 JwtFilter 直接信任该头写入 TenantContext，OPEN_SYSTEM_TENANT_CONTROL=false 时不校验归属，
 * 而 ERP jsh_ 表按该租户过滤，故在 /business/** 上补校验：头为空或 "0" 放行；未登录交给 Shiro；
 * 已登录且 sys_user_tenant 中无 status='1' 的关联则返回 403。
 */
@Slf4j
@Component
public class ErpTenantHeaderInterceptor implements HandlerInterceptor {

    private static final String SQL = "SELECT COUNT(1) FROM sys_user_tenant WHERE user_id = ? AND tenant_id = ? AND status = '1'";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JdbcTemplate jdbcTemplate;

    public ErpTenantHeaderInterceptor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(CommonConstant.TENANT_ID);
        if (header == null || header.trim().isEmpty() || "0".equals(header.trim())) {
            return true;
        }
        LoginUser user = currentUser();
        if (user == null) {
            return true;
        }
        Integer tenantId;
        try {
            tenantId = Integer.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return reject(response, user, request);
        }
        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class, user.getId(), tenantId);
        if (count != null && count > 0) {
            return true;
        }
        return reject(response, user, request);
    }

    private static LoginUser currentUser() {
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            return principal instanceof LoginUser ? (LoginUser) principal : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean reject(HttpServletResponse response, LoginUser user, HttpServletRequest request) throws Exception {
        log.warn("ERP tenant header rejected: userId={} uri={}", user.getId(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(MAPPER.writeValueAsString(Result.error(403, "无权访问该租户数据")));
        return false;
    }
}
