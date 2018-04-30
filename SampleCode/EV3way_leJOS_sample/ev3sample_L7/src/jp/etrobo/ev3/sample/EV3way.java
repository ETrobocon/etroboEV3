/*
 *  EV3way.java (for leJOS EV3)
 *  Created on: 2016/02/11
 *  Copyright (c) 2016 Embedded Technology Software Design Robot Contest
 */
package jp.etrobo.ev3.sample;

import jp.etrobo.ev3.balancer.Balancer;
import lejos.hardware.Battery;
import lejos.hardware.port.BasicMotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

/**
 * EV3way本体のモータとセンサーを扱うクラス。
 */
public class EV3way {
    public static final int   TAIL_ANGLE_STAND_UP   = 94;   // 完全停止時の角度[度]
    public static final int   TAIL_ANGLE_DRIVE      = 3;    // バランス走行時の角度[度]

    // 下記のパラメータはセンサ個体/環境に合わせてチューニングする必要があります
    private static final Port  MOTORPORT_LWHEEL     = MotorPort.C;    // 左モータポート
    private static final Port  MOTORPORT_RWHEEL     = MotorPort.B;    // 右モータポート
    private static final Port  MOTORPORT_TAIL       = MotorPort.A;    // 尻尾モータポート
    private static final Port  SENSORPORT_TOUCH     = SensorPort.S1;  // タッチセンサーポート
    private static final Port  SENSORPORT_SONAR     = SensorPort.S2;  // 超音波センサーポート
    private static final Port  SENSORPORT_COLOR     = SensorPort.S3;  // カラーセンサーポート
    private static final Port  SENSORPORT_GYRO      = SensorPort.S4;  // ジャイロセンサーポート
    private static final float GYRO_OFFSET          = 0.0F;           // ジャイロセンサーオフセット値
    private static final float LIGHT_WHITE          = 0.4F;           // 白色のカラーセンサー輝度値
    private static final float LIGHT_BLACK          = 0.0F;           // 黒色のカラーセンサー輝度値
    private static final float SONAR_ALERT_DISTANCE = 0.3F;           // 超音波センサーによる障害物検知距離[m]
    private static final float P_GAIN               = 2.5F;           // 完全停止用モータ制御比例係数
    private static final int   PWM_ABS_MAX          = 60;             // 完全停止用モータ制御PWM絶対最大値
    private static final float THRESHOLD = (LIGHT_WHITE+LIGHT_BLACK)/2.0F;  // ライントレースの閾値

    // モータ制御用オブジェクト
    // EV3LargeRegulatedMotor では PWM 制御ができないので、TachoMotorPort を利用する
    public TachoMotorPort motorPortL; // 左モータ
    public TachoMotorPort motorPortR; // 右モータ
    public TachoMotorPort motorPortT; // 尻尾モータ

    // タッチセンサ
    private EV3TouchSensor touch;
    private SensorMode touchMode;
    private float[] sampleTouch;

    // 超音波センサ
    private EV3UltrasonicSensor sonar;
    private SampleProvider distanceMode;  // 距離検出モード
    private float[] sampleDistance;

    // カラーセンサ
    private EV3ColorSensor colorSensor;
    private SensorMode redMode;           // 輝度検出モード
    private float[] sampleLight;

    // ジャイロセンサ
    private EV3GyroSensor gyro;
    private SampleProvider rate;          // 角速度検出モード
    private float[] sampleGyro;

    private int         driveCallCounter = 0;
    private boolean     sonarAlert   = false;

    /**
     * コンストラクタ。
     */
    public EV3way() {
        motorPortL = MOTORPORT_LWHEEL.open(TachoMotorPort.class); // 左モータ
        motorPortR = MOTORPORT_RWHEEL.open(TachoMotorPort.class); // 右モータ
        motorPortT = MOTORPORT_TAIL.open(TachoMotorPort.class);   // 尻尾モータ
        motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
        motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
        motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

        // タッチセンサー
        touch = new EV3TouchSensor(SENSORPORT_TOUCH);
        touchMode = touch.getTouchMode();
        sampleTouch = new float[touchMode.sampleSize()];

        // 超音波センサー
        sonar = new EV3UltrasonicSensor(SENSORPORT_SONAR);
        distanceMode = sonar.getDistanceMode(); // 距離検出モード
        sampleDistance = new float[distanceMode.sampleSize()];
        sonar.enable();

        // カラーセンサー
        colorSensor = new EV3ColorSensor(SENSORPORT_COLOR);
        redMode = colorSensor.getRedMode();     // 輝度検出モード
        sampleLight = new float[redMode.sampleSize()];

        // ジャイロセンサー
        gyro = new EV3GyroSensor(SENSORPORT_GYRO);
        rate = gyro.getRateMode();              // 角速度検出モード
        sampleGyro = new float[rate.sampleSize()];
    }

