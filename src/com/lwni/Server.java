package com.lwni;

import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

public class Server {
ServerSocket listeningSocket;
		Map<String, List<Socket>> rooms = new HashMap<>();
	public void start(int port) {
		try {
		this.listeningSocket = new ServerSocket(port);
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		while(true) {
			System.out.println("waiting for client connections");
			Socket clientSock = this.listeningSocket.accept();
			InputStream is = clientSock.getInputStream();
			OutputStream os = clientSock.getOutputStream();
			System.out.println("new client connected");
			String httpreq = ParsingHelper.parseInputStream(is);
			System.out.println(httpreq);
			System.out.println("key is");
			String wsKey = ParsingHelper.extractSecWebSocketKey(httpreq);
			System.out.println(wsKey);
			String accKey = SecurityHelper.genAcceptKey(wsKey);
			ParsingHelper.putStringInOS(
					ParsingHelper.genParsingResponse(accKey), os);

			
			threadPool.submit(() -> {
				
				
				
			while(true) {
			System.out.println("client sent a message");
			System.out.println(ParsingHelper.getClientMessage(is));
			}

				
				
			});

//			ParsingHelper.sendMessage("helllo fker", os);

		}
		}
		catch(Exception e) {
			System.out.println("err: " + e.getMessage());
		}

	}
	
}
