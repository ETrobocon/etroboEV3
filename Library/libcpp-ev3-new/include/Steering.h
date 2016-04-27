//
// Steering.h
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#ifndef EV3CPPAPI_STEERING_H_
#define EV3CPPAPI_STEERING_H_

#include "Motor.h"

namespace ev3api {
/**
 * ステアリング操作クラス
 */
class Steering
{
public:
    /**
     * コンストラクタ<br>
     * ステアリング操作を行う左右のモータを設定する
     * @param leftMotor 左モータ
     * @param rightMotor 右モータ
     */
    explicit Steering(Motor& leftMotor, Motor& rightMotor);

    /**
     * ステアリング操作を行う<br>
     * 例えば，turnRatio は+25である場合、左モータのパワーは power で，右モータのパワーは power の75%になり、
     * 車体は右へ転回する
     * @param power モータのパワー [-100..+100]<br>マイナスの値は後退
     * @param turnRatio ステアリングの度合い [-100..100]<br>マイナスの値は左への転回、プラスの値は右への転回
     */
    void setPower(int power, int turnRatio);

private:
    Motor& mLeftMotor;
    Motor& mRightMotor;
}; // class Steering
}  // namespace ev3api
#endif // ! EV3CPPAPI_STEERING_H_
