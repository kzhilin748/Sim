package com.use.utils;
import java.util.Random;

public class NormalDistribution extends Random {
	private static final long serialVersionUID = 2043626063309789043L;
	private double nextNextGaussian;
	private boolean haveNextNextGaussian = false;
	private double currentGaussian;
	
	public NormalDistribution(long seed) {
		this.setSeed(seed);
	}
	
	public double nextGaussian(double mean, double sigm) {	
		
		if (haveNextNextGaussian) {
			haveNextNextGaussian = false;
			currentGaussian = nextNextGaussian;
		} else {
			double v1,v2, s;
			do {
				v1 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v2 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			nextNextGaussian = v2 * multiplier;
			haveNextNextGaussian = true;
			currentGaussian = v1 * multiplier;
		}
		return mean + currentGaussian * sigm;
	}
}