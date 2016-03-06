//
// TouchSensor.cpp
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#include "TouchSensor.h"
using namespace ev3api;


//=============================================================================
// Constructor
TouchSensor::TouchSensor(ePortS port)
:
Sensor(port, TOUCH_SENSOR)
{}

//=============================================================================
// get Touch Sensor status
bool TouchSensor::isPressed(void) const
{
    return static_cast<bool>(ev3_touch_sensor_is_pressed(getPort()));
}

