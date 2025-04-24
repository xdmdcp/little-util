package cn.chenlijian.little.starter.social.util;

import cn.chenlijian.little.starter.social.prop.SocialProperties;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.AuthRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SocialUtilTest {

    @Mock
    private SocialProperties socialProperties;

    @InjectMocks
    private SocialUtil socialUtil;

    private Map<AuthDefaultSource, AuthConfig> authConfigMap;

    // 提取公共方法以减少重复代码
    private AuthConfig createAuthConfig(String clientId, String clientSecret, String redirectUri) {
        return AuthConfig.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .build();
    }

    @Before
    public void setUp() {
        authConfigMap = new HashMap<>();
        authConfigMap.put(AuthDefaultSource.GITHUB, createAuthConfig("testClientId", "testClientSecret", "http://localhost:8080/callback"));
        when(socialProperties.getOauth()).thenReturn(authConfigMap);
    }

    @Test
    public void testGetAuthRequest_ValidSourceAndConfig_ReturnsAuthRequest() {
        AuthRequest authRequest = socialUtil.getAuthRequest("GitHub", socialProperties);
        assertNotNull(authRequest);
    }

    @Test(expected = AuthException.class)
    public void testGetAuthRequest_ValidSourceButNoConfig_ThrowsAuthException() {
        when(socialProperties.getOauth()).thenReturn(new HashMap<>());
        socialUtil.getAuthRequest("GitHub", socialProperties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAuthRequest_InvalidSource_ThrowsIllegalArgumentException() {
        socialUtil.getAuthRequest("InvalidSource", socialProperties);
    }

    @Test(expected = AuthException.class)
    public void testGetAuthRequest_UnsupportedSource_ThrowsAuthException() {
        authConfigMap.put(AuthDefaultSource.DINGTALK, createAuthConfig("testClientId", "testClientSecret", "http://localhost:8080/callback"));
        socialUtil.getAuthRequest("DINGTALK", socialProperties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAuthRequest_EmptyClientId_ThrowsIllegalArgumentException() {
        authConfigMap.put(AuthDefaultSource.GITHUB, createAuthConfig("", "testClientSecret", "http://localhost:8080/callback"));
        socialUtil.getAuthRequest("GitHub", socialProperties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAuthRequest_EmptyClientSecret_ThrowsIllegalArgumentException() {
        authConfigMap.put(AuthDefaultSource.GITHUB, createAuthConfig("testClientId", "", "http://localhost:8080/callback"));
        socialUtil.getAuthRequest("GitHub", socialProperties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAuthRequest_InvalidRedirectUri_ThrowsIllegalArgumentException() {
        authConfigMap.put(AuthDefaultSource.GITHUB, createAuthConfig("testClientId", "testClientSecret", "invalid-url"));
        socialUtil.getAuthRequest("GitHub", socialProperties);
    }
}
