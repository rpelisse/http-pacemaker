Http Pacemaker
==============

This simple HttpProxy intended to provide something that you wish you will never have to use, but, saldy,
maybe have to. Basicaly, it's designs to stack requests to a HTTP backend not design to be used in
concurrent context. For instance, the project was setup to cope with XML/RPC API of the Cobbler
project (has been fixed since then).

So, to make it short, HttpPacemaker allow you to reduce the amount of concurrent HTTP requests
forwarded to the backend system (to one, if needed), and even introduced some extra time delay, in
case the back end needs some more time to process the request asynchronously. As said, you really
wish had *not* to use this product... ;)

Why did I wrote that ? Because I did not find anything existing or easily configurable within nginx
and Apache communities - or, at least, not within the version boundaries of the project. This
implementation does have some compiling features:

 * It's simple -- a couple of source files implementation
 * It's cross platform -- will works on anykind of system where Java runs
 * It's securable -- via Java EE web.xml or via a servlet filter
 * It's extendible -- via simple class extension
 * It's embeddable -- into your Java web application making testing your app easier
 * It's easy to install -- it's a simple Java WebApp, and a SPEC file to install it with Wildfly AS7,
                                            as a service is even provided !

This is implementation is more/less a fork from the following prokect (as sadly, I could not easily
reuse the stuff):

https://github.com/dsmiley/HTTP-Proxy-Servlet

This proxy depends on [Apache HttpClient](http://hc.apache.org/httpcomponents-client-ga/), which
offers another point of extension for this proxy:

     +- org.apache.httpcomponents:httpclient:jar:4.1.2:compile
        +- org.apache.httpcomponents:httpcore:jar:4.1.2:compile
        |  +- commons-logging:commons-logging:jar:1.1.1:compile
        |  \- commons-codec:commons-codec:jar:1.4:compile

Build & Installation
--------------------

It's a simple Maven project, so to build it, just install maven and run the following command:

$ mvn clean install

To set up your Eclipse IDE, you can also use maven to do it:

$ mvn eclipse:clean eclipse:eclipse

See maven documentation online: TODO

How do I configure the webapp ?
-------------------------------

* how do I set the backend URL ?

Simply add the following property, using whatever mechanism is at your disposal:

$ java ... -Dorg.redhat.jboss.httppacemaker.targetUri=http://mybackend:port/url/

Obviously, no default value, it needs to be set.

* how do I set the max number of concurrent requests to send to the backend ?

$ java ... -Dorg.redhat.jboss.httppacemaker.executor.poolsize=4

Default is 1.

* how do I set the time delay after each request has been returned by the backend system ?

$ java ... -Dorg.redhat.jboss.httppacemaker.executor.sleepTime

Default is 0 (no delay).

How do I deploy the WebApp within Wildfly AS 7 (for instance) ?
---------------------------------------------------------------

* set the webapp's context path
** edit the file 'src/main/webapp/WEB-INF/jboss-web.xml' to points to the write URL:

    <?xml version="1.0"?>
    <jboss-web>
        <context-root>/url_to_proxy/</context-root>
    </jboss-web>
** use the provided Wildfly configuration in 'src/main/resources/http-pacemaker.xml' as a main
   configuration file for Wildly, to built a very low memory foot print instance fo the server.
** start the app server.
