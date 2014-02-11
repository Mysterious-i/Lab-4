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
    private Navigation nagivator;
    private ColorSensor lightSens;
    private int numberOfLinesRead, valueOfLight;
    private double x, y, x1, x2, y1, y2;
    private boolean senseLine;
    private int NUMBER_OF_LINES_NEEDED = 4;
 
    public LightLocalizer(Odometer odo, ColorSensor ls) {
        this.odo = odo;
        this.robot = odo.getTwoWheeledRobot();
        this.lightSens = ls;
        this.nagivator = odo.getNavigation();
        valueOfLight = 0;
        numberOfLinesRead = 0;
        senseLine = false;

        // turn on the light
        ls.setFloodlight(true);
    }
      
    public void doLocalization() {
    	
    	//Start the robot rotating
        robot.setSpeeds(0, -50);
  
        
        /* Rotate the robot until it reads 4 lines
         * Make sure the robot does not read a line twice by setting a boolean
         * to true of the robot does read a line.
         */
      
        while (numberOfLinesRead < NUMBER_OF_LINES_NEEDED) {
        	
        	valueOfLight = lightSens.getLightValue();
            
        	LCD.drawInt(valueOfLight, 3, 7);
           
        	if (!senseLine && valueOfLight <= 50) {
            	numberOfLinesRead++;
                senseLine = true;
                switch (numberOfLinesRead) {
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
            else if (valueOfLight > 50) senseLine = false;
        }

        // stop the robot
        robot.setSpeeds(0,0);
          
        //  Use the angles and equation to calculate the current x and y
        x = -d * Math.cos(Math.toRadians(y1 - y2) / 2);
        y = -d * Math.cos(Math.toRadians(x1 - x2) / 2);
               
        // Update the robot's position
        odo.setPosition(new double[] {x, y, 0}, new boolean[] {true, true, true});
          
        // Make the robot go to the origin (intersection of the two lines)
        nagivator.travelTo(0, 0);
          
        // Make the robot face straight
        nagivator.turnTo(0, true);
          
        // Update the odometer so it says its at the origin 
        odo.setPosition(new double[] {0, 0, 0}, new boolean[] {true, true, true});
    }
} 