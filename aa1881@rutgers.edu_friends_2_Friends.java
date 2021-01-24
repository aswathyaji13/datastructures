package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2. Chain is returned as a
	 * sequence of names starting with p1, and ending with p2. Each pair (n1,n2) of
	 * consecutive names in the returned chain is an edge in the graph.
	 * 
	 * @param g  Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there
	 *         is no path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {

		/** COMPLETE THIS METHOD **/

		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION

		if (g == null || p1 == null || p2 == null) {
			return null;
		}

		boolean[] visited = new boolean[g.members.length];
		Queue<Person> queue = new Queue<Person>();
		Person[] visitedAlready = new Person[g.members.length];
		ArrayList<String> shortestPath = new ArrayList<String>();

		int index = g.map.get(p1);
		visited[index] = true;
		queue.enqueue(g.members[index]);

		while (queue.isEmpty() == false) {

			Person pivot = queue.dequeue();

			int pivotIndex = g.map.get(pivot.name);
			visited[pivotIndex] = true;
			Friend neighbor = pivot.first;

			if (neighbor == null) {
				return null;
			}

			while (neighbor != null) {

				if (visited[neighbor.fnum] == false) {
					visited[neighbor.fnum] = true;
					visitedAlready[neighbor.fnum] = pivot;
					queue.enqueue(g.members[neighbor.fnum]);

					if (g.members[neighbor.fnum].name.equals(p2)) {
						pivot = g.members[neighbor.fnum];

						while (pivot.name.equals(p1) == false) {
							shortestPath.add(0, pivot.name);
							pivot = visitedAlready[g.map.get(pivot.name)];
						}
						shortestPath.add(0, p1);
						return shortestPath;
					}
				}
				neighbor = neighbor.next;
			}
		}

		return null;
	}

	private static ArrayList<ArrayList<String>> firstHelper(Graph g, Person start,
			ArrayList<ArrayList<String>> listOfCliques, boolean[] visited, String school) {

		Queue<Person> queue = new Queue<Person>();
		ArrayList<String> cliquesResults = new ArrayList<String>();

		Person pivot = new Person();
		Friend neighbor;
		queue.enqueue(start);
		visited[g.map.get(start.name)] = true;

		if (start.school.equals(school) != true || start.school == null) {
			queue.dequeue();
			for (int j = 0; j <= visited.length - 1; j++) {

				if (visited[j] != true) {
					return firstHelper(g, g.members[j], listOfCliques, visited, school);
				}

			}
		}

		while (queue.isEmpty() != true) {

			pivot = queue.dequeue();
			neighbor = pivot.first;
			cliquesResults.add(pivot.name);

			while (neighbor != null) {

				if (visited[neighbor.fnum] == false) {
					if (g.members[neighbor.fnum].school != null) {
						if (g.members[neighbor.fnum].school.equals(school) == true) {
							queue.enqueue(g.members[neighbor.fnum]);
						}
					} else {
					}
					visited[neighbor.fnum] = true;
				}
				neighbor = neighbor.next;
			}
		}

		if (listOfCliques.isEmpty() == false && cliquesResults.isEmpty() == true) {

		} else {

			listOfCliques.add(cliquesResults);

		}

		for (int i = 0; i <= visited.length - 1; i++) {
			if (visited[i] == false) {

				return firstHelper(g, g.members[i], listOfCliques, visited, school);
			}
		}
		return listOfCliques;
	}

	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g      Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there
	 *         is no student in the given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {

		/** COMPLETE THIS METHOD **/

		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		if (g == null || school == null) {
			return null;
		}

		ArrayList<ArrayList<String>> listOfCliques = new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[g.members.length];

		return firstHelper(g, g.members[0], listOfCliques, visited, school);

	}
	
	private static ArrayList<String> second(ArrayList<String> connectors, Graph g, Person start, boolean[] visited,
			int[] count, int[] numbersOfDFS, int[] back, ArrayList<String> backward, boolean started) {

		Friend neighbor = start.first;
		visited[g.map.get(start.name)] = true;
		back[g.map.get(start.name)] = count[1];
		numbersOfDFS[g.map.get(start.name)] = count[0];

		while (neighbor != null) {

			if (visited[neighbor.fnum] != true) {
				count[1]++;
				count[0]++;
				connectors = second(connectors, g, g.members[neighbor.fnum], visited, count, numbersOfDFS, back,
						backward, false);

				if (back[neighbor.fnum] >= numbersOfDFS[g.map.get(start.name)]) {
					if ((connectors.contains(start.name) != true && backward.contains(start.name))==true
							|| (connectors.contains(start.name) != true && started != true)) {
						
						connectors.add(start.name);
						
					}
				} 
				else {

					int second = back[neighbor.fnum];
					int first = back[g.map.get(start.name)];

					if (second > first) {
						back[g.map.get(start.name)] = first;
					} 
					else {
						back[g.map.get(start.name)] = second;
					}
				}
				
				backward.add(start.name);
			}
			else {
				int fourth = numbersOfDFS[neighbor.fnum];
				int third = back[g.map.get(start.name)];

				if (fourth > third) {
					back[g.map.get(start.name)] = third;
				} 
				else {
					back[g.map.get(start.name)] = fourth;
				}
			}
			
			neighbor = neighbor.next;
		}
		
		return connectors;
	}

	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no
	 *         connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {

		/** COMPLETE THIS METHOD **/

		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION

		if (g == null == true) {
			return null;
		}
		int[] before = new int[g.members.length];
		int[] numbersOfDFS = new int[g.members.length];

		boolean[] visited = new boolean[g.members.length];
		ArrayList<String> predecessor = new ArrayList<String>();
		ArrayList<String> connectors = new ArrayList<String>();

		for (int i = 0; i <= g.members.length - 1; i++) {
			if (visited[i] != true) {
				connectors = second(connectors, g, g.members[i], visited, new int[] { 0, 0 }, numbersOfDFS, before,
						predecessor, true);
			}
		}
		return connectors;

	}

	
}
