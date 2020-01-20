/*
 * The actual node of our model since two lines of the 
 * input nodes.csv file may refer to the same actual node
 * due to itnersections
 * Fields:
 * x,y: coordinates of the node
 * gScore, fScore: values unique to each node used in A*
 * cameFrom: a reference to the node (Position object)
 * that leads to this one most efficiently
*/


public class Position {
	private double X;
	private double Y;
	private double gScore = Double.MAX_VALUE;
	private double fScore = Double.MAX_VALUE;
	private Position cameFrom;
	
	// get edge weight between current node and other 
	public double getEdgeWeight(Position other)
	{
		
		double dx = Math.abs(other.getX() - this.X);
		double dy = Math.abs(other.getY() - this.Y);
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	// heuristic: the euclidean distance between this node and the goal-client
	public double getEstimatedDistanceToGoal(Position client){
		
		double dx = Math.abs(client.getX()- this.X);
		double dy = Math.abs(client.getY()- this.Y);
		
		return Math.sqrt(dx*dx + dy*dy);
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
	
	public double getgScore() {
		return gScore;
	}
	
	public void setgScore(double gScore) {
		this.gScore = gScore;
	}
	
	public double getfScore() {
		return fScore;
	}
	
	public void setfScore(double fScore) {
		this.fScore = fScore;
	}
	
	
	public Position getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(Position cameFrom) {
		this.cameFrom = cameFrom;
	}

	/*hashcode and equals override for the hashmap
	*/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(X);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(Y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return ( (this.X == ((Position) obj).getX()) && (this.Y == ((Position) obj).getY()) );
	}

	@Override
	public String toString() {
		return "Position [X=" + X + ", Y=" + Y + ", gScore=" + gScore + ", fScore=" + fScore + ", "
				+ (cameFrom != null ? "cameFrom=" + cameFrom : "") + "]";
	}
	
	
	
	
	
	
	
}
