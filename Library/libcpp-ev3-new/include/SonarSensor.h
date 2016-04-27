//
// SonarSensor.h
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#ifndef EV3CPPAPI_SONARSENSOR_H_
#define EV3CPPAPI_SONARSENSOR_H_

#include "Sensor.h"

namespace ev3api {
/**
 * EV3 ソナー(超音波)センサクラス
 */
class SonarSensor: public Sensor
{
public:
    /**
     * コンストラクタ
     * @param port ソナーセンサ接続ポート
     * @return -
     */
    explicit SonarSensor(ePortS port);

    /**
     * デストラクタ
     * @param -
     * @return -
     */
    virtual ~SonarSensor(void);

    /**
     * 距離を測定する
     * @param -
     * @return 距離 [cm]
     */
    int16_t getDistance(void) const;

    /**
     * 超音波信号を検出する
     * @param -
     * @return true 超音波信号を検出した
     * @return false 超音波信号を検出しなかった
     */
    bool listen(void) const;
}; // class SonarSensor
}  // namespace ev3api

#endif // ! EV3CPPAPI_SONARSENSOR_H_
