package logger;

import java.io.*;
import java.net.*;

import static logger.Settings.*;

public class Logger {
	
	Socket socket;
	InputStream in;
	OutputStream out;

	public static void main(String[] args) {
		Logger logger = new Logger();
		logger.start();
	}
	
	public Logger() {
		try {
			socket = new Socket(InetAddress.getLoopbackAddress(), SERVER_PORT);
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			System.err.println("Could't create socket");			
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void start() {
		while(true) {
			String msg = getMessageFromKeyboard();
			if(msg == null) {
				continue;
			}
			
			sendToServer(msg);
			
			if(LoggerServer.isPrintLogCommand(msg)) {
				receiveAndPrintLog();
			}
		}
	}
	
	private void sendToServer(String msg) {
		try {
			out.write(msg.getBytes());
		} catch (IOException e) {
			System.err.println("Sending error");
			e.printStackTrace();
		}
	}
	
	private void receiveAndPrintLog() {
		byte[] buf = new byte[4096];
        int bufLength;
		try {
			bufLength = in.read(buf);
			String msg = new String(buf, 0, bufLength);
			System.out.println(msg.trim());
		} catch (IOException e) {
			System.err.println("Error occured while receiving from server");
			e.printStackTrace();
		}
	}
	
	private String getMessageFromKeyboard() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            return br.readLine();
        } catch (IOException ex) {
        	System.err.println("Error during manual input");
            return null;
        }
    }
	
	private void close() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Error while closing socket");
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
		
	}
}
