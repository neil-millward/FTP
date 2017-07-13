

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import java.net.UnknownHostException;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/**
 * 
 * @author Neil Millward - P13197943
 * This is the client class. The client will not connect to the server until either the download or upload button are pressed
 * 
 *
 */
public class Client {
	
	//GUI Variables
	private JFrame frame;
	private JTextArea textArea;
	private JButton btnUpload;
	private JButton btnDownload;
	private JButton infoBtn;
	private JTextField txtField;
	private JLabel server;
	private JLabel inputFileName;
	private JLabel lblClient;
	private JLabel statusLabel;
	private JScrollPane sp;
	//Socket variables with corresponding streams
	static Socket socket;
	static InputStream is;
	static OutputStream out;
	
	//File streams for reading and writing to and from file
	static FileOutputStream fos;
	static BufferedOutputStream bos;
	private PrintWriter printWriterOut;
	private DataOutputStream dos;
	
	//Connection variables	
	public final static int port = 4000;
	public final static String ip = "127.0.0.1";
	
	//Used for text area reset
	private String a = "";
	//hard coded file size variable
	public final static int FILE_SIZE = 90022386;
	

	public static void main(String[] args) throws UnknownHostException, IOException {

		try {
			// calls constructor on start
			Client ftpClient = new Client();

			ftpClient.frame.setVisible(true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	public Client() throws UnknownHostException, IOException {
		// SETTING UP JFRAME COMPONENTS
		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("Button.highlight"));
		frame.setBackground(UIManager.getColor("Button.highlight"));
		frame.setBounds(50, 50, 527, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.BLACK);

		// CLIENT LABEL
		lblClient = new JLabel("Client");
		lblClient.setForeground(Color.ORANGE);
		lblClient.setFont(new Font("Khmer OS Content", Font.BOLD, 50));
		lblClient.setBounds(170, 1, 192, 98);
		frame.getContentPane().add(lblClient);

		// INPUT FILE LABEL
		inputFileName = new JLabel("Input file to download:");
		inputFileName.setFont(new Font("Caladea", Font.PLAIN, 15));
		inputFileName.setBounds(130, 90, 192, 98);
		inputFileName.setBackground(Color.ORANGE);
		inputFileName.setForeground(Color.ORANGE);
		frame.getContentPane().add(inputFileName);

		// TEXTFIELD FOR FILE SELECTION
		txtField = new JTextField("test.txt");
		txtField.setForeground(Color.ORANGE);
		txtField.setBackground(Color.DARK_GRAY);
		txtField.setBounds(130, 150, 200, 30);
		txtField.setEnabled(true);
		frame.getContentPane().add(txtField);

		// STATUS LABEL
		statusLabel = new JLabel("Status: ");
		statusLabel.setFont(new Font("Caladea", Font.PLAIN, 15));
		statusLabel.setBounds(100, 160, 500, 98);
		// statusLabel.setSize(statusLabel.getPreferredSize());
		statusLabel.setForeground(Color.ORANGE);
		frame.getContentPane().add(statusLabel);

		// TEXTAREA FOR SERVER FILES
		textArea = new JTextArea();
		textArea.setFont(new Font("Caladea", Font.PLAIN, 20));
		textArea.setBounds(70, 250, 350, 300);
		textArea.setBackground(Color.GRAY);
		textArea.setForeground(Color.ORANGE);
		textArea.setSelectedTextColor(Color.ORANGE);
		textArea.setColumns(10);

		// SCROLL PANE FOR TEXTAREA
		sp = new JScrollPane(textArea);
		sp.setFont(new Font("Caladea", Font.PLAIN, 20));
		sp.setBounds(70, 250, 350, 200);
		sp.setBackground(Color.GRAY);
		sp.setForeground(Color.ORANGE);
		frame.getContentPane().add(sp);

		// SERVER FILES LABEL
		server = new JLabel("Server Files:");
		server.setForeground(Color.ORANGE);
		server.setBounds(frame.getWidth() / 2 - 75, 200, 205, 80);
		frame.getContentPane().add(server);

		// INFORMATION BUTTON
		infoBtn = new JButton("?");
		infoBtn.setForeground(Color.ORANGE);
		infoBtn.setBackground(Color.DARK_GRAY);
		infoBtn.setBounds(0, 0, 50, 50);
		infoBtn.setFont(new Font("Arial", Font.PLAIN, 20));

		frame.getContentPane().add(infoBtn);
		infoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// infoPane = new JOptionPane();
				JOptionPane.showMessageDialog(frame,
						"FTP Client:\nCreated by Neil Millward\nP13197943\n\nHow to use:\n\n"
								+ "Upload:\nClick the upload button. From there navigate to the specified client drive directory: 'c:/ClientDrive/' and select the desired file.\n\nDownload:"
								+ "\nEnter the desired file to download in the provided test box, from there the file will be saved in the clients drive 'c:/ClientDrive/fileSelected'");

			}
		});
		// UPLOAD BUTTON
		btnUpload = new JButton("Upload");
		btnUpload.setForeground(Color.ORANGE);
		btnUpload.setBackground(Color.DARK_GRAY);
		btnUpload.setBounds(100, 90, 105, 33);
		frame.getContentPane().add(btnUpload);
		btnUpload.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					socket = new Socket(ip, port);
					// AQUIRE OUTPUT STREAM TO WRITE TO SERVER
					out = socket.getOutputStream();
					System.out.println("THREADED CLIENT MSG: CLIENTSOCKET CREATED!: " + socket.getLocalPort());
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				// SENDING 1 TO SERVER, USED TO DETERMINE IF FILE SENT OR
				// RECIEVE
				try {
					 dos = new DataOutputStream(socket.getOutputStream());

					 dos.writeByte(1);
					// Send off the data
					 dos.flush();
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				// UPLOAD FILE
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(btnUpload);
				try {

					while (fileChooser.getSelectedFile() == null) {
						fileChooser.setSelectedFile(new File("c:/ClientDrive/test.txt"));

					}

					// GETTING FILE TO SEND
					File myFile = new File("c:/ClientDrive/" + fileChooser.getSelectedFile().getName());
					byte[] byteArray = new byte[(int) myFile.length()];

					// CREATE INPUTS INTO FILE OBJECT
					FileInputStream fis2 = new FileInputStream(myFile);
					BufferedInputStream bis2 = new BufferedInputStream(fis2);

					DataInputStream dis2 = new DataInputStream(bis2);
					// READING BYTES OF DATA FROM FILE AND STORING INTO BYTE
					// ARRAY
					dis2.readFully(byteArray, 0, byteArray.length);
					dis2.close();

					// SENDING FILE NAME TO SERVER
					dos = new DataOutputStream(out);
					dos.writeUTF(myFile.getName());
					dos.flush();

					// SENDING FILE DATA FROM BYTE ARRAY TO SERVER
					out.write(byteArray, 0, byteArray.length);
					
					if (fileChooser.getSelectedFile().getName().contains("test.txt")) {
						statusLabel.setText("Status:");
						statusLabel.setForeground(Color.ORANGE);
					} else {
						statusLabel.setText("Status: "+fileChooser.getSelectedFile().getName()+" uploaded to c:/TempServer/");
						statusLabel.setForeground(Color.GREEN);
					}
					
					

				} catch (IOException e1) {

				}

				finally {
					try {
						System.out.println("THREADED CLIENT MSG: CLIENTSOCKET CLOSING...: " + socket.getLocalPort());
						out.flush();
						socket.close();
						System.out.println("THREADED CLIENT MSG: CLIENTSOCKET CLOSED!: " + socket.getLocalPort());

					} catch (IOException e1) {

						e1.printStackTrace();
					}

				}
			}

		});

		// DOWNLOAD FILE
		btnDownload = new JButton("Download");
		btnDownload.setForeground(Color.ORANGE);
		btnDownload.setBackground(Color.DARK_GRAY);
		btnDownload.setBounds(frame.getWidth() / 2, 90, 105, 33);
		frame.getContentPane().add(btnDownload);
		btnDownload.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					//creating socket and getting input stream for transfer purposes
					socket = new Socket(ip, port);
					is = socket.getInputStream();

					System.out.println("client socket initialised");
				} catch (UnknownHostException e2) {

					e2.printStackTrace();
				} catch (IOException e2) {

					e2.printStackTrace();
				}

				// SENDING 2 TO SERVER, USED TO DETERMINE IF FILE SENT OR
				// RECIEVED
				try {
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
					dOut.writeByte(2);
					dOut.flush(); // Send off the data
					System.out.println("2 sent to server");
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				// RECIEVE SERVER FILES, SET TO TEXT AREA
				try {
					InputStreamReader is2 = new InputStreamReader(socket.getInputStream());
					System.out.println("input stream accepted");
					BufferedReader in = new BufferedReader(is2);
					String inputLine;

					// clearing text area to prevent duplicates after more than
					// 1 download
					a = "";
					while (!(inputLine = in.readLine()).isEmpty()) {

						a += inputLine + "\n";

					}

					textArea.setText(a);

					// SENDING FILE NAME TO SERVER
					printWriterOut = new PrintWriter(socket.getOutputStream(), true);
					printWriterOut.println(txtField.getText());
					printWriterOut.flush();

				} catch (IOException e1) {

					e1.printStackTrace();
				}
				//bytes read
				int bytesRead;
				// current total number of bytes read
				int current = 0;
				
				
			

				try {
					// ByteArray used to store file data
					// given hard coded file size by FILE_SIZE as file is on the
					// server so cant assign filesize.length
					byte[] mybytearray = new byte[FILE_SIZE];

					// CREATE FILE OBJECT TO BE FILLED WITH BYTE ARRAY FILE DATA
					try {
						fos = new FileOutputStream("C:/ClientDrive/" + txtField.getText());
						
					} catch (FileNotFoundException e1) {

						e1.printStackTrace();
					}
					
					bos = new BufferedOutputStream(fos);
				
			
					do {
						//Reading file data from server and storing in byte array 
						bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
						if (bytesRead >= 0)
							current += bytesRead;
						// When bytesRead is -1 (no data left) the loop ends
					} while (bytesRead > -1);
					
					// WRITE BYTES TO CREATED FILE OBJECT
					bos.write(mybytearray, 0, current); 
					bos.flush();
					fos.flush();
					System.out.println("THREADED CLIENT MSG: FILE SUCCESSFULLY WRITTEN! ");

					// USED FOR DUMMY UPLOAD
					if (txtField.getText().contains("test.txt")) {
						statusLabel.setText("Status:");
						statusLabel.setForeground(Color.ORANGE);
					} else {
						//Setting server gui labels
						statusLabel.setText("Status: "+txtField.getText()+" downloaded to c:/ClientDrive/");
						statusLabel.setForeground(Color.GREEN);

					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						//Closing socket and streams upon completion
						fos.close();
						bos.close();
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});

	}

}
