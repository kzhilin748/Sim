package com.use.speedup;

import java.math.BigDecimal;

import com.use.config.DAGVariable;

public class Speedup {
	public static double getAMDlawSpeedup(int CPU){
		
		double Speedup=(DAGVariable.getInstance().getAlpha()
									+(1-DAGVariable.getInstance().getAlpha())/CPU);
//		Speedup += 0.5;

		return Speedup;
		
	}
}
