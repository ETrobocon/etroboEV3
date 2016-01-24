package jp.etrobo.ev3.sample;

import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * ETrikeV の leJOS EV3 用 Java サンプルプログラム。
 */
public class ETrileVSample {

	// 下記のパラメータはセンサ個体/環境に合わせてチューニングする必要があります
	private static final float LIGHT_WHITE = 0.22F; // 白色の光センサ値
	private static final float LIGHT_BLACK = 0.0F; // 黒色の光センサ値
	private static final float THRESHOLD = (LIGHT_WHITE+LIGHT_BLACK)/2.0F;
	private static final int MAX_STEERING_ANGLE	= 630;
	private static final int DRIVING_POWER = 30;

	// モータ制御用オブジェクト
	private static TachoMotorPort motorPortDriveL = MotorPort.A.open(TachoMotorPort.class);
	private static TachoMotorPort motorPortDriveR = MotorPort.B.open(TachoMotorPort.class);
	private static TachoMotorPort motorPortSteer  = MotorPort.C.open(TachoMotorPort.class);

	// カラーセンサー
	private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
	private static SensorMode redMode = colorSensor.getRedMode();
	private static float[] sampleLight = new float[redMode.sampleSize()];

	// タッチセンサー
	private static EV3TouchSensor touch = new EV3TouchSensor(SensorPort.S2);
	private static SensorMode touchMode = touch.getTouchMode();
	private static float sampleTouch[] = new float[touchMode.sampleSize()];
	
	public static void main(String[] args) {
		motorPortDriveL.setPWMMode(BasicMotorPort.PWM_BRAKE);
		motorPortDriveR.setPWMMode(BasicMotorPort.PWM_BRAKE);
		motorPortSteer.setPWMMode(BasicMotorPort.PWM_BRAKE);
		motorPortDriveL.controlMotor(0, 0);
		motorPortDriveR.controlMotor(0, 0);
		motorPortSteer.controlMotor(0, 0);
		motorPortDriveL.resetTachoCount();
		motorPortDriveR.resetTachoCount();
		motorPortSteer.resetTachoCount();

		float light = 0.0F;
		int count = 0;

		// スタート待ち
		LCD.drawString("Touch to START", 0, 4);
		boolean touchPressed = false;
		for (;;) {
			if (touchSensorIsPressed()) {
				touchPressed = true;
			} else {
				if (touchPressed) break;
			}
			Delay.msDelay(10);
		}

		LCD.drawString("Running       ", 0, 4);

		while (true) {
			if (touchSensorIsPressed()) break;
			light = getBrightness();
			count = motorPortSteer.getTachoCount();
			if  (light > THRESHOLD) {
				if (count < MAX_STEERING_ANGLE) {
					motorPortSteer.controlMotor(100, 1);
				}else{
					motorPortSteer.controlMotor(0, 1);
				}
				motorPortDriveL.controlMotor(-DRIVING_POWER, 1);
				motorPortDriveR.controlMotor(1, 1);
			}else{
				if (count >- MAX_STEERING_ANGLE){
					motorPortSteer.controlMotor(-100, 1);
				}else{
					motorPortSteer.controlMotor(0, 1);
				}
				motorPortDriveL.controlMotor(1, 1);
				motorPortDriveR.controlMotor(-DRIVING_POWER, 1);
			}
			Delay.msDelay(10); /* 10msec wait */
		}
		motorPortDriveL.close();
		motorPortDriveR.close();
		motorPortSteer.close();
		colorSensor.setFloodlight(false);
	}

	/*
	 * カラーセンサーから輝度値の取得
	 */
	private static final float getBrightness() {
		redMode.fetchSample(sampleLight, 0);
		return sampleLight[0];
	}

	/*
	 *  タッチセンサー押下のチェック
	 */
	private static final boolean touchSensorIsPressed() {
		touchMode.fetchSample(sampleTouch, 0);
		return ((int)sampleTouch[0] != 0);
	}
}
