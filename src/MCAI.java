/* Copyright 2012 David Pearson.
 * BSD License.
 */

import java.util.ArrayList;
import java.util.*;

/**
 * An AI that uses Monte Carlo Tree Search to play Hex.
 *
 * @author David Pearson
 */
public class MCAI extends AI {
	private int aiplayer=1;
	private int minLen=49;
	private Location lastPlayed;
	public int diffLevel=75;
	public int humanplayer=1;
	/**
	 * The default constructor.
	 * Assumes that the player is 1.
	 */
	public MCAI() {}

	/**
	 * Creates a new instance of MCAI.
	 *
	 * @param player The color to play as (see Constants.java)
	 */
	public MCAI(int player) {
		aiplayer=player;
		if (aiplayer == 1) humanplayer = 2;
	}

	/**
	 * Gets the color this AI is playing as.
	 *
	 * @return The color that the AI is playing as (see Constants.java)
	 */
	public int getPlayerCode() {
		return aiplayer;
	}

	public boolean hasWon(int[][] board, int player) {
		// Check if the player has won
		HashSet<Location> side1 = new HashSet<Location>();
		HashSet<Location> side2 = new HashSet<Location>();

		for (int i = 0; i < board.length; i++) {
			if (player == 1) {
				if (board[0][i] == player) {
					Location l = new Location(i, 0);
					side1.add(l);
				}
				
				if (board[board.length -1][i] == player) {
					Location l = new Location(i, board.length -1);
					side2.add(l);
				}
			}
			
			if (player == 2) {
				if (board[i][0] == player) {
					Location l = new Location(0, i);
					side1.add(l);
				}
				
				if (board[i][board.length -1] == player) {
					Location l = new Location(board.length -1, i);
					side2.add(l);
				}
			}
		}
		
		if (side1.isEmpty() || side2.isEmpty()) {
			return false;
		}
		
		// Check if there is a path from side1 to side2 using DFS
		Stack<Location> stack = new Stack<Location>();
		for (Location l : side1) {
			stack.push(l);
		}
		HashSet<Location> visited = new HashSet<Location>();
		while (!stack.isEmpty()) {
			Location l = stack.pop();
			visited.add(l);
			for (Location adj : l.getAdjacentLocations()) {
				if (side2.contains(adj)) {
					return true;
				}
				if (!visited.contains(adj) && board[adj.y][adj.x] == player) {
					stack.push(adj);
				}
			}
		}

		return false;
	}

