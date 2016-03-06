//
// Sensor.h
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#ifndef EV3CPPAPI_SENSOR_H_
#define EV3CPPAPI_SENSOR_H_

#include "Port.h"

#include "ev3api.h"

namespace ev3api {
/**
 * A/Dセンサ抽象クラス
 */
class Sensor
{
protected:
    /**
     * センサ接続ポート取得
     * @param -
     * @return センサ接続ポート
     */
    inline sensor_port_t getPort(void) const { return mPort; }

    /**
     * コンストラクタ
     * @param port センサ接続ポート
     * @param type センサ種別
     * @return -
     */
    explicit Sensor(ePortS port, sensor_type_t type)
    :mPort(static_cast<sensor_port_t>(port)), mType(type)
    {
        ev3_sensor_config(getPort(), type);
    }

    /**
     * デストラクタ
     * @param -
     * @return -
     */
    virtual ~Sensor(void) { }

private:
    sensor_port_t mPort;
    sensor_type_t mType;
}; // class Sensor
}  // namespace ev3api

#endif // ! EV3CPPAPI_SENSOR_H_
