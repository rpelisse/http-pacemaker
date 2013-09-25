package org.redhat.jboss.webapp;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsClient {

	
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    private static final String destinationConfig = "queue/incomingRequests";

    private static Context buildContext() {
        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
//            env.put(Context.SECURITY_PRINCIPAL, usernameConfig);
//            env.put(Context.SECURITY_CREDENTIALS, passwordConfig);
			return new InitialContext(env);
		} catch (NamingException e) {
			throw new IllegalStateException(e);
		}
        
    }
    
    public void executeJMSClient() throws Exception {
    	Connection connection = null;
        try {
            final Context context = buildContext();


            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY);
            Destination destination = (Destination) context.lookup(destinationConfig);
            // Create the JMS connection, session, producer, and consumer
            connection = connectionFactory.createConnection();
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            connection.start();

//          MessageConsumer consumer = session.createConsumer(destination);

            producer.send(session.createTextMessage("hello"));
    
    
        } catch (Exception e) {
        	throw new IllegalStateException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
