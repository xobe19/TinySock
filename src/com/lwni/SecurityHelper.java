package com.lwni;

import java.security.MessageDigest;
import java.util.Base64;

public class SecurityHelper {
	static final String WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	static public String genAcceptKey(String wsKey) throws Exception {
		String toHash = wsKey + WS_GUID;
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(toHash.getBytes());
		byte[] digest = md.digest();
		return Base64.getEncoder().encodeToString(digest);

		
	}
}