	private int countPossibleBridge(int[][] board, int player) {
		int count = 0;
		int opp = 1;
		if (player == 1) opp = 2;
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] == player) {
					Location loc = new Location(x, y);
					
					for (Bridge b :loc.getBridges()) {
						if (b.dir == 1) {
			
							if (board[b.l1.y][b.l1.x] == player) {
								int l11 = board[loc.y][loc.x + 1];
								int l12 = board[loc.y - 1][loc.x];
								
								if (l11 == 0 && l12 == 0) count+=2; 
								else if (l11 == player && l12 == player) count-=50;
								else if ((l11 == opp && l12 == player) || (l11 == player && l12 == player)) {
									if (player == 2) count+=20;
									else count+=5;
								}
							}
							
							if (board[b.l2.y][b.l2.x] == player) {
								int l11 =board[loc.y][loc.x - 1];
								int l12 =board[loc.y + 1][loc.x];
								if (l11 == 0 && l12 == 0) count+=2;
								else if (l11 == player && l12 == player) count-=50;
								else if ((l11 == opp && l12 == player) || (l11 == player && l12 == player)) {
									if (player == 2) count+=20;
									else count+=5;
								}
							}
						}
						
						if (b.dir == 2) {
							if (board[b.l1.y][b.l1.x] == player) {
								int l11 = board[loc.y][loc.x + 1];
								int l12 = board[loc.y + 1][loc.x + 1];
								if (l11 == 0 && l12 == 0) count+=5;
								else if (l11 == player && l12 == player) count-=50;
								else if ((l11 == opp && l12 == player) || (l11 == player && l12 == player)) {
									if (player == 1) count+=50;
									else count+=5;
								}
							}
							
							if (board[b.l2.y][b.l2.x] == player) {
								int l11 = board[loc.y - 1][loc.x - 1];
								int l12 = board[loc.y][loc.x - 1];
								if (l11 == 0 && l12 == 0) count+=5; 
								else if (l11 == player && l12 == player) count-=50;
								else if ((l11 == opp && l12 == player) || (l11 == player && l12 == player)) {
									if (player == 1) count+=50;
									else count+=5;
								}
							}
						}
						
					}
				}
			}
		}
		return count;
	}
	
	private int countEdgeControl(int[][] board, int player) {
		int count = 0;
		
		for (int i = 0; i < board.length; i++) {
			if (player == 1) {
				if (board[0][i] == player) {
					count++;
				}
				
				if (board[board.length -1][i] == player) {
					count++;
				}
			}
			
			if (player == 2) {
				if (board[i][0] == player) {
					count++;
				}
				
				if (board[i][board.length -1] == player) {
					count++;
				}
			}
			
		}
		return count;
	}
	
	private int countEmptyAdjacentHex(int[][] board, int player) {
		int count = 0;
		HashSet<Location> visited = new HashSet<Location>();
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] == player) {
					Location l = new Location(x,y);
					for (Location adj: l.getAdjacentLocations()) {
						if (!visited.contains(adj)) {
							count++;
							visited.add(adj);
						}
						
					}
				}
			}
		}
		return count;
	}
	
	private int findMinNextMoves(int[][] board, int player) {
		int opponent = 1;
		if (player == 1) opponent = 2;
		
		int min = Integer.MAX_VALUE;
		
		for (int i=0; i < board.length; i++) {
			if (player == 1 && board[0][i] != opponent) {
				Location l = new Location(i, 0);
				int numMove = countNumOfNextMoves(board, player, l);
				min = Math.min(min, numMove);
			} 
			
			if (player == 2 && board[i][0] != opponent) {
				Location l = new Location(0, i);
				int numMove = countNumOfNextMoves(board, player, l);
				min = Math.min(min, numMove);
            }			
		}
		
		return min;
	}

	// Count the smallest number of next moves for the player to win
	private int countNumOfNextMoves(int[][] board, int player, Location starting) {
		// queue stores Locations to investigates
		Queue<Location> queue = new LinkedList<>();
		queue.offer(starting);

		// Hashmap store the number of empty hex from Location to the starting edge
		HashMap<Location, Integer> visited = new HashMap<Location, Integer>();
		if (board[starting.y][starting.x] == 0) {
			visited.put(starting, 1);
		} else {
			visited.put(starting, 0);
		}
		
		int opponent = 1;
		if (player == 1) opponent = 2;
		
		// Create the Location instances of the starting edges
		
		int min = Integer.MAX_VALUE;
		// Breath-first-search adjacent Locations of each queue
		while (!queue.isEmpty()) {
            Location pos = queue.poll();
            int dist = visited.get(pos);
            
            // Once reaching to the ending edge, we return the how many time we encounter the empty hex
            if (reachedOtherSide(board, pos, player)) {
            	if (dist == 0) return 0;
                min = Math.min(min, dist) ; // value stored from the hashmap 
            }
    
            // Enqueue all adjacent Hexagons that haven't been visited yet
            for (Location adjPos : pos.getAdjacentLocations()) {
                if (!isVisited(visited, adjPos) && board[adjPos.y][adjPos.x] != opponent) {
                	queue.offer(adjPos);
                    if (board[adjPos.y][adjPos.x] == player) {
                    	visited.put(adjPos, dist);		// we don't count this
                    } else if (board[adjPos.y][adjPos.x] == 0) {
                    	visited.put(adjPos, dist + 1);	// we count this because we see an empty hex
                    }   
                } 
            }
        }
		return min;
	}
	
	private int findLongestPath(int[][] board, int player) {
		int maxLength = 0;
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				HashSet<Location> visited = new HashSet<Location>();
				if (board[y][x] == player) {
					Location l = new Location(x, y);
					int pathLength = dfs(board, l, visited, 1, player);
					maxLength = Math.max(maxLength, pathLength);
				}
			}
		}
		return maxLength;
	}
	
	private int dfs(int[][] board, Location l, HashSet<Location> visited, int pathLength, int player) {
		visited.add(l);
		for (Location adj : l.getAdjacentLocations()) {
			if (board[adj.y][adj.x] == player && !visited.contains(adj)) {
				visited.add(adj);
				pathLength = dfs(board, adj, visited, pathLength + 1, player);
			}
		}
		return pathLength;
	}

	//Helper function for countNumOfNextMoves
	// return true if Location position contains in the Hashmap
	private boolean isVisited(HashMap<Location, Integer> map, Location pos) {
	    for (Location key : map.keySet()) {
	        if (key.equals(pos)) return true;
	    }
		return false;
	}
	
	// Helper function for countNumOfNextMoves
	// return true if l sit at the end of the side of the edge
	private boolean reachedOtherSide(int[][] board, Location l, int player) {
		if (player==1 && l.y == board.length -1 ) {
			return true;
		} else if (player==2 && l.x == board.length -1 ) {
			return true;
		} 
		
		return false;
	}
	
	
	/* 
	 * @author Vuthy Vey
	 * Implement the minimax algorithm with alpha-beta pruning: Monte Carlo Tree Search
	 * @param board: the current board state
	 * @param isMaximizer: true if the current player is the AI, false if the current player is the human
	 * @param depth: the depth of the tree
	 * @param alpha: the best value that the maximizer currently can guarantee at that level or above
	 * @param beta: the best value that the minimizer currently can guarantee at that level or above
	 * @return the score of the current board state
	 */
	private int miniMax(int[][] board, boolean isMaximizer, int depth, int alpha, int beta) {
		ArrayList<Location> possibleLoc = getEmptyLocations(board);
		int possibleSize = possibleLoc.size();
		if (depth == 0) {
	
			int nextMovesScores = (1000 - findMinNextMoves(board, aiplayer)*20) + findMinNextMoves(board, humanplayer)*20;
			
			int emptyHexScores = countEmptyAdjacentHex(board, aiplayer) - countEmptyAdjacentHex(board, humanplayer);
			
			if (possibleSize > 46) {
				emptyHexScores *= 3;
			} else if (possibleSize > 10) {
				emptyHexScores *= 1.2;
			} 
			
			int bridgeScore = countPossibleBridge(board, aiplayer) - countPossibleBridge(board, humanplayer);
			if (possibleSize > 45) {
				bridgeScore *= 5;
			} else if (possibleSize > 40) {
				bridgeScore *= 3;
			} else if (possibleSize > 30) {
				bridgeScore *= 1.5;
			} 
			
			int edgeScore = countEdgeControl(board, aiplayer) - countEdgeControl(board, humanplayer);
			
			int longestPathScore = findLongestPath(board, aiplayer) - findLongestPath(board, humanplayer);
			if (possibleSize > 45) {
				longestPathScore *= 5;
			} else if (possibleSize > 40){
				longestPathScore /= 2;
			} else {
				longestPathScore /= 4;
			}

			
			
			return nextMovesScores  + emptyHexScores + bridgeScore + edgeScore + longestPathScore;
			
		}
		
		if (isMaximizer) {
			for (int i = 0; i < possibleSize; i++) {
				int[][] b = Board.BoardCopy(board);
				Location l = possibleLoc.get(i);
				b[l.y][l.x] = aiplayer;
				
				int score = miniMax(b, true, depth - 1, alpha, beta);
		
				alpha = Math.max(score, alpha);
				
				if (alpha >= beta) {
					return beta;
				}
			}
			
			return alpha;
		} else {
			int opp = 1;
			if (aiplayer == 1) opp = 2;
			
			// Randomize the order of the locations
//			Collections.shuffle(possibleLoc);
	
			for (int i = 0; i < possibleSize; i++) {
				int[][] b = Board.BoardCopy(board);
				Location l = possibleLoc.get(i);
				b[l.y][l.x] = opp;
				
				int score = miniMax(b, true, depth - 1, alpha, beta);
			
				
				beta = Math.min(score, beta);
				
				
				if (alpha >= beta) {
					return alpha;
				}
				
			}
			
			return beta;
		}
		
	}
	
	
	
	// @return an arraylist of possible location that AI can pick
	private ArrayList<Location> getEmptyLocations(int[][] board) {
		ArrayList<Location> arr = new ArrayList<Location>();
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] == 0) {
					Location l = new Location(x, y);
					arr.add(l);
				}
			}
		}
		
		return arr;
	}

	/**
	 * Chooses a location for the next play by this AI.
	 *
	 * @param board The board state used in determining the play location
	 * @param last The last play in the game, as made by the opponent
	 *
	 * @return A location to play at, chosen using MCTS
	 */
	public Location getPlayLocation(int[][] board, Location last) {
		long t=System.currentTimeMillis();
		
		lastPlayed=last;
		if (last == null || last.x == -1) return new Location(3,3);
	
		ArrayList<Location> locations = getEmptyLocations(board);
		Location bestMove = locations.get(0);
		double bestScore = 0;
		
		int bridge = findLongestPath(board, humanplayer);
		
		for ( int i = 0; i < locations.size(); i++) {
			int[][] b = Board.BoardCopy(board);
			Location l = locations.get(i);
			b[l.y][l.x] = aiplayer;
			
			int depth = 1;
			if (locations.size() > 15) {
				depth = 2;
			} else {
				depth = 3;
			}
			
			if (this.hasWon(board, aiplayer)) {
				return locations.get(i);
			}
			double score = miniMax(b, false, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			 // Final move to winn
			if (score > bestScore) {
				bestScore = score;
				bestMove = locations.get(i);
			}
			long curentime = System.currentTimeMillis();
			
		}
		System.out.println(bestMove);
		return bestMove;	
	}
}