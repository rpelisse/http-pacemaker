package org.redhat.jboss.httppacemaker;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.redhat.jboss.httppacemaker.executor.HttpProxyRequestExecutor;

@WebServlet(displayName="HttpPaceMaker", loadOnStartup=1, urlPatterns="/*")
public class PacerProxyServlet extends ProxyServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	HttpProxyRequestExecutor httpRequestExecutor;
	
	protected HttpClient createHttpClient(HttpParams hcParams) {
		return super.createHttpClient(hcParams);
	}

	protected void sendProxyRequestToBackend(final HttpRequest proxyRequest, final HttpServletRequest servletRequest, 
			final HttpServletResponse servletResponse) throws ServletException, IOException {
		httpRequestExecutor.pushRequestToBackend(new ExecuteHttpRequest(proxyRequest, servletRequest, servletResponse));
	}

	public class ExecuteHttpRequest implements Callable<HttpResponse> {

		private final HttpRequest proxyRequest;
		private final HttpServletRequest servletRequest;
		private final HttpServletResponse servletResponse;
		
		public ExecuteHttpRequest(final HttpRequest proxyRequest, final HttpServletRequest servletRequest, 
				final HttpServletResponse servletResponse ) {
			this.proxyRequest = proxyRequest;
			this.servletRequest = servletRequest;
			this.servletResponse = servletResponse;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public HttpResponse call() throws Exception {
			System.out.println("Start processing request: [" +  proxyRequest.getRequestLine() + "]");
			try {
				// Execute the request
				HttpResponse proxyResponse = proxyClient.execute(new HttpHost(targetUri.getHost(), targetUri.getPort()), proxyRequest);
				// Process the response
				int statusCode = proxyResponse.getStatusLine().getStatusCode();

				if (doResponseRedirectOrNotModifiedLogic(servletRequest, servletResponse, proxyResponse, statusCode)) {
					//just to be sure, but is probably a no-op
					EntityUtils.consume(proxyResponse.getEntity());
				} else {
					// Pass the response code. This method with the "reason phrase" is deprecated but it's the only way to pass the
					//  reason along too.
					// noinspection deprecation
					servletResponse.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());
					copyResponseHeaders(proxyResponse, servletResponse);
					// Send the content to the client
					copyResponseEntity(proxyResponse, servletResponse);
				}
				System.out.println("End processing request");
				return proxyResponse;
			} catch (Exception e) {
				//abort request, according to best practice with HttpClient
				if (proxyRequest instanceof AbortableHttpRequest) {
					AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest) proxyRequest;
					abortableHttpRequest.abort();
				}
				if (e instanceof RuntimeException)
					throw (RuntimeException)e;
				if (e instanceof ServletException)
					throw (ServletException)e;
				//noinspection ConstantConditions
				if (e instanceof IOException)
					throw (IOException) e;
				throw new RuntimeException(e);
			}	
		}
	} 
}
