/**
******************************************************************************
** FILE NAME : Balancer.java
**
** ABSTRUCT : Two wheeled self-balancing robot "NXTway-GS" balance control program.
** NXTway-GS balance control algorithum is based on modern control theory
** which is servo control (state + integral feedback).
** To develop the controller and indentify the plant, The MathWorks
** MATLAB&Simulink had been used and this design methodology is
** called MBD (Model-Based Design/Development). This C source code
** is automatically generated from a Simulink model by using standard features
** of Real-Time Workshop Embedded Coder. All control parameters need to be defined
** by user and the sample parameters are defined in nxtOSEK\samples\nxtway_gs\balancer_param.c.
** For more detailed information about the controller alogorithum, please check:
** English : http://www.mathworks.com/matl･･･/fileexchange/loadFile.do･･･
** Japanese: http://www.cybernet.co.jp/･･･/library/library/detail.php･･･
**
** MODEL INFO:
** MODEL NAME : balancer.mdl
** VERSION : 1.893
** HISTORY : y_yama - Tue Sep 25 11:37:09 2007
** takashic - Sun Sep 28 17:50:53 2008
** INACHI Minoru - Thu Apr 16 00:10:24 2015 
** ported from balancer.c
**
** Copyright (c) 2009-2016 MathWorks, Inc.
** All rights reserved.
******************************************************************************
**/
/**
******************************************************************************
** ファイル名 : Balancer.java
**
** 概要 : 2輪型倒立振子ロボット「NXTway-GS」バランス制御プログラム
** NXTway-GSのバランス制御には、サーボ制御(状態 + 積分フィードバック)
** という現代制御を適用しています。制御対象の同定および制御器の開発
** にはThe MathWorks社のMATLAB&Simulinkという製品を使用した、
** MBD(モデルベースデザイン/開発)開発手法を用いています。このCプログラムは
** SimulinkモデルからReal-Time Workshop Embedded Coderコード生成標準機能を
** 使用して自動生成されたものです。バランス制御器の制御パラメータについては
** ユーザーハンドコード側で定義する必要があります。定義例として、
** nxtOSEK\samples\nxtway_gs\balancer_param.cを参照してください。
** バランス制御アルゴリズムの詳細情報につきましては
** 日本語: http://www.cybernet.co.jp/･･･/library/library/detail.php･･･
** 英語 : http://www.mathworks.com/matl･･･/fileexchange/loadFile.do･･･
** を参照してください。
**
** モデル関連情報:
** モデル名 : balancer.mdl
** バージョン : 1.893
** 履歴 : y_yama - Tue Sep 25 11:37:09 2007
** takashic - Sun Sep 28 17:50:53 2008
** INACHI Minoru - Thu Apr 16 00:10:24 2015 
** balancer.cを移植
**
** Copyright (c) 2009-2016 MathWorks, Inc.
** All rights reserved.
******************************************************************************
**/
package jp.etrobo.ev3.balancer;

/**
 * Balancer クラス
 */
public class Balancer {
    /*
     * * ローパスフィルタ係数(左右車輪の平均回転角度用)
    */
    private static float A_D = 0.8F;

    /*
     * ローパスフィルタ係数(左右車輪の目標平均回転角度用)
     */
    private static float A_R = 0.996F;

    /*
     * サーボ制御用状態フィードバック係数
     * K_F[0]: 車輪回転角度係数
     * K_F[1]: 車体傾斜角度係数
     * K_F[2]: 車輪回転角速度係数
     * K_F[3]: 車体傾斜角速度係数
     */
    private static float[] K_F = new float[] { -0.870303F, -31.9978F, -1.1566F*0.6F, -2.78873F };

    /*
     * サーボ制御用積分フィードバック係数
     */
    private static float K_I = -0.44721F;

    /*
     * 車体の目標平面回転速度(dφ/dt)係数
     */
    private static float K_PHIDOT = 25.0F*2.5F;

