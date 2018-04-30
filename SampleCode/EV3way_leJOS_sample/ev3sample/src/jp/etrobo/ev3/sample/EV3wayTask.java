/*
 *  EV3wayTask.java (for leJOS EV3)
 *  Created on: 2016/02/11
 *  Updated on: 2018/04/30
 *  Copyright (c) 2016-2018 Embedded Technology Software Design Robot Contest
 */
package jp.etrobo.ev3.sample;

import lejos.utility.Delay;

/**
 * EV3way を制御するタスク。
 */
public class EV3wayTask implements Runnable {
    private EV3way body;

    /**
     * コンストラクタ。
     * @param way EV3本体
     */
    public EV3wayTask(EV3way way) {
        body = way;
    }

    /**
     * EV3本体の制御。
     */
    @Override
    public void run() {
        while(true){
            long time = System.currentTimeMillis();
            body.controlDrive();
            body.controlTail(EV3way.TAIL_ANGLE_DRIVE);
            time = System.currentTimeMillis() - time;
            if(time < 4){
                Delay.msDelay(4 - time);
            }
        }
    }
}
