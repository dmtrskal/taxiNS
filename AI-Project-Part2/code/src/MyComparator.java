/*
 * a Comparator class implementation for the
 * priority queue used in A* as openSet
 * It sorts the entries based on the lowest fScore
 * of each Position object
*/

import java.util.Comparator;

class MyComparator implements Comparator<Position> {

    public int compare(Position a, Position b) {
       if (a.getfScore() < b.getfScore())
        {
            return -1;
        }
        if ( a.getfScore() > b.getfScore())
        {
            return 1;
        }
        return 0;
    }
}