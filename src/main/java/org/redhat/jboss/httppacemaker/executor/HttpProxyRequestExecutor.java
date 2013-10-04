package org.redhat.jboss.httppacemaker.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.management.RuntimeErrorException;

import org.apache.http.HttpResponse;
import org.redhat.jboss.httppacemaker.utils.ConfigurationUtils;

@ApplicationScoped
public class HttpProxyRequestExecutor {

	private final static String POOL_SIZE_PARAMETER_NAME = "org.redhat.jboss.httppacemaker.executor.poolsize";
	private final static String SLEEP_TIME_PARAMETER_NAME = "org.redhat.jboss.httppacemaker.executor.sleepTime";
	
	private final static String DEFAULT_POOL_SIZE = "1";
	private final static String DEFAULT_SLEEP_TIME = "0";
	private ExecutorService executor;	

	private int nbThread;
	private int sleepTime;
	
	@PostConstruct
	public void init() {
		this.nbThread = Integer.valueOf(retrieveParameterValue(POOL_SIZE_PARAMETER_NAME, DEFAULT_POOL_SIZE));
		this.sleepTime = Integer.valueOf(retrieveParameterValue(SLEEP_TIME_PARAMETER_NAME, DEFAULT_SLEEP_TIME));
		executor = Executors.newFixedThreadPool(this.nbThread);
	}

	private static String retrieveParameterValue(final String parameterName, final String defaultValue) {
		String parameterValue = ConfigurationUtils.retrieveParameterFromSystemProperties(parameterName);
		if ( parameterValue == null || "".equals(parameterValue))
			return defaultValue;
		return parameterValue;
	}
	
	public HttpResponse pushRequestToBackend(Callable<HttpResponse> request) {
		System.out.println("ObjectId:" + this.hashCode());
	    Future<HttpResponse> submit = executor.submit(request);
	    HttpResponse response = getHttpResponse(submit);
	    waitBeforeSendingReply();
	    return response;
	}

	private HttpResponse getHttpResponse(Future<HttpResponse> submit ) {
	    HttpResponse response;
	    try {
			 response = submit.get();
		} catch (InterruptedException e ){
			throw new RuntimeException(e);
		} catch ( ExecutionException e) {
			throw new RuntimeException(e);
		}
	    if ( response == null )
	    	throw new RuntimeErrorException(new Error("HTTP response returned was 'null' !"));
	    return response;
	}
	
	private void waitBeforeSendingReply() {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			//FIXME: this should be logged
		}		
	}

	public void preDestroy() {
		executor.shutdown();
	}
}
