/**
******************************************************************************
** FILE NAME : Balancer.cs
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
** English : http://www.mathworks.com/matlabcentral/fileexchange/19147-nxtway-gs--self-balancing-two-wheeled-robot--controller-design
** Japanese: http://www.cybernet.co.jp/matlab/library/library/detail.php?id=TA060
**
** MODEL INFO:
** MODEL NAME : balancer.mdl
** VERSION : 1.893
** HISTORY : y_yama - Tue Sep 25 11:37:09 2007
** takashic - Sun Sep 28 17:50:53 2008
** Tadashi Huruya - Mon Feb 09 21:36:00 2015
** ported from balancer.c
***
** Copyright (c) 2009-2015 MathWorks, Inc.
** All rights reserved.
******************************************************************************
**/
/**
******************************************************************************
** ファイル名 : Balancer.cs
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
** 日本語: http://www.cybernet.co.jp/matlab/library/library/detail.php?id=TA060
** 英語 : http://www.mathworks.com/matlabcentral/fileexchange/19147-nxtway-gs--self-balancing-two-wheeled-robot--controller-design
** を参照してください。
**
** モデル関連情報:
** モデル名 : balancer.mdl
** バージョン : 1.893
** 履歴 : y_yama - Tue Sep 25 11:37:09 2007
** takashic - Sun Sep 28 17:50:53 2008
** Tadashi Huruya - Mon Feb 09 21:36:00 2015
** balancer.cを移植
**
** Copyright (c) 2009-2015 MathWorks, Inc.
** All rights reserved.
******************************************************************************
**/

using System;
using System.Threading;

using MonoBrickFirmware;
using MonoBrickFirmware.Display.Dialogs;
using MonoBrickFirmware.Display;
using MonoBrickFirmware.Movement;
using MonoBrickFirmware.Sensors;

namespace ETRobocon.EV3
{
	class Balancer
	{
		const float CMD_MAX = 100.0F;                    /* 前進/旋回命令絶対最大値 */
		const float DEG2RAD = 0.01745329238F;            /* 角度単位変換係数(=pi/180) */
		const float EXEC_PERIOD = 0.00400000019F;            /* バランス制御実行周期(秒) */

		const float A_D = 0.8F;    	   /* low pass filter gain for motors average count */
		const float A_R = 0.996F;      /* low pass filter gain for motors target count */
		/* servo control state feedback gain for NXT standard tire */
		static float[] K_F = new float[4] {-0.870303F, -31.9978F, -1.1566F * 0.6F, -2.78873F};
		const float K_I = -0.44721F;   /* servo control integral gain */
		const float K_PHIDOT = 25.0F * 2.5F;  /* turn target speed gain */
		const float K_THETADOT = 7.5F; /* forward target speed gain */

		const float BATTERY_GAIN = 0.001089F;	/* battery voltage gain for motor PWM outputs */
		const float BATTERY_OFFSET = 0.625F;	/* battery voltage offset for motor PWM outputs */

		static float ud_err_theta;          /* 左右車輪の平均回転角度(θ)目標誤差状態値 */
		static float ud_psi;                /* 車体ピッチ角度(ψ)状態値 */
		static float ud_theta_lpf;          /* 左右車輪の平均回転角度(θ)状態値 */
		static float ud_theta_ref;          /* 左右車輪の目標平均回転角度(θ)状態値 */
		static float ud_thetadot_cmd_lpf;   /* 左右車輪の目標平均回転角速度(dθ/dt)状態値 */

