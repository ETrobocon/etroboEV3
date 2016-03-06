//
// ColorSensor.h
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#ifndef EV3CPPAPI_COLORSENSOR_H_
#define EV3CPPAPI_COLORSENSOR_H_

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
     * 環境光の強さを測定する
     * @param -
     * @return 環境光の強さ（0〜100）
     */
    uint8_t getAmbient(void) const;

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

    /**
     * RGB Raw値を測定する
     * @param rgb 取得した値を格納する変数のポインタ
     * @return -
     */
    void getRawColor(rgb_raw_t& rgb) const;
}; // class ColorSensor
}  // namespace ev3api

#endif // ! EV3CPPAPI_COLORSENSOR_H_
