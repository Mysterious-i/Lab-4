import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;
//Alessandro Parisi 260529758
//Shahrzad Tighnavardmollasarae 260413622
public class Odometer implements TimerListener {
    public static final int DEFAULT_PERIOD = 25;
    private NXTRegulatedMotor leftMotor, rightMotor;
    private TwoWheeledRobot robot;
    private Timer odometerTimer;
    private Navigation navigation;
    private Object lock;
    private double x, y, theta;
    private double [] oldDH, dDH;
      
    private double leftRadius, rightRadius, width;
      
    public Odometer(TwoWheeledRobot robot, int period, boolean start) {
        
        this.robot = robot;
        this.navigation = new Navigation(this);
        odometerTimer = new Timer(period, this);
        leftMotor = Motor.A;
        rightMotor = Motor.B;
        x = 0.0;
        y = 0.0;
        theta = 0.0;
        oldDH = new double [2];
        dDH = new double [2];
        lock = new Object();
        this.rightRadius = 2.1;
        this.leftRadius = 2.1;
        this.width = 15.45;
          
        if (start)
            odometerTimer.start();
    }
      
    public Odometer(TwoWheeledRobot robot) {
        this(robot, DEFAULT_PERIOD, false);
    }
      
    public Odometer(TwoWheeledRobot robot, boolean start) {
        this(robot, DEFAULT_PERIOD, start);
    }
      
    public Odometer(TwoWheeledRobot robot, int period) {
        this(robot, period, false);
    }
  
      
    public void timedOut() {    
         this.getVector(dDH);
        dDH[0] -= oldDH[0];
        dDH[1] -= oldDH[1];
  
        // update the position in a critical region
        synchronized (this) {
            theta -= dDH[1];
            theta = fixDegAngle(theta);
  
            x += dDH[0] * Math.cos(Math.toRadians(theta));
            y += dDH[0] * Math.sin(Math.toRadians(theta));
        }
  
        oldDH[0] += dDH[0];
        oldDH[1] += dDH[1]; 
    }
    
    //Calculates the displacement and direction
    private void getVector(double[] data) {
        int leftTacho, rightTacho;
        leftTacho = leftMotor.getTachoCount();
        rightTacho = rightMotor.getTachoCount();
  
        data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
        data[1] = (rightTacho * rightRadius - leftTacho * leftRadius) / width;
    }
    
    // Getters of x y and the angle
    public double getX() {
        synchronized (lock) {
            return x;
        }
    }
      
    public double getY() {
        synchronized (lock) {
            return y;
        }
    }
      
    public double getAng() {
        synchronized (lock) {
            return theta;
        }
    }
    //Setting the angle
    public void setAng(double angle) {
        synchronized (lock) {
            theta = angle;
        }
    } 
    public void getPosition(double [] pos) {
        synchronized (lock) {
            pos[0] = x;
            pos[1] = y;
            pos[2] = theta;
        }
    }
      
    // accessors to motors
        public NXTRegulatedMotor [] getMotors() {
            return new NXTRegulatedMotor[] {this.leftMotor, this.rightMotor};
        }
        public NXTRegulatedMotor getLeftMotor() {
            return this.leftMotor;
        }
        public NXTRegulatedMotor getRightMotor() {
            return this.rightMotor;
        }
  
      
    public TwoWheeledRobot getTwoWheeledRobot() {
        return robot;
    }
      
    public Navigation getNavigation() {
        return this.navigation;
    }
      
    // mutators
    public void setPosition(double [] pos, boolean [] update) {
        synchronized (lock) {
            if (update[0]) x = pos[0];
            if (update[1]) y = pos[1];
            if (update[2]) theta = pos[2];
        }
    }
  
    // static helper methods
    public static double fixDegAngle(double angle) {        
        if (angle < 0.0)
            angle = 360.0 + (angle % 360.0);
          
        return angle % 360.0;
    }
      
    public static double minimumAngleFromTo(double a, double b) {
        double d = fixDegAngle(b - a);
          
        if (d < 180.0)
            return d;
        else
            return d - 360.0;
    }

} 