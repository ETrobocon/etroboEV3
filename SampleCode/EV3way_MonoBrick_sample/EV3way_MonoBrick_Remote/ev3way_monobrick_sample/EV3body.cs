using System;
using System.Net.Sockets;

using MonoBrickFirmware.Movement;
using MonoBrickFirmware.Sensors;

namespace ETTobocon.EV3.Sample
{
	struct EV3body{
		//モータオブジェクト
		public  Motor motorL;
		public  Motor motorR;
		public  Motor motorT;
		//センサーオブジェクト
		public  EV3TouchSensor touch;
		public  EV3UltrasonicSensor sonar;
		public  EV3ColorSensor color;
		public  EV3GyroSensor gyro;

		public static void init(ref EV3body body){
			body.motorL = new Motor (MotorPort.OutC);
			body.motorR = new Motor (MotorPort.OutB);
			body.motorT = new Motor (MotorPort.OutA);
			body.touch = new EV3TouchSensor (SensorPort.In1); 
			body.sonar = new EV3UltrasonicSensor (SensorPort.In2, UltraSonicMode.Centimeter); // return [mm]
			body.color = new EV3ColorSensor (SensorPort.In3, ColorMode.Reflection);
			body.gyro = new EV3GyroSensor (SensorPort.In4,	GyroMode.AngularVelocity);
		}
	}
}

