import lejos.nxt.LightSensor;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	private Navigation navigator;
	
	
	
	private int lightReading;
	private int lineCount;
	private boolean lineCrossed;
	
	public static double FWD_SPEED = 5, ROTATION_SPEED = 30;
	public static double LS_RAD = 10; //LightSensor radius
	public static double LS_THRES= 7;
	public static double ROTATION_ANGLE = 90;
	
	
	
	
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.navigator = odo.getNavigation();
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		
	}
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
