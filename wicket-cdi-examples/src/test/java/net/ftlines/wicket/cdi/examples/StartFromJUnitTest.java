package net.ftlines.wicket.cdi.examples;

import java.io.IOException;
import java.net.ServerSocket;

import javax.naming.NamingException;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartFromJUnitTest
{
	private static final Logger log = LoggerFactory.getLogger(StartFromJUnitTest.class);

	public static class SessionMetaData extends MetaDataKey<Session>
	{
		private static final long serialVersionUID = 1L;
	}

	private static Server server;

	private static int port;

	@BeforeClass
	public static void before() throws Exception
	{
		findFreePort();

		server = createServer(port);
		server.start();
	}

	private static void findFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			port = socket.getLocalPort();
			socket.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static int getPort()
	{
		return port;
	}

	@AfterClass
	public static void after() throws Exception
	{
		server.stop();
		server.join();
		ThreadContext.setApplication(null);
		ThreadContext.setSession(null);
	}

	protected static Server createServer(int port) throws NamingException
	{
		Server server = new Server();
		SocketConnector connector = new SocketConnector();
		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(port);
		server.setConnectors(new Connector[] {connector});

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		server.setHandler(contexts);

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");
		
		server.setHandler(bb);

		return server;
	}



	@Test
	public void test()
	{
		Assert.assertNotNull(server);
	}
}
