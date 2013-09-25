package org.redhat.jboss.webapp;

import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;
import org.mitre.dsmiley.httpproxy.ProxyServlet;


//@WebServlet(name="CobblerProxy", urlPatterns="/*")
public class QueuerProxyServlet extends ProxyServlet {
	
	  protected HttpClient createHttpClient(HttpParams hcParams) {
		  return super.createHttpClient(hcParams);
		  //FIXME: Add CDI integration to have a singleton here !
//		  return new HttpClientWithQueue(hcParams);
	  }
}
