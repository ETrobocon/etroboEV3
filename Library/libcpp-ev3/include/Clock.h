//
// Clock.h
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#ifndef EV3CPPAPI_CLOCK_H_
#define EV3CPPAPI_CLOCK_H_

#include "ev3api.h"

namespace ev3api {
/**
 * EV3 クロッククラス
 */
class Clock
{
public:
    /**
     * コンストラクタ
     * 開始時間をシステムクロックで初期化する
     * @param -
     * @return -
     */
    Clock(void);

    /**
     * リセット
     * 開始時間を現在のシステムクロックでリセットする
     * @param -
     * @return -
     */
    void reset(void);

    /**
     * 経過時間取得
     * 開始時間からの経過時間を取得する
     * @param -
     * @return 経過時間[msec]
     */
    uint32_t now(void) const;

    /**
     * 自タスク遅延
     * @param duration 遅延時間[msec]
     * @return -
     */
    inline void wait(uint32_t duration)
    {
        dly_tsk(duration);
    }

    /**
     * 自タスクスリープ
     * @param duration スリープ時間[msec]
     * @return -
     */
    inline void sleep(uint32_t duration)
    {
        tslp_tsk(duration);
    }

protected:
    /**
     * システムクロック取得
     * @param -
     * @return システムクロック現在値
     */
    static uint32_t getTim();

private:
    uint32_t mStartClock;
}; // class Clock
}  // namespace ev3api

#endif // !EV3CPPAPI_CLOCK_H_