    /**
     * 走行関連メソッドの空回し。
     * Java の初期実行性能が悪く、倒立振子に十分なリアルタイム性が得られない。
     * そのため、走行によく使うメソッドについて、HotSpot がネイティブコードに変換するまで空実行する。
     * HotSpot が起きるデフォルトの実行回数は 1500。
     */
    public void idling() {
        for (int i=0; i < 1500; i++) {
            motorPortL.controlMotor(0, 0);
            getBrightness();
            getSonarDistance();
            getGyroValue();
            Battery.getVoltageMilliVolt();
            Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
        }
        Delay.msDelay(10000);       // 別スレッドで HotSpot が完了するだろう時間まで待つ。
    }

    /**
     * センサー、モータ、倒立振子ライブラリのリセット。
     */
    public void reset() {
        gyro.reset();
        motorPortL.controlMotor(0, 0);
        motorPortR.controlMotor(0, 0);
        motorPortT.controlMotor(0, 0);
        motorPortL.resetTachoCount();   // 左モータエンコーダリセット
        motorPortR.resetTachoCount();   // 右モータエンコーダリセット
        motorPortT.resetTachoCount();   // 尻尾モータエンコーダリセット
        Balancer.init();                // 倒立振子制御初期化
    }

    /**
     * センサー、モータの終了処理。
     */
    public void close() {
        motorPortL.close();
        motorPortR.close();
        motorPortT.close();
        colorSensor.setFloodlight(false);
        sonar.disable();
    }

    /**
     * タッチセンサー押下のチェック。
     * @return true ならタッチセンサーが押された。
     */
    public final boolean touchSensorIsPressed() {
        touchMode.fetchSample(sampleTouch, 0);
        return ((int)sampleTouch[0] != 0);
    }

    /**
     * 走行制御。
     */
    public void controlDrive() {
        if (++driveCallCounter >= 40/4) {  // 約40msごとに障害物検知
            sonarAlert = alertObstacle();  // 障害物検知
            driveCallCounter = 0;
        }
        float forward =  0.0F; // 前後進命令
        float turn    =  0.0F; // 旋回命令
        if (sonarAlert) {           // 障害物を検知したら停止
            forward = 0.0F;
            turn = 0.0F;
        } else {
            forward = 50.0F;  // 前進命令
            if (getBrightness() > THRESHOLD) {
                turn = 50.0F;  // 右旋回命令
            } else {
                turn = -50.0F; // 左旋回命令
            }
        }

        float gyroNow = getGyroValue();                 // ジャイロセンサー値
        int thetaL = motorPortL.getTachoCount();        // 左モータ回転角度
        int thetaR = motorPortR.getTachoCount();        // 右モータ回転角度
        int battery = Battery.getVoltageMilliVolt();    // バッテリー電圧[mV]
        Balancer.control (forward, turn, gyroNow, GYRO_OFFSET, thetaL, thetaR, battery); // 倒立振子制御
        motorPortL.controlMotor(Balancer.getPwmL(), 1); // 左モータPWM出力セット
        motorPortR.controlMotor(Balancer.getPwmR(), 1); // 右モータPWM出力セット
    }

    /**
     * 走行体完全停止用モータの角度制御
     * @param angle モータ目標角度[度]
     */	
    public void controlTail(int angle) {
        float pwm = (float)(angle - motorPortT.getTachoCount()) * P_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        motorPortT.controlMotor((int)pwm, 1);
    }

    /*
     * 超音波センサーによる障害物検知
     * @return true(障害物あり)/false(障害物無し)
     */
    private boolean alertObstacle() {
        float distance = getSonarDistance();
        if ((distance <= SONAR_ALERT_DISTANCE) && (distance >= 0)) {
            return true;  // 障害物を検知
        }
        return false;
    }

    /*
     * 超音波センサーにより障害物との距離を取得する。
     * @return 障害物との距離(m)。
     */
    private final float getSonarDistance() {
        distanceMode.fetchSample(sampleDistance, 0);
        return sampleDistance[0];
    }

    /*
     * カラーセンサーから輝度値を取得する。
     * @return 輝度値。
     */
    private final float getBrightness() {
        redMode.fetchSample(sampleLight, 0);
        return sampleLight[0];
    }

    /*
     * ジャイロセンサーから角速度を取得する。
     * @return 角速度。
     */
    private final float getGyroValue() {
        rate.fetchSample(sampleGyro, 0);
        // leJOS ではジャイロセンサーの角速度値が正負逆になっているので、
        // 倒立振子ライブラリの仕様に合わせる。
        return -sampleGyro[0];
    }
}
