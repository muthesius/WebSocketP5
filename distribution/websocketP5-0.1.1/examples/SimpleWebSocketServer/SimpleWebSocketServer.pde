import muthesius.net.*;
import org.webbitserver.*;

WebSocketP5 socket;

void setup() {
  socket = new WebSocketP5(this,8080);
}

void draw() {}

void websocketOnMessage(WebSocketConnection con, String msg){
	println(msg);
}

void websocketOnOpen(WebSocketConnection con){
  println("A client joined");
}

void websocketOnClosed(WebSocketConnection con){
  println("A client left");
}
