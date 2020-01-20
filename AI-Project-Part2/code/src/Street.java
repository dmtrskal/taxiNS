/*
 * Each line of the nodes.csv input file
 * is modeled as a Street Object
 * Fields:
 * id: id of each node-line
 * name: name of each node-line
 * prev, next : Position object of a node's next and previous neighbor
*/

public class Street {
	private String lineId;
	private String nodeId;
	private String name;

	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Street [" + (lineId != null ? "lineId=" + lineId + ", " : "")
				+ (nodeId != null ? "nodeId=" + nodeId + ", " : "") + (name != null ? "name=" + name : "") + "]";
	}
	
//	public Position getPrev() {
//		return prev;
//	}
//	public void setPrev(Position prev) {
//		this.prev = prev;
//	}
//	public Position getNext() {
//		return next;
//	}
//	public void setNext(Position next) {
//		this.next = next;
//	}
//	
//	@Override
//	public String toString() {
//		return "Street [" + (lineId != null ? "lineId=" + lineId + ", " : "")
//				+ (nodeId != null ? "nodeId=" + nodeId + ", " : "") + (name != null ? "name=" + name + ", " : "")
//				+ (prev != null ? "prev=" + prev + ", " : "") + (next != null ? "next=" + next : "") + "]";
//	}
	
	
	
	
	
	
	
}
