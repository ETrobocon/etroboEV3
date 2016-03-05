//
// NxtColorSensor.h
//

#ifndef COLORSENSOR_H_
#define COLORSENSOR_H_

#include "Sensor.h"

namespace ev3api {
/**
 * カラーセンサクラス
 */
class ColorSensor :public Sensor
{
public:
    /**
     * コンストラクタ
     * @param port カラーセンサポート番号
     * @return -
     */
    explicit ColorSensor(ePortS port);

    /**
     * デストラクタ
     * @param -
     * @return -
     */
    virtual ~ColorSensor(void);

    /**
     * 反射光の強さを測定する
     * @param -
     * @return 環境光の強さ (0-100)
     */
    int8_t getBrightness(void) const;

    /**
     * 識別した色を取得する
     * @param -
     * @return 識別した色
     */
    colorid_t getColorNumber(void) const;
}; // class ColorSensor
}  // namespace ev3api

#endif
