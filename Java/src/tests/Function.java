package tests;

public class Function {
	
	double[] gamma;
	double R;
	public Function(){};
	public Function(double[] gamma, double R) {
		this.gamma = gamma;
		this.R = R;
	}

	double f(double c) {
		double val = -2 * Math.PI;
		for (int i = 0; i < gamma.length; i++) {
			val += Math.acos((.5)*(-2 * c*c + 2 * (c*c+1) * Math.cos(gamma[i])));
		}
		return val;
	}
	/*double f(double x) {	
		return x*x*x +x*x -2;
	}
	
	double fprime(double x) {
	return 3*x*x + 2*x;
	}*/

	double fprime(double c) {
		double val = 0;
		for (int i = 0; i < gamma.length; i++) {
			val += (4*c - 4*c*Math.cos(gamma[i])) /
					(2* Math.sqrt(1-.25*Math.Math.pow((-2*c*c+2*(1+c*c) * Math.cos(gamma[i])),2)));
		}
		return val;
	}

}
