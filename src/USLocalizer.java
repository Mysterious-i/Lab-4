import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
//Alessandro Parisi 260529758
//Shahrzad Tighnavardmollasarae 260413622
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
	/*Preforms a certain localization technic depending on whether ther user wants 
	 * falling edge or rising edge localization.
	 * It then moves the robot to a location where light localization can occur (it 
	 * moves it to the nearest cross section of the lines).
	 */
	public void doLocalization() {
		double [] pos = new double [3];
		double angleMid = 0;
		double angleMid2 = 0;

		/*This type of localization (falling edge) makes the robot rotate until it sees no wall
		 * and then rotate until it sees a wall and marks down the angle.
		 * Afterwards it moves in the opposite direction and does the same thing.
		 * It then uses both these midpoint angles to rotate the robot to a known
		 * angle (in this case facing the positive y-axis. 
		 */
		if (locType == LocalizationType.FALLING_EDGE) {
			
			// rotate the robot
			robot.setRotationSpeed(ROTATION_SPEED);

			//Get and calculate the middle angle
			angleMid = getMidAngleFalling(pos);
			
			//Start moving the robot in the opposite direction
			robot.setRotationSpeed(-1*ROTATION_SPEED);
			
			//Get and calculate the middle angle
			angleMid2 = getMidAngleFalling(pos);
			
			//Make the robot stop moving
			robot.setSpeeds(0,0);
			
			//Rotate the robot to the correct angle (facing the positive y-axis)
            goToAngle(angleMid, angleMid2); 
            
			// update the odometer position
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		} 
		
		/*In this type of localization (rising edge) makes the robot rotate until it sees
		 * a wall and then keep rotating until it sees no more wall. It records the angles it
		 * no longer saw the wall at and calculates the midpoint angle. It then rotates in the
		 * opposite direction and performs the same task to calculate the mid angle again.
		 * It then uses both of the angles to calculate the angle it need to rotate to, to be at
		 * a known angle (in this case facing the positive y axis)
		 */
		else {
			
			//Start the robot rotating
			robot.setRotationSpeed(ROTATION_SPEED);
              
			//Get the middle angle
            angleMid = getMidAngleRising(pos, false);
              
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
              
            //Get the middle angle
            angleMid2 = getMidAngleRising(pos, true);

            //Stop the robot from moving
            robot.setSpeeds(0, 0);
      
            //Rotate the robot to the correct angle (facing the positive y-axis)
            goToAngle(angleMid2, angleMid);          
              
            // update the odometer position
            odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
        }
		
		//Move the robot to the nearest cross section of lines
		
		//TODO change the numbers and fill it in below in COMMENT
		
		/*First we turn the robot so it is facing the wall on its left and move it back
		 * till its a certain distance ( in this case) from the wall.
		 */
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
         
         /*Then we turn the robot again to make it face the back wall and move it back
          * till its a a certain distance ( in this case) from the wall.
          */
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
           
         // Turn the robot 45 degrees so it is not on top of a line to start with
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
	
	/*This method turns the robot until it no longer sees a wall
	 * It has a filter (numberOfReadings to make sure it is recording correct values.
	 */
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
	/*This method turns the robot until it sees a wall
	 * It has a filter (numberOfReadings to make sure it is recording correct values.
	 */
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
	//This method turns the robot until it is at a certain distance from the wall.	 
	public void turnUntilDistance(){
		while(getFilteredData() >= d - k){
			
		}
	}
	
	//Calculates the middle angle using the falling edge technic
	public double getMidAngleFalling(double[] pos){
		
		double angleA = 0, angleB = 0;
		
		//Turn the robot until it no longer sees the wall
		lookUntilNoWall();
		
		//Turn the robot until it sees a wall
		lookUntilWall();
		
		//Update the odometer position and get the angle
		odo.getPosition(pos);
		angleA = pos[2];
		Sound.beep();
	
		//Turn the robot until you are at a certain distance and then read the angle 
		turnUntilDistance();
		
		//Update the odometer position and get the angle
		odo.getPosition(pos);
		angleB = pos[2];
		
		//Calculate and return the middle of the two previous angles calculated
		return (angleA + angleB) / 2;
	}
	
	//Calculates the middle angle using the rising edge technic
	public double getMidAngleRising(double[] pos, boolean secondTrip){
		double angleA = 0, angleB = 0;
		
		//Turn the robot until it sees a wall
        lookUntilWall();
        
        //Turn the robot until it no longer sees the wall
        lookUntilNoWall();
        
		//Update the odometer position and get the angle
        odo.getPosition(pos);
        angleB = pos[2];
        Sound.beep();   
        
        /*If the robot is making its second trip it needs to move in the 
         * opposite direction, 
         */
        if(secondTrip){
        	robot.setRotationSpeed(ROTATION_SPEED);
        }
        else{
        	robot.setRotationSpeed(-ROTATION_SPEED);
        }
        
        //Turn the robot until you are at a certain distance and then read the angle 
        turnUntilDistance();
        
        //Update the odometer position and get the angle
        odo.getPosition(pos);
        angleA = pos[2];
          
        //Calculate and return the middle of the two previous angles calculated
        return (angleA + angleB)/2;
	}
	/*This method moves the robot to a certain angle depending on the middle angles
	 * that were previously calculated.
	 */
	
	public void goToAngle(double angleMid, double angleMid2){
		/* Depending on where the robot is facing (the magnitude of the middle angles),
		 * we turn the robot a certain amount.
		 */
		
        if(angleMid < angleMid2){
            navigation.turnTo(-222 + (angleMid + angleMid2)/2, true);
        }else{
            navigation.turnTo(-46 + (angleMid + angleMid2)/2, true);
        } 
	}
}
