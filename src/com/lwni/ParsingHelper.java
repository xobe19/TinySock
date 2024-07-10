package com.lwni;
import java.io.*;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
public class ParsingHelper {
	static String parseInputStream(InputStream is) throws Exception {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		while(true) {
			if(sb.length() >= 4 && sb.substring(sb.length() - 4).equals("\r\n\r\n")) {
				break;
			}
			sb.append((char)isr.read());
		}

		return sb.toString();
		


	}
	
	static String extractSecWebSocketKey(String httpReq) {
		Pattern p = Pattern.compile("Sec-WebSocket-Key: (.+)\r\n");
		Matcher m = p.matcher(httpReq);
		m.find();
		return m.group(1);

	}
	
	static String genParsingResponse(String acceptKey) {
		return    "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
	}
	
	static void putStringInOS(String output, OutputStream os) throws IOException {
		byte[] barr = output.getBytes();
		os.write(barr);
	}
	
	static String getClientMessage(InputStream is) throws Exception {
		int fb = is.read();
		
		int fin = (fb&(1<<7)) > 0 ? 1 : 0;
		int OP_CODE = fb&Integer.parseInt("00001111", 2);
		if(fin == 1) {
			if(OP_CODE == 1) {
				// client sending a text message
				int payload_len = is.read() & Integer.parseInt("01111111", 2);
				if(payload_len == 126) {
					String binRep = Integer.toString(is.read(), 2) + Integer.toString(is.read(), 2);
					payload_len = Integer.parseInt(binRep, 2);
				}
				else if(payload_len == 127) {
					StringBuilder binRep = new StringBuilder();
					int bytes_to_read = 8;
					while(bytes_to_read-->0) {
						binRep.append(Integer.toString(is.read(), 2));
					}
					payload_len = Integer.parseInt(binRep.toString(), 2);
				}
				
				int[] message_key = new int[4];
				for(int i = 0; i < 4; i++) {
					message_key[i] = is.read();
				}
				
				StringBuilder finalMessage = new StringBuilder();
				
				for(int i = 0; i < payload_len; i++) {
					int xored = (is.read()) ^ ( message_key[i%4] );
					finalMessage.append((char)xored);
				}
				return finalMessage.toString();
				
			}
			else if(OP_CODE == 9) {
				return "client sent a ping";
			}
		}
		
		
		return "couldnt get message";
	}
	
	static void sendMessage(String message, OutputStream os) throws Exception {
		int fb = Integer.parseInt("10000001", 2);
		int payload_len = message.length();
		int sb = payload_len;
		if(payload_len > 125) {
			// never will need more than 16 bits for my use-case (fix this later)
			sb = 126;
		}
		
		os.write(new byte[] {(byte)fb, (byte)sb});
		
	if(payload_len > 125) {
			
		String bs = Integer.toString(payload_len);
		String padded_bs = String.format("%016d", bs);
		
		int tb = Integer.parseInt(padded_bs.substring(0, 8), 2);
		int fob = Integer.parseInt(padded_bs.substring(9), 2);
		os.write(new byte[] {(byte) tb, (byte)fob});
		}
	
	
	os.write(message.getBytes());
	
	
		}
		
	
		
		
		


	}
	
