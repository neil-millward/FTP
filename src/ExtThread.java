

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Neil Millward - P13197943
 * 
 * This class is used to handle  client transfers independently from one another, hence this class implementing runnable (threads).
 * 
 * 
 *
 */
public class ExtThread implements Runnable {

	//Server directory
	public static String SERVER_DIR = "C:/TempServer/";
	// Static variable giving a hard coded file size
	public final static int FILE_SIZE = 90022386;
	static int port = 4000;
	static String a;
	
	//Socket with accompanying input/output streams
	public static Socket clientSoc;
	public static InputStream in;
	public static OutputStream out;
	
	//Streams to read and write from file
	public static FileInputStream fis;
	public static BufferedInputStream bis;
	private static FileOutputStream fos;
	private static  BufferedOutputStream bos;
	private static BufferedReader bIn;
	
	//Used for transferring of specific information. i.e read UTF
	private static DataInputStream dIn; 
	private static PrintWriter pOut;
	
	public void run() {
		
			System.out.println("EXT THREAD MSG: EXT THREAD CREATED, THREAD ID: " + Thread.currentThread().getId());
			
			
			System.out.println("EXT THREAD MSG: client socket connection accepted:" + clientSoc.getRemoteSocketAddress().toString());

			
		try {
			 dIn = new DataInputStream(clientSoc.getInputStream());
			//receiving 1 or 2 from client to determine is server is sending or receiving a file
			byte messageType = dIn.readByte();
			if (messageType == 1) {
				System.out.println("preparing to recieve file");
				receiveFile();
				
				

			} else {
				System.out.println("preparing to send file");
				sendFile();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void receiveFile() throws IOException, InterruptedException {
		//GETTING INPUT STREAM OF CLIENT
		in = clientSoc.getInputStream();
		dIn = new DataInputStream(in);
		
		//READING FILE NAME FROM CLIENT
		String fileName = dIn.readUTF();

		System.out.println("EXT THREAD MSG: FILE NAME READ SUCCESS:");
		
		
		int bytesRead = 0;
		int current = 0;
		
		try {
			//CREATING EMPTY FILE OBJECT
			File myFile = new File(SERVER_DIR + fileName);
			
			//BYTE ARRAY FOR STORING SENT FILE DATA FORM CLIENT
			byte[] mybytearray = new byte[FILE_SIZE];
			
			//STREAM INTO FILE OBJECT
			fos = new FileOutputStream(myFile);
			bos = new BufferedOutputStream(fos); // stored in bytearray
			
			
			//READING BYTES OF FILE BEING SENT FROM CLIENT AND STORING IN BYTE ARRAY
			bytesRead = in.read(mybytearray, 0, mybytearray.length);
			
			current = bytesRead;
			do {
				//STORING BYTES FROM CLIENT INTO BYTE ARRAY
				bytesRead = in.read(mybytearray, current, (mybytearray.length - current));
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead > -1);
			
			
			//WRITING BYTES OF FILE SENT INTO FILE OBJECT
			bos.write(mybytearray, 0, current);
			
			System.out.println("EXT THREAD MSG: FILE WRITE SUCCESS!");
			
			//updating server label
			Server.noFileRecievedLabel.setText(fileName+ " Received");
			Server.noFileRecievedLabel.setForeground(Color.GREEN);

			//CLOSING STREAMS 
			bos.flush();
			fos.flush();
			bos.close();
			
			
			System.out.println("File " + myFile + " downloaded (" + current + " bytes read)");
			System.out.println("EXT THREAD MSG: CLIENT SOCKET CLOSING: "+ clientSoc.getPort());
		
			
		
		} catch (IOException e) {
			
		}
		//CLOSING SOCKET
		clientSoc.close();
	
	}

	public static void sendFile() throws IOException, InterruptedException {

		System.out.println("EXT THREAD MSG: READING FILE NAME TO DOWNLOAD FROM CLIENT ");
		out = clientSoc.getOutputStream();
				
		// listing files in directory
		File folder = new File("C:/TempServer");
		File[] listOfFiles = folder.listFiles();
		
		int i = 0;
		a = new String();

		while (i != listOfFiles.length) {

			a += listOfFiles[i].getName() + " " + "\n";
			i++;
			Server.textArea.setText(a);

		}


		//SENDING SERVER FILE LIST TO CLIENT
		pOut = new PrintWriter(clientSoc.getOutputStream(), true);
		pOut.println(a);
		pOut.flush();
		
		// file selected from client
		bIn = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
		
		
		
		//READING FILE NAME FROM CLIENT
		String inputLine = bIn.readLine();


		// GETTING FILE TO SEND
		File myFile = new File(SERVER_DIR + inputLine);
		
		//BYTE ARRAY TO STORE FILE DATA IN PREPARATION TO SEND
		byte[] mybytearray = new byte[(int) myFile.length()];
		
		try{
			//STREAM INTO FILE
		fis = new FileInputStream(myFile);
		}
		catch(FileNotFoundException e){
			return;
		}
		//BUFFERED STREAM WRAPPED OVER FIS
		bis = new BufferedInputStream(fis);
		//READIONG FILE CONTENTS INTO BYTE ARRAY IN PREPARATION FOR SENDING
		bis.read(mybytearray, 0, mybytearray.length);
		
		
		
		System.out.println("Sending " + SERVER_DIR + myFile.getName() + "(" + mybytearray.length + " bytes)");
		
		//OUTPUT STREAM WRITES THE FILE DATA FROM BYTE ARRAY TO THE SERVER
		out.write(mybytearray, 0, mybytearray.length);
		Server.noFileRecievedLabel.setText(inputLine+ " Sent");
		Server.noFileRecievedLabel.setForeground(Color.GREEN);
		
		System.out.println("Done.");
		
		// Flushes this output stream and forces any buffered output bytes
		// to be written out
		out.flush();
		pOut.flush();
		bIn.close();
		bis.close();
		out.close();
		clientSoc.close();


	}
	//Constructor. parameter socket represents a client socket that is passed to handle independent transfers
	public ExtThread(Socket socket) {
		ExtThread.clientSoc = socket;


	}
	
}
