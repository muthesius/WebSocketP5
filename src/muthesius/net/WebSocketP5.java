/**
 * A WebSocket Bridge to the Webbit WebScocket for easy use.
 * Processing is non evented, so we nee to make some adjustements ;)
 *
 * ##copyright##
 * jens alexander ewald, muthesius kunsthochschule kiel, 2011
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

package muthesius.net;

import java.io.*;
import java.util.ArrayList;
import java.lang.reflect.Method;
import processing.core.*;
import org.webbitserver.*;

/**
 * @example SimpleWebSocketServer 
 */

public class WebSocketP5 implements WebSocketHandler {
	
	// Reference to the sketch
	PApplet parent;

	// The websocket events
	Method newMessageEvent;
	Method newConnectionOpenedEvent;
	Method newConnectionClosedEvent;
	
	// The internal Server
	WebServer server = null;
	int port;
	String socketname;
	
	public final static String VERSION = "##version##";
	public final static String DEFAULT_SOCKET = "p5websocket";

	/**
	 * Initialize a Websocket as a Webbit Webserver with a socket.
	 * 
	 * @example SimpleWebScketServer
	 * @param theParent
	 * @param port The port the socket should be served on (mus be >1024)
	 * @param socketname Give the socket a name - this results to the URL
	 * 									 in your javascript, e.g. you give it "mysocket", 
	 *                   the URL will be ws://<servername>/mysocket
	 * 
	 */
	public WebSocketP5(PApplet theParent) {
		this(theParent,8080,DEFAULT_SOCKET);
	}
	public WebSocketP5(PApplet theParent, int port) {
		this(theParent,port,DEFAULT_SOCKET);
	}
	public WebSocketP5(PApplet theParent, int port, String socketname){
		parent = theParent;
		this.port = port;
		this.socketname = socketname;
		
		server = WebServers.createWebServer(this.port);
 		server.add("/"+this.socketname, this);

		try {
   		server.start();
			System.out.println("Server running at " + server.getUri()+this.socketname);
		} catch (Exception e) {
			// just catch it and do nothing
			server = null;
		}

		try {
			Class args[] = { WebSocketConnection.class, String.class};
      newMessageEvent =
        parent.getClass().getMethod("websocketOnMessage",args);
    } catch (Exception e) {}

		try {
			Class args[] = { WebSocketConnection.class };
      newConnectionOpenedEvent =
        parent.getClass().getMethod("websocketOnOpen",args);
    } catch (Exception e) {}

		try {
			Class args[] = { WebSocketConnection.class };
      newConnectionClosedEvent =
        parent.getClass().getMethod("websocketOnClose",args);
    } catch (Exception e) {}
		/// END CONSTRUCTOR
	}
	
	
	public void start(){
		if (server!=null) {try{server.start();}catch(Exception e){}}
	}
	
	public void stop(){
		if (server!=null) {try{server.join();}catch(Exception e){}}
		for (WebSocketConnection c : connections){
		 c.close();
		} 
	}
	
	public void dispose() {
		stop();
  }

	
	/**
	 * Return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

	/**
	 * Return the port of the server.
	 * 
	 * @return int
	 * 				The port the server is running on
	 */
	public int getPort(){return port;}
	
	/**
	 * Return the URI of the server.
	 * 
	 * @return String
	 */
	public String getUri() {
		return (server!=null)?server.getUri().toString():"";
	}
	
	/**
	 * How many connections do we have?
	 */
	public int howManyConnections(){return connectionCount;}
	
	//// ACTIONS
	
	/**
	 * Broadcast a message to all clients
	 * 
	 * @param msg The message to braodcast to all connected clients
	 */
	public void broadcast(String msg){
		if (server==null) {System.out.print("Server not running!"); return;}
		for (WebSocketConnection c : connections){
		 c.send(msg);
		} 
	}
	
	/**
	 * Alias for broadcast
	 */
	public void	sendAll(String msg){broadcast(msg);}
	
	
	
	
	
	
	/////////// BEGINN SOCKETHANDLING
	int connectionCount;
	ArrayList<WebSocketConnection> connections = new ArrayList();

	// class DefaultWebSocketSocket implements WebSocketHandler {
	  public void onOpen(WebSocketConnection connection) {
	    if (newConnectionOpenedEvent != null) {
		    try {
					Object args[] = {connection};
		      newConnectionOpenedEvent.invoke(parent, args);
		    } catch (Exception e) {
		      // System.err.println("Disabling onWebSocketMessage() for " +
		      //                    " because of an error.");
		      // e.printStackTrace();
		      newConnectionOpenedEvent = null;
	    	}
	  	}
	    connections.add(connection);
	    connectionCount++;
	  }

	  public void onClose(WebSocketConnection connection) {
	    if (newConnectionClosedEvent != null) {
		    try {
					Object args[] = {connection};
		      newConnectionClosedEvent.invoke(parent, args);
		    } catch (Exception e) {
		      // System.err.println("Disabling onWebSocketMessage() for " +
		      //                    " because of an error.");
		      // e.printStackTrace();
		      newConnectionClosedEvent = null;
	    	}
	  	}
	    if (connections.contains(connection))
	      connections.remove(connections.indexOf(connection));
	    connectionCount--;
	  }

	  public void onMessage(WebSocketConnection connection, String message) {
	    if (newMessageEvent != null) {
		    try {
					Object args[] = {connection, message};
		      newMessageEvent.invoke(parent, args);
		    } catch (Exception e) {
		      // System.err.println("Disabling onWebSocketMessage() for " +
		      //                    " because of an error.");
		      // e.printStackTrace();
		      newMessageEvent = null;
	    	}
	  	}
	  }
	// }
	/////////// END SOCKETHANDLING
}