package model;

import java.util.HashMap;

public class GraphNode {
	
	public static HashMap<Tile, Boolean> reached = new HashMap<Tile, Boolean>();

	private Tile tile;
	private int dist, relDir, curDir;
	
	public GraphNode(Tile t, int d, int rel, int cur) {
		tile = t;
		dist = d;
		relDir = rel;
		curDir = cur;

		reached.put(tile, true);
	}

	public Tile tile() {
		return tile;
	}
	
	public int dist() {
		return dist;
	}
	
	public int relDir() {
		return relDir;
	}
	
	public int curDir() {
		return curDir;
	}
	
	public static void reset() {
		reached = new HashMap<Tile, Boolean>();
	}
	
	public int compareTo(GraphNode n) {
		if(dist < n.dist)
			return -1;
		else if(dist > n.dist)
			return 1;
		return 0;
	}
	
}
