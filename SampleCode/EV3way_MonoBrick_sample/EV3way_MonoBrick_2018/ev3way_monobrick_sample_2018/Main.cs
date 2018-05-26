using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Diagnostics;

using MonoBrickFirmware.Display.Dialogs;
using MonoBrickFirmware.Display;

using ETRobocon.EV3;

// 2輪倒立振子ライントレースロボットの MonoBrick 用 c# サンプルプログラム。
namespace ETRobocon.EV3.Sample
{
	class MainClass
	{
		//下記のパラメータは個体/環境に合わせて変更する必要があります
		const int GYRO_OFFSET = 0;          //ジャイロセンサオフセット値
		const int LIGHT_BLACK = 0;            //白色の光センサ値
		const int LIGHT_WHITE = 40;           //黒色の光センサ値
		const int TAIL_ANGLE_STAND_UP = 94;   //完全停止時の角度[deg]
		const int TAIL_ANGLE_DRIVE = 3;       //バランス走行時の角度[deg]
		const float P_GAIN = 2.5F;            //完全停止用モータ制御比例係数
		const int PWM_ABS_MAX = 60;           //完全停止用モータ制御PWM絶対最大値

		public static void Main()
		{
			// 構造体の宣言と初期化
			var body = new EV3body ();
			EV3body.init (ref body);

			// Bluetooth関係のETロボコン拡張機能を有効にする
			Brick.InstallETRoboExt ();
		
			// センサーおよびモータに対して初回アクセスをしておく
			body.color.Read();
			body.gyro.Read ();
			body.motorL.SetPower (0);
			body.motorR.SetPower (0);
			body.motorT.SetPower (0);

			body.motorL.ResetTacho ();
			body.motorR.ResetTacho ();
			body.motorT.ResetTacho ();
			Balancer.init ();

			// スタート待ち
			wait_start(body);

			try{
				run(body);
			}catch(Exception){
				var dialogE = new InfoDialog ("Exception.");
				dialogE.Show();//Wait for enter to be pressed
			}

			body.motorL.Off ();
			body.motorR.Off ();
			body.motorT.Off ();

			Lcd.Clear ();
			Lcd.Update ();

			if (Debugger.IsAttached) {
				Brick.ExitToMenu (); // MonoBrickメインメニューへ戻る
			}
		}
			
		static void wait_start(EV3body body){
			//スタート待ち
			while (!body.touch.IsPressed ()) {
				tail_control(body, TAIL_ANGLE_STAND_UP);
				Thread.Sleep (4);
			}
		}

		static void run(EV3body body){
			// 電圧を取得
			int battery = Brick.GetVoltageMilliVolt();

			sbyte forward;
			sbyte turn;

            sbyte oldPwmL = 0, oldPwmR = 0;

            while (true) 
			{
				tail_control(body, TAIL_ANGLE_DRIVE); // バランス走行用角度に制御

				forward = 50;
				turn = (body.color.Read () >= (LIGHT_BLACK + LIGHT_WHITE) / 2) ? (sbyte)50 : (sbyte)-50;

				int gyroNow = body.gyro.Read();
				int theTaL = body.motorL.GetTachoCount();
				int theTaR = body.motorR.GetTachoCount();

                // バックラッシュをキャンセル
                Balancer.backlash_cancel(oldPwmL, oldPwmR, ref theTaL, ref theTaR);

                sbyte pwmL, pwmR;
				Balancer.control (
					(float)forward, (float)turn, (float)gyroNow, (float)GYRO_OFFSET, (float)theTaL, (float)theTaR, (float)battery,
					out pwmL, out pwmR
				);

                oldPwmL = pwmL;
                oldPwmR = pwmR;

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
				Thread.Sleep(1);
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
	}
}

