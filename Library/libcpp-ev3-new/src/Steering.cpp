//
// Steerng.cpp
//
// Copyright (c) 2016 Embedded Technology Software Design Robot Contest
//

#include "Steering.h"
using namespace ev3api;

//=============================================================================
// Constructor
Steering::Steering(Motor& leftMotor, Motor& rightMotor)
:mLeftMotor(leftMotor), mRightMotor(rightMotor) {}

//=============================================================================
// Cset steering power
void Steering::setPower(int power, int turnRatio)
{
    (void)ev3_motor_steer(mLeftMotor.getPort(), mRightMotor.getPort(), power, turnRatio);
}

