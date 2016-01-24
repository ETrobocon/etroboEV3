package jp.etrobo.ev3.tools;

import java.io.*;
import org.apache.commons.cli.*;
import com.jcraft.jsch.*;
import lejos.ev3.tools.DummyUserInfo;

public class BluetoothSetting {
	private static final String DEFAULT_NAME = "ET000";
	private static final String DEFAULT_PINCODE = "1234";
	private static final String DEFAULT_IPADDRESS = "10.0.1.1";

	private static final String HOME = "/home/root";
	private static final String LEJOS_HOME = HOME + "/lejos/bin";
	private static final String START_BT = LEJOS_HOME + "/startbt";
	private static final String START_BT_ORG = START_BT + ".org";
	private static final String HOSTNAME = "/hostname";
	private static final String SYSTEM_HOSTNAME = "/etc" + HOSTNAME;
	private static final String BTPIN = "/btpin";
	private static final String SYSTEM_BTPIN = "/etc/bluetooth" + BTPIN;

	private static String name = DEFAULT_NAME;
	private static String pincode = DEFAULT_PINCODE;
	private static String ipaddress = DEFAULT_IPADDRESS;
	
	private static Session session;

    private static int execRemote(String command) throws JSchException, IOException {
    	int status = 0;
    	System.out.println("execRemote: " + command);
		ChannelExec channel = (ChannelExec)session.openChannel("exec");
		channel.setCommand(command);
		//OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
   	    channel.connect();
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0) break;
				System.out.print(new String(tmp, 0, i));
			}

			if (channel.isClosed()) {
				status = channel.getExitStatus();
				//System.err.println("status: " + status);
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		}
		channel.disconnect();
		return status;
    }

	public static void main(String[] args) {
		Options opt = new Options();
		opt.addOption("h", false, "Print help for this application");
		opt.addOption("n", true, "Device name");
		opt.addOption("p", true, "Bluetooth pincode");
		opt.addOption("a", true, "IP address of EV3 Brick");
		BasicParser parser = new BasicParser();
		try {
			CommandLine cl = parser.parse(opt,  args);

			if (cl.hasOption('h')) {
				HelpFormatter f = new HelpFormatter();
				f.printHelp("OptionTip", opt);
				System.exit(0);
			}
			if (cl.hasOption('n')) {
				name = cl.getOptionValue("n");
			}
			if (cl.hasOption('p')) {
				pincode = cl.getOptionValue("p");
			}
			if (cl.hasOption('a')) {
				ipaddress = cl.getOptionValue("a");
			}
		
			JSch.setConfig("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();

			session = jsch.getSession("root", ipaddress, 22);
			session.setPassword("");
			UserInfo ui = new DummyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			// デバイス名設定
			execRemote("echo '" + name + "' >" + SYSTEM_HOSTNAME);
			execRemote("hostname " + name);
			execRemote("hciconfig hci0 name " + name);

			// Bluetooth関係のETロボコン拡張機能を有効にする
			execRemote("if [ ! -f " + START_BT_ORG + " ]; then cp -f "
					+ START_BT + " " + START_BT_ORG + "; fi");
			execRemote("echo '#! /bin/sh' >" + START_BT);
			execRemote("echo 'hciconfig hci0 auth' >>" + START_BT);
			execRemote("echo " + START_BT_ORG + " >>" + START_BT);
			execRemote("echo '" + pincode + "' >" + SYSTEM_BTPIN);

			session.disconnect();
		} catch (Exception ex) {
			System.err.println("BluetoothSetting: Failed to remote execution: " + ex);
			System.exit(1);
		}
		System.exit(0);
	}
}
