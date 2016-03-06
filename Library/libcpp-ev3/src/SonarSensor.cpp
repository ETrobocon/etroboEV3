//
// SonarSensor.cpp
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#include "SonarSensor.h"
using namespace ev3api;


//=============================================================================
// Constructor
SonarSensor::SonarSensor(ePortS port)
:
Sensor(port, ULTRASONIC_SENSOR)
{
    (void)ev3_ultrasonic_sensor_get_distance(getPort());
}

//=============================================================================
// Destructor
SonarSensor::~SonarSensor(void)
{
    //uart_sensor_config(getPort(), 3);
}

//=============================================================================
// get distance in cm
int16_t SonarSensor::getDistance(void) const
{
    return ev3_ultrasonic_sensor_get_distance(getPort());
}

//=============================================================================
// listen sonar signal
bool SonarSensor::listen(void) const
{
    return ev3_ultrasonic_sensor_listen(getPort());
}

