/* Copyright 2012 David Pearson.
 * BSD License.
 */

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a location on a standard Hex board.
 *
 * @author David Pearson
 */
public class Location {
	public int x;
	public int y;

	/**
	 * Creates a new Location object with the supplied coordinates.
	 *
	 * @param x1 the new Location's x coordinate
	 * @param y1 the new Location's y coordinate
	 */
	public Location(int x1, int y1) {
		x=x1;
		y=y1;
	}

	/**
	 * Gets Locations that share an edge with this Location.
	 *
	 * @return An ArrayList of Locations that are adjacent
	 */
	public ArrayList<Location> getAdjacentLocations() {
		ArrayList<Location> l=new ArrayList<Location>();

		if (x>0) {
			l.add(new Location(x-1, y));
		}

		if (x<6) {
			l.add(new Location(x+1, y));
		}

		if (y>0) {
			l.add(new Location(x, y-1));
		}

		if (y<6) {
			l.add(new Location(x, y+1));
		}

		if (x>0 && y>0) {
			l.add(new Location(x-1, y-1));
		}

		if (x<6 && y<6) {
			l.add(new Location(x+1, y+1));
		}

		return l;
	}

	/**
	 * Gets Bridges that include this Location as an end Location.
	 *
	 * @return An ArrayList of Bridges that are adjacent
	 */
	public ArrayList<Bridge> getBridges() {
		ArrayList<Bridge> bridges=new ArrayList<Bridge>();

		if (x+2<7 && y+1<7 && x-2>0 && y-1>0) {
			Bridge b=new Bridge();
			b.l1=new Location(x+2, y+1);
			b.l2=new Location(x-2, y-1);

			ArrayList<Location> midLocs=new ArrayList<Location>();
			midLocs.add(new Location(x+1, y));
			midLocs.add(new Location(x+1, y+1));
			midLocs.add(new Location(x-1, y));
			midLocs.add(new Location(x-1, y-1));

			b.mids=midLocs;

			b.dir=2;

			bridges.add(b);
		}

		if (x+1<7 && y-1>0 && x-1>0 && y+1<7) {
			Bridge b=new Bridge();
			b.l1=new Location(x+1, y-1);
			b.l2=new Location(x-1, y+1);

			ArrayList<Location> midLocs=new ArrayList<Location>();
			midLocs.add(new Location(x+1, y));
			midLocs.add(new Location(x, y-1));
			midLocs.add(new Location(x-1, y));
			midLocs.add(new Location(x, y+1));

			b.mids=midLocs;

			b.dir=1;

			bridges.add(b);
		}

		return bridges;
	}

	/**
	 * Compares this Location and another for equality.
	 *
	 * @param l The Location to compare to
	 *
	 * @return true if the two have the same coordinates, else false
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true; 
		if (obj == null || getClass() != obj.getClass()) return false; 

		Location location = (Location) obj; 
    	return x == location.x && y == location.y; 
	}

	/**
	 * Gets a String representation of this Location.
	 *
	 * @return A String describing this Location
	 */
	@Override
	public String toString() {
		return "("+x+", "+y+")";
	}
	
	/**
	 * Gets a hash code for this Location.
	 *
	 * @return A hash code for this Location
	 */
	@Override
	public int hashCode() {
		return Objects.hash(x,y);
	}
}
