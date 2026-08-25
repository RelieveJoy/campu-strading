package com.campus.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "campus.oauth.github")
public class OAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizeUrl;
    private String tokenUrl;
    private String userUrl;
}
