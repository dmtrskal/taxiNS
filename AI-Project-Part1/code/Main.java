import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

public class Main {
	
	

	/*
	 * Parses the client.csv input file, generating a Client Object
	*/
	

	private static Client readClientFile(BufferedReader br, String cvsSplitBy) throws NumberFormatException, IOException {
		String line = "";
		String[] lineArray = null;
		
		Client cl = new Client();
		Position clCoord =  new Position();
		
		line = br.readLine(); // read first line with parameters
				
		while ( (line = br.readLine() ) != null ) {
			lineArray = line.split(cvsSplitBy);
			
			clCoord.setX(Double.parseDouble(lineArray[0]));
			clCoord.setY(Double.parseDouble(lineArray[1]));
			cl.setMyCoord(clCoord);
		}
		return cl;
	}
	
	/*
	 * Parses the taxis.csv input file, generating a list with Taxi objects
	*/
	
	private static LinkedList<Taxi> readTaxisFile(BufferedReader br, String cvsSplitBy) throws NumberFormatException, IOException {
		LinkedList<Taxi> taxis = new LinkedList<Taxi>();
		
		String line = "";
		String[] lineArray = null;
				
		line = br.readLine(); // read first line with parameters
		
		while ( (line = br.readLine() ) != null ) {
			lineArray = line.split(cvsSplitBy);
			
			Taxi simpleTaxi = new Taxi();
			Position taxiCoord = new Position();
			
			
			taxiCoord.setX(Double.parseDouble(lineArray[0]));
			taxiCoord.setY(Double.parseDouble(lineArray[1]));
			simpleTaxi.setMyCoord(taxiCoord);
			simpleTaxi.setId(Integer.parseInt(lineArray[2]));
			
			taxis.add(simpleTaxi);
		}
		return taxis;
	}
	
	
	/*
	 * reads the nodes.csv file, constructs the hashmap,
	 * and calculates the nearest node to the taxi and to the client
	*/
	
	private static  HashMap<Position, LinkedList<Street> > readNodesFile(BufferedReader br, String cvsSplitBy, LinkedList<Taxi> taxisList, Client client) throws NumberFormatException, IOException {
		HashMap<Position, LinkedList<Street> > myMap = new HashMap<Position, LinkedList<Street> >();
		
		String line1 = "";
		String line2 = "";
		String[] lineArray1 = null;
		String[] lineArray2 = null;
		
		Stack<Position> tempStack = new Stack<Position>();
		
		line1 = br.readLine(); // read first line with parameters
		
		
		/*
		 * We read nodes in pairs and utilize a stack, so that
		 * each street object that goes into the hashmap has 
		 * a previous and next neighbor, if said neighbors exist
		*/
		
		
		
		
		line1 = br.readLine();
		while ( (line2 = br.readLine() ) != null ) {
			
			lineArray1 = line1.split(cvsSplitBy);
			lineArray2 = line2.split(cvsSplitBy);

			Street currentNode = new Street();
			LinkedList<Street> nodes = new LinkedList<Street>();
			
			//currentCoord will serve as a key for the hashmap
			Position currentCoord =  new Position();
			currentCoord.setX(Double.parseDouble(lineArray1[0]));
			currentCoord.setY(Double.parseDouble(lineArray1[1]));
			
			currentNode.setId(Integer.parseInt(lineArray1[2]));
			if (lineArray1.length > 3)
				currentNode.setName(lineArray1[3]);
			else
				currentNode.setName(null);

			// add previous street-neighbor's coordinates(if he exists)
			if ( !tempStack.empty() ) {
				currentNode.setPrev(tempStack.pop());
			}
			else {
				currentNode.setPrev(null);
			}
				
			
			// streets with same id 
			if ( (Integer.parseInt(lineArray1[2])) == (Integer.parseInt(lineArray2[2])) ) {
				
				// add next street-neighbor's coordinates(if he exists)
				Position childCoord =  new Position();
				childCoord.setX(Double.parseDouble(lineArray2[0]));
				childCoord.setY(Double.parseDouble(lineArray2[1]));
				currentNode.setNext(childCoord);
				
				// add currentNode to stack for next iteration
				tempStack.push(currentCoord);
			}
			else {
				currentNode.setNext(null);
			}
			
			// add currentNode to nodes list(nodes with same coordinates)
			nodes.add(currentNode);
			
			
			// add to HashMap
			if ( myMap.containsKey(currentCoord) ) {
				
				/*
				 * if an entry for said key already exists,
				 * it means there is an intersection
				 * --add currentNode to that list--
				*/
				
				myMap.get(currentCoord).add(currentNode);
			}
			else {
				myMap.put(currentCoord, nodes);
			}
			
			
			// update taxis' and client's nearest coordinates
			for (Taxi taxi : taxisList) {
				taxi.calculateNear(currentCoord);
			}
			client.calculateNear(currentCoord);
			
			
			
			line1 = line2;
		} // end-of-while
		
		
		// insert last line of file(which is string line1) exactly as above
		lineArray1 = line1.split(cvsSplitBy);

		Street currentNode = new Street();
		LinkedList<Street> nodes = new LinkedList<Street>();
		
		Position currentCoord =  new Position();
		currentCoord.setX(Double.parseDouble(lineArray1[0]));
		currentCoord.setY(Double.parseDouble(lineArray1[1]));
		
		currentNode.setId(Integer.parseInt(lineArray1[2]));
		if (lineArray1.length > 3)
			currentNode.setName(lineArray1[3]);
		else
			currentNode.setName(null);

		if ( !tempStack.empty() ) {
			currentNode.setPrev(tempStack.pop());
		}
		else {
			currentNode.setPrev(null);
		}
		
		nodes.add(currentNode);
		
		
		if ( myMap.containsKey(currentCoord) ) {
			myMap.get(currentCoord).add(currentNode);
		}
		else {
			myMap.put(currentCoord, nodes);
		}
		
		
		for (Taxi taxi : taxisList) {
			taxi.calculateNear(currentCoord);
		}
		
		
		client.calculateNear(currentCoord);
		
		return myMap;
	}
	
	
	
