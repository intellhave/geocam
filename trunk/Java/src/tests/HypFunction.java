package tests;

public class HypFunction extends Function{
	
	double[] gamma;
	double R;
	public HypFunction(){};
	public HypFunction(double[] gamma, double R) {
		this.gamma = gamma;
		this.R = R;
	}

	double f(double c) {
		double val = -2 * Math.PI;
		for (int i = 0; i < gamma.length; i++) {
			val += Math.acos((2*c*c+2*(c*c+R*R)*Math.cos(gamma[i]))/(2*R*R));
		}
		return val;
	}

	double fprime(double c) {
		double val = 0;
		for (int i = 0; i < gamma.length; i++) {
			val += (-1*(4*c + 4*c*Math.cos(gamma[i]))) /
					(2* R*R* Math.sqrt(1-Math.pow(2*c*c+2*(c*c+R*R)*Math.cos(gamma[i]),2)/(4*R*R*R*R)));
		}
		return val;
	}

}
