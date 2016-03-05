//
// GyroSensor.cpp
//
// Copyright 2009 by Takashi Chikamasa, Jon C. Martin and Robert W. Kramer
//

#include "GyroSensor.h"
using namespace ev3api;


//=============================================================================
// Constructor
GyroSensor::GyroSensor(ePortS port)
:
Sensor(port, GYRO_SENSOR),
mOffset(DEFAULT_OFFSET)
{
    ev3_gyro_sensor_reset(getPort());
    (void)ev3_gyro_sensor_get_rate(getPort());
}

//=============================================================================
// set sensor offset data at 0 [deg/sec]
void GyroSensor::setOffset(int16_t offset)
{
    mOffset = (offset>1023)? 1023:((offset<0)? 0:offset);
}


//=============================================================================
// Reset gyro sensor.
void GyroSensor::reset(void)
{
    ev3_gyro_sensor_reset(getPort());
}

//=============================================================================
// get anguler velocity [deg/sec]
int16_t GyroSensor::getAnglerVelocity(void) const
{
    return ev3_gyro_sensor_get_rate(getPort()) - mOffset;
};

