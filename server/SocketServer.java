/*
 * Copyright (c) 2010-2018 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


public class SocketServer extends WebSocketServer
{
   public SocketServer(int port)
   {
      super(new InetSocketAddress(port));
   }

   @Override
   public void onStart()
   {
      System.out.println("Server started.");
   }

   @Override
   public void onOpen(WebSocket conn, ClientHandshake handshake)
   {
      System.out.println("New connection ! conn_id = " + conn);
   }

   @Override
   public void onClose(WebSocket conn, int code, String reason, boolean remote)
   {
      GameOnlineServer.updateFromSocket("disconnected", conn);
   }

   @Override
   public void onMessage(WebSocket conn, String message)
   {
      GameOnlineServer.updateFromSocket(message, conn);
   }

   @Override
   public void onError(WebSocket conn, Exception ex)
   {
      ex.printStackTrace();
   }


   public void serverBroadcast(String msg)
   {
      broadcast(msg);
      System.out.println("Sent : " + msg);
   }
}
