/*
 * ETrikeV firmware
 *
 * app.c for TOPPERS/HRP2
 */

#include "ev3api.h"
#include "app.h"

#if defined(BUILD_MODULE)
#include "module_cfg.h"
#else
#include "kernel_cfg.h"
#endif

#define BLACK  5
#define WHITE 50

#define MAX_STEERING_ANGLE	630
#define DRIVING_POWER		 30

#define STEER	EV3_PORT_C
#define DRIVE_L	EV3_PORT_A
#define DRIVE_R	EV3_PORT_B
#define IR		EV3_PORT_3
#define TOUCH	EV3_PORT_2

void main_task(intptr_t unused) {
	int grey = (BLACK+WHITE)/2;
	int light = 0;
	int count = 0;

    // Configure motors
    ev3_motor_config(STEER, LARGE_MOTOR);
    ev3_motor_config(DRIVE_L, LARGE_MOTOR);
    ev3_motor_config(DRIVE_R, LARGE_MOTOR);

    // Configure sensors
    ev3_sensor_config(IR, COLOR_SENSOR);
    ev3_sensor_config(TOUCH, TOUCH_SENSOR);

    ev3_led_set_color(LED_GREEN);

	ev3_motor_reset_counts(STEER);
	ev3_motor_reset_counts(DRIVE_L);
	ev3_motor_reset_counts(DRIVE_R);

	while(!ev3_touch_sensor_is_pressed(TOUCH));
	tslp_tsk(500); /* 500msec wait */

	while(!ev3_button_is_pressed(BACK_BUTTON))
	{
		light = ev3_color_sensor_get_reflect(IR);
		count = ev3_motor_get_counts(STEER);
		if(light>grey){
			if(count<MAX_STEERING_ANGLE){
				ev3_motor_set_power(STEER, 100);
			}else{
				ev3_motor_stop(STEER, true);
			}
			ev3_motor_set_power(DRIVE_L, -DRIVING_POWER);
			ev3_motor_set_power(DRIVE_R,   1);
		}else{
			if(count>-MAX_STEERING_ANGLE){
				ev3_motor_set_power(STEER, -100);
			}else{
				ev3_motor_stop(STEER, true);
			}
			ev3_motor_set_power(DRIVE_L,   1);
			ev3_motor_set_power(DRIVE_R, -DRIVING_POWER);
		}

		tslp_tsk(10); /* 10msec wait */
	}

	ev3_motor_stop(STEER, false);
	ev3_motor_stop(DRIVE_L, false);
	ev3_motor_stop(DRIVE_R, false);

    ext_tsk();
}
