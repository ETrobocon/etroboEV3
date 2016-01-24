package jp.etrobo.ev3.pcsample;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * 
 */
public class RemoteClient extends Frame implements KeyListener {
	private static final long serialVersionUID = 1630664954341554884L;

	public static final int PORT = 7360;

	public static final int CLOSE = 0;
	public static final int START = 71; // 'g'
	public static final int STOP = 83;  // 's'

	Button btnConnect;
	TextField txtIPAddress;
	TextArea messages;

	private Socket socket;
	private DataOutputStream outStream;

	public RemoteClient(String title, String ip) {
		super(title);
		this.setSize(400, 300);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Ending Warbird Client");
				disconnect();
				System.exit(0);
			}
		});
		buildGUI(ip);
		this.setVisible(true);
		btnConnect.addKeyListener(this);
	}

	public static void main(String args[]) {
		String ip = "10.0.1.1";
		if (args.length > 0) ip = args[0];
		System.out.println("Starting Client...");
		new RemoteClient("LeJOS EV3 Client Sample", ip);
	}

	public void buildGUI(String ip) {
		Panel mainPanel = new Panel(new BorderLayout());
		ControlListener cl = new ControlListener();
		btnConnect = new Button("Connect");
		btnConnect.addActionListener(cl);
		txtIPAddress = new TextField(ip, 16);
		messages = new TextArea("status: DISCONNECTED");
		messages.setEditable(false);
		Panel north = new Panel(new FlowLayout(FlowLayout.LEFT));
		north.add(btnConnect);
		north.add(txtIPAddress);
		Panel center = new Panel(new GridLayout(5, 1));
		center.add(new Label("G to start, S to stop"));
		Panel center4 = new Panel(new FlowLayout(FlowLayout.LEFT));
		center4.add(messages);
		center.add(center4);
		mainPanel.add(north, "North");
		mainPanel.add(center, "Center");
		this.add(mainPanel);
	}
	
	private void sendCommand(int command) {
		messages.setText("status: SENDING command.");
		try {
			outStream.writeInt(command);
			messages.setText("status: Comand SENT");
		} catch(IOException io) {
			messages.setText("status: ERROR Probrems occurred sending data.");
		}
	}
	
	private class ControlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("Connect")) {
				try {
					socket = new Socket(txtIPAddress.getText(), PORT);
					outStream = new DataOutputStream(socket.getOutputStream());
					messages.setText("status: CONNECTED");
					btnConnect.setLabel("Disconnect");
				} catch (Exception exc) {
					messages.setText("status: FAILURE Error establishing connection with EV3.");
					System.out.println("Error" + exc);
				}
			} else if (command.equals("Disconnect")) {
				disconnect();
			}
		}
	}
	
	public void disconnect() {
		try {
			sendCommand(CLOSE);
			socket.close();
			btnConnect.setLabel("Connect");
			messages.setText("status: DISCONNECTED");
		} catch (Exception exc) {
			messages.setText("status: FAILURE Error closing connection with EV3.");
			System.out.println("Error" + exc);
		}
	}
	
	public void keyPressed(KeyEvent e) {
		sendCommand(e.getKeyCode());
		System.out.println("Pressed " + e.getKeyCode());
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent arg0) {}
}
