/*
 * Each line of the nodes.csv input file
 * is modeled as a Street Object
 * Fields:
 * id: id of each node-line
 * name: name of each node-line
 * prev, next : Position object of a node's next and previous neighbor
*/

public class Street {
	private int id;
	private String name;
	private Position prev;
	private Position next;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Position getPrev() {
		return prev;
	}
	public void setPrev(Position prev) {
		this.prev = prev;
	}
	public Position getNext() {
		return next;
	}
	public void setNext(Position next) {
		this.next = next;
	}
	
	
	@Override
	public String toString() {
		return "Street [id=" + id + ", " + (name != null ? "name=" + name + ", " : "")
				+ (prev != null ? "prev=" + prev + ", " : "") + (next != null ? "next=" + next : "") + "]";
	}
	
	
	
	
}
