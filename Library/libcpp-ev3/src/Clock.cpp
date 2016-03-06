//
// Clock.cpp
//
// Copyright (c) 2015-2016 Embedded Technology Software Design Robot Contest
//

#include "Clock.h"
using namespace ev3api;


//=============================================================================
// Constructor
Clock::Clock(void)
:mStartClock(getTim())
{}

//=============================================================================
// reset system tick to 0
void Clock::reset(void)
{
    mStartClock = getTim();
}

//=============================================================================
// get tick after app began
uint32_t Clock::now(void) const
{
    return (getTim() - mStartClock);
}

uint32_t Clock::getTim()
{
    SYSTIM time;
    get_tim(&time);
    // We're using a 32-bitter and can assume that we
    // don't need to do any locking here.
    return static_cast<uint32_t>(time);
}
