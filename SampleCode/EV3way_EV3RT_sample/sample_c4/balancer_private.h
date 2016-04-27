/**
 ******************************************************************************
 **	ファイル名 : balancer_private.h
 **
 ** モデル関連情報:
 **   モデル名   : balancer.mdl
 **   バージョン : 1.893
 **   履歴       : y_yama - Tue Sep 25 11:37:09 2007
 **                takashic - Sun Sep 28 17:50:53 2008
 **
 ** Copyright (c) 2009-2016 MathWorks, Inc.
 ** All rights reserved.
 ******************************************************************************
 **/

/* Imported (extern) block parameters */
extern float A_D;                   /* Variable: A_D
                                        * Referenced by blocks:
                                        * '<S11>/Constant1'
                                        * '<S11>/Gain2'
                                        * ローパスフィルタ係数(左右車輪の平均回転角度用)
                                        */
extern float A_R;                   /* Variable: A_R
                                        * Referenced by blocks:
                                        * '<S8>/Constant1'
                                        * '<S8>/Gain2'
                                        * ローパスフィルタ係数(左右車輪の目標平均回転角度用)
                                        */
extern float K_F[4];                /* Variable: K_F
                                        * '<S1>/FeedbackGain'
                                        * サーボ制御用状態フィードバック係数
                                        */
extern float K_I;                   /* Variable: K_I
                                        * '<S1>/IntegralGain'
                                        * サーボ制御用積分係数
                                        */
extern float K_PHIDOT;              /* Variable: K_PHIDOT
                                        * '<S3>/Gain2'
                                        * 車体の目標平面回転速度(dφ/dt)係数
                                        */
extern float K_THETADOT;            /* Variable: K_THETADOT
                                        * '<S3>/Gain1'
                                        * 左右車輪の平均回転速度(dθ/dt)係数
                                        */
extern const float BATTERY_GAIN;    /* PWM出力算出用バッテリ電圧補正係数 */
extern const float BATTERY_OFFSET;  /* PWM出力算出用バッテリ電圧補正オフセット */

/*======================== TOOL VERSION INFORMATION ==========================*
 * MATLAB 7.7 (R2008b)30-Jun-2008                                             *
 * Simulink 7.2 (R2008b)30-Jun-2008                                           *
 * Real-Time Workshop 7.2 (R2008b)30-Jun-2008                                 *
 * Real-Time Workshop Embedded Coder 5.2 (R2008b)30-Jun-2008                  *
 * Stateflow 7.2 (R2008b)30-Jun-2008                                          *
 * Stateflow Coder 7.2 (R2008b)30-Jun-2008                                    *
 * Simulink Fixed Point 6.0 (R2008b)30-Jun-2008                               *
 *============================================================================*/

/*======================= LICENSE IN USE INFORMATION =========================*
 * matlab                                                                     *
 * real-time_workshop                                                         *
 * rtw_embedded_coder                                                         *
 * simulink                                                                   *
 *============================================================================*/
/******************************** END OF FILE ********************************/
