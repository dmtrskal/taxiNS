import java.io.IOException;
import java.util.LinkedList;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

public class JavaProlog {
	
	private JIPEngine jip;
	
	public JavaProlog() {
		jip = new JIPEngine();
	}
	
	public JIPEngine getJip() {
		return jip;
	}

	
	/**
	 * Returns the lineId of the node
	 * */
	public String nodeLineQuery(JIPEngine nodeJip, String nodeId) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser nodeParser = nodeJip.getTermParser();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = " belongsTo(" + nodeId + ",X).";
		
		String lineId = ""; 
		
		jipQuery = nodeJip.openSynchronousQuery(nodeParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			lineId = term.getVariablesTable().get("X").toString();
			System.out.println(nodeId + " belongs to " + lineId);
			term = jipQuery.nextSolution();
		}
		
		return lineId;
	}

	/**
	 * Returns the oneway field of a line
	 * Oneway has one of the following values:(yes,no,-1,nn)
	 * */
	public String lineOnewayQuery(JIPEngine lineJip, String lineId) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser lineParser = lineJip.getTermParser();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = " line(" + lineId + ",X,_,_,_).";
		
		String oneway = "";
		
		jipQuery = lineJip.openSynchronousQuery(lineParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			oneway = term.getVariablesTable().get("X").toString();
			//System.out.println(lineId + " oneway value is: " + oneway);
			term = jipQuery.nextSolution();
		}
	
		return oneway;
	}
	
	/**
	 * Returns a double with the priority of street(line)
	 * This priority will be substracted from the edgeWeight in order to be preffered from taxi
	 * */
	public Double findPriority(JIPEngine jip, String lineId) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser rulesParser = jip.getTermParser();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = "priority(" + lineId + ",_,V).";

		Double prio = 0.0;
		
		jipQuery = jip.openSynchronousQuery(rulesParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			String value = term.getVariablesTable().get("V").toString();
			prio = (Double.parseDouble(value));

			term = jipQuery.nextSolution();
		}
	
		return prio;
	}
	
	/**
	 * Returns a linked-list with all possible next positions from the current position 
	 * use of canMoveFromTo() rule in order to force taxi move through acceptable type of streets 
	 * */
	public LinkedList<Position> findNext(JIPEngine jip, String nodeId) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser nodeLinksParser = jip.getTermParser();
		
		LinkedList<Position> neighbors = new LinkedList<Position>();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = " canMoveFromTo(" + nodeId + ",_,X,Y).";
		
		String nextX;
		String nextY;
		
		jipQuery = jip.openSynchronousQuery(nodeLinksParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			nextX = term.getVariablesTable().get("X").toString();
			nextY = term.getVariablesTable().get("Y").toString();
			Position position = new Position();
			position.setX(Double.parseDouble(nextX));
			position.setY(Double.parseDouble(nextY));
			
			neighbors.add(position);
			term = jipQuery.nextSolution();
		}
	
		return neighbors;
	}
	
	
	/**
	 * Finds all suitable taxis for the client's route and update linked-list with taxis
	 *  */
	public void findSuitableTaxis(JIPEngine jip,Double distance, LinkedList<Taxi> taxisList) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser rulesParser = jip.getTermParser();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = "isSuitable(Id" + "," + distance + ").";
		
		String id;
		
		jipQuery = jip.openSynchronousQuery(rulesParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			id = term.getVariablesTable().get("Id").toString();

			for (Taxi taxi : taxisList) {
				if ( Integer.parseInt(id) == taxi.getId() ) {
					taxi.setSuitable(true);
				}
			}
			term = jipQuery.nextSolution();
		}
	}
	
	/**
	 * Returns a sorted list with all ids of taxis suitable for the client's route 
	 * List is sorted according the distance from taxi to client's start position 
	 *  */
	public String[] findRanking1(JIPEngine jip, LinkedList<Taxi> taxisList) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser rulesParser = jip.getTermParser();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = "ranking1([";
		for (Taxi taxi : taxisList) {
			if ( taxi.isSuitable() ) {
				query += "[" +taxi.getId() + "," + Double.toString( taxi.getDistanceToClient()) + "],";
			}
		}
		query = query.substring(0, query.length()-1);
		query += "],Result).";
		String str = "";
		String sortedList = "";
		
		jipQuery = jip.openSynchronousQuery(rulesParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			sortedList = term.getVariablesTable().get("Result").toString();
				
			term = jipQuery.nextSolution();
		}
		/* used because prolog represents lists using dots */
		str = sortedList.replaceAll("[^0-9]+", " ");
		String[] sortedIds1 = str.trim().split(" ");
		
		return sortedIds1;
	}
	
	
	/**
	 * Returns a sorted list with all ids of taxis suitable for the client's route based on ranking1
	 * List is sorted according the rating and the vehicle type of taxi
	 *  */
	public String[] findRanking2(JIPEngine jip, String[] array, int size) throws JIPSyntaxErrorException, IOException {
		
		JIPTermParser rulesParser = jip.getTermParser();
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		String query = "ranking2([";
		for (int i = 0; i < size; i++) {
			query += array[i] + "," ;
		} 

		query = query.substring(0, query.length()-1);
		query += "],Result).";
		
		String sortedList2 = "";
		String str = "";
		
		jipQuery = jip.openSynchronousQuery(rulesParser.parseTerm(query));
		term = jipQuery.nextSolution();
		while (term != null) {
			sortedList2 = term.getVariablesTable().get("Result").toString();
			
			term = jipQuery.nextSolution();
		}
		/* used because prolog represents lists using dots */
		str = sortedList2.replaceAll("[^0-9]+", " ");
		String[] sortedIds2 = str.trim().split(" ");
		
		return sortedIds2;
	}
	
}
