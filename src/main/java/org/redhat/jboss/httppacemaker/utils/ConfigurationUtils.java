package org.redhat.jboss.httppacemaker.utils;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletConfig;

public final class ConfigurationUtils {

	private ConfigurationUtils() {	}
	
	public static String retrieveParameterFromContext(ServletConfig servletConfig, String parameterName) {
		String parameterValue = retrieveParameterFromSystemProperties(parameterName);
		if ( parameterValue == null || "".equals(parameterValue) ) {
			parameterValue = retrieveParameterFromServletConfig(servletConfig, parameterName);
			if ( parameterValue == null || "".equals(parameterValue) ) {
				throw new IllegalStateException("No parameter named " + parameterName + " is defined in the servlet init-parameters, nor in the system properties.");
			}
		}
		return parameterValue;
	}
	
	public static URI buildURI(String uri) {
		if ( uri == null || "".equals(uri) )
			throw new IllegalArgumentException("Invalid URI pattern:" + uri);
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid URI pattern:" + uri,e);
		}
	}
	
	public static String retrieveParameterFromSystemProperties(String parameterName) {		
		return	System.getProperty(parameterName);
	}
	
	public static String retrieveParameterFromServletConfig(ServletConfig servletConfig, String parameterName) {
		try {
			return servletConfig.getInitParameter(parameterName);
		} catch (Exception e) {
			throw new RuntimeException("Trying to process " + parameterName + " init parameter: "+e,e);
		}		
	}
}
