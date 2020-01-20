/*
 * Taxi class corresponding to a taxi
 * contains its coordinates, its id,
 * the nearest node of the map closest to the taxi,
 * finalscore indicating the cost of the taxi-cient route
 * And a list of Coordinates object which models the path 
 * the taxi followed to the client
*/
import java.util.ArrayList;

public class Taxi {
	private Position myCoord;
	private int id;
	private Position near;
	private double curDistance = Double.MAX_VALUE;
	private double finalScore = Double.MAX_VALUE;
	private ArrayList<Coordinates> totalPath;
	
	public void calculateNear(Position other) {
		double dx = Math.abs(other.getX() - myCoord.getX());
		double dy = Math.abs(other.getY() - myCoord.getY());
		
		if ( (dx < 0.001) && (dy < 0.001) ) {
			if (Math.sqrt(dx*dx + dy*dy) < (this.curDistance)) {
				this.curDistance = Math.sqrt(dx*dx + dy*dy);
				this.setNear(other);
			}
		}
			
	}
	
	public Position getMyCoord() {
		return myCoord;
	}

	public void setMyCoord(Position myCoord) {
		this.myCoord = myCoord;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Position getNear() {
		return near;
	}

	public void setNear(Position near) {
		this.near = near;
	}

	public double getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}

	public ArrayList<Coordinates> getTotalPath() {
		return totalPath;
	}

	public void setTotalPath(ArrayList<Coordinates> totalPath) {
		this.totalPath = totalPath;
	}
	
	
}