	/*
	 * A* implementation. 
	 * Arguements:
	 * 		 
	 * 		the hashmap (our visualization of the map )
	 * 		a taxi's position to serve as START
	 * 		client's position to serve as GOAL
	 * 
	 * returns a position Object which is the GOAL node fully evaluated 
	 * 
	*/
	
	private static Position Astar(HashMap<Position, LinkedList<Street>> map,Taxi taxi,Client client){
		
		Comparator<Position> comparator = new MyComparator();
		PriorityQueue<Position> openSet = new PriorityQueue<Position>(10, comparator);
		
		HashSet<Position> closedSet = new HashSet<Position>();
		
		double tentativegScore;
		Position neighbor;
		
		Position start = new Position();
		start.setX(taxi.getNear().getX());
		start.setY(taxi.getNear().getY());
		
		
		LinkedList<Street> crossroads = map.get(start);
		
		
		start.setgScore(0.0);     //cost for going from start to start is 0
		
		//for the start node f score is totally heuristic
		start.setfScore(start.getEstimatedDistanceToGoal(client.getNear()));    
		openSet.add(start);       //add start to openset
		
		while (!openSet.isEmpty()){   //while there are still unevaluated nodes we have visited
			
			//on each step we examine the node with the lowest estimated distance from the goal
			Position curr = openSet.remove();
			
			/*
			crossroads returns a list of street objects
			Size of the list indicates the degree of the crossing
			2 nodes mean a simple intersection
			*/
			
			crossroads = map.get(curr);
			
			
			if (curr.equals(client.getNear())){     //goal is reached
				return curr;
			}
			
			closedSet.add(curr);
			for (Street node : crossroads) {
				
				/*
				visit each neighbor and add it to the openset
				*/
				neighbor = node.getPrev();
				
				// previous neighbor exists and is not fully evaluated
				if ( (neighbor != null) && ( !(closedSet.contains(neighbor)) ) ){
					
					// distance from start to previous neighbor
					tentativegScore = curr.getgScore() + curr.getEdgeWeight(neighbor); 
					
					// neighbor hasn't been visited before
					if ( !(openSet.contains(neighbor)) ) {
						//This is the best path until now
						neighbor.setCameFrom(curr);
						neighbor.setgScore(tentativegScore);
						neighbor.setfScore(neighbor.getgScore() + 
								 	 		neighbor.getEstimatedDistanceToGoal( client.getNear() ) );
						
						openSet.add(neighbor);
					}
					else if (tentativegScore < neighbor.getgScore()) {
						/*
						this is a shorter path than the one generated from a previous
						visit to neighbor
						*/
						neighbor.setCameFrom(curr);
						neighbor.setgScore(tentativegScore);
						neighbor.setfScore(neighbor.getgScore() + 
								 	 		neighbor.getEstimatedDistanceToGoal( client.getNear() ) );
					}
						
					
				}
				
				neighbor = node.getNext();
				// the exact same procedure for the next neighbor
				if ( (neighbor != null) && ( !(closedSet.contains(neighbor)) ) ){
					
					
					tentativegScore = curr.getgScore() + curr.getEdgeWeight(neighbor); 
					
					if ( !(openSet.contains(neighbor)) ) {
						 
						neighbor.setCameFrom(curr);
						neighbor.setgScore(tentativegScore);
						neighbor.setfScore(neighbor.getgScore() + 
								 	 		neighbor.getEstimatedDistanceToGoal( client.getNear() ) );
						
						openSet.add(neighbor);
					}
					else if (tentativegScore < neighbor.getgScore()) {
						neighbor.setCameFrom(curr);
						neighbor.setgScore(tentativegScore);
						neighbor.setfScore(neighbor.getgScore() + 
								 	 		neighbor.getEstimatedDistanceToGoal( client.getNear() ) );
					}
				}
					
			}
			
			
		}
		// A* failed---there is no path from start to goal
		return null;
	}
	
	
	/*
	reconstructPath returns a list 
	with the x,y coordinates of each node
	 of the taxi-client path
	*/
	private static ArrayList<Coordinates> reconstuctPath(Position solution, Taxi taxi) {
		
		ArrayList<Coordinates> lista = new ArrayList<Coordinates>();
		
		Coordinates start =  new Coordinates(taxi.getNear().getX(), taxi.getNear().getY());
		Coordinates goal =  new Coordinates(solution.getX(), solution.getY());
		
		
		lista.add(goal);
		Coordinates current = goal;
		Position currPos = solution;
		while ( !(current.equals(start)) ) {
			currPos = currPos.getCameFrom();
			if (currPos == null)
				break;
			current =  new Coordinates(currPos.getX(), currPos.getY());
			lista.add(0, current);
				
		}
		
		return lista;
	}
	
	
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		
		BufferedReader br = null;
		String cvsSplitBy = ",";
		
