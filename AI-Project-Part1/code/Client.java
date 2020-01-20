/*
 * this class models the client,
 * containing his/her position,
 * the position of the closest actual node on the map
*/

public class Client {
	private Position myCoord;
	private Position near;
	private double curDistance = Double.MAX_VALUE;
	
	
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
	
	
	
}
