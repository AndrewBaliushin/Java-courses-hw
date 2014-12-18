package logger;

import java.io.*;
import java.net.*;
import java.util.*;

import static logger.Settings.*;

public class LoggerServer {
    
	
	List<String> log;
	
	
    List<Socket> socketPool;
    ServerSocket serverSocket;
    
    public static boolean isPrintLogCommand(String msg) {
		return (msg.equalsIgnoreCase(PRINT_CMD));
	}
    
    public static boolean isExitCommand(String msg) {
		return (msg.equalsIgnoreCase(EXIT_CMD));
	}

	public static void main(String[] args) throws IOException {
    	LoggerServer server = new LoggerServer();
    	server.start();
    }
    
    public LoggerServer() {
    	log = new ArrayList<>();
    	socketPool = new ArrayList<Socket>();
    	
    	try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			System.err.println("Unable to create ServerSocket");
			e.printStackTrace();
		}
	}
    
    public void start() {
    	new Thread(listenForNewConnections()).start();
    	new Thread(listenForLogsAndCommands()).start();  
    }
        
    private Runnable listenForNewConnections() {
    	Runnable listner = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Socket newSocket = serverSocket.accept();
						socketPool.add(newSocket);
					} catch (IOException e) {
						System.err.println("Exception during Scoket creation");
						e.printStackTrace();
					}
				}
			}
		};
		
		return listner;
    }
    
    private Runnable listenForLogsAndCommands() {
    	Runnable listner = new Runnable() {
			@Override
			public void run() {
				while(true) {
					parseOpentSocketsForLogsAndCommands();
				}				
			}
		};
		
		return listner;
    }
    
    private void parseOpentSocketsForLogsAndCommands() {
    	for (Socket s : socketPool) {
    		if(!s.isConnected()) {
				socketPool.remove(s);
				continue;
			}
    		
			try {				
				InputStream in = s.getInputStream();				
				String msg = getMsgFromStream(in);
	    		
	    		if(isPrintLogCommand(msg)) {
	    			sendLogToSocket(s);
	    			continue;
	    		} else if(isExitCommand(msg)) {
	    			close();
	    		} else {
	    			writeToLogAndConsole(msg);
	    		}
			} catch (IOException e) {
				System.err.println("IO exception while reading\\writing into scoket");
				e.printStackTrace();
			}    		
    	}    	
    }
    
    private String getMsgFromStream(InputStream in) throws IOException {
    	byte[] buf = new byte[2048];
        int bufLength = in.read(buf);
        String msg = new String(buf, 0, bufLength);
        
        return msg;
    }
    
    private void sendLogToSocket(Socket s) throws IOException {
    	OutputStream out = s.getOutputStream();
    	
    	for(String entry : log) {
    		out.write((entry + '\n').getBytes());
    	}
    	System.out.println("Log have been send");    	    	
    }
    
    private void writeToLogAndConsole(String msg) {
    	log.add(msg);
    	System.out.println(msg);
    }
    
    private void close() {
		try {
			serverSocket.close();
			for (Socket s : socketPool) {
				s.close();
			}
		} catch (IOException e) {
			System.err.println("Error while closing sockets");
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
    }
}