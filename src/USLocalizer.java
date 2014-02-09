import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
	private final int WALL_DIST_MAX = 71;
	private final int d = 45;
	private final int k = 6;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private int numberOfReadings = 0;
	private Navigation navigation;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		this.navigation = odo.getNavigation();
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA = 0, angleB = 0, angleMid = 0;
		double angleA2 = 0, angleB2 = 0, angleMid2 = 0;

		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			
			 robot.setRotationSpeed(ROTATION_SPEED);
			 
			//Turn the robot until you dont see the wall anymore
			lookUntilNoWall();
			
			//Turn the robot until it detects a wall
			lookUntilWall();
			
			//Update the odometer position and angle
			odo.getPosition(pos);
			angleA = pos[2];
			Sound.beep();
			
			//Turn the robot until you are at a certain distance  
			do{
				odo.getPosition(pos);
				angleB = pos[2];
			}while(getFilteredData() >= d - k);
				
			//Calculate the middle of the two previous angles calculated
			angleMid = (angleA + angleB) / 2;
			
			//Start moving the robot in the opposite direction
			robot.setRotationSpeed(-1*ROTATION_SPEED);
			
			//Turn the robot until you dont see the wall anymore
			lookUntilNoWall();
			
			//Turn the robot until it detects a wall
			lookUntilWall();
			
			//Update the odometer position and angle
			odo.getPosition(pos);		
			angleA2 = pos[2];
			Sound.beep();
			
			//Turn the robot until you are at a certain distance  
			do{
				odo.getPosition(pos);
				angleB2 = pos[2];
			}while(getFilteredData() >= d - k);
			
			//Calculate the middle of the two previous angles calculated
			angleMid2 = (angleA2 + angleB2) / 2;
			
			robot.setSpeeds(0,0);
			
            if(angleMid < angleMid2){
                navigation.turnTo(-235 + (angleMid + angleMid2)/2, true);
            }else{
                navigation.turnTo(-55 + (angleMid + angleMid2)/2, true);
            } 
			// keep rotating until the robot sees a wall, then latch the angle
			
			// switch direction and wait until it sees no wall
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			//navigation.travelTo(15, 15);
		} 
		else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			//
			// FILL THIS IN
			//
			int  counter = 0;            // the counter is used to filter the data from the ultrasonic sensor
	            robot.setRotationSpeed(ROTATION_SPEED);
	              
	            // keep rotating until the robot faces a wall
	            lookUntilWall();
	            
	            lookUntilNoWall();
	            
                odo.getPosition(pos);
                angleB = pos[2];
                Sound.beep();   
                
                robot.setRotationSpeed(-ROTATION_SPEED);

	            do {
	                odo.getPosition(pos);
	                angleA = pos[2];
	            } while (getFilteredData() >= 35);
	              
	            angleMid = (angleA + angleB)/2;
	              
	            try { Thread.sleep(2000); } catch (InterruptedException e) {}
	            counter = 0;
	              
	            lookUntilWall();
	            lookUntilNoWall();
	            
	            odo.getPosition(pos);
	            angleB2 = pos[2];
	            Sound.beep();   
	            robot.setRotationSpeed(ROTATION_SPEED);
	            
	            do {
	                odo.getPosition(pos);
	                angleA2 = pos[2];
	            } while (getFilteredData() >= 35);
	              
	              
	            angleMid2 = (angleA2 + angleB2)/2;

	            robot.setSpeeds(0, 0);
	      
	            // not facing the wall
	            if(angleMid2 < angleMid){
	                navigation.turnTo(-222 + (angleMid + angleMid2)/2, true);
	            // facing the wall
	            }else{
	                navigation.turnTo(-46 + (angleMid + angleMid2)/2, true);
	            }           
	              
	            // update the odometer position (example to follow:)
	            odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
	        }
			
			//navigation.travelTo(0, 25);
		
	        // next we need to move the robot to a position in which it can perform light localization
	        // start by facing the wall to the left
	        // using the ultrasonic sensor, position the robot at a distance of about 26 cm away from the wall
	         navigation.turnTo(-90, true);
	         while (getFilteredData() >= 27 || getFilteredData() <= 25) {
	            if (getFilteredData() >= 27) {
	                robot.setSpeeds(5, 0);
	            } else if (getFilteredData() <= 25) {
	                robot.setSpeeds(-5, 0);
	            } else {
	                robot.setSpeeds(0, 0);
	            }
	         }
	           
	         // then face the wall in the back
	         // using the ultrasonic sensor, position the robot at a distance of about 26 cm away from the wall
	         navigation.turnTo(180, true);
	         while (getFilteredData() >= 27 || getFilteredData() <= 25) {
	            if (getFilteredData() >= 27) {
	                robot.setSpeeds(5, 0);
	            } else if (getFilteredData() <= 25) {
	                robot.setSpeeds(-5, 0);
	            } else {
	                robot.setSpeeds(0, 0);
	            }
	         }
	           
	         // turn to 45 degrees to avoid confusion with the light sensor (black line)
	         navigation.turnTo (45, true); 
		}
	
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
				
		return distance;
	}
	public void lookUntilNoWall(){
		boolean seeWall = true;
		while(seeWall){
			if(getFilteredData() > d + 3*k && numberOfReadings > 10){
				seeWall = false;
			}
			else{
				numberOfReadings++;
			}
		}
		numberOfReadings = 0;
		return;
	}
	public void lookUntilWall(){
		boolean seeWall = false;
		while(!seeWall){
			if(getFilteredData() < d + k && numberOfReadings > 10){
				seeWall = true;
			}
			else{
				numberOfReadings++;
			}
		}
		numberOfReadings = 0;
	}
}