    /*
     * 左右車輪の平均回転速度(dθ/dt)係数
     */
    private static float K_THETADOT = 7.5F;

    /*
     * PWM出力算出用バッテリ電圧補正係数
     */
    private static float BATTERY_GAIN = 0.001089F;

    /*
     * PWM出力算出用バッテリ電圧補正オフセット
     */
    private static float BATTERY_OFFSET = 0.625F;

    /*
     * 前進/旋回命令絶対最大値
     */
    private static final float CMD_MAX = 100.0F;

    /*
     * 角度単位変換係数(=pi/180)
     */
    private static final float DEG2RAD = 0.01745329238F;

    /*
     *  バランス制御実行周期(秒)
     */
    private static final float EXEC_PERIOD = 0.00400000019F;

    private static float ud_err_theta = 0.0F;            /* 左右車輪の平均回転角度(θ)目標誤差状態値 */
    private static float ud_psi = 0.0F;                  /* 車体ピッチ角度(ψ)状態値 */
    private static float ud_theta_lpf = 0.0F;            /* 左右車輪の平均回転角度(θ)状態値 */
    private static float ud_theta_ref = 0.0F;            /* 左右車輪の目標平均回転角度(θ)状態値 */
    private static float ud_thetadot_cmd_lpf = 0.0F;     /* 左右車輪の目標平均回転角速度(dθ/dt)状態値 */

    private static int pwm_l = 0;                        /* 左モータPWM出力値 */
    private static int pwm_r = 0;                        /* 右モータPWM出力値 */

    private static float[] tmp = new float[4];
    private static float[] tmp_theta_0 = new float[4];

    /**
     * ローパスフィルタ係数(左右車輪の平均回転角度用)を取得する。
     * @return ローパスフィルタ係数(左右車輪の平均回転角度用)
     */
    public static final float getAD() {
        return A_D;
    }

    /**
     * ローパスフィルタ係数(左右車輪の平均回転角度用)を設定する。
     * @param a_d ローパスフィルタ係数(左右車輪の平均回転角度用)
     */
    public static final void setAD(float a_d) {
        A_D = a_d;
    }

    /**
     * ローパスフィルタ係数(左右車輪の目標平均回転角度用)を取得する。
     * @return ローパスフィルタ係数(左右車輪の目標平均回転角度用)
     */
    public static final float getAR() {
        return A_R;
    }

    /**
     * ローパスフィルタ係数(左右車輪の目標平均回転角度用)を設定する。
     * @param a_r ローパスフィルタ係数(左右車輪の目標平均回転角度用)
     */
    public static final void setAR(float a_r) {
        A_R = a_r;
    }

    /**
     * サーボ制御用状態フィードバック係数を取得する。
     * @return サーボ制御用状態フィードバック係数 <br>
     *            [0]: 車輪回転角度係数 [1]: 車体傾斜角度係数 [2]: 車輪回転角速度係数 [3]:
     *            車体傾斜角速度係数
     */
    public static final float[] getKF() {
        return K_F;
    }

    /**
     * サーボ制御用状態フィードバック係数を設定する。
     * @param k_f サーボ制御用状態フィードバック係数 <br>
     *            k_f[0]: 車輪回転角度係数 k_f[1]: 車体傾斜角度係数 k_f[2]: 車輪回転角速度係数 k_f[3]:
     *            車体傾斜角速度係数
     */
    public static final void setKF(float[] k_f) {
        K_F = k_f;
    }

    /**
     * サーボ制御用積分係数を取得する。
     * @param k_i サーボ制御用積分係数
     */
    public static final float getKI() {
        return K_I;
    }

    /**
     * サーボ制御用積分係数を設定する。
     * @param k_i サーボ制御用積分係数
     */
    public static final void setKI(float k_i) {
        K_I = k_i;
    }

