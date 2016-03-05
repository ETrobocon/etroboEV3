//
// NxtColorSensor.cpp
//

#include "ColorSensor.h"
using namespace ev3api;

//=============================================================================
// Constructor
ColorSensor::ColorSensor(ePortS port)
:Sensor(port, COLOR_SENSOR)
{
    (void)ev3_color_sensor_get_reflect(getPort());
}

//=============================================================================
// Destructor
ColorSensor::~ColorSensor(void)
{
    //uart_sensor_config(getPort(), -1);
}

//=============================================================================
// Get brightness in the light sensor modes.
int8_t ColorSensor::getBrightness(void) const
{
    return ev3_color_sensor_get_reflect(getPort());
}

//=============================================================================
// get color number in the color sensor mode.
colorid_t ColorSensor::getColorNumber(void) const
{
    return ev3_color_sensor_get_color(getPort());
}


