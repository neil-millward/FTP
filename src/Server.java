import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * 
 * @author Neil Millward - P13197943
 * 
 * This class is the server. The server will constantly listen for client connections. When a client connects (clientSoc = serverSocket.accept();)the newly created client ext socket is passed to an 
 * its own individual thread. This thread is used to handle clients independently from each other in turn making the application concurrent. 
 *
 */

public class Server {
	//GUI variables
	private JFrame frame;
	private JLabel serverFiles;
	static JTextArea textArea;
	private JScrollPane sp;
	private JLabel serverLabel;
	private JLabel statusLabel;
	private JLabel clientConnectionLabel;
	static JLabel receivedFilesLable;
	static JLabel listening;
	private JLabel running;
	static JLabel noFileRecievedLabel;
	private JLabel clientConnectedLabel;
	private JButton infoBtn;
	
	//server and client socket variables
	static ServerSocket serverSocket;
	static Socket clientSoc;
	
	// Static variable giving a hard coded file size
	public final static int FILE_SIZE = 90022386;
	
	//Connection variables
	public final static String ip = "127.0.0.1";
	public final static int port = 4000;
	

	
	
    public static void main(String args[]) throws IOException, InterruptedException {
        
       
        try {
        	//set up server soc, bind to ip and port
        	Server ftpServer = new Server();
        	ftpServer.frame.setVisible(true);
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            System.out.println("MAIN SERVER MSG: ADDRESS AND PORT " +serverSocket.getInetAddress() +":"+serverSocket.getLocalPort());
            
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
            	//Listen for clients
            	System.out.println("MAIN SERVER MSG: LISTENING");
            	clientSoc = serverSocket.accept();
            	listening.setText("Client: "+ clientSoc.getPort()+" connected.");
            	listening.setForeground(Color.GREEN);
               
                System.out.println("MAIN SERVER MSG: CLIENT CONNECTED, EXT SOCKET CREATED: " +clientSoc.getPort());
                
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
           
            System.out.println("MAIN SERVER MSG: PASSING CLIENT EXT SOCKET TO INDEPENDANT THREAD");
            
            //Thread created, passed client socket and initiate run method
            //This handles the concurrent nature of the application as each client has its own ind thread for input/output socket communication
            new Thread(new ExtThread(clientSoc)).start();

          
            System.out.println("MAIN SERVER MSG: EXT THREAD CREATED");
        }
        
    }
    public Server() {

		// GUI setup
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setBackground(Color.BLUE);
		frame.setBounds(50, 50, 527, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// Server label
		serverLabel = new JLabel("Server");
		serverLabel.setForeground(Color.ORANGE);
		serverLabel.setFont(new Font("Khmer OS Content", Font.BOLD, 50));
		serverLabel.setBounds(170, 10, 192, 98);
		frame.getContentPane().add(serverLabel);

		// Status label
		statusLabel = new JLabel("Status : ");
		statusLabel.setForeground(Color.ORANGE);
		statusLabel.setBounds(120, 111, 94, 34);
		frame.getContentPane().add(statusLabel);

		//Client connection label
		clientConnectionLabel = new JLabel("Client connection : ");
		clientConnectionLabel.setForeground(Color.ORANGE);
		clientConnectionLabel.setBounds(80, 157, 208, 39);
		frame.getContentPane().add(clientConnectionLabel);
		
		//Received files label
		receivedFilesLable = new JLabel("Sent/Received files : ");
		receivedFilesLable.setForeground(Color.ORANGE);
		receivedFilesLable.setBounds(80, 208, 192, 33);
		frame.getContentPane().add(receivedFilesLable);

		//Running label
		running = new JLabel("Running");
		running.setFont(new Font("Dialog", Font.BOLD, 18));
		running.setForeground(Color.GREEN);
		running.setBounds(220, 120, 249, 22);
		frame.getContentPane().add(running);
		
		//Listening label
		listening = new JLabel("Listening...");
		listening.setFont(new Font("Dialog", Font.BOLD, 18));
		listening.setForeground(Color.GREEN);
		listening.setBounds(224, 157, 249, 39);
		frame.getContentPane().add(listening);
		
		//No file received label
		noFileRecievedLabel = new JLabel("no file recieved");
		noFileRecievedLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		noFileRecievedLabel.setBounds(224, 208, 221, 33);
		frame.getContentPane().add(noFileRecievedLabel);
		
		//client connected label
		clientConnectedLabel = new JLabel("Client Connected");
		clientConnectedLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		clientConnectedLabel.setBounds(224, 157, 249, 39);
		
		//text area
		textArea = new JTextArea();
		textArea.setFont(new Font("Caladea", Font.PLAIN, 20));
		textArea.setBounds(100, 250, 350, 300);
		textArea.setBackground(Color.GRAY);
		textArea.setForeground(Color.ORANGE);
		frame.getContentPane().add(textArea);
		textArea.setColumns(10);
		
		//scroll pane for text area
		sp = new JScrollPane(textArea);
		sp.setFont(new Font("Caladea", Font.PLAIN, 20));
		sp.setBounds(70, 280, 350, 200);
		sp.setBackground(Color.GRAY);
		sp.setForeground(Color.ORANGE);
		frame.getContentPane().add(sp);
		
		//server file label
		serverFiles = new JLabel("Server Files:");
		serverFiles.setForeground(Color.ORANGE);
		serverFiles.setBounds(frame.getWidth() / 2 - 75, 230, 205, 80);
		frame.getContentPane().add(serverFiles);
		
		//information button
		infoBtn = new JButton("?");
		infoBtn.setForeground(Color.ORANGE);
		infoBtn.setBackground(Color.DARK_GRAY);
		infoBtn.setBounds(0, 0, 50, 50);
		infoBtn.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.getContentPane().add(infoBtn);
		infoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//infoPane = new JOptionPane();
				JOptionPane.showMessageDialog(frame, "FTP Server:\nCreated by Neil Millward\nP13197943\n\nHow to use:\n\n"
						+ "Server directory located in c:/TempServer");

			}});

	}
}