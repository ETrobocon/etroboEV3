using System;
using MonoBrickFirmware;
using MonoBrickFirmware.Display.Dialogs;
using MonoBrickFirmware.Display;
using MonoBrickFirmware.Movement;
using System.Threading;
using MonoBrickFirmware.Sensors;

namespace ETrikeV
{
    class Program
    {
        const int BLACK = 5;
        const int WHITE = 50;

        const int MAX_STEERING_ANGLE = 630;
        const int DRIVING_POWER = 30;

        static void Main(string[] args)
        {
            int grey = (BLACK + WHITE) / 2;
            int light = 0;
            int count = 0;

            EV3TouchSensor touch = new EV3TouchSensor(SensorPort.In2);
            EV3ColorSensor color = new EV3ColorSensor(SensorPort.In3, ColorMode.Reflection);
            Motor steerMotor = new Motor(MotorPort.OutC);
            Motor leftMotor = new Motor(MotorPort.OutA);
            Motor rightMotor = new Motor(MotorPort.OutB);

            steerMotor.ResetTacho();
            leftMotor.ResetTacho();
            rightMotor.ResetTacho();

            //スタート待ち
            InfoDialog dialogSTART = new InfoDialog("Enter to START", true);
            dialogSTART.Show();//Wait for enter to be pressed

            while (!touch.IsPressed())
            {
                light = color.Read();
                count = steerMotor.GetTachoCount();
                if (light > grey)
                {
                    if (count < MAX_STEERING_ANGLE)
                    {
                        steerMotor.SetPower(100);
                    }
                    else
                    {
                        steerMotor.Brake();
                    }
                    leftMotor.SetPower(-1 * DRIVING_POWER);
                    rightMotor.SetPower(1);
                }
                else
                {
                    if (count > -1 * MAX_STEERING_ANGLE)
                    {
                        steerMotor.SetPower(-100);
                    }
                    else
                    {
                        steerMotor.Brake();
                    }
                    leftMotor.SetPower(1);
                    rightMotor.SetPower(-1 * DRIVING_POWER);
                }
                Thread.Sleep(8);
            }

            steerMotor.Off();
            leftMotor.Off();
            rightMotor.Off();

            Lcd.Instance.Clear();
            Lcd.Instance.Update();
        }
    }
}
