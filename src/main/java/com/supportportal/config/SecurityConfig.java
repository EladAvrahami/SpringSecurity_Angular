package com.supportportal.config;

import com.supportportal.constant.SecurityConstant;
import com.supportportal.filterReq.JwtAccessDeniedHandler;
import com.supportportal.filterReq.JwtAuthenticationEntryPoint;
import com.supportportal.filterReq.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //let spring security access
@EnableGlobalMethodSecurity(prePostEnabled = true) //allow to have security in method level (just as in the controller delete method @PreAuthorize annotation)
class SecurityConfig extends WebSecurityConfigurerAdapter  {
    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniendHandler;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    @Qualifier("UserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;//TO encrypt data in DB




    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        super.configure(auth);
    }

    //protect against cyber attack
    //https://he.wikipedia.org/wiki/CSRF
    //https://he.wikipedia.org/wiki/XSS
    //Cross Origin Resource Sharing- dont let anyone from any url to be able to connect our api,
    //we want to define a list or spesific urls/domain to access my API
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().cors().and()
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//we used to track customer session but today expires date in token is enough so session is stateless
              .and().authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll()//specify the public url to let SpringSecu allow to access without authenticate for everyone trying to reach
              .anyRequest().authenticated()//for all other req that is not defined in PUBLIC_URLS client need to be authenticated
              .and()
              .exceptionHandling().accessDeniedHandler(jwtAccessDeniendHandler)//the class that writ handle 401 exception
              .authenticationEntryPoint(jwtAuthenticationEntryPoint)
              .and()
              .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
