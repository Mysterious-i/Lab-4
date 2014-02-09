import lejos.nxt.LightSensor;
import lejos.nxt.Sound;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	private Navigation navigator;
	
	private double xCord1 ,xCord2, yCord1,yCord2;
	private double curX , curY;
	private double deltaX, deltaY;
	
	//the value read by the lightSensor
	private int lightReading;
	//the counter for crossing the lines
	private int lineCount;
	
	private boolean lineCrossed;
	//the distance from light sensor to the center of rotation
	private final int  LS_TO_C = 12;
	public static double FWD_SPEED = 5, ROTATION_SPEED = 30;
	public static double LS_RAD = 10; //LightSensor radius
	public static double LS_THRES= 7;
	public static double ROTATION_ANGLE = 90;
	
	
	
	
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.navigator = odo.getNavigation();
		
		lightReading = 0;
		lineCount = 0;
		lineCrossed = false;
	
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		boolean pathFinished = false;
		double angle;
		
		robot.setSpeeds(FWD_SPEED, ROTATION_SPEED);
		
		while(lineCount<4){
			lightReading = ls.getLightValue();
			if( !lineCrossed && lightReading <=50){
				lineCount++;
				lineCrossed = true;
				
				switch (lineCount) {
				
				case 1:
					yCord1 = odo.getAng();
					Sound.beep();
					break;
				case 2:
					xCord1 = odo.getAng();
					Sound.beep();
					break;
				case 3:
					yCord2 = odo.getAng();
					Sound.beep();
					break;
				case 4:
					xCord2 = odo.getAng();
					Sound.beep();
					break;
				default:
					
					
				}
			}
			
			if(lightReading > 50) 
				lineCrossed =false;
		}
		//stop the robot
		robot.stop();
		//get the change in heading for x axis and y axis 
		deltaX = xCord1-xCord2;
		deltaY = yCord1-yCord2;
		
		//get the current coord
		curX = -LS_TO_C*Math.cos(Math.toRadians(deltaX) /2);
		curY = -LS_TO_C*Math.cos(Math.toRadians(deltaY) /2);
		
		//turn to 0 deg
		navigator.turnTo(0, true);
		//update the position
		odo.setPosition(new double[] {curX, curY, 0}, new boolean[] {true, true, true});
		
		//go to point (0,0)
		navigator.travelTo(0, 0);
		
		//turn back to angle 0 heading
		navigator.turnTo(0, true);
		
		//update the position 
		
		odo.setPosition(new double[] {0, 0, 0}, new boolean[] {true, true, true});
	}
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
