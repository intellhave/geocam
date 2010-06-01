package tests;

public class Newton {

	public static void main(String[] args) {
		Newton newton = new Newton();
		double x = newton.newtonsMethod(.5, new Function());
		System.out.println("Got the value " + x);
	}
	
		//give an x to start at
	public double newtonsMethod(double cMax, Function func) {
		
		double x = .5 * cMax;

		double tolerance = .0000001; // Our approximation of zero
		int max_count = 500; // Maximum number of Newton's method iterations
        for (int i=0; i<=20; i++) {
		// x is our current guess. 
		System.out.println("Starting at " + func.f(x));
		for (int count = 1; (Math.abs(func.f(x)) > tolerance) && (count < max_count); count++) {
			x = x - .4*(func.f(x) / func.fprime(x));
			System.out.println("Step: " + count + " x:" + x + " Value:" + func.f(x));
		}

		if (Math.abs(func.f(x)) <= tolerance) {
			System.out.println("Exuberence!!! Zero found at x=" + x);
			break;
		} else {
			System.out.println("Failed to find a zero");
			System.out.println("Changing starting value");
			x=x-Math.Math.pow(-1.0,i)*((cMax/20)*i);
			System.out.println("x is " + x );
			if (i==20) {
				x=.9 * cMax;
			}
		}
        }
		
		return x;
	}
}
