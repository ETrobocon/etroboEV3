//Brick class (get battery voltage, power off ,reboot, exit and change hostname )
// by jtFuruhata 2014

using System;
using System.IO;
using MonoBrickFirmware.Native;

namespace ETRobocon.EV3
{
	public class Brick
	{
		protected const int ANALOG_SIZE = 32;
		protected const int ANALOG_BAT_CUR_OFF = 28;
		protected const int ANALOG_BAT_V_OFF = 30;

		protected const float AMP_CIN = 22.0f;
		protected const float VCE = 0.05f;
		protected const float AMP_VIN = 0.5f;

		protected const float ADC_REF = 5.0f; // 5.0 Volts
		protected const int ADC_RES = 4095;

		protected const string HOME = "/home/root";
		protected const string LEJOS_HOME = HOME + "/lejos/bin";
		protected const string START_NETWORK = LEJOS_HOME + "/startnetwork";
		protected const string START_NETWORK_ORG = START_NETWORK + ".org";
		protected const string START_BT = LEJOS_HOME + "/startbt";
		protected const string START_BT_ORG = START_BT + ".org";
		protected const string MONO_HOME = "/usr/local/bin";
		protected const string HOSTNAME = "/hostname";
		protected const string SYSTEM_HOSTNAME = "/etc" + HOSTNAME;
		protected const string LEJOS_HOSTNAME = LEJOS_HOME + HOSTNAME;
		protected const string BTPIN = "/btpin";
		protected const string SYSTEM_BTPIN = "/etc/bluetooth" + BTPIN;
		protected const string LEJOS_BTPIN = LEJOS_HOME + BTPIN;

		protected static Brick Instance;
		protected UnixDevice dev;
		protected MemoryArea batteryMem;

		static Brick ()
		{
			Brick.Instance = new Brick ();
		}

		protected Brick ()
		{
			this.dev = new UnixDevice ("/dev/lms_analog");
			this.batteryMem = this.dev.MMap (ANALOG_SIZE, 0);
		}
			
		protected static short getBatteryCurrentRaw ()
		{
			return System.BitConverter.ToInt16 (Brick.Instance.batteryMem.Read (ANALOG_BAT_CUR_OFF, 2), 0);
		}

		protected static short getBatteryVoltageRaw ()
		{
			return System.BitConverter.ToInt16 (Brick.Instance.batteryMem.Read (ANALOG_BAT_V_OFF, 2), 0);
		}

		protected static float convert (int val)
		{
			return (float)val * ADC_REF / ADC_RES;
		}

		protected static int shell (string fileName, string arguments = "")
		{
			return MonoBrickFirmware.Native.ProcessHelper.RunAndWaitForProcess (fileName, arguments);
		}

		/// <summary>
		/// バッテリの電源電圧をボルト単位で取得する
		/// </summary>
		/// <returns>電圧(V)</returns>
		public static float GetVoltage ()
		{
			float CinV = convert(Brick.getBatteryCurrentRaw())/AMP_CIN;
			return convert(Brick.getBatteryVoltageRaw())/AMP_VIN + CinV + VCE;
		}

		/// <summary>
		/// バッテリの電源電圧をミリボルト単位で取得する
		/// </summary>
		/// <returns>電圧(mV)</returns>
		public static int GetVoltageMilliVolt ()
		{
			return (int)(Brick.GetVoltage()*1000f);
		}

		/// <summary>
		/// EV3インテリジェントブリックの電源を切る
		/// </summary>
		public static void Poweroff ()
		{
			MonoBrickFirmware.Native.ProcessHelper.StartProcess ("halt", "-p");
		}

		/// <summary>
		/// EV3インテリジェントブリックを再起動する
		/// </summary>
		public static void Reboot ()
		{
			MonoBrickFirmware.Native.ProcessHelper.StartProcess ("reboot");
		}

		/// <summary>
		/// ユーザアプリを終了し、MonoBrickメインメニューへ戻る
		/// </summary>
		public static void ExitToMenu ()
		{
			MonoBrickFirmware.Native.ProcessHelper.StartProcess (MONO_HOME + "/mono", 
				"--full-aot " + MONO_HOME + "/StartupApp.exe");
		}

		/// <summary>
		/// EV3インテリジェントブリックのホスト名を取得する
		/// </summary>
		/// <returns>ホスト名</returns>
		public static string GetName ()
		{
			return MonoBrickFirmware.Native.ProcessHelper.RunAndWaitForProcessWithOutput ("hostname");
		}

		/// <summary>
		/// EV3インテリジェントブリックのホスト名およびBluetoothデバイス名を設定する
		/// </summary>
		/// <param name="name">設定するホスト名（デバイス名）</param>
		public static void SetName (string name)
		{
			FileStream fs = File.Create (LEJOS_HOSTNAME);
			StreamWriter sw = new StreamWriter (fs);
			sw.WriteLine (name);
			sw.Close ();
			fs.Close ();
			Brick.shell ("cp", " -f " + LEJOS_HOSTNAME + " " +SYSTEM_HOSTNAME);
			Brick.shell ("hostname", name);
			Brick.shell ("hciconfig", "hci0 name " + name);
		}

		/// <summary>
		/// Bluetooth関係のETロボコン拡張機能を有効にする
		/// </summary>
		public static void InstallETRoboExt ()
		{
			if (!File.Exists (START_NETWORK_ORG)) {
				Brick.shell ("cp", "-f " + START_NETWORK + " " + START_NETWORK_ORG);
			}
			FileStream fs = File.Create (START_NETWORK);
			StreamWriter sw = new StreamWriter (fs);
			sw.WriteLine ("#! /bin/sh");
			sw.WriteLine ("HOSTNAME=`cat " + LEJOS_HOSTNAME + "`");
			sw.WriteLine ("cp -f " + LEJOS_HOSTNAME + " " +SYSTEM_HOSTNAME);
			sw.WriteLine ("hostname ${HOSTNAME}");
			sw.WriteLine (START_NETWORK_ORG);
			sw.Close ();
			fs.Close ();

			if (!File.Exists (START_BT_ORG)) {
				Brick.shell ("cp", "-f " + START_BT + " " + START_BT_ORG);
			}
			fs = File.Create (START_BT);
			sw = new StreamWriter (fs);
			sw.WriteLine ("#! /bin/sh");
			sw.WriteLine ("cp -f " + LEJOS_BTPIN + " " + SYSTEM_BTPIN);
			sw.WriteLine ("hciconfig hci0 auth");
			sw.WriteLine (START_BT_ORG);
			sw.Close ();
			fs.Close ();

			if (!File.Exists (LEJOS_BTPIN)) {
				fs = File.Create (LEJOS_BTPIN);
				sw = new StreamWriter (fs);
				sw.WriteLine ("1234");
				sw.Close ();
				fs.Close ();
			}

			if (!File.Exists (LEJOS_HOSTNAME)) {
				fs = File.Create (LEJOS_HOSTNAME);
				sw = new StreamWriter (fs);
				sw.WriteLine ("EV3");
				sw.Close ();
				fs.Close ();
			}
		}
	}
}

