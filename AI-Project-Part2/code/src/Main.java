import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import com.ugos.jiprolog.engine.JIPSyntaxErrorException;

public class Main {
	
	private static final int k = 5;	/* max number of taxis presented to user - used for ranking available taxis */
	
	/**
	 * Read input from stdin if the route should contain tolls(if required) or avoid roads with tolls   
	 * */
	private static String readTollAvoidance() throws IOException {
		
		BufferedReader br = null;
	
        // Refer to this http://www.mkyong.com/java/how-to-read-input-from-console-java/
        // for JDK 1.6, please use java.io.Console class to read system input.
        br = new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        
        while ( !input.equals("yes") && !input.equals("no") ) {
            System.out.print("Would you like to avoid tolls? (yes/no) ");
            input = br.readLine();
        }
        
        return input;
	}

	
	/**
	 * function to write to file
	 **/
	private static void writeFile(BufferedWriter bfWr, String factToWrite) {
		try {
			// Note that write() does not automatically
			// append a newline character.
			bfWr.write(factToWrite);
			bfWr.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	/**
	 * reads the traffic.csv file, and creates a prolog style file with facts concerning traffic
	 **/
	private static void readTrafficFile(BufferedReader br, BufferedWriter bw, String cvsSplitBy)
			throws NumberFormatException, IOException {

		String line = "";
		String[] lineArray = null;

		line = br.readLine(); // read first line with parameters

		while ((line = br.readLine()) != null) {
			lineArray = line.split(cvsSplitBy);
			
			String[] traffic = null;
			String trafficString = "";
			
			/* check if traffic_info exists */
			if ( lineArray.length > 2) {
				/* format traffic_info so it appears as a list in fact */
				traffic = lineArray[2].split("\\|");
				
				String trafficFact = "traffic(" + lineArray[0] + ",[" ;
				for (String string : traffic) {
					string = string.replaceAll(":", ".");
					
					String start = string.substring(0, string.indexOf("-"));
					String end = string.substring((string.indexOf("-")+1), string.indexOf("="));
					String status = string.substring(string.indexOf("=")+1);
					
					trafficString = Double.parseDouble(start) + "," + Double.parseDouble(end) + "," + status ;
					
					
					trafficFact += "[" + trafficString + "]" + "," ;
				}
				/* write fact to file */
				/* remove last "," */
				trafficFact = trafficFact.substring(0, trafficFact.length()-1); 
				trafficFact += "]).";
				writeFile(bw, trafficFact);
			} else {
				/* no traffic_info is given */
				trafficString = "";	/* empty string */
				
				/* write fact to file */
				String trafficFact = "traffic(" + lineArray[0] + "," + "[" + trafficString + "]" + ").";
				writeFile(bw, trafficFact);
			}
		}
	}

	/**
	 * reads the lines.csv file, and creates a prolog style file with facts concerning 
	 * information about lines
	 **/
	private static void readLinesFile(BufferedReader br, BufferedWriter bw, String cvsSplitBy, String avoidToll)
			throws NumberFormatException, IOException {

		String line = "";
		String[] lineArray = null;

		line = br.readLine(); // read first line with parameters

		while ((line = br.readLine()) != null) {
			lineArray = line.split(cvsSplitBy);
			
			/* initialize all values with nn --> no_name */
			/* the indexes(0-17) of lineInfo array are(follow the order of lines.csv):
			 * id,highway,name,oneway,lit,lanes,maxspeed,railway,boundary,access,
			 * natural,barrier,tunnel,bridge,incline,waterway,busway,toll
			 * 
			 * */
			String[] lineInfo = new String[18];
			for (int i = 0; i < lineInfo.length; i++) {
				lineInfo[i] = "nn";
			}
			
			
			/* add "'" before and after the incline(if exists and it's not empty) */
			if ( (lineArray.length > 14) && ( !lineArray[14].isEmpty() ) ){
					lineArray[14] = "'" + lineArray[14] + "'";
			}
			
			/* change nn to value read from lines.csv file(if value exists and it's not empty) */
			for (int i = 0; i < lineArray.length; i++) {
				if ( !lineArray[i].isEmpty() ) {
					lineInfo[i] = lineArray[i];
				}
			}
			
			/* create lineFact with proper values and then write it to .pl file */
			String lineFact = "";
			if ( avoidToll.equals("no") ){
				/* permit to traverse roads with tolls
				/*
				 * line(id,oneway,highway,[list1],[list2]
				 * list1 contains values that affect priority of street
				 * 		list1: lit,lanes,maxspeed,tunnel,bridge,incline,busway,toll
				 * list2 contains that prevent taxi from going through this street(lineid)
				 * 		list2: railway,boundary,access,natural,barrier,waterway
				 * name of line is not taken into account
				 * */
				lineFact = "line(" + lineInfo[0] + "," + lineInfo[3] + "," + lineInfo[1] + "," + 
						"[" + lineInfo[4] + "," + lineInfo[5] + "," + lineInfo[6] + "," + lineInfo[12] + ","
						+ lineInfo[13] + "," + lineInfo[14] + "," + lineInfo[16] + "," + lineInfo[17] + "],"
						+ "[" + lineInfo[7] + "," + lineInfo[8] + "," + lineInfo[9] + ","  + lineInfo[10] + ","
						+ lineInfo[11] + "," + lineInfo[15] + "]" +  ").";
			}
			else if ( avoidToll.equals("yes") ){
				/* DON'T permit to traverse roads with tolls
				/*
				 * line(id,oneway,highway,[list1],[list2]
				 * list1 contains values that affect priority of street
				 * 		list1: lit,lanes,maxspeed,tunnel,bridge,incline,busway
				 * list2 contains that prevent taxi from going through this street(lineid)
				 * 		list2: railway,boundary,access,natural,barrier,waterway,toll
				 * name of line is not taken into account
				 * */
				lineFact = "line(" + lineInfo[0] + "," + lineInfo[3] + "," + lineInfo[1] + "," + 
						"[" + lineInfo[4] + "," + lineInfo[5] + "," + lineInfo[6] + "," + lineInfo[12] + ","
						+ lineInfo[13] + "," + lineInfo[14] + "," + lineInfo[16] + "],"
						+ "[" + lineInfo[7] + "," + lineInfo[8] + "," + lineInfo[9] + ","  + lineInfo[10] + ","
						+ lineInfo[11] + "," + lineInfo[15] + "," + lineInfo[17] + "]" +  ").";
			}
			writeFile(bw, lineFact);
		}
	}

	/**
	 * reads the taxis.csv file, and creates a prolog style file with facts concerning taxis
	 * returns a linked list with taxis
	 **/
	private static LinkedList<Taxi> readTaxisFile(BufferedReader br, BufferedWriter bw, String cvsSplitBy)
			throws NumberFormatException, IOException {
		LinkedList<Taxi> taxis = new LinkedList<Taxi>();

		String line = "";
		String[] lineArray = null;

		line = br.readLine(); // read first line with parameters

		while ((line = br.readLine()) != null) {
			lineArray = line.split(cvsSplitBy);

			Taxi simpleTaxi = new Taxi();
			Position taxiCoord = new Position();
			
			/* taxi's initial coordinates */
			taxiCoord.setX(Double.parseDouble(lineArray[0]));
			taxiCoord.setY(Double.parseDouble(lineArray[1]));
			simpleTaxi.setMyCoord(taxiCoord);
			simpleTaxi.setId(Integer.parseInt(lineArray[2]));

			taxis.add(simpleTaxi);

			/* format capacity so it appears as a list in fact */
			String start = lineArray[4].substring(0, lineArray[4].indexOf("-"));
			String end = lineArray[4].substring(lineArray[4].indexOf("-")+1);
			String capacity = start + "," + end; 
			
			/* format languages so they appear as a list in fact */
			String[] languages = null;
			String languageString = "";
			if (lineArray[5].contains("|")) {
				languages = lineArray[5].split("\\|");
				for (int i = 0; i < languages.length; i++) {
					if (i != (languages.length - 1)) {
						languageString += languages[i] + ",";
					} else {
						languageString += languages[i];
					}
				}
			} else
				languageString = lineArray[5];

			/* replace parenthesis from description with "'" */
			lineArray[9] = lineArray[9].replace("(", "'");
			lineArray[9] = lineArray[9].replace(")", "'");

			/*
			 * available: lineArray[3] 
			 * capacity: lineArray[4] 
			 * languages: lineArray[5] 
			 * rating: lineArray[6] 
			 * long_distance: lineArray[7]
			 * type: lineArray[8] 
			 * description: lineArray[9]
			 */
			String taxiFact = "taxi(" + lineArray[2] + "," + lineArray[3] + "," + "["
					+ capacity + "]" + "," + "[" + languageString + "]" + "," + lineArray[6] + 
					"," + lineArray[7] + "," + lineArray[8] + "," + lineArray[9] + ").";
			writeFile(bw, taxiFact);
		}
		return taxis;
	}

	/**
	 * Parses the client.csv input file, generating a Client Object and
	 * creates a prolog style file with facts concerning client
	 **/
	private static Client readClientFile(BufferedReader br, BufferedWriter bw, String cvsSplitBy)
			throws NumberFormatException, IOException {
		String line = "";
		String[] lineArray = null;

		Client cl = new Client();
		Position clCoord = new Position();
		Position destCoord = new Position();

		line = br.readLine(); // read first line with parameters

		while ((line = br.readLine()) != null) {
			lineArray = line.split(cvsSplitBy);

			/* client's coordinates */
			clCoord.setX(Double.parseDouble(lineArray[0]));
			clCoord.setY(Double.parseDouble(lineArray[1]));
			cl.setMyCoord(clCoord);
			
			/* destination's coordinates */
			destCoord.setX(Double.parseDouble(lineArray[2]));
			destCoord.setY(Double.parseDouble(lineArray[3]));
			cl.setDestCoord(destCoord);

			/*
			 * time: lineArray[4] 
			 * persons: lineArray[5] 
			 * language: lineArray[6]
			 * luggage: lineArray[7]
			 */
			String clientFact = "client(" + lineArray[4].replace(':', '.') + "," + lineArray[5] + "," + lineArray[6]
					+ "," + lineArray[7] + ").";
			writeFile(bw, clientFact);
		}
		return cl;
	}

	/**
	 * reads the nodes.csv file, constructs the hashmap, 
	 * creates a prolog style file with facts concerning the links between the nodes(which node is the next
	 * of each node, according to oneway value from lines file) and calculates the nearest node to the taxi and to the client
	 **/
	private static HashMap<Position, LinkedList<Street>> readNodesFile(BufferedReader br, JavaProlog query, BufferedWriter nodebw,
			BufferedWriter nodeLinksBw, String cvsSplitBy, LinkedList<Taxi> taxisList, Client client) throws NumberFormatException, IOException {
		HashMap<Position, LinkedList<Street>> myMap = new HashMap<Position, LinkedList<Street>>();

		String line1 = "";
		String line2 = "";
		String[] lineArray1 = null;
		String[] lineArray2 = null;
		
		line1 = br.readLine(); // read first line with parameters

		/*
		 * We read nodes in pairs and utilize a stack, so that each street
		 * object that goes into the hashmap has a previous and next neighbor,
		 * if said neighbors exist
		 */
		line1 = br.readLine();
		while ((line2 = br.readLine()) != null) {

			lineArray1 = line1.split(cvsSplitBy);
			lineArray2 = line2.split(cvsSplitBy);

			Street currentNode = new Street();
			LinkedList<Street> nodes = new LinkedList<Street>();

			// currentCoord will serve as a key for the hashmap
			Position currentCoord = new Position();
			currentCoord.setX(Double.parseDouble(lineArray1[0]));
			currentCoord.setY(Double.parseDouble(lineArray1[1]));

			currentNode.setLineId(lineArray1[2]);
			currentNode.setNodeId(lineArray1[3]);

			if (lineArray1.length > 4)
				currentNode.setName(lineArray1[4]);
			else
				currentNode.setName(null);


			/* write belongsTo() fact to nodeFacts.pl file */
			String belongsToFact = "belongsTo(" + lineArray1[3] + "," + lineArray1[2] + ").";
			writeFile(nodebw, belongsToFact);
			
			
			// streets with same line_id
			if (lineArray1[2].equals(lineArray2[2])) {

				/* oneway is X to the following fact : line(lineArray1[2],X,_,_,_)
				 * oneway = 	yes	  -> next(lineArray1[3],lineArray2[3],X2coord,Y2coord)
				 * 				-1 	  -> next(lineArray2[3],lineArray1[3],X1coord,Y1coord)
				 * 			no or nn  -> next(lineArray1[3],lineArray2[3],X2coord,Y2coord) && 
				 * 				   		 next(lineArray2[3],lineArray1[3],X1coord,Y1coord)
				 * */
				String oneway = query.lineOnewayQuery(query.getJip(), lineArray1[2]);
				if (oneway.equals("yes")) {
					/* next(X,Y,Xycoord,Yycoord) */	
					String nextFact = "next(" + lineArray1[3] + "," + lineArray2[3] + "," 
											+ lineArray2[0] + "," + lineArray2[1] + ").";
					writeFile(nodeLinksBw, nextFact);
				}
				else if ((oneway.equals("-1"))) {
					/* next(Y,X,Xxcoord,Yxcoord) */
					String nextFact = "next(" + lineArray2[3] + "," + lineArray1[3] + "," 
											+ lineArray1[0] + "," + lineArray1[1] + ").";
					writeFile(nodeLinksBw, nextFact);
				}
				else if ( ( oneway.equals("no") ) || ( oneway.equals("nn") ) ) {
					/* nn : not given value, so consider road as two-way
					 * next(X,Y,Xycoord,Yycoord) and next(Y,X,Xxcoord,Yxcoord) */
					String nextFact1 = "next(" + lineArray1[3] + "," + lineArray2[3] + "," 
											+ lineArray2[0] + "," + lineArray2[1] + ").";
					writeFile(nodeLinksBw, nextFact1);
					String nextFact2 = "next(" + lineArray2[3] + "," + lineArray1[3] + "," 
											+ lineArray1[0] + "," + lineArray1[1] + ").";
					writeFile(nodeLinksBw, nextFact2);
				}
			}
			
			// add currentNode to nodes list(nodes with same coordinates)
			nodes.add(currentNode);

			// add to HashMap
			if (myMap.containsKey(currentCoord)) {

				/*
				 * if an entry for said key already exists, it means there is an
				 * intersection --add currentNode to that list--
				 */

				myMap.get(currentCoord).add(currentNode);
			} else {
				myMap.put(currentCoord, nodes);
			}

			// update taxis' and client's nearest coordinates
			for (Taxi taxi : taxisList) {
				taxi.calculateNear(currentCoord);
			}
			client.calculateNear(currentCoord);
			client.calculateDestNear(currentCoord);

			line1 = line2;
		} // end-of-while

		// insert last line of file(which is string line1) exactly as above
		lineArray1 = line1.split(cvsSplitBy);

		Street currentNode = new Street();
		LinkedList<Street> nodes = new LinkedList<Street>();

		Position currentCoord = new Position();
		currentCoord.setX(Double.parseDouble(lineArray1[0]));
		currentCoord.setY(Double.parseDouble(lineArray1[1]));

		currentNode.setLineId(lineArray1[2]);
		currentNode.setNodeId(lineArray1[3]);
		if (lineArray1.length > 4)
			currentNode.setName(lineArray1[4]);
		else
			currentNode.setName(null);


		/* write fact to file */
		String belongsToFact = "belongsTo(" + lineArray1[3] + "," + lineArray1[2] + ").";
		writeFile(nodebw, belongsToFact);
		
		nodes.add(currentNode);

		if (myMap.containsKey(currentCoord)) {
			myMap.get(currentCoord).add(currentNode);
		} else {
			myMap.put(currentCoord, nodes);
		}

		// update taxis' and client's nearest coordinates
		for (Taxi taxi : taxisList) {
			taxi.calculateNear(currentCoord);
		}
		client.calculateNear(currentCoord);
		client.calculateDestNear(currentCoord);
		
		return myMap;
	}

	
	
	/**
	 * A* implementation. 
	 * Arguements:
	 * 		 
	 * 		the hashmap (our visualization of the map )
	 * 		a taxi's position to serve as START
	 * 		client's position to serve as GOAL
	 * 
	 * returns a position Object which is the GOAL node fully evaluated 
	 * 
	 **/
	private static Position Astar(JavaProlog query, HashMap<Position, LinkedList<Street>> map,
								Position source,Position dest) throws JIPSyntaxErrorException, IOException{
		
		Comparator<Position> comparator = new MyComparator();
		PriorityQueue<Position> openSet = new PriorityQueue<Position>(10, comparator);
		
		HashSet<Position> closedSet = new HashSet<Position>();
		
		double tentativegScore;
		//Position neighbor;
		
		Position start = new Position();
		start.setX(source.getX());
		start.setY(source.getY());
		
		LinkedList<Street> crossroads = map.get(start);
		
		
		start.setgScore(0.0);     //cost for going from start to start is 0
		
		//for the start node f score is totally heuristic
		start.setfScore(start.getEstimatedDistanceToGoal(dest));    
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
			
			
			if (curr.equals(dest)){     //goal is reached
				return curr;
			}
			
			closedSet.add(curr);
			for (Street node : crossroads) {
				
					Double priority = query.findPriority(query.getJip(), node.getLineId());
				
					LinkedList<Position> neighbors = query.findNext(query.getJip(), node.getNodeId());	
					/* visit each neighbor and add it to the openset */
					for (Position neighbor : neighbors) {
						// previous neighbor exists and is not fully evaluated
						if ( (neighbor != null) && ( !(closedSet.contains(neighbor)) ) ){
							
							// distance from start to previous neighbor
							tentativegScore = curr.getgScore() + ( curr.getEdgeWeight(neighbor) - priority ); 
							
							// neighbor hasn't been visited before
							if ( !(openSet.contains(neighbor)) ) {
								//This is the best path until now
								neighbor.setCameFrom(curr);
								neighbor.setgScore(tentativegScore);
								neighbor.setfScore(neighbor.getgScore() + 
										 	 		neighbor.getEstimatedDistanceToGoal( dest ) );
								neighbor.setRouteLength( curr.getRouteLength() + curr.getEdgeWeight(neighbor) );  // update routeLength until this position
								
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
										 	 		neighbor.getEstimatedDistanceToGoal( dest ) );
								neighbor.setRouteLength( curr.getRouteLength() + curr.getEdgeWeight(neighbor) );  // update routeLength until this position
							}
						}
					}

			}
		}
		// A* failed---there is no path from start to goal
		return null;
	}
	
