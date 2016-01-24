package jp.etrobo.ev3.sample;

import jp.etrobo.ev3.balancer.Balancer;
import lejos.hardware.port.*;
import lejos.hardware.Battery;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import java.net.*;
import java.io.*;

/**
 * 2輪倒立振子ライントレースロボットの leJOS EV3 用 Java サンプルプログラム。
 */
public class EV3waySample {
	// 下記のパラメータはセンサ個体/環境に合わせてチューニングする必要があります
	private static final float GYRO_OFFSET = 0.0F; //ジャイロセンサオフセット値
	private static final float LIGHT_WHITE = 0.4F; // 白色のカラーセンサ輝度値
	private static final float LIGHT_BLACK = 0.0F; // 黒色のカラーセンサ輝度値
	private static final float SONAR_ALERT_DISTANCE = 0.3F; // 超音波センサによる障害物検知距離[m]
	private static final int TAIL_ANGLE_STAND_UP = 94;   // 完全停止時の角度[度]
	private static final int TAIL_ANGLE_DRIVE    = 3;    // バランス走行時の角度[度]
	private static final float P_GAIN            = 2.5F; // 完全停止用モータ制御比例係数
	private static final int PWM_ABS_MAX         = 60;   // 完全停止用モータ制御PWM絶対最大値
	private static final int SOCKET_PORT = 7360;
	private static final int REMOTE_COMMAND_START = 71; // 'g'
	private static final int REMOTE_COMMAND_STOP  = 83; // 's'

	// ライントレースの閾値
	private static final float THRESHOLD = (LIGHT_WHITE+LIGHT_BLACK)/2.0F;

	/**
	 * メイン
	 */
	public static void main(String[] args) {
		EV3Body body = new EV3Body();

		// リモート接続
		LCD.drawString("Please connect...", 0, 4);
		ServerSocket server;
		Socket client;
		InputStream inputStream;
		DataInputStream dataInputStream;
		try {
			server = new ServerSocket(SOCKET_PORT);
			client = server.accept();
			inputStream = client.getInputStream();
			dataInputStream = new DataInputStream(inputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			server = null;
			dataInputStream = null;
		}

		float forward =  0.0F; // 前後進命令
		float turn    =  0.0F; // 旋回命令

		LCD.drawString("Please Wait...  ", 0, 4);
		body.gyro.reset();
		body.sonar.enable();
		body.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
		body.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
		body.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

		// Java の初期実行性能が悪く、倒立振子に十分なリアルタイム性が得られない。
		// 走行によく使うメソッドについて、HotSpot がネイティブコードに変換するまで空実行する。
		// HotSpot が起きるデフォルトの実行回数は 1500。
		for (int i=0; i < 1500; i++) {
			body.motorPortL.controlMotor(0, 0);
			body.getBrightness();
			body.getSonarDistance();
			body.getGyroValue();
			Battery.getVoltageMilliVolt();
			Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
		}
		Delay.msDelay(10000);       // 別スレッドで HotSpot が完了するだろう時間まで待つ。

		body.motorPortL.controlMotor(0, 0);
		body.motorPortR.controlMotor(0, 0);
		body.motorPortT.controlMotor(0, 0);
		body.motorPortL.resetTachoCount();   // 左モータエンコーダリセット
		body.motorPortR.resetTachoCount();   // 右モータエンコーダリセット
		body.motorPortT.resetTachoCount();   // 尻尾モータエンコーダリセット
		Balancer.init();            // 倒立振子制御初期化

		// スタート待ち
		LCD.drawString("Touch to START", 0, 4);
		boolean touchPressed = false;
		for (;;) {
			tailControl(body, TAIL_ANGLE_STAND_UP); // 完全停止用角度に制御
			if (body.touchSensorIsPressed()) {
				touchPressed = true;          // タッチセンサが押された
			} else {
				if (touchPressed) break;      // タッチセンサが押された後に放した
			}
			if (checkRemoteCommand(dataInputStream, REMOTE_COMMAND_START)) break;  // PC で 'g' キーが押された
			Delay.msDelay(4);
		}

		LCD.drawString("Running       ", 0, 4);
		int counter = 0;
		boolean alert = false;
		for (;;) {
			tailControl(body, TAIL_ANGLE_DRIVE); // バランス走行用角度に制御

			if (body.touchSensorIsPressed()) break; // タッチセンサが押されたら走行終了
			if (checkRemoteCommand(dataInputStream, REMOTE_COMMAND_STOP)) break; // PC で 's' キー押されたら走行終了

			if (++counter >= 40/4) {      // 約40msごとに障害物検知
				alert = sonarAlert(body); // 障害物検知
				counter = 0;
			}
			if (alert) {           // 障害物を検知したら停止
				forward = 0.0F;
				turn = 0.0F;
			} else {
				forward = 50.0F;  // 前進命令
				if (body.getBrightness() > THRESHOLD) {
					turn = 50.0F;  // 右旋回命令
				} else {
					turn = -50.0F; // 左旋回命令
				}
			}

			float gyroNow = body.getGyroValue();              // ジャイロセンサー値
			int thetaL = body.motorPortL.getTachoCount();     // 左モータ回転角度
			int thetaR = body.motorPortR.getTachoCount();     // 右モータ回転角度
			int battery = Battery.getVoltageMilliVolt(); // バッテリー電圧[mV]
			Balancer.control (forward, turn, gyroNow, GYRO_OFFSET, thetaL, thetaR, battery); // 倒立振子制御
			body.motorPortL.controlMotor(Balancer.getPwmL(), 1); // 左モータPWM出力セット
			body.motorPortR.controlMotor(Balancer.getPwmR(), 1); // 右モータPWM出力セット
			Delay.msDelay(2);  // だいたい 4ms/ループ となるようにウェイト
		}

		body.motorPortL.close();
		body.motorPortR.close();
		body.motorPortT.close();
		body.colorSensor.setFloodlight(false);
		body.sonar.disable();
		if (server != null) {
			try { server.close(); } catch (IOException ex) {}
		}
	}

	/*
	 * 超音波センサによる障害物検知
	 * @return true(障害物あり)/false(障害物無し)
	 */
	private static final boolean sonarAlert(EV3Body body) {
		float distance = body.getSonarDistance();
		if ((distance <= SONAR_ALERT_DISTANCE) && (distance >= 0)) {
			return true;  // 障害物を検知
		}
		return false;
	}

	/*
	 * 走行体完全停止用モータの角度制御
	 * @param angle モータ目標角度[度]
	 */	
	private static final void tailControl(EV3Body body, int angle) {
		float pwm = (float)(angle - body.motorPortT.getTachoCount()) * P_GAIN; // 比例制御
		// PWM出力飽和処理
		if (pwm > PWM_ABS_MAX) {
			pwm = PWM_ABS_MAX;
		} else if (pwm < -PWM_ABS_MAX) {
			pwm = -PWM_ABS_MAX;
		}
		body.motorPortT.controlMotor((int)pwm, 1);
	}

    /*
     * リモートコマンドのチェック	
     */
	private static final boolean checkRemoteCommand(DataInputStream dataInputStream, int command) {
		if (dataInputStream == null) return false;
		try {
			if (dataInputStream.available() > 0) {
				int cmd = dataInputStream.readInt();
				if (cmd == command) {
					return true;
				}
			}
			return false;
		} catch (IOException ex) {
			return false;
		}
	}		
}
