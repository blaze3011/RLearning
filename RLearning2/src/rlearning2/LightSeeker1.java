// --- Brief description
// --- LightSeeker1 --- (greedy)
// - Repeat until finished
//    - Make random motor action
//    - If the action decreases light, backtrack
// --- History
// 12/03/15. LightSeeker1 implemented. See "Versions.txt"
// 12/03/15. Initial testing. Simplistic and ineffective. Lots of back & forth. Excessively stuck. 

package rlearning2;

import java.io.IOException;
import java.util.Random;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.device.NXTMMX;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.video.Video;
import lejos.robotics.EncoderMotor;
import lejos.robotics.RegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
//import lejos.utility.Delay;
//import lejos.utility.Delay;

public class LightSeeker1 {
	  
	private static final int WIDTH = 160;
    private static final int HEIGHT = 120;
    //private static final int NUM_PIXELS = WIDTH * HEIGHT;
    
    // Frames and motion maps
    private static byte [][] luminanceFrame = new byte[HEIGHT][WIDTH];
    private static int threshold = 5;//70;
    //private static MotionMap aMotMap = new MotionMap();
    // Motors
    //private static RegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);
    private static EncoderMotor motorB = new UnregulatedMotor(MotorPort.B);
    //private static RegulatedMotor motorC = new EV3LargeRegulatedMotor(MotorPort.C);
	private static EncoderMotor motorC = new UnregulatedMotor(MotorPort.C);
	//private static float alpha = 180; // amplification for motor signals
	// Light features
	private static LightFeatures aLightFeat = new LightFeatures();
	private static double oldMeanLight, newMeanLight;
	// Randomness
	//private static Random randGenerator = new Random();
	private static int randDegLeft, randDegRight;
	private static int randMotor; // 0 = left; 1 = right
	
	private static float[] sample = new float[10];
    
    public LightSeeker1() {
    	// Various initializations
    	randDegLeft = 0;
    	randDegRight = 0;
		// Initialize luminance frame
    	for (int x=0; x<WIDTH; x += 1) {
    		for (int y=0; y<HEIGHT; y += 1) {
    			luminanceFrame[y][x] = 0;
    		}
    	}
	}
    
    public static void main(String[] args) throws IOException  {
         
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        Video video = ev3.getVideo();
        video.open(WIDTH, HEIGHT);
        EV3ColorSensor evColour = new EV3ColorSensor(SensorPort.S2);
    	evColour.getAmbientMode().fetchSample(sample, 0);
        
        byte[] frame = video.createFrame();
        double mot_amplif_larger = 1.2*0.6;
        double mot_amplif_smaller = 1.2*0.3;
        double left_field = 0;
        double right_field = 0;
        double front,left,right,back,centre;
        double black_threshold;
        double white_threshold;
        int state = 0;
        
    	/*Reinforcement Learning Components*/
    	
    	/*Actions
        1 Front			5 right 45
        2 left 90		6 reverse
        3 left 45		7 stop
        4 right 90		8 180    
        */
        byte actions = 9;//Actions
        byte states = 24;
        QUtil qutil = new QUtil(actions, states);
        QBrain q = new QBrain(actions, states, qutil);
        byte[] e = new byte[5]; // The environment values
        byte [] motorCommand = new byte[2]; // Commands to send to motors
    	
        //*******************************************
        
        
        // Grab frame
        video.grabFrame(frame);
    	// Extract luminanceFrame
        extractLuminanceValues(frame);
    	// Compute light features
        aLightFeat.compFourScreen(luminanceFrame, HEIGHT, WIDTH);
        //oldMeanLight = aLightFeat.meanTot;
    	 
        while(Button.ESCAPE.isUp()) {
        	System.out.println("Machine Learning Running");
        	// --- Get webcam information
        	// Grab frame
        	video.grabFrame(frame);
        	       	
        	
        	// Extract luminanceFrame
        	extractLuminanceValues(frame);
        	// Compute light features
        	aLightFeat.compFourScreen(luminanceFrame, HEIGHT, WIDTH);
        	      	
  			right = (aLightFeat.meanMR/255)*180; 
        	left = (aLightFeat.meanML/255)*180;
        	centre = (aLightFeat.meanMM/255)*180;
        	front = (aLightFeat.meanTM/255)*180;
        	back = (aLightFeat.meanBM/255)*180;
        	
            getEnvironmentVals(e);
            
            byte a = q.getAction(e);
            
            qutil.getCommands(a, motorCommand);
            
            performAction(motorCommand); 
        	
        	}
        	        	        
        evColour.close();
        video.close();
    }
    
    // DO: Improve this possibly by combining with chrominance values.
    public static void extractLuminanceValues(byte [] frame) {
    	int x,y;
    	int doubleWidth = 2*WIDTH; // y1: pos 0; u: pos 1; y2: pos 2; v: pos 3.
    	int frameLength = frame.length;
    	for(int i=0;i<frameLength;i+=2) {
    		x = (i / 2) % WIDTH;
    		y = i / doubleWidth;
    		luminanceFrame[y][x] = frame[i];
    	}
    }
    
    public static void dispFrame() {
    	for (int y=0; y<HEIGHT; y++) {
    		for (int x=0; x<WIDTH; x++) {
    			if (luminanceFrame[y][x] <= threshold) {
    				LCD.setPixel(x, y, 1);
    			}
    			else {
    				LCD.setPixel(x, y, 0);
    			}	
    		}
    	}
    	
    }
    
    public static void performAction(byte [] motorCommand) {

     }
     
     public static void getEnvironmentVals(byte [] e) {
        // Collect environment percepts:

     }
     public class MovementPilot {
    		private RegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);
    		private RegulatedMotor motorC = new EV3LargeRegulatedMotor(MotorPort.C);
    		
    		private EV3UltrasonicSensor ultraSensor = new EV3UltrasonicSensor(SensorPort.S1);
    		float[] onDist = new float[10];
    		//change the numbers to wheel diameters in inches
    		DifferentialPilot diff = new DifferentialPilot(2.2f, 2.2f, motorB, motorC, true);
    		
    		public void obsAvoid(){
    			while(ultraSensor.isEnabled()){
    				ultraSensor.getDistanceMode().fetchSample(onDist, 0);
    				//if distance is less than 0.2
    				if(onDist[0] < 0.2){
    					diff.quickStop();
    				}
    			}
    		}
    		
    		public void forward(){
    			diff.travel(-5);	
    		}
    		
    		public void reverse(){
    			diff.travel(-5);
    		}
    		
    		public void left45(){
    			//turn left immediately at 45 degrees
    			diff.steer(0, 45);
    		}
    		
    		public void left90(){
    			//turn left immediately at 90 degrees
    			diff.steer(0, 90);
    		}
    		
    		public void right45(){
    			//turn right immediately at 45 degrees
    			//negative values means turn right
    			diff.steer(0, -45);
    		}
    		
    		public void right90(){
    			//turn right immediately at 90 degrees
    			diff.steer(0, -90);
    		}
    		
    		public void turn180(){
    			diff.rotate(180);
    		}
    		
    		public void stop(){
    			diff.quickStop();
    		}
    	}
     
}