	/**
	* reconstructPath returns a list 
	* with the x,y coordinates of each node
	* from source to solution
	**/
	private static ArrayList<Coordinates> reconstuctPath(Position solution, Position source) {
		
		ArrayList<Coordinates> lista = new ArrayList<Coordinates>();
		
		Coordinates start =  new Coordinates(source.getX(), source.getY());
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
	
	/**
	 * Write to files the route(Coordinates) of taxis from initial position 
	 * to client's start position
	 * */
	public static void writeResults(LinkedList<Taxi> taxisList) {
		/*
		for each taxi create an out_id.txt file
		with the nodes of the route
		*/
		PrintWriter writer;
		String filename;
		
		for (Taxi taxi : taxisList) {
			// write results only for suitable taxis, whose route has been calculated
			if ( taxi.isSuitable() ) {
				try{
					filename = "./results/out_"+taxi.getId()+".txt";
				     writer = new PrintWriter(filename, "UTF-8");
				     //writer.println("scoref = "+ taxi.getFinalScore());
						for ( Coordinates coord :taxi.getTotalPath() ) {
							writer.println(coord.toString());
						}
				     
				     writer.close();
				} catch (IOException e) {
				   System.err.println(taxi.getId());
				   System.err.println("problem with creating file");
				}
			}
		}
	}
	
		
	
	
	
	/**
	 * MAIN METHOD
	 * */
	public static void main(String[] args) throws IOException {
		
		// Check how many arguments were passed in
		if (args.length != 5) {
			System.out.println("Proper Usage is: client.csv taxis.csv lines.csv nodes.csv traffic.csv");
			System.exit(0);
		}
		
		String avoidToll = readTollAvoidance();
		
		System.out.println();
		System.out.println("Calculating client's route length...");
		if ( avoidToll.equals("yes") )
			System.out.println("Filter : 'Avoid tolls' is ENABLED\n");
		else if ( avoidToll.equals("no") )
			System.out.println("Filter : 'Avoid tolls' is DISABLED\n");
		
		/* class used for making Prolog queries using Java */
		JavaProlog makeQuery=  new JavaProlog();
		
		BufferedReader br = null;
		String cvsSplitBy = ",";
		DecimalFormat df = new DecimalFormat("#.00"); /* keep only 2 decimal places */
		
		/* Prolog files, containing facts about our "world" */
		String clientFile = "clientFacts.pl";
		String taxiFile = "taxiFacts.pl";
		String nodeFile = "nodeFacts.pl";
		String nodeLinksFile = "nodeLinksFacts.pl";
		String lineFile = "lineFacts.pl";
		String trafficFile = "trafficFacts.pl";
		String rulesFile = "rules.pl";

		
		/***********************************Read and format client.csv***********************************************/
		BufferedWriter clientBufferedWriter = new BufferedWriter(new FileWriter(clientFile));

		// create client object from file
		br = new BufferedReader(new FileReader(args[0]));
		Client client = readClientFile(br, clientBufferedWriter, cvsSplitBy);

		// Close and load file
		clientBufferedWriter.close();
		makeQuery.getJip().consultFile(clientFile);
		
		/*********************************** Read and format taxis.csv ***********************************************/
		BufferedWriter taxiBufferedWriter = new BufferedWriter(new FileWriter(taxiFile));

		// create a taxi object list from file
		br = new BufferedReader(new FileReader(args[1]));
		LinkedList<Taxi> taxisList = readTaxisFile(br, taxiBufferedWriter, cvsSplitBy);

		// Close and load file
		taxiBufferedWriter.close();
		makeQuery.getJip().consultFile(taxiFile);
		
		/*********************************** Read and format lines.csv ***********************************************/
		BufferedWriter lineBufferedWriter = new BufferedWriter(new FileWriter(lineFile));

		// read lines.csv file
		br = new BufferedReader(new FileReader(args[2]));
		readLinesFile(br, lineBufferedWriter, cvsSplitBy, avoidToll);

		// Close and load file
		lineBufferedWriter.close();
		makeQuery.getJip().consultFile(lineFile);
		
		/*********************************** Read and format nodes.csv ***********************************************/
		BufferedWriter nodeBufferedWriter = new BufferedWriter(new FileWriter(nodeFile));
		BufferedWriter nodeLinksBufferedWriter = new BufferedWriter(new FileWriter(nodeLinksFile));

		// create the hashmap of the nodes
		br = new BufferedReader(new FileReader(args[3]));
		HashMap<Position, LinkedList<Street>> nodesMap = readNodesFile(br, makeQuery, nodeBufferedWriter, nodeLinksBufferedWriter, cvsSplitBy, taxisList,
				client);

		// Close and load files
		nodeBufferedWriter.close();
		nodeLinksBufferedWriter.close();
		makeQuery.getJip().consultFile(nodeFile);
		makeQuery.getJip().consultFile(nodeLinksFile);

		/*********************************** Read and format traffic.csv ***********************************************/
		BufferedWriter trafficBufferedWriter = new BufferedWriter(new FileWriter(trafficFile));

		// read lines.csv file
		br = new BufferedReader(new FileReader(args[4]));
		readTrafficFile(br, trafficBufferedWriter, cvsSplitBy);

		// Close and load file
		trafficBufferedWriter.close();
		makeQuery.getJip().consultFile(trafficFile);
		
		
		// Load rules.pl file
		makeQuery.getJip().consultFile(rulesFile);
		
		/*********************************** A* algorithm(client_start --> client_dest) ***********************************************/
		/* A* algorithm from client's start position to client's destination */
		Position clientSolution = Astar(makeQuery, nodesMap, client.getNear(),client.getDestNear());
		
		if (clientSolution != null){
			/* factor 100 is used because of scaling */
			System.out.println("Client's route is " +  String.format( "%.2f", 100*clientSolution.getRouteLength() ) + " km approximately.\n");
			client.setTotalPath( reconstuctPath(clientSolution, client.getNear()) );
			Double clientDistance = Double.parseDouble( df.format(100*clientSolution.getRouteLength() ) );
			client.setDistanceToDest(clientDistance);
		}
		
		/* write client's route coordinates to file */
		PrintWriter writer;
		String filename;
		File dir = new File("results");
		dir.mkdir();
		
		try{
			filename = "./results/out_client.txt";
		     writer = new PrintWriter(filename, "UTF-8");
		     //writer.println("score = "+ clientSolution.getgScore());
				for ( Coordinates coord :client.getTotalPath() ) {
					writer.println(coord.toString());
				}
		     
		     writer.close();
		} catch (IOException e) {
		   System.err.println("problem with creating file");
		}
		
		// find suitable taxis for client's route 
		makeQuery.findSuitableTaxis(makeQuery.getJip(), client.getDistanceToDest(), taxisList);
		
		/*********************************** A* algorithm( (for each suitable taxi) --> client_start) ***********************************************/
		System.out.println("Searching for available taxis... Please wait... :) ");
		if ( avoidToll.equals("yes") )
			System.out.println("Filter : 'Avoid tolls' is ENABLED\n");
		else if ( avoidToll.equals("no") )
			System.out.println("Filter : 'Avoid tolls' is DISABLED\n");
		
		/* A* algorithm from taxis to clients */
		for (Taxi taxi: taxisList) {	// execute only for suitable taxis
			if ( taxi.isSuitable() ) {
				//for each taxi, run A* and calculate final path, if A* was successful	
				Position taxiSolution = Astar(makeQuery, nodesMap,taxi.getNear(),client.getNear());
				//System.out.println("gScore = " + solution.getgScore());
				taxi.setFinalScore(taxiSolution.getgScore());
				
				if (taxiSolution != null){
					taxi.setTotalPath( reconstuctPath(taxiSolution, taxi.getNear()) );
					Double taxiDistance = Double.parseDouble( df.format(100*taxiSolution.getRouteLength() ) );
					taxi.setDistanceToClient(taxiDistance);
				}
			}
		}
		/* for each taxi write the coordinates of route to a file */
		writeResults(taxisList);

		/* Print 2 requested rankings
		 * Ranking 1: Ids of taxis are sorted according the distance between taxi and client
		 * Ranking 2: Ids of taxis are sorted according the rating and the type of vehicle
		 * Both rankings are based on the suitability of taxis for the client's route 
		 *  */
		System.out.println("****************** RANKING 1 *****************");
		System.out.println("************ distance to client ************");
		String[] array1 = makeQuery.findRanking1(makeQuery.getJip(), taxisList);		
		
		/* in case k > #suitable taxis */
		int size = 0;
		if (array1.length < k) 
			size = array1.length;
		else
			size = k;		
		
		for (int i = 0; i < size; i++) {
			String id = array1[i];
			for (Taxi taxi : taxisList) {
				if ( Integer.parseInt(id) == taxi.getId() )
					System.out.println(i+1 + ")" + id + " || ~" + taxi.getDistanceToClient() + " km away");
			}
		}
		
		
		
		System.out.println("");
		System.out.println("******************* RANKING 2 *******************");
		System.out.println("************ rating and vehicle type ************");
		System.out.println("************** according ranking 1 ************");
		String[] array2 = makeQuery.findRanking2(makeQuery.getJip(), array1, size);
		
		for (int i = 0; i < size; i++) {
			String id = array2[i];
			for (Taxi taxi : taxisList) {
				if ( Integer.parseInt(id) == taxi.getId() )
					System.out.println(i+1 + ") " + id );
			}
		}
	}
}
