//
// TouchSensor.h
//

#ifndef TOUCHSENSOR_H_
#define TOUCHSENSOR_H_

#include "Sensor.h"

namespace ev3api {
/**
 * EV3 タッチセンサクラス
 */
class TouchSensor: public Sensor
{
public:
    /**
     * コンストラクタ
     * @param port タッチセンサ接続ポート
     * @return -
     */
    explicit TouchSensor(ePortS port);

    /**
     * タッチセンサ状態取得
     * @param -
     * @retval true  押されている状態
     * @retval false 押されていない状態
     */
    bool isPressed(void) const;
}; // class TouchSensor
}  // namespace etrobo::ev3

#endif
