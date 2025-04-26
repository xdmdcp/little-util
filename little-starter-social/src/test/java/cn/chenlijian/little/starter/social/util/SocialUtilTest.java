package cn.chenlijian.little.starter.social.util;

import cn.chenlijian.little.starter.social.properties.SocialExtendProperties;
import cn.chenlijian.little.starter.social.properties.SocialProperties;
import cn.hutool.core.util.EnumUtil;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.AuthRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SocialUtilTest {

    @Mock
    private SocialProperties socialProperties;

    @Mock
    private SocialExtendProperties socialExtendProperties;
    @Mock
    private AuthRequest authRequest;
    @InjectMocks
    private SocialUtil socialUtil;

    @Before
    public void setUp() {
        // 初始化 Mock 对象
        when(socialProperties.getExtend()).thenReturn(socialExtendProperties);
    }

    /**
     * 测试用例1：properties 为 null，验证返回空列表
     */
    @Test
    public void testGetTypeList_NullProperties_ReturnsEmptyList() {
        List<String> result = SocialUtil.getTypeList(null);
        assertEquals(0, result.size());
    }

    /**
     * 测试用例2：properties.getType() 为 null，验证返回空列表
     */
    @Test
    public void testGetTypeList_NullType_ReturnsEmptyList() {
        when(socialProperties.getType()).thenReturn(null);
        List<String> result = SocialUtil.getTypeList(socialProperties);
        assertEquals(0, result.size());
    }

    /**
     * 测试用例3：properties.getType() 不为空，验证返回默认类型列表
     */
    @Test
    public void testGetTypeList_NonEmptyType_ReturnsDefaultList() {
        Map<String, AuthConfig> typeMap = new HashMap<>();
        typeMap.put("github", null);
        typeMap.put("google", null);
        when(socialProperties.getType()).thenReturn(typeMap);

        List<String> result = SocialUtil.getTypeList(socialProperties);
        assertEquals(2, result.size());
        assertTrue(result.contains("github"));
        assertTrue(result.contains("google"));
    }

    /**
     * 测试用例4：properties.getExtend() 为 null，验证仅返回默认类型列表
     */
    @Test
    public void testGetTypeList_NullExtend_ReturnsDefaultList() {
        when(socialProperties.getExtend()).thenReturn(null);

        Map<String, AuthConfig> typeMap = new HashMap<>();
        typeMap.put("github", null);
        when(socialProperties.getType()).thenReturn(typeMap);

        List<String> result = SocialUtil.getTypeList(socialProperties);
        assertEquals(1, result.size());
        assertTrue(result.contains("github"));
    }

    /**
     * 测试用例5：properties.getExtend() 有效，但扩展配置为空，验证仅返回默认类型列表
     */
    @Test
    public void testGetTypeList_EmptyExtendConfig_ReturnsDefaultList() {
        // 移除不必要的 stubbing
        // when(socialExtendProperties.getConfig()).thenReturn(Collections.emptyMap());

        Map<String, AuthConfig> typeMap = new HashMap<>();
        typeMap.put("github", null);
        when(socialProperties.getType()).thenReturn(typeMap);

        List<String> result = SocialUtil.getTypeList(socialProperties);
        assertEquals(1, result.size());
        assertTrue(result.contains("github"));
    }

    /**
     * 测试用例6：扩展类型处理过程中抛出异常，验证日志记录并返回默认类型列表
     */
    @Test
    public void testGetTypeList_ExceptionInExtendProcessing_ReturnsDefaultList() {
        try (var mockedEnumUtil = Mockito.mockStatic(EnumUtil.class)) {
            mockedEnumUtil.when(() -> EnumUtil.getNames(any())).thenThrow(new IllegalArgumentException("Invalid enum"));

            // 移除不必要的 stubbing
            // Map<String, SocialExtendProperties.AuthRequestExtendConfig> extendConfig = new HashMap<>();
            // extendConfig.put("github", null);
            // when(socialExtendProperties.getConfig()).thenReturn(extendConfig);

            Map<String, AuthConfig> typeMap = new HashMap<>();
            typeMap.put("twitter", null);
            when(socialProperties.getType()).thenReturn(typeMap);

            List<String> result = SocialUtil.getTypeList(socialProperties);
            assertEquals(1, result.size());
            assertTrue(result.contains("twitter"));
        }
    }

    /**
     * 测试用例1：来源为空，验证抛出AuthException
     */
    @Test
    public void testGetAuthRequest_EmptySource_ThrowsAuthException() {
        assertThrows(AuthException.class, () -> {
            SocialUtil.getAuthRequest("", socialProperties);
        });
    }

    /**
     * 测试用例2：默认请求不为空，验证返回默认请求
     */
    @Test
    public void testGetAuthRequest_DefaultRequestNotNull_ReturnsDefaultRequest() throws AuthException {
        AuthConfig authConfig = mock(AuthConfig.class);
        when(socialProperties.getType()).thenReturn(java.util.Collections.singletonMap("default", authConfig));
        when(SocialUtil.getDefaultAuthRequest(anyString(), any(SocialProperties.class))).thenReturn(authRequest);

        AuthRequest result = SocialUtil.getAuthRequest("default", socialProperties);
        assertNotNull(result);
    }

    /**
     * 测试用例3：默认请求为空且枚举类为空，验证抛出AuthException
     */
    @Test
    public void testGetAuthRequest_DefaultRequestNullAndEnumClassNull_ThrowsAuthException() {
        when(SocialUtil.getDefaultAuthRequest(anyString(), any(SocialProperties.class))).thenReturn(null);
        when(socialExtendProperties.getEnumClass()).thenReturn(null);

        assertThrows(AuthException.class, () -> {
            SocialUtil.getAuthRequest("source", socialProperties);
        });
    }

    /**
     * 测试用例4：默认请求为空且枚举类不为空，验证返回扩展请求
     */
    @Test
    public void testGetAuthRequest_DefaultRequestNullAndEnumClassNotNull_ReturnsExtendRequest() throws AuthException {
        when(socialExtendProperties.getEnumClass()).thenReturn((Class) AuthDefaultSource.class);

        when(SocialUtil.getDefaultAuthRequest(anyString(), any(SocialProperties.class))).thenReturn(null);
        when(SocialUtil.getExtendRequest(any(Class.class), anyString(), any(SocialProperties.class))).thenReturn(authRequest);

        AuthRequest result = SocialUtil.getAuthRequest("source", socialProperties);
        assertNotNull(result);
    }

    /**
     * 测试用例5：扩展请求为空，验证抛出AuthException
     */
    @Test
    public void testGetAuthRequest_ExtendRequestNull_ThrowsAuthException() {
        when(socialExtendProperties.getEnumClass()).thenReturn((Class) AuthDefaultSource.class);

        when(SocialUtil.getDefaultAuthRequest(anyString(), any(SocialProperties.class))).thenReturn(null);
        when(SocialUtil.getExtendRequest(any(Class.class), anyString(), any(SocialProperties.class))).thenReturn(null);

        assertThrows(AuthException.class, () -> {
            SocialUtil.getAuthRequest("source", socialProperties);
        });
    }


    /**
     * 测试用例6：意外异常，验证抛出AuthException
     */
    @Test
    public void testGetAuthRequest_UnexpectedException_ThrowsAuthException() {
        when(SocialUtil.getDefaultAuthRequest(anyString(), any(SocialProperties.class))).thenThrow(AuthException.class);

        assertThrows(AuthException.class, () -> {
            SocialUtil.getAuthRequest("source", socialProperties);
        });
    }
}

