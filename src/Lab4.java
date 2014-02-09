import lejos.nxt.*;
import lejos.util.Timer;

public class Lab4 {

	public static void main(String[] args) {
		
		int buttonChoice;
		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		
		//Timer LCDTimer = new Timer(100, lcd);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left  |  Right >", 0, 0);
			LCD.drawString("        |         ", 0, 1);
			LCD.drawString(" Falling|  Rising ", 0, 2);
			LCD.drawString(" Edge   |  Edge   ", 0, 3);
 
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		

		if (buttonChoice == Button.ID_LEFT) 
		{	//falling edge US localization
			//LCDTimer.start();
			
			LCD.clear();
			LCDInfo lcd = new LCDInfo(odo);
			USLocalizer usl = new USLocalizer (odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
	    	LightLocalizer lsl = new LightLocalizer(odo, ls);
			lsl.doLocalization();
			
		} else {
			//Rising edge US localization
			//LCDTimer.start();
			USLocalizer usl = new USLocalizer (odo, us, USLocalizer.LocalizationType.RISING_EDGE);
			usl.doLocalization();
	    	LightLocalizer lsl = new LightLocalizer(odo, ls);
			lsl.doLocalization();

		}
		
		
		// perform the light sensor localization
    	//LightLocalizer lsl = new LightLocalizer(odo, ls);
		//lsl.doLocalization();			
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
		//Button.waitForPress();
	}

}
