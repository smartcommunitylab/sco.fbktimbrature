package it.smartcommunitylab.mobile_attendance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.mobile_attendance.web.AACAuthenticationInterceptor;

@Configuration
@Profile("sec")
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Value("${aac.url}")
    private String aacServiceUrl;

    @Value("${aac.clientId}")
    private String aacClientId;

    @Value("${aac.clientSecret}")
    private String aacClientSecret;

    @Bean
    public AACService aacService() {
        return new AACService(aacServiceUrl, aacClientId, aacClientSecret);
    }

    @Bean
    public AACProfileService aacProfileService() {
        return new AACProfileService(aacServiceUrl);
    }

    @Bean
    public AACAuthenticationInterceptor authInterceptor() {
        return new AACAuthenticationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor()).addPathPatterns("/api/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll().and().anonymous();
        super.configure(http);
    }

    public String getAacServiceUrl() {
        return aacServiceUrl;
    }

    public void setAacServiceUrl(String aacServiceUrl) {
        this.aacServiceUrl = aacServiceUrl;
    }

    public String getAacClientId() {
        return aacClientId;
    }

    public void setAacClientId(String aacClientId) {
        this.aacClientId = aacClientId;
    }

    public String getAacClientSecret() {
        return aacClientSecret;
    }

    public void setAacClientSecret(String aacClientSecret) {
        this.aacClientSecret = aacClientSecret;
    }
}