    /**
     * 車体の目標平面回転速度(dφ/dt)係数を取得する。
     * @return 車体の目標平面回転速度(dφ/dt)係数
     */
    public static final float getKPhidot() {
        return K_PHIDOT;
    }

    /**
     * 車体の目標平面回転速度(dφ/dt)係数を設定する。
     * @param k_phidot 車体の目標平面回転速度(dφ/dt)係数
     */
    public static final void setKPhidot(float k_phidot) {
        K_PHIDOT = k_phidot;
    }

    /**
     * 左右車輪の平均回転速度(dθ/dt)係数を取得する。
     * @return 左右車輪の平均回転速度(dθ/dt)係数
     */
    public static final float getKThetadot() {
        return K_THETADOT;
    }

    /**
     * 左右車輪の平均回転速度(dθ/dt)係数を設定する。
     * @param k_thetadot 左右車輪の平均回転速度(dθ/dt)係数
     */
    public static final void setKThetadot(float k_thetadot) {
        K_THETADOT = k_thetadot;
    }

    /**
     * PWM出力算出用バッテリ電圧補正係数を取得する。
     * @return PWM出力算出用バッテリ電圧補正係数
     */
    public static final float getBatteryGain() {
        return BATTERY_GAIN;
    }

    /**
     * PWM出力算出用バッテリ電圧補正係数を設定する。
     * @param battery_gain PWM出力算出用バッテリ電圧補正係数
     */
    public static final void setBatteryGain(float battery_gain) {
        BATTERY_GAIN = battery_gain;
    }

    /**
     * PWM出力算出用バッテリ電圧補正オフセットを取得する。
     * @return PWM出力算出用バッテリ電圧補正オフセット
     */
    public static final float getBatteryOffset() {
        return BATTERY_OFFSET;
    }

    /**
     * PWM出力算出用バッテリ電圧補正オフセットを設定する。
     * @param battery_offset PWM出力算出用バッテリ電圧補正オフセット
     */
    public static void setBatteryOffset(float battery_offset) {
        BATTERY_OFFSET = battery_offset;
    }

    /**
     * EV3way-ETバランス制御初期化メソッド。
     * 内部状態量変数を初期化します。このメソッドによりバランス制御機能を初期化する場合は、
     * 併せて左右の車輪駆動モーターのエンコーダ値を0にリセットしてください。
     */
    /* Model initialize function */
    public static final void init() {
        /* Registration code */

        /* states (dwork) */

        /* custom states */
        ud_err_theta = 0.0F;
        ud_theta_ref = 0.0F;
        ud_thetadot_cmd_lpf = 0.0F;
        ud_psi = 0.0F;
        ud_theta_lpf = 0.0F;
    }

