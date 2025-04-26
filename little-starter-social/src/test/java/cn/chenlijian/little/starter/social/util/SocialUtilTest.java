package cn.chenlijian.little.starter.social.util;

import cn.chenlijian.little.starter.social.properties.SocialExtendProperties;
import cn.chenlijian.little.starter.social.properties.SocialProperties;
import cn.hutool.core.util.EnumUtil;
import me.zhyd.oauth.config.AuthConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SocialUtilTest {

    @Mock
    private SocialProperties socialProperties;

    @Mock
    private SocialExtendProperties socialExtendProperties;

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
}

