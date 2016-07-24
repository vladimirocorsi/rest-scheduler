package com.vcorsi.rest_scheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;


public class App 
{
	//Thanks to http://crunchify.com/how-to-start-embedded-http-jersey-server-during-java-application-startup/
	public static void main(String[] args) throws IOException {
        System.out.println("Starting Embedded Jersey HTTPServer...\n");
        HttpServer crunchifyHTTPServer = createHttpServer();
        crunchifyHTTPServer.start();
        System.out.println(String.format("\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n", getURI()));
        System.out.println("Started Embedded Jersey HTTPServer Successfully !!!");
    }
 
        private static HttpServer createHttpServer() throws IOException {
        ResourceConfig config = new PackagesResourceConfig("com.vcorsi.rest_scheduler.rest");
        return HttpServerFactory.create(getURI(), config);
    }
 
    private static URI getURI() {
        return UriBuilder.fromUri("http://" + getHostName() + "/").port(8085).build();
    }
 
    private static String getHostName() {
        String hostName = "localhost";
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostName;
    }
}