    /**
     * EV3way-GSバランス制御メソッド。
     * 本メソッド実行後、getPwmL および getPwmR で左右モータPMW出力値を取得します。
     *
     * このメソッドは4msec周期で起動されることを前提に設計されています。 
     * 左右の車輪駆動モータは個体差により、同じPWM出力を与えても回転数が異なる場合が
     * あります。その場合は別途補正機能を追加する必要があります。
     * 
     * @param args_cmd_forward
     *            前進/後進命令。100(前進最大値)～-100(後進最大値)
     * @param args_cmd_turn
     *            旋回命令。100(右旋回最大値)～-100(左旋回最大値)
     * @param args_gyro
     *            ジャイロセンサ値
     * @param args_gyro
     *            ジャイロセンサオフセット値
     * @param args_theta_m_l
     *            左モータエンコーダ値
     * @param args_theta_m_r
     *            右モータエンコーダ値
     * @param args_battery
     *            バッテリ電圧値(mV)
     */
    /* Model step function */
    public static final void control(float args_cmd_forward, float args_cmd_turn,
                                     float args_gyro, float args_gyro_offset, float args_theta_m_l,
                                     float args_theta_m_r, float args_battery) {
        float tmp_theta;
        float tmp_theta_lpf;
        float tmp_pwm_r_limiter;
        float tmp_psidot;
        float tmp_pwm_turn;
        float tmp_pwm_l_limiter;
        float tmp_thetadot_cmd_lpf;
        int tmp_0;

       /*
        * Sum: '<S8>/Sum' incorporates: Constant: '<S3>/Constant6' Constant:
        * '<S8>/Constant' Constant: '<S8>/Constant1' Gain: '<S3>/Gain1' Gain:
        * '<S8>/Gain2' Inport: '<Root>/cmd_forward' Product: '<S3>/Divide'
        * Product: '<S8>/Product' Sum: '<S8>/Sum1' UnitDelay: '<S8>/Unit Delay'
        */
       tmp_thetadot_cmd_lpf = (((args_cmd_forward / CMD_MAX) * K_THETADOT) * (1.0F - A_R))
                              + (A_R * ud_thetadot_cmd_lpf);

        /*
         * Gain: '<S4>/Gain' incorporates: Gain: '<S4>/deg2rad' Gain:
         * '<S4>/deg2rad1' Inport: '<Root>/theta_m_l' Inport: '<Root>/theta_m_r'
         * Sum: '<S4>/Sum1' Sum: '<S4>/Sum4' Sum: '<S4>/Sum6' UnitDelay:
         * '<S10>/Unit Delay'
         */
        tmp_theta = (((DEG2RAD * args_theta_m_l) + ud_psi) + ((DEG2RAD * args_theta_m_r) + ud_psi)) * 0.5F;

        /*
         * Sum: '<S11>/Sum' incorporates: Constant: '<S11>/Constant' Constant:
         * '<S11>/Constant1' Gain: '<S11>/Gain2' Product: '<S11>/Product' Sum:
         * '<S11>/Sum1' UnitDelay: '<S11>/Unit Delay'
         */
        tmp_theta_lpf = ((1.0F - A_D) * tmp_theta) + (A_D * ud_theta_lpf);

        /*
         * Gain: '<S4>/deg2rad2' incorporates: Inport: '<Root>/gyro'
         * Sum: '<S4>/Sum2'
         */
        tmp_psidot = (args_gyro - args_gyro_offset) * DEG2RAD;

        /*
         * Gain: '<S2>/Gain' incorporates: Constant: '<S3>/Constant2' Constant:
         * '<S3>/Constant3' Constant: '<S6>/Constant' Constant: '<S9>/Constant'
         * Gain: '<S1>/FeedbackGain' Gain: '<S1>/IntegralGain' Gain:
         * '<S6>/Gain3' Inport: '<Root>/battery' Product: '<S2>/Product'
         * Product: '<S9>/Product' Sum: '<S1>/Sum2' Sum: '<S1>/sum_err' Sum:
         * '<S6>/Sum2' Sum: '<S9>/Sum' UnitDelay: '<S10>/Unit Delay' UnitDelay:
         * '<S11>/Unit Delay' UnitDelay: '<S5>/Unit Delay' UnitDelay: '<S7>/Unit
         * Delay'
         */
        tmp[0] = ud_theta_ref;
        tmp[1] = 0.0F;
        tmp[2] = tmp_thetadot_cmd_lpf;
        tmp[3] = 0.0F;
        tmp_theta_0[0] = tmp_theta;
        tmp_theta_0[1] = ud_psi;
        tmp_theta_0[2] = (tmp_theta_lpf - ud_theta_lpf) / EXEC_PERIOD;
        tmp_theta_0[3] = tmp_psidot;
        tmp_pwm_r_limiter = 0.0F;
        for (tmp_0 = 0; tmp_0 < 4; tmp_0++) {
            tmp_pwm_r_limiter += (tmp[tmp_0] - tmp_theta_0[tmp_0]) * K_F[(tmp_0)];
        }

        tmp_pwm_r_limiter = (((K_I * ud_err_theta) + tmp_pwm_r_limiter) / ((BATTERY_GAIN * args_battery) - BATTERY_OFFSET)) * 100.0F;

        /*
         * Gain: '<S3>/Gain2' incorporates: Constant: '<S3>/Constant1' Inport:
         * '<Root>/cmd_turn' Product: '<S3>/Divide1'
         */
        tmp_pwm_turn = (args_cmd_turn / CMD_MAX) * K_PHIDOT;

        /* Sum: '<S2>/Sum' */
        tmp_pwm_l_limiter = tmp_pwm_r_limiter + tmp_pwm_turn;

        /* Saturate: '<S2>/pwm_l_limiter' */
        tmp_pwm_l_limiter = rt_SATURATE(tmp_pwm_l_limiter, -100.0F, 100.0F);

        /*
         * Outport: '<Root>/pwm_l' incorporates: DataTypeConversion: '<S1>/Data
         * Type Conversion'
         */
        pwm_l = (int) tmp_pwm_l_limiter;

        /* Sum: '<S2>/Sum1' */
        tmp_pwm_r_limiter -= tmp_pwm_turn;

        /* Saturate: '<S2>/pwm_r_limiter' */
        tmp_pwm_r_limiter = rt_SATURATE(tmp_pwm_r_limiter, -100.0F, 100.0F);

        /*
         * Outport: '<Root>/pwm_r' incorporates: DataTypeConversion: '<S1>/Data
         * Type Conversion6'
         */
        pwm_r = (int) tmp_pwm_r_limiter;

        /*
         * Sum: '<S7>/Sum' incorporates: Gain: '<S7>/Gain' UnitDelay: '<S7>/Unit
         * Delay'
         */
        tmp_pwm_l_limiter = (EXEC_PERIOD * tmp_thetadot_cmd_lpf) + ud_theta_ref;

        /*
         * Sum: '<S10>/Sum' incorporates: Gain: '<S10>/Gain' UnitDelay:
         * '<S10>/Unit Delay'
         */
        tmp_pwm_turn = (EXEC_PERIOD * tmp_psidot) + ud_psi;

        /*
         * Sum: '<S5>/Sum' incorporates: Gain: '<S5>/Gain' Sum: '<S1>/Sum1'
         * UnitDelay: '<S5>/Unit Delay' UnitDelay: '<S7>/Unit Delay'
         */
        tmp_pwm_r_limiter = ((ud_theta_ref - tmp_theta) * EXEC_PERIOD) + ud_err_theta;

        /* user code (Update function Body) */
        /* System '<Root>' */
        /* 次回演算用状態量保存処理 */

        /* Update for UnitDelay: '<S5>/Unit Delay' */
        ud_err_theta = tmp_pwm_r_limiter;

        /* Update for UnitDelay: '<S7>/Unit Delay' */
        ud_theta_ref = tmp_pwm_l_limiter;

        /* Update for UnitDelay: '<S8>/Unit Delay' */
        ud_thetadot_cmd_lpf = tmp_thetadot_cmd_lpf;

        /* Update for UnitDelay: '<S10>/Unit Delay' */
        ud_psi = tmp_pwm_turn;

        /* Update for UnitDelay: '<S11>/Unit Delay' */
        ud_theta_lpf = tmp_theta_lpf;
    }

    /**
     * 左モータPMW出力値取得
     * 
     * @return 100(前進最大値)～-100(後進最大値)
     */
	/* left motor PWM output */
	public static final int getPwmL() {
		return pwm_l;
	}

    /**
     * 右モータPMW出力値取得
     * 
     * @return 100(前進最大値)～-100(後進最大値)
     */
    /* right motor PWM output */
    public static final int getPwmR() {
        return pwm_r;
    }

    /* rt_SATURATE.h */
    private static final float rt_SATURATE(float sig, float ll, float ul) {
        return (((sig) >= (ul)) ? (ul) : (((sig) <= (ll)) ? (ll) : (sig)));
    }
}