		//create client object from file
		br = new BufferedReader(new FileReader(args[0]));
		Client client = readClientFile(br, cvsSplitBy);
		
		//create a taxi object list from file
		br = new BufferedReader(new FileReader(args[1]));
		LinkedList<Taxi> taxisList = readTaxisFile(br, cvsSplitBy);
		
		
		//create the hashmap of the nodes 
		br = new BufferedReader(new FileReader(args[2]));
		HashMap<Position, LinkedList<Street>> nodesMap = readNodesFile(br, cvsSplitBy, taxisList, client);
		
		for (Taxi taxi: taxisList) {
		
		//for each taxi, run A* and calculate final path, if A* was successful	
			Position solution = Astar(nodesMap,taxi,client);
			taxi.setFinalScore(solution.getgScore());
			
		
			if (solution != null){
				taxi.setTotalPath( reconstuctPath(solution, taxi) );
			}
		}
		
		
		/*
		for each taxi create an out_id.txt file
		with the nodes of the route
		*/
		PrintWriter writer;
		String filename;
		File dir = new File("results");
		dir.mkdir();
		
		for (Taxi taxi : taxisList) {
			try{
				filename = "./results/out_"+taxi.getId()+".txt";
			     writer = new PrintWriter(filename, "UTF-8");
			     writer.println("score = "+ taxi.getFinalScore());
					for ( Coordinates coord :taxi.getTotalPath() ) {
						writer.println(coord.toString());
					}
			     
			     writer.close();
			} catch (IOException e) {
			   System.err.println("problem with creating file");
			}
		}
		/*
		calculate the taxi closest to the client and print it's id
		*/
		int winnerid=0;
		double min=Double.MAX_VALUE;
		for(Taxi taxi: taxisList){
			if (taxi.getFinalScore()<min){
				min = taxi.getFinalScore();
				winnerid= taxi.getId();
			}
		}	
		System.out.println("...Αnd the winner is taxi with id="+winnerid+"\n");

	}

}
