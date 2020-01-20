/*
 * a simple two field class <X,Y>
 * it's only used in the reconstructPath method	
*/
public class Coordinates {
	private double X;
	private double Y;
	
	public Coordinates(double x, double y) {
		X = x;
		Y = y;
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	@Override
	public String toString() {
		return X + "," + Y + ",0";
	}
	
	
	
}
