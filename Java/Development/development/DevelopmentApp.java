package development;

public class DevelopmentApp {

	/**
	 * @param args
	 */
	public static void main(String[] args)  {

		Vector a = new Vector(new double[] {2,3,4});
		Vector b = new Vector(1,0,0);
		try{
			a.add(b);
			System.out.print(a);
		}catch(Exception e){
			System.err.print(e.getMessage());
		}
	}

}
