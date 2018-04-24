package it.smartcommunitylab.mobile_attendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.smartcommunitylab.mobile_attendance.web.DefaultAuthenticationInterceptor;

@Configuration
@Profile("no-sec")
public class NoSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor authInterceptor() {
        return new DefaultAuthenticationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor()).addPathPatterns("/api/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**").anonymous().and().csrf().disable();
    }
}
