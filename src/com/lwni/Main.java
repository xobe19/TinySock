package com.lwni;

public class Main {
 public static void main(String[] args) {
	 Server s = new Server();
	 System.out.println("server starting");
	 s.start(2999);
	 System.out.println("server started");
 }
}
