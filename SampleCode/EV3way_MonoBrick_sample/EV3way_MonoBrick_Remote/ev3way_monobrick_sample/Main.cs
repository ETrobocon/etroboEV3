using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Diagnostics;

using MonoBrickFirmware.Display.Dialogs;
using MonoBrickFirmware.Display;

using ETRobocon.EV3;

// 2輪倒立振子ライントレースロボットの MonoBrick 用 c# サンプルプログラム。
namespace ETTobocon.EV3.Sample
{
	class MainClass
	{
		//下記のパラメータは個体/環境に合わせて変更する必要があります
		const int GYRO_OFFSET = 0;          //ジャイロセンサオフセット値
		const int LIGHT_BLACK = 0;            //白色の光センサ値
		const int LIGHT_WHITE = 40;           //黒色の光センサ値
		const int SONAR_ALERT_DISTANCE = 300;  //超音波センサによる障害物検知距離 [mm]
		const int TAIL_ANGLE_STAND_UP = 94;   //完全停止時の角度[deg]
		const int TAIL_ANGLE_DRIVE = 3;       //バランス走行時の角度[deg]
		const float P_GAIN = 2.5F;            //完全停止用モータ制御比例係数
		const int PWM_ABS_MAX = 60;           //完全停止用モータ制御PWM絶対最大値
		const int SOCKET_PORT = 7360;
		const int REMOTE_COMMAND_CLOSE = 0;
		const int REMOTE_COMMAND_START = 'g'; // 'g'
		const int REMOTE_COMMAND_STOP  = 's'; // 's'

		public static void Main()
		{
			// 構造体の宣言と初期化
			var body = new EV3body ();
			EV3body.init (ref body);

			// Bluetooth関係のETロボコン拡張機能を有効にする
			Brick.InstallETRoboExt ();
		
			// リモート接続
			NetworkStream connection = connect();

			// センサーおよびモータに対して初回アクセスをしておく
			body.color.Read();
			body.sonar.Read();
			body.gyro.Read ();
			body.motorL.SetPower (0);
			body.motorR.SetPower (0);
			body.motorT.SetPower (0);

			body.motorL.ResetTacho ();
			body.motorR.ResetTacho ();
			body.motorT.ResetTacho ();
			Balancer.init ();

			// スタート待ち
			wait_start(body, connection);

			var dialogRun = new InfoDialog ("Running", false);
			dialogRun.Show ();//Wait for enter to be pressed

			try{
				run(body, connection);
			}catch(Exception){
				var dialogE = new InfoDialog ("Exception.", false);
				dialogE.Show();//Wait for enter to be pressed
			}

			body.motorL.Off ();
			body.motorR.Off ();
			body.motorT.Off ();

			// ソケットを閉じる
			if (connection != null) {
				connection.Close ();
			}

			Lcd.Instance.Clear ();
			Lcd.Instance.Update ();

			if (Debugger.IsAttached) {
				Brick.ExitToMenu (); // MonoBrickメインメニューへ戻る
			}
		}
			
		static NetworkStream connect(){
			// サーバとの接続設定
			NetworkStream connection;
			IPAddress ipAddr = IPAddress.Parse("10.0.1.1");

			var listener = new TcpListener (ipAddr, SOCKET_PORT); 
			listener.Start(); // クライアントからの受信接続要求の待機を開始

			var dialogCON = new InfoDialog ("Please connect...", false);
			dialogCON.Show(); // Wait for enter to be pressed

			try {
				Socket sock = listener.AcceptSocket(); // 接続要求の受け入れ
				connection = new NetworkStream(sock, true);
			} catch (SocketException) {
				var dialogE = new InfoDialog ("Connect is Failed.", false);
				dialogE.Show(); // Wait for enter to be pressed
				connection = null;
			}
			listener.Stop();
			return connection;
		}

