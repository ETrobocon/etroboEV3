//
// SonarSensor.h
//
//

#ifndef SONARSENSOR_H_
#define SONARSENSOR_H_

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
}; // class SonarSensor
}  // namespace ev3api

#endif
