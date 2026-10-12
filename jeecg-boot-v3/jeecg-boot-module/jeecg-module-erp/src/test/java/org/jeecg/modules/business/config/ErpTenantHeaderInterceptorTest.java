package org.jeecg.modules.business.config;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ErpTenantHeaderInterceptorTest {

    private JdbcTemplate jdbcTemplate;
    private ErpTenantHeaderInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        interceptor = new ErpTenantHeaderInterceptor(jdbcTemplate);
        request = new MockHttpServletRequest("GET", "/business/order/jshOrder/list");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        ThreadContext.unbindSubject();
    }

    private void loginAs(String userId) {
        Subject subject = mock(Subject.class);
        if (userId != null) {
            LoginUser user = new LoginUser();
            user.setId(userId);
            when(subject.getPrincipal()).thenReturn(user);
        }
        ThreadContext.bind(subject);
    }

    private void givenMembership(String userId, int tenantId, int count) {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(userId), eq(tenantId))).thenReturn(count);
    }

    @Test
    void allowsMemberOfTenant() throws Exception {
        loginAs("u1");
        givenMembership("u1", 1001, 1);
        request.addHeader(CommonConstant.TENANT_ID, "1001");

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(200, response.getStatus());
    }

    @Test
    void rejectsNonMemberWith403Json() throws Exception {
        loginAs("u1");
        givenMembership("u1", 1000, 0);
        request.addHeader(CommonConstant.TENANT_ID, "1000");

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));
        assertTrue(response.getContentAsString().contains("\"success\":false"));
    }

    @Test
    void rejectsNonNumericTenantHeader() throws Exception {
        loginAs("u1");
        request.addHeader(CommonConstant.TENANT_ID, "abc");

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
    }

    @Test
    void allowsMissingHeaderWithoutQuery() throws Exception {
        loginAs("u1");

        assertTrue(interceptor.preHandle(request, response, new Object()));
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void allowsZeroOrBlankHeaderWithoutQuery() throws Exception {
        loginAs("u1");
        request.addHeader(CommonConstant.TENANT_ID, "0");
        assertTrue(interceptor.preHandle(request, response, new Object()));

        MockHttpServletRequest blank = new MockHttpServletRequest("GET", "/business/x");
        blank.addHeader(CommonConstant.TENANT_ID, " ");
        assertTrue(interceptor.preHandle(blank, response, new Object()));
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void allowsWhenNoLoggedInUser() throws Exception {
        loginAs(null);
        request.addHeader(CommonConstant.TENANT_ID, "1000");

        assertTrue(interceptor.preHandle(request, response, new Object()));
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void allowsWhenNoShiroContext() throws Exception {
        request.addHeader(CommonConstant.TENANT_ID, "1000");

        assertTrue(interceptor.preHandle(request, response, new Object()));
        verifyNoInteractions(jdbcTemplate);
    }
}
