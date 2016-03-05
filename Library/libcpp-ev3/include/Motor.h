//
// Motor.h
//
// Copyright 2009 by Takashi Chikamasa, Jon C. Martin and Robert W. Kramer
//

#ifndef MOTOR_H_
#define MOTOR_H_

#include "Port.h"
#include "ev3api.h"

namespace ev3api {
/**
 * EV3 モータクラス
 */
class Motor
{
public:
    /**
     * PWM最大値
     */
    static const int PWM_MAX = 100;

    /**
     * PWM最小値
     */
    static const int PWM_MIN = -100;

    /**
     * コンストラクタ
     * @param port  モータポート番号
     * @param brake true:ブレーキモード/false:フロートモード
     * @param type  モータタイプ 
     * @return -
     */
    explicit Motor(ePortM port, bool brake = true, motor_type_t type = LARGE_MOTOR);

    /**
     * デストラクタ<br>
     * モータを停止する
     * @param -
     * @return -
     */
    ~Motor(void);

    /**
     * モータリセット<br>
     * モータ停止および角位置の0リセット、オフセット初期化を行う
     * @param -
     * @return -
     */
    inline void reset(void)
    {
        ev3_motor_stop(mPort, true); // need to set brake to stop the motor immidiately
        ev3_motor_reset_counts(mPort);
        mOffset = 0;
    }

    /**
     * オフセット付き角位置取得
     * @param -
     * @return モータ角位置 [deg]
     */
    inline int32_t getCount(void) const { return ev3_motor_get_counts(mPort) - mOffset; }

    /**
     * 角位置設定<br>
     * 現在のモータ角位置と設定角位置との差分をオフセットとして設定する
     * @param count モータ角位置 [deg]
     * @return -
     */
    inline void setCount(int32_t count) { mOffset = ev3_motor_get_counts(mPort) - count; }

    /**
     * モータPWM値設定<br>
     * ブレーキモードでPWM値が0の場合、モータを停止する
     * @param pwm PWM値 (-100 - 100)
     * @return -
     */
    void setPWM(int pwm);

    /**
     * ブレーキモード設定
     * @param brake true:ブレーキモード/false:フロートモード
     * @return -
     */
    void setBrake(bool brake);

protected:
    /**
     * モータ接続ポート取得
     * @param -
     * @return モータ接続ポート
     */
    inline motor_port_t getPort(void) const { return mPort; }

    /**
     * ブレーキ設定取得
     * @param -
     * @retval true  ブレーキモード
     * @retval false フロートモード
     */
    inline bool getBrake(void) const { return mBrake; }

    /**
     * PWM設定値取得
     * @param -
     * @return PWM設定値
     */
    inline int getPWM(void) const { return mPWM; }

private:
    motor_port_t mPort;
    bool mBrake;
    motor_type_t mType;
    int mPWM;
    int32_t mOffset;
}; // class Motor
}  // namespace ev3api

#endif
