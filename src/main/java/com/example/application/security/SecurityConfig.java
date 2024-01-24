package com.example.application.security;

import com.example.application.views.login.LoginView;
import com.vaadin.collaborationengine.CollaborationEngineConfiguration;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Collections;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig() {

    }

    /**
     * Demo SimpleInMemoryUserDetailsManager, which only provides
     * two hardcoded in-memory users and their roles.
     * NOTE: This shouldn't be used in real-world applications.
     */
    private static class SimpleInMemoryUserDetailsManager extends InMemoryUserDetailsManager {
        public SimpleInMemoryUserDetailsManager() {
            createUser(new User("user1",
                    "{noop}user1",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user2",
                    "{noop}user2",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user3",
                    "{noop}user3",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user4",
                    "{noop}user4",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user5",
                    "{noop}user5",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user6",
                    "{noop}user6",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user7",
                    "{noop}user7",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user8",
                    "{noop}user8",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user9",
                    "{noop}user9",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("user10",
                    "{noop}user10",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            createUser(new User("admin",
                    "{noop}password",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
            ));
        }
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/images/**", "/h2-console/**").permitAll();

        super.configure(http);

        setLoginView(http, LoginView.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new SimpleInMemoryUserDetailsManager();
    }

    @Bean
    public CollaborationEngineConfiguration ceConfigBean() {
        CollaborationEngineConfiguration configuration = new CollaborationEngineConfiguration(
                licenseEvent -> {
                    switch (licenseEvent.getType()) {
                        case GRACE_PERIOD_STARTED:
                        case LICENSE_EXPIRES_SOON:
                            logger.warn(licenseEvent.getMessage());
                            break;
                        case GRACE_PERIOD_ENDED:
                        case LICENSE_EXPIRED:
                            logger.error(licenseEvent.getMessage());
                            break;
                    }
                    logger.info("Vaadin Collaboration Kit license needs to be updated: %s".formatted(
                            licenseEvent.getMessage()));
                });
        configuration.setDataDir("projects/651755147298/secrets/ce-license");
        return configuration;
    }
}