		static void wait_start(EV3body body, NetworkStream connection){
			//スタート待ち
			var dialogSTART = new InfoDialog ("Touch to START", false);
			dialogSTART.Show (); // Wait for enter to be pressed

			while (!body.touch.IsPressed()) {
				tail_control(body, TAIL_ANGLE_STAND_UP); //完全停止用角度に制御
				if (checkRemoteCommand(connection, REMOTE_COMMAND_START))
					break;  // PC で 'g' キーが押された
				Thread.Sleep (4);
			}

			while (body.touch.IsPressed ()) {
				tail_control(body, TAIL_ANGLE_STAND_UP); //完全停止用角度に制御
				Thread.Sleep (4);
			}
		}

		static void run(EV3body body, NetworkStream connection){
			// 電圧を取得
			int battery = Brick.GetVoltageMilliVolt();

			sbyte forward;
			sbyte turn;
			int counter = 0;
			bool alert = false;

			while (!body.touch.IsPressed ()) 
			{
				tail_control(body, TAIL_ANGLE_DRIVE); // バランス走行用角度に制御
				if (checkRemoteCommand(connection, REMOTE_COMMAND_STOP)) 
					break; // PC で 's' キー押されたら走行終了

				if (++counter >= 40/4) {
					alert = sonar_alert (body);
					counter = 0;
				}
				if (alert) {
					forward = 0;
					turn = 0;
				} else {
					forward = 50;
					turn = (body.color.Read () >= (LIGHT_BLACK + LIGHT_WHITE) / 2) ? (sbyte)50 : (sbyte)-50;
				}

				int gyroNow = -body.gyro.Read();
				int thetaL = body.motorL.GetTachoCount();
				int theTaR = body.motorR.GetTachoCount();
				sbyte pwmL, pwmR;
				Balancer.control (
					(float)forward, (float)turn, (float)gyroNow, (float)GYRO_OFFSET, (float)thetaL, (float)theTaR, (float)battery,
					out pwmL, out pwmR
				);

				if (pwmL == 0) {
					body.motorL.Brake();
				} else {
					body.motorL.SetPower(pwmL);
				}
				if (pwmR == 0) {
					body.motorR.Brake();
				} else {
					body.motorR.SetPower(pwmR);
				}

				// バランス制御のみだと3msecで安定
				// 尻尾制御と障害物検知を使用する場合2msecで安定
				Thread.Sleep(2);
			}
		}

		/*
		 * 超音波センサによる障害物検知
		 * @return true(障害物あり)/false(障害物無し)
		 */
		static bool sonar_alert(EV3body body)
		{
			int distance = body.sonar.Read();
			if ((distance <= SONAR_ALERT_DISTANCE) && (distance >= 0)){
				return true; /* 障害物を検知 */
			}else{
				return false; /* 障害物無し */
			}
		}

		/*
		 * 走行体完全停止用モータの角度制御
		 * @param angle モータ目標角度[度]
		 */	
		static void tail_control(EV3body body, int angle)
		{
			float pwm = (float)(angle - body.motorT.GetTachoCount ()) * P_GAIN; // 比例制御
			// PWM出力飽和処理
			if (pwm > PWM_ABS_MAX) {
				pwm = PWM_ABS_MAX;
			} else if (pwm < -PWM_ABS_MAX) {
				pwm = -PWM_ABS_MAX;
			}
			if ((sbyte)pwm == 0) {
				body.motorT.Brake();
			} else {
				body.motorT.SetPower((sbyte)pwm);
			}
		}

		/*
    	 * リモートコマンドのチェック	
     	 */
		static bool checkRemoteCommand(NetworkStream connection, int command) 
		{
			try{
				if (connection.DataAvailable) {
					var buff = new byte[4];
					connection.Read(buff, 0, buff.Length);
					// ネットワークバイトオーダー(big endian)で受信したため little endian に変換
					Array.Reverse(buff); // big endian -> little endian

					if (BitConverter.ToInt32(buff,0) == command) {
						return true;
					}
				}
			}catch(Exception){
				return false;
			}
			return false;
		}

	}
}

