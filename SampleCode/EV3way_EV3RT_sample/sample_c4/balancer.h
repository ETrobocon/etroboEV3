/**
 ******************************************************************************
 **	ファイル名 : balancer.h
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
#ifndef _ETROBO_BALANCER_H_INCLUDED
#define _ETROBO_BALANCER_H_INCLUDED

#ifdef __cplusplus
extern "C" {
#endif

/* Macros for accessing real-time model data structure */
#ifndef rtmGetErrorStatus
# define rtmGetErrorStatus(rtm)        ((void*) 0)
#endif

#ifndef rtmSetErrorStatus
# define rtmSetErrorStatus(rtm, val)   ((void) 0)
#endif

#ifndef rtmGetStopRequested
# define rtmGetStopRequested(rtm)      ((void*) 0)
#endif

#define CMD_MAX                        100.0F                    /* 前進/旋回命令絶対最大値 */
#define DEG2RAD                        0.01745329238F            /* 角度単位変換係数(=pi/180) */
#define EXEC_PERIOD                    0.00400000019F            /* バランス制御実行周期(秒) */

/* Model entry point functions */
extern void balance_init(void);

/* Customized model step function */
extern void balance_control(float args_cmd_forward, float args_cmd_turn,
  float args_gyro, float args_gyro_offset, float args_theta_m_l,
  float args_theta_m_r, float args_battery, signed char *ret_pwm_l, signed char
  *ret_pwm_r);

/*-
 * The generated code includes comments that allow you to trace directly
 * back to the appropriate location in the model.  The basic format
 * is <system>/block_name, where system is the system number (uniquely
 * assigned by Simulink) and block_name is the name of the block.
 *
 * Use the MATLAB hilite_system command to trace the generated code back
 * to the model.  For example,
 *
 * hilite_system('<S3>')    - opens system 3
 * hilite_system('<S3>/Kp') - opens and selects block Kp which resides in S3
 *
 * Here is the system hierarchy for this model
 *
 * '<Root>' : balancer
 * '<S1>'   : balancer/Control
 * '<S2>'   : balancer/Control/Cal_PWM
 * '<S3>'   : balancer/Control/Cal_Reference
 * '<S4>'   : balancer/Control/Cal_x1
 * '<S5>'   : balancer/Control/DiscreteIntegrator
 * '<S6>'   : balancer/Control/Cal_PWM/Cal_vol_max
 * '<S7>'   : balancer/Control/Cal_Reference/DiscreteIntegrator
 * '<S8>'   : balancer/Control/Cal_Reference/LowPathFilter
 * '<S9>'   : balancer/Control/Cal_x1/DiscreteDerivative
 * '<S10>'  : balancer/Control/Cal_x1/DiscreteIntegrator
 * '<S11>'  : balancer/Control/Cal_x1/LowPathFilter
 */
//#endif                                 /* RTW_HEADER_balancer_h_ */

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

#ifdef __cplusplus
}
#endif

#endif /* ! _ETROBO_BALANCER_H_INCLUDED */
