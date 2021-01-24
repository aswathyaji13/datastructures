package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {

	// prevent instantiation
	private Trie() {
	}

	/**
	 * Builds a trie by inserting all words in the input array, one at a time, in
	 * sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!) The words in the
	 * input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		// makes root as a TrieNode Object
		TrieNode r = new TrieNode(null, null, null);

		// forloop to build the trie from all the words
		for (int i = 0; i <= allWords.length - 1; i++) {
			r = buildingMethod(r, allWords, i, false);
		}

		// returns root
		return r;
	}

	private static String findBeg(String one, String two) {
		// finds smallest value
		int small = one.length() < two.length() ? one.length() : two.length();
		// initialize beginning of word
		String beg = "";

		// for loop search to find the beginning
		for (int i = 0; i <= small - 1; i++) {
			if (one.charAt(i) != two.charAt(i)) {
				// break instantly once u find a difference
				break;
			}
			// if same continue loop
			beg += one.substring(i, i + 1);
		}
		// return statement
		return beg;
	}

	// helps to find if the words are similar by cross checking the first character
	private static boolean alike(String first, String second) {
		return (second.charAt(0) == first.charAt(0));
	}

	// building method
	public static TrieNode buildingMethod(TrieNode one, String[] words, int i, boolean inside) {
		// new indexes object
		Indexes index = new Indexes(i, (short) 0, (short) (words[i].length() - 1));
		// new trienode object
		TrieNode buildingMethod = new TrieNode(index, null, null);

		if (one.sibling == null && one.firstChild == null && one.substr == null) {
			// sets only if statement is true
			one.firstChild = buildingMethod;

			return one;
		}

		TrieNode head = one;
		TrieNode bef = null;
		TrieNode ptr = head.firstChild;


		while (inside && ptr != null && !(alike(words[ptr.substr.wordIndex].substring(ptr.substr.startIndex),words[i].substring(head.substr.endIndex+1)))) {
			bef = ptr;
			ptr = ptr.sibling;
		}
		
		while (ptr != null && !(alike(words[ptr.substr.wordIndex].substring(ptr.substr.startIndex), words[i]))&& !inside) {
			bef = ptr;
			ptr = ptr.sibling;
		}
		
		

		if (ptr != null) {
			if (ptr.firstChild != null) {
				// prefix string
				String prefix = words[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1);

				if (!(words[i].substring(0, prefix.length()).contains(prefix))) {
					String newPre = findBeg(words[i], prefix);
					Indexes parentInd = new Indexes(ptr.substr.wordIndex, (short) 0, (short) (newPre.length() - 1));

					TrieNode newParent = new TrieNode(parentInd, ptr, null);

					ptr.substr.startIndex = (short) newPre.length();
					buildingMethod.substr.startIndex = (short) newPre.length();
					ptr.sibling = buildingMethod;

					if ((bef == null) == true) {
						head.firstChild = newParent;
					} else {
						bef.sibling = newParent;
					}
					return head;
				}
				ptr = buildingMethod(ptr, words, i, true);
				return head;

			} else {
				String pre = findBeg(words[i], words[ptr.substr.wordIndex]);
				Indexes parentInd = new Indexes(ptr.substr.wordIndex, (short) 0, (short) (pre.length() - 1));

				if ((head.substr != null) == true) {
					parentInd.startIndex = (short) (head.substr.endIndex + 1);
				}
				TrieNode parent = new TrieNode(parentInd, ptr, ptr.sibling);
				ptr.substr.startIndex = (short) pre.length();
				buildingMethod.substr.startIndex = (short) pre.length();
				ptr.sibling = buildingMethod;
				if ((bef == null) == true) {
					head.firstChild = parent;
				} else {
					bef.sibling = parent;
				}
				return head;
			}
		} else {

			if (inside == true) {
				buildingMethod.substr.startIndex = (short) (head.substr.endIndex + 2 - 1);
			}
			bef.sibling = buildingMethod;
			return head;

		}
	}

	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf
	 * nodes in the trie whose words start with this prefix. For instance, if the
	 * trie had the words "bear", "bull", "stock", and "bell", the completion list
	 * for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell";
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and
	 * "bell", and for prefix "bell", completion would be the leaf node that holds
	 * "bell". (The last example shows that an input prefix can be an entire word.)
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be", the
	 * returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root     Root of Trie that stores all words to search on for
	 *                 completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix   Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the
	 *         prefix, order of leaf nodes does not matter. If there is no word in
	 *         the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/

		ArrayList<TrieNode> arrL = new ArrayList<TrieNode>();
		// returns
		return recursion(root, allWords, prefix, arrL);
	}

	public static ArrayList<TrieNode> recursion(TrieNode head, String[] allWords, String prefix,
			ArrayList<TrieNode> list) {
		//out on first if
		if ((head == null) == true) {
			return list;
		}
		if (alike(prefix, allWords[head.substr.wordIndex]) == true) {

			if (prefix.equals(allWords[head.substr.wordIndex].substring(0, head.substr.endIndex + 1)) == true) {
				if (allWords[head.substr.wordIndex].length() == allWords[head.substr.wordIndex]
						.substring(0, head.substr.endIndex + 1).length()) {
					list.add(head);
					return list;
				}
				return recursion(head.firstChild, allWords, prefix, list);
			}

				if ((head.substr == null) == true) {
					list = recursion(head.firstChild, allWords, prefix, list);

					if ((list.size() == 0) == true) {
						return null;
					}
					return list;
				}

				else if (!(alike(prefix.substring(head.substr.startIndex),
						allWords[head.substr.wordIndex].substring(head.substr.startIndex)))) {
					list = recursion(head.sibling, allWords, prefix, list);
					return list;
				}

				else if (allWords[head.substr.wordIndex].contains(prefix)
						&& (head.substr.endIndex + 1) == (allWords[head.substr.wordIndex].length())) {
					list.add(head);
					return recursion(head.sibling, allWords, prefix, list);
				}

				if ((list == null) == true) {
					list = recursion(head.sibling, allWords, prefix, list);
				}
				// assigns list
				list = recursion(head.firstChild, allWords, prefix, list);
				return list;
			}
		
		
		return recursion(head.sibling, allWords, prefix, list);

	}

	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}

	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}

		if (root.substr != null) {
			String pre = words[root.substr.wordIndex].substring(0, root.substr.endIndex + 1);
			System.out.println("      " + pre);
		}

		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}

		for (TrieNode ptr = root.firstChild; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < indent - 1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent + 1, words);
		}
	}
}
