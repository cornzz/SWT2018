/*
 * Copyright 2014.2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flowershop;

import org.salespointframework.EnableSalespoint;
import org.salespointframework.SalespointSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * The central application class to configure the Spring container and run the application.
 *
 * @author Cornelius Kummer
 */
@EnableSalespoint
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	static class WebSecurityConfiguration extends SalespointSecurityConfiguration {

		/**
		 * Disabling Spring Securitys CSRF support as we do not implement pre-flight request handling for the sake of
		 * simplicity. Setting up basic security, defining login and logout options and an access denied page.
		 *
		 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();

			http.authorizeRequests().antMatchers("/**").permitAll().and()
					.formLogin().loginPage("/login").loginProcessingUrl("/login").and()
					.logout().logoutUrl("/logout").logoutSuccessUrl("/?logout").and()
					.exceptionHandling().accessDeniedPage("/accessDenied");
		}

	}

	@Configuration
	static class FlowerShopWebConfiguration implements WebMvcConfigurer {

		/**
		 * @return {@link LocaleResolver} for the project with default locale set to German.
		 */
		@Bean
		public LocaleResolver localeResolver() {
			SessionLocaleResolver localeResolver = new SessionLocaleResolver();
			localeResolver.setDefaultLocale(Locale.GERMAN);
			return localeResolver;
		}

		/**
		 * @return {@link LocaleChangeInterceptor} for the project with the parameter name to be intercepted set to <code>lang</code>.
		 */
		@Bean
		public LocaleChangeInterceptor localeChangeInterceptor() {
			LocaleChangeInterceptor changeInterceptor = new LocaleChangeInterceptor();
			changeInterceptor.setParamName("lang");
			return changeInterceptor;
		}

		/**
		 * Adds our {@link LocaleChangeInterceptor} for pre- and post-processing of controller method invocations.
		 *
		 * @param registry will never be {@literal null}.
		 */
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(localeChangeInterceptor());
		}

	}

}