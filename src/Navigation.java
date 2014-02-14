/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 *
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
//Alessandro Parisi 260529758
//Shahrzad Tighnavardmollasarae 260413622
public class Navigation {
    private final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
    private final static double DEG_ERR = 5.0, CM_ERR = 1.0;
    private final double wheelRadius = 2.1, width = 15.45;
    private Odometer odometer;
    private NXTRegulatedMotor leftMotor, rightMotor;

     
    public Navigation(Odometer odo) {
        this.odometer = odo;
 
        this.leftMotor = Motor.A;
        this.rightMotor = Motor.B;
 
        // set acceleration
        this.leftMotor.setAcceleration(ACCELERATION);
        this.rightMotor.setAcceleration(ACCELERATION);
    }
 
    private int convertDistance(double radius, double travelDis) {
        return (int) ((180.0 * travelDis) / (Math.PI * radius));
    }
 
     
    private int convertAngle(double radius, double width, double angle) {
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
    
    /*
     * Functions to set the motor speeds jointly
     */
    public void setSpeeds(float lSpd, float rSpd) {
        this.leftMotor.setSpeed(Math.abs(lSpd));
        this.rightMotor.setSpeed(Math.abs(rSpd));
        if (lSpd < 0)
            this.leftMotor.backward();
        else
            this.leftMotor.forward();
        if (rSpd < 0)
            this.rightMotor.backward();
        else
            this.rightMotor.forward();
    }
 
    public void setSpeeds(int lSpd, int rSpd) {
        this.leftMotor.setSpeed(Math.abs(lSpd));
        this.rightMotor.setSpeed(Math.abs(rSpd));
        if (lSpd < 0)
            this.leftMotor.backward();
        else
            this.leftMotor.forward();
        if (rSpd < 0)
            this.rightMotor.backward();
        else
            this.rightMotor.forward();
    }
 
    /*
     * Float the two motors jointly
     */
    public void setFloat() {
        this.leftMotor.stop();
        this.rightMotor.stop();
        this.leftMotor.flt(true);
        this.rightMotor.flt(true);
    }
 
    /*
     * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
     * constantly updating it's heading
     */
    public void travelTo(double x, double y) {
        
	double dX, dY, angle, travelDis;
	int tries = 0;
	
        //Get the x y and that we need to travel
        dX = x - odometer.getX();
        dY = y - odometer.getY();
                 
        // find the direction by using tan
        angle = Math.atan2(dX, dY) * 180 / Math.PI;
         
        // Correct the angle until its good
        while (Math.abs(odometer.getAng() - angle) > DEG_ERR && tries < 10) {
            tries = tries + 1;
            turnTo(angle, true);
            Sound.beep();
        }
                 
        //Make the wheels move slowly
        leftMotor.setSpeed(SLOW);
        rightMotor.setSpeed(SLOW);
                 
        //Calcuate the travel distance by using pythagorean
        travelDis = Math.sqrt(dX*dX + dY*dY);
                 
        //Move the robot a certain distance and then stop the motors
        Motor.A.rotate(convertDistance(wheelRadius, travelDis), true);
        Motor.B.rotate(convertDistance(wheelRadius, travelDis), false);
        
        Motor.A.stop();
        Motor.B.stop();
    }
 
    /*
     * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
     * motors when the turn is completed
     */
    public void turnTo(double turnAngle, boolean stop) {        
       
        //Get the angle the robot needs to turn  
        double angleNeedToTravel = (turnAngle - odometer.getAng())%360;  
            
        //Get the minimal angle
        if ((angleNeedToTravel > 180)||(angleNeedToTravel < -180)) {
            if (angleNeedToTravel > 180) {
                angleNeedToTravel = angleNeedToTravel - 360;
            } 
            else {
                angleNeedToTravel = angleNeedToTravel + 360;
            }
        }
        
        // if the  angle is not within the given error margin
        if (Math.abs(angleNeedToTravel) > DEG_ERR) {
                
            // if angle change is positive we move clockwise
            if (angleNeedToTravel > 0){
        	
                Motor.A.setSpeed(SLOW);
                Motor.B.setSpeed(SLOW);
                    
                Motor.A.rotate(convertAngle(wheelRadius, width, Math.abs(angleNeedToTravel)), true);
                Motor.B.rotate(-convertAngle(wheelRadius, width, Math.abs(angleNeedToTravel)), false);
                 
             // if angle is negative move counterclockwise
            } 
            else {
        	
            	Motor.A.setSpeed(SLOW);
                Motor.B.setSpeed(SLOW);
                    
                Motor.A.rotate(-convertAngle(wheelRadius, width, Math.abs(angleNeedToTravel)), true);
                Motor.B.rotate(convertAngle(wheelRadius, width, Math.abs(angleNeedToTravel)), false);
            }
        }
         
        //Stop the motors after we turned the amount we desired
        Motor.B.stop();
        Motor.A.stop();
    }
     
    /*
     * Go foward a set travelDis in cm
     */
    public void goForward(double travelDis) {
        this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * travelDis, Math.cos(Math.toRadians(this.odometer.getAng())) * travelDis);
 
    }
}