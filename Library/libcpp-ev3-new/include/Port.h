//
// Port.h
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#ifndef EV3CPPAPI_PORT_H_
#define EV3CPPAPI_PORT_H_

/**
 * @file Port.h
 * @brief モータ/センサポート関連定義
 */

/**
 * センサポート番号
 */
enum ePortS
{
    PORT_1 = 0, /**< EV3 センサポート1 */
    PORT_2,     /**< EV3 センサポート2 */
    PORT_3,     /**< EV3 センサポート3 */
    PORT_4      /**< EV3 センサポート4 */
};

/**
 * モータポート番号
 */
enum ePortM
{
    PORT_A = 0, /**< EV3 モータポートA */
    PORT_B,     /**< EV3 モータポートB */
    PORT_C,     /**< EV3 モータポートC */
    PORT_D      /**< EV3 モータポートD */
};

/**
 * センサポートに対する電源供給状態
 */
enum ePower
{
    POWER_OFF = 0,          /**< 電源供給なし */
    POWER_LOWSPEED_9V = 1,  /**< 9V電源供給 */
    POWER_LOWSPEED = 2      /**< I2Cデバイス */
};

/** センサポート数 */
#define NUM_PORT_S (4) // number of sensor ports

/** モータポート数 */
#define NUM_PORT_M (4) // number of motor ports

#endif // ! EV3CPPAPI_PORT_H_
