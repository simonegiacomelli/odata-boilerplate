package com.penninkhof.odata.utils;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CxfServletRegister {

	@Bean
	public ServletRegistrationBean getODataServletRegistrationBean() {
		ServletRegistrationBean odataServletRegistrationBean = new ServletRegistrationBean(new CXFNonSpringJaxrsServlet(), "/odata.svc/*");
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("javax.ws.rs.Application", "org.apache.olingo.odata2.core.rest.app.ODataApplication");
		initParameters.put("org.apache.olingo.odata2.service.factory", "com.penninkhof.odata.utils.JPAServiceFactory");
		odataServletRegistrationBean.setInitParameters(initParameters);
		return odataServletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.addUrlPatterns("/odata.svc/*");
		registration.setName("corsFilter");
		registration.setOrder(1);
		registration.setFilter(new Filter() {

			@Override
			public void doFilter(ServletRequest req, ServletResponse res,
								 FilterChain chain) throws IOException, ServletException {
				HttpServletResponse response = (HttpServletResponse) res;
				HttpServletRequest request = (HttpServletRequest) req;
				response.addHeader("Access-Control-Allow-Origin", "*");
				response.addHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, MaxDataServiceVersion,sap-contextid-accept,x-csrf-token,DataServiceVersion,sap-cancel-on-close");
				if ("OPTIONS".equals(request.getMethod())) {
					response.setStatus(200);
					response.setContentType("text/plain");
				} else
					chain.doFilter(req, res);

			}

			@Override
			public void destroy() {
			}

			@Override
			public void init(FilterConfig arg0) throws ServletException {
			}
		});
		return registration;
	}

}
