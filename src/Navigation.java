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
 
    private double deltaX, deltaY, theta, distance;
     
    public Navigation(Odometer odo) {
        this.odometer = odo;
 
        this.leftMotor = Motor.A;
        this.rightMotor = Motor.B;
 
        // set acceleration
        this.leftMotor.setAcceleration(ACCELERATION);
        this.rightMotor.setAcceleration(ACCELERATION);
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
         
        // check how much the robot has to travel in the x and y direction
        int counter = 0;
        deltaX = x - odometer.getX();
        deltaY = y - odometer.getY();
                 
        // find the direction the robot must face in order to get to his destination
        theta = Math.atan2(deltaX, deltaY) * 180 / Math.PI;
         
        // keep correcting the angle
        while (Math.abs(odometer.getAng() - theta) > DEG_ERR && counter < 10) {
            turnTo(theta, true);
            counter++;
            Sound.beep();
        }
                 
        // set the speed of the motors
        leftMotor.setSpeed(SLOW);
        rightMotor.setSpeed(SLOW);
                 
        distance = Math.sqrt(Math.pow(deltaX,  2) + Math.pow(deltaY, 2));
                 
        Motor.A.rotate(convertDistance(wheelRadius, distance), true);
        Motor.B.rotate(convertDistance(wheelRadius, distance), false);
        Motor.A.stop();
        Motor.B.stop();
    }
 
    /*
     * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
     * motors when the turn is completed
     */
    public void turnTo(double turnAngle, boolean stop) {        
        // get the angle reported by the odometer
        double odometerTheta = this.odometer.getAng();
            
        double angleChange = (turnAngle - odometerTheta)%360;  
            
        // makes sure that angle is minimal
        if ((angleChange > 180)||(angleChange < -180)) {
            if (angleChange > 180) {
                angleChange = angleChange - 360;
            } else {
                angleChange = angleChange + 360;
            }
        }
        
        // if the  angle is not within the given error margin
        if (Math.abs(angleChange) > DEG_ERR) {
                
            // if angle change is positive
            if (angleChange > 0){
                    
                // turn to the right
                Motor.A.setSpeed(SLOW);
                Motor.B.setSpeed(SLOW);
                    
                Motor.A.rotate(convertAngle(wheelRadius, width, Math.abs(angleChange)), true);
                Motor.B.rotate(-convertAngle(wheelRadius, width, Math.abs(angleChange)), false);
                 
             // if angle change is negative
            } else {
                // turn to the left
                Motor.A.setSpeed(SLOW);
                Motor.B.setSpeed(SLOW);
                    
                Motor.A.rotate(-convertAngle(wheelRadius, width, Math.abs(angleChange)), true);
                Motor.B.rotate(convertAngle(wheelRadius, width, Math.abs(angleChange)), false);
            }
        }
         
        Motor.B.stop();
        Motor.A.stop();
    }
     
     
    private int convertDistance(double radius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }
 
     
    private int convertAngle(double radius, double width, double angle) {
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
 
     
    /*
     * Go foward a set distance in cm
     */
    public void goForward(double distance) {
        this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);
 
    }
}