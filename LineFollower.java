package Assignment2;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.subsumption.Behavior;

public class LineFollower implements Behavior
{
	private static ColorSampleExample cse = new ColorSampleExample();
	private int BasicSpeed = 300;
	private int maximumSpeed = 550;
	private double PGain = 300;
	private double DGain = 2000;
	private float middleValue;
	
	
	private boolean suppressed = false;
	
	public LineFollower()
	{
		
	}
	public boolean takeControl()
	{
		return true;
	}
	
	public void suppress()
	{
		suppressed = true;
	}
	
	public void action()
	{
		suppressed = false;
		Motor.A.forward();
		Motor.D.forward();
		while (!suppressed)
		{
			Thread.yield();
		}
		Motor.A.stop();
		Motor.D.stop();
	}
	
	public void followLine()
	{
		double formerError = 0.0;
		while(true)
		{
			int speed = 0;
			double reading = 0.0;
			float[] result =cse.redSample();
			for(float number : result)
				reading = number;
			
			double error = middleValue - reading;
			speed = (int) (7*(PGain * error + DGain * (error - formerError))); 
			formerError = error;
			
			int rightSpeed = BasicSpeed + speed;
			int leftSpeed = BasicSpeed - speed;
			
			//Now we need the speeds to have a value between their max and min values
			leftSpeed = Math.max(leftSpeed, 0);
			leftSpeed = Math.min(leftSpeed, maxSpeed);
			rightSpeed = Math.max(rightSpeed, 0);
			rightSpeed = Math.min(rightSpeed, maxSpeed);
			
			Motor.D.setSpeed(rightSpeed);
			Motor.A.setSpeed(leftSpeed);
			
			Motor.D.backward();
			Motor.A.backward();
		}
	}
	
	public void autoCalibrate()
	{
		Button.waitForAnyPress();
		float sum = (float) 0.0;
		for(int times = 0; times < 100; times++)
		{
			float[] midValue = cse.redSample();
			double measured = 0.0;
			for(float midVal : midValue)
				measured = midVal;
			sum += measured;
		}
		this.middleValue = sum/100;
	}
}
