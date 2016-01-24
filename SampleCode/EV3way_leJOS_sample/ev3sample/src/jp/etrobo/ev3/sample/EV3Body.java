package jp.etrobo.ev3.sample;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

/**
 * EV3のモータとセンサを扱うクラス。
 */
final class EV3Body {
	// モータ制御用オブジェクト
	// EV3LargeRegulatedMotor では PWM 制御ができないので、TachoMotorPort を利用する
	public TachoMotorPort motorPortL; // 左モータ
	public TachoMotorPort motorPortR; // 右モータ
	public TachoMotorPort motorPortT; // 尻尾モータ

	// タッチセンサ
	public EV3TouchSensor touch;
	public SensorMode touchMode;
	public float[] sampleTouch;

	// 超音波センサ
	public EV3UltrasonicSensor sonar;
	public SampleProvider distanceMode;  // 距離検出モード
	public float[] sampleDistance;

	// カラーセンサ
	public EV3ColorSensor colorSensor;
	public SensorMode redMode;           // 輝度検出モード
	public float[] sampleLight;

	// ジャイロセンサ
	public EV3GyroSensor gyro;
	public SampleProvider rate;          // 角速度検出モード
	public float[] sampleGyro;

	/**
	 * コンストラクタ。
	 */
	public EV3Body() {
		motorPortL = MotorPort.C.open(TachoMotorPort.class); // 左モータ
		motorPortR = MotorPort.B.open(TachoMotorPort.class); // 右モータ
		motorPortT = MotorPort.A.open(TachoMotorPort.class); // 尻尾モータ

		// タッチセンサ
		touch = new EV3TouchSensor(SensorPort.S1);
		touchMode = touch.getTouchMode();
		sampleTouch = new float[touchMode.sampleSize()];

		// 超音波センサ
		sonar = new EV3UltrasonicSensor(SensorPort.S2);
		distanceMode = sonar.getDistanceMode(); // 距離検出モード
		sampleDistance = new float[distanceMode.sampleSize()];

		// カラーセンサ
		colorSensor = new EV3ColorSensor(SensorPort.S3);
		redMode = colorSensor.getRedMode();                    // 輝度検出モード
		sampleLight = new float[redMode.sampleSize()];

		// ジャイロセンサ
		gyro = new EV3GyroSensor(SensorPort.S4);
		rate = gyro.getRateMode();                             // 角速度検出モード
		sampleGyro = new float[rate.sampleSize()];
	}
	
	/**
	 * タッチセンサ押下のチェック
	 * @return true ならタッチセンサーが押された。
	 */
	public final boolean touchSensorIsPressed() {
		touchMode.fetchSample(sampleTouch, 0);
		return ((int)sampleTouch[0] != 0);
	}
	
	/**
	 * 超音波センサにより障害物との距離を取得する。
	 * @return 障害物との距離(m)。
	 */
	public final float getSonarDistance() {
		distanceMode.fetchSample(sampleDistance, 0);
		return sampleDistance[0];
	}

	/**
	 * カラーセンサから輝度値を取得する。
	 * @return 輝度値。
	 */
	public final float getBrightness() {
		redMode.fetchSample(sampleLight, 0);
		return sampleLight[0];
	}
	
	/**
	 * ジャイロセンサから角速度を取得する。
	 * @return 角速度。
	 */
	public final float getGyroValue() {
		rate.fetchSample(sampleGyro, 0);
		return sampleGyro[0];
	}
}
