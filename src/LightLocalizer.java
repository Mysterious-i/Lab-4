import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.NXTRegulatedMotor;
  
public class LightLocalizer{
    private Odometer odo;
    private final int d = 12;
    private TwoWheeledRobot robot;
    private Navigation nav;
    private ColorSensor ls;
    private int counter, lightValue;
    private double x, y, x1, x2, y1, y2, dThetaY, dThetaX;
    private boolean line;
    private boolean midPoint;  
    public LightLocalizer(Odometer odo, ColorSensor ls) {
        this.odo = odo;
        this.robot = odo.getTwoWheeledRobot();
        this.ls = ls;
        this.nav = odo.getNavigation();
        lightValue = 0;
        counter = 0;
        line = false;
        midPoint = false;
        // turn on the light
        ls.setFloodlight(true);
    }
      
    public void doLocalization() {
        boolean done = false;
        double angle;
       /* while(!midPoint)
        {
        	lightValue = ls.getLightValue();
        	if(lightValue <= 50)
        		midPoint = true;
        	nav.goForward(1.0);
        }*/
        robot.setSpeeds(0, -50);
  
        // keep turning the robot until all 4 lines are picked up by the sensor
       while (counter < 4) {
            lightValue = ls.getLightValue();
            LCD.drawInt(lightValue, 3, 7);
            if (!line && lightValue <= 50) {
                counter++;
                line = true;
                switch (counter) {
                case 1:
                    y1 = odo.getAng();
                    Sound.beep();
                    break;
                case 2:
                    x1 = odo.getAng();
                    Sound.beep();
                    break;
                case 3:
                    y2 = odo.getAng();
                    Sound.beep();
                    break;
                case 4:
                    x2 = odo.getAng();
                    Sound.beep();
                    break;
                default:            
                }   
            }
            else if (lightValue > 50) line = false;
        }
        
        
        
        // stop the robot
        robot.setSpeeds(0,0);
          
        // calculate the difference in the heading for x and y axes
        dThetaY = y1 - y2;
        dThetaX = x1 - x2;
          
        // compute the current x and y position
        x = -d * Math.cos(Math.toRadians(dThetaY) / 2);
        y = -d * Math.cos(Math.toRadians(dThetaX) / 2);
          
        // rotate to 0 degrees
        nav.turnTo(0, true);
          
        // update the robot's position
        odo.setPosition(new double[] {x, y, 0}, new boolean[] {true, true, true});
          
        // go to the point (0, 0)
        nav.travelTo(3,0);
          
        // turn back to a heading of 0
        nav.turnTo(18, true);
          
        // update the odometer with the position and heading of the robot
        odo.setPosition(new double[] {0, 0, 0}, new boolean[] {true, true, true});
    }
  
    private int convertDistance(double radius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }
  
      
    private int convertAngle(double radius, double width, double angle) {
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
} 