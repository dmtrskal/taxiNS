import java.util.ArrayList;

/*
 * this class models the client,
 * containing his/her position,
 * the position of the closest actual node on the map
*/

public class Client {
	private Position myCoord;
	private Position destCoord;
	private Position near;
	private Position destNear;
	private double curDistance = Double.MAX_VALUE;
	private double curDestDistance = Double.MAX_VALUE;
	private ArrayList<Coordinates> totalPath;
	private double distanceToDest = 0;
	
	/*each time we process a new node line
	 * we call this method to update the near field
	*/
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
	
	/*each time we process a new node line
	 * we call this method to update the destNear field
	*/
	public void calculateDestNear(Position other) {
		double dx = Math.abs(other.getX() - destCoord.getX());
		double dy = Math.abs(other.getY() - destCoord.getY());
		
		if ( (dx < 0.001) && (dy < 0.001) ) {
			if (Math.sqrt(dx*dx + dy*dy) < (this.curDestDistance)) {
				this.curDestDistance = Math.sqrt(dx*dx + dy*dy);
				this.setDestNear(other);
			}
		}
			
	}
			
	
	public Position getMyCoord() {
		return myCoord;
	}

	public void setMyCoord(Position myCoord) {
		this.myCoord = myCoord;
	}

	public Position getNear() {
		return near;
	}

	public void setNear(Position near) {
		this.near = near;
	}

	public Position getDestCoord() {
		return destCoord;
	}

	public void setDestCoord(Position destCoord) {
		this.destCoord = destCoord;
	}

	public Position getDestNear() {
		return destNear;
	}

	public void setDestNear(Position destNear) {
		this.destNear = destNear;
	}
	
	public ArrayList<Coordinates> getTotalPath() {
		return totalPath;
	}

	public void setTotalPath(ArrayList<Coordinates> totalPath) {
		this.totalPath = totalPath;
	}
	
	public double getDistanceToDest() {
		return distanceToDest;
	}

	public void setDistanceToDest(double distanceToDest) {
		this.distanceToDest = distanceToDest;
	}

	@Override
	public String toString() {
		return "Client [" + (myCoord != null ? "myCoord=" + myCoord + ", " : "")
				+ (destCoord != null ? "destCoord=" + destCoord + ", " : "")
				+ (near != null ? "near=" + near + ", " : "") + (destNear != null ? "destNear=" + destNear + ", " : "")
				+ "curDistance=" + curDistance + "]";
	}
	
	
	
}
