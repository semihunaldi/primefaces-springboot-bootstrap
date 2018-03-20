package com.semihunaldi.primefaces.springboot.bootstrap;

import com.semihunaldi.primefaces.springboot.bootstrap.jsf.FacesViewScope;
import com.sun.faces.config.FacesInitializer;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.primefaces.util.Constants;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.faces.application.ProjectStage;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.annotation.HandlesTypes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class PrimeFacesSpringBootBootstrapApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PrimeFacesSpringBootBootstrapApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PrimeFacesSpringBootBootstrapApplication.class);
	}

	@Bean
	public static CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.setScopes(Collections.singletonMap(
				FacesViewScope.NAME, new FacesViewScope()));
		return configurer;
	}

	@Bean
	public ServletRegistrationBean facesServletRegistration() {
		ServletRegistrationBean<FacesServlet> registration = new ServletRegistrationBean<>(new FacesServlet(), "*.xhtml");
		registration.setName("Faces Servlet");
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Bean
	public ServletContextInitializer servletContextCustomizer() {
		return sc -> {
			sc.setInitParameter(Constants.ContextParams.THEME, "bootstrap");
			sc.setInitParameter(Constants.ContextParams.FONT_AWESOME, "true");
			sc.setInitParameter(ProjectStage.PROJECT_STAGE_PARAM_NAME, ProjectStage.Development.name());
			sc.setInitParameter(Constants.ContextParams.PFV_KEY, Boolean.TRUE.toString());
			sc.setInitParameter(Constants.ContextParams.UPLOADER, "commons");
			sc.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
			sc.setInitParameter("javax.faces.FACELETS_SKIP_COMMENTS", Boolean.TRUE.toString());
		};
	}

	@Bean
	public TomcatServletWebServerFactory embeddedServletContainerFactory() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

		tomcat.addContextCustomizers((TomcatContextCustomizer) context -> {
			context.addServletContainerInitializer(new FacesInitializer(), getServletContainerInitializerHandlesTypes(FacesInitializer.class));
			context.addWelcomeFile("index.xhtml");
			context.addMimeMapping("eot", "application/vnd.ms-fontobject");
			context.addMimeMapping("ttf", "application/x-font-ttf");
			context.addMimeMapping("woff", "application/x-font-woff");
			prepareErrorPages(context);
		});

		return tomcat;
	}

	private void prepareErrorPages(Context context) {
		ErrorPage errorPage = new ErrorPage();
		errorPage.setErrorCode(500);
		errorPage.setErrorCode("500");
		errorPage.setLocation("/error/errorOccurred.xhtml");

		ErrorPage e404 = new ErrorPage();
		e404.setErrorCode(404);
		e404.setErrorCode("404");
		e404.setLocation("/error/404.xhtml");

		context.addErrorPage(errorPage);
		context.addErrorPage(e404);
	}

	private Set<Class<?>> getServletContainerInitializerHandlesTypes(Class<? extends ServletContainerInitializer> sciClass) {
		HandlesTypes annotation = sciClass.getAnnotation(HandlesTypes.class);
		if(annotation == null){
			return Collections.emptySet();
		}
		Class[] classesArray = annotation.value();
		Set<Class<?>> classesSet = new HashSet<>(classesArray.length);
		for(Class clazz : classesArray){
			classesSet.add(clazz);
		}
		return classesSet;
	}
}