		public static void control(float args_cmd_forward, float args_cmd_turn, float
			args_gyro, float args_gyro_offset, float
			args_theta_m_l, float args_theta_m_r, float
			args_battery, out sbyte ret_pwm_l, out sbyte ret_pwm_r)
		{
			float tmp_theta;
			float tmp_theta_lpf;
			float tmp_pwm_r_limiter;
			float tmp_psidot;
			float tmp_pwm_turn;
			float tmp_pwm_l_limiter;
			float tmp_thetadot_cmd_lpf;
			float[] tmp = new float[4];
			float[] tmp_theta_0 = new float[4];
			int tmp_0;

			/* Sum: '<sbyte>/Sum' incorporates:
			 *  Constant: '<S3>/Constant6'
			 *  Constant: '<sbyte>/Constant'
			 *  Constant: '<sbyte>/Constant1'
			 *  Gain: '<S3>/Gain1'
			 *  Gain: '<sbyte>/Gain2'
			 *  Inport: '<Root>/cmd_forward'
			 *  Product: '<S3>/Divide'
			 *  Product: '<sbyte>/Product'
			 *  Sum: '<sbyte>/Sum1'
			 *  UnitDelay: '<sbyte>/Unit Delay'
			 */
			tmp_thetadot_cmd_lpf = (((args_cmd_forward / CMD_MAX) * K_THETADOT) * (1.0F
				- A_R)) + (A_R * ud_thetadot_cmd_lpf);

			/* Gain: '<S4>/Gain' incorporates:
			 *  Gain: '<S4>/deg2rad'
			 *  Gain: '<S4>/deg2rad1'
			 *  Inport: '<Root>/theta_m_l'
			 *  Inport: '<Root>/theta_m_r'
			 *  Sum: '<S4>/Sum1'
			 *  Sum: '<S4>/Sum4'
			 *  Sum: '<S4>/Sum6'
			 *  UnitDelay: '<S10>/Unit Delay'
			 */
			tmp_theta = (((DEG2RAD * args_theta_m_l) + ud_psi) + ((DEG2RAD *
				args_theta_m_r) + ud_psi)) * 0.5F;

			/* Sum: '<S11>/Sum' incorporates:
			 *  Constant: '<S11>/Constant'
			 *  Constant: '<S11>/Constant1'
			 *  Gain: '<S11>/Gain2'
			 *  Product: '<S11>/Product'
			 *  Sum: '<S11>/Sum1'
			 *  UnitDelay: '<S11>/Unit Delay'
			 */
			tmp_theta_lpf = ((1.0F - A_D) * tmp_theta) + (A_D * ud_theta_lpf);

			/* Gain: '<S4>/deg2rad2' incorporates:
			 *  Inport: '<Root>/gyro'
			 *  Inport: '<Root>/gyro_offset'
			 *  Sum: '<S4>/Sum2'
			 */
			tmp_psidot = (args_gyro - args_gyro_offset) * DEG2RAD;

			/* Gain: '<S2>/Gain' incorporates:
			 *  Constant: '<S3>/Constant2'
			 *  Constant: '<S3>/Constant3'
			 *  Constant: '<S6>/Constant'
			 *  Constant: '<S9>/Constant'
			 *  Gain: '<S1>/FeedbackGain'
			 *  Gain: '<S1>/IntegralGain'
			 *  Gain: '<S6>/Gain3'
			 *  Inport: '<Root>/battery'
			 *  Product: '<S2>/Product'
			 *  Product: '<S9>/Product'
			 *  Sum: '<S1>/Sum2'
			 *  Sum: '<S1>/sum_err'
			 *  Sum: '<S6>/Sum2'
			 *  Sum: '<S9>/Sum'
			 *  UnitDelay: '<S10>/Unit Delay'
			 *  UnitDelay: '<S11>/Unit Delay'
			 *  UnitDelay: '<S5>/Unit Delay'
			 *  UnitDelay: '<S7>/Unit Delay'
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

			tmp_pwm_r_limiter = (((K_I * ud_err_theta) + tmp_pwm_r_limiter) /
				((BATTERY_GAIN * args_battery) - BATTERY_OFFSET)) *
				100.0F;

			/* Gain: '<S3>/Gain2' incorporates:
			 *  Constant: '<S3>/Constant1'
			 *  Inport: '<Root>/cmd_turn'
			 *  Product: '<S3>/Divide1'
			 */
			tmp_pwm_turn = (args_cmd_turn / CMD_MAX) * K_PHIDOT;

			/* Sum: '<S2>/Sum' */
			tmp_pwm_l_limiter = tmp_pwm_r_limiter + tmp_pwm_turn;

			/* Saturate: '<S2>/pwm_l_limiter' */
			tmp_pwm_l_limiter = rt_SATURATE(tmp_pwm_l_limiter, -100.0F, 100.0F);

			/* Outport: '<Root>/pwm_l' incorporates:
			 *  DataTypeConversion: '<S1>/Data Type Conversion'
			 */
			ret_pwm_l = (sbyte)tmp_pwm_l_limiter;

			/* Sum: '<S2>/Sum1' */
			tmp_pwm_r_limiter -= tmp_pwm_turn;

			/* Saturate: '<S2>/pwm_r_limiter' */
			tmp_pwm_r_limiter = rt_SATURATE(tmp_pwm_r_limiter, -100.0F, 100.0F);

			/* Outport: '<Root>/pwm_r' incorporates:
			 *  DataTypeConversion: '<S1>/Data Type Conversion6'
			 */
			ret_pwm_r = (sbyte)tmp_pwm_r_limiter;

			/* Sum: '<S7>/Sum' incorporates:
			 *  Gain: '<S7>/Gain'
			 *  UnitDelay: '<S7>/Unit Delay'
			 */
			tmp_pwm_l_limiter = (EXEC_PERIOD * tmp_thetadot_cmd_lpf) + ud_theta_ref;

			/* Sum: '<S10>/Sum' incorporates:
			 *  Gain: '<S10>/Gain'
			 *  UnitDelay: '<S10>/Unit Delay'
			 */
			tmp_pwm_turn = (EXEC_PERIOD * tmp_psidot) + ud_psi;

			/* Sum: '<S5>/Sum' incorporates:
			 *  Gain: '<S5>/Gain'
			 *  Sum: '<S1>/Sum1'
			 *  UnitDelay: '<S5>/Unit Delay'
			 *  UnitDelay: '<S7>/Unit Delay'
			 */
			tmp_pwm_r_limiter = ((ud_theta_ref - tmp_theta) * EXEC_PERIOD) +
				ud_err_theta;

			/* user code (Update function Body) */
			/* System '<Root>' */
			/* 次回演算用状態量保存処理 */

			/* Update for UnitDelay: '<S5>/Unit Delay' */
			ud_err_theta = tmp_pwm_r_limiter;

			/* Update for UnitDelay: '<S7>/Unit Delay' */
			ud_theta_ref = tmp_pwm_l_limiter;

			/* Update for UnitDelay: '<sbyte>/Unit Delay' */
			ud_thetadot_cmd_lpf = tmp_thetadot_cmd_lpf;

			/* Update for UnitDelay: '<S10>/Unit Delay' */
			ud_psi = tmp_pwm_turn;

			/* Update for UnitDelay: '<S11>/Unit Delay' */
			ud_theta_lpf = tmp_theta_lpf;
		}

		/* Model initialize function */
		public static void init()
		{
			/* Registration code */

			/* states (dwork) */

			/* custom states */
			ud_err_theta = 0.0F;
			ud_theta_ref = 0.0F;
			ud_thetadot_cmd_lpf = 0.0F;
			ud_psi = 0.0F;
			ud_theta_lpf = 0.0F;
		}

		private static float rt_SATURATE(float sig, float ll, float ul)
		{
			return (((sig) >= (ul)) ? (ul) : (((sig) <= (ll)) ? (ll) : (sig)) );
		}
	}
}

