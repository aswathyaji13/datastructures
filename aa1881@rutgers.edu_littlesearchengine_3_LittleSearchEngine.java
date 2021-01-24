package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		File document = new File(docFile);
		HashMap<String, Occurrence> info = new HashMap<String, Occurrence>(1000, 2.0f);
		
		
		if (document.exists()==false) {
			throw new FileNotFoundException("File Not Found");
		}
		
		Scanner sc = new Scanner(document);

		while (sc.hasNext()==true) {
			String phrase = getKeyword(sc.next());
			
			if ( phrase != null && phrase.length() > 0 ) {
				if (info.containsKey(phrase)==false) {
					info.put(phrase, new Occurrence(docFile, 1));
					
				} else {
					Occurrence value = info.get(phrase);
					value.frequency++;
				}
			}
		}
		sc.close();

		
		return info;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Occurrence> collection;
		
		for (String key : kws.keySet()) {
			collection = keywordsIndex.containsKey(key) ? keywordsIndex.get(key) : new ArrayList<Occurrence>();
			
			collection.add(kws.get(key));
			
			insertLastOccurrence(collection);
			
			if (collection.size()-1 == 0) {
				
				keywordsIndex.put(key, collection);
				
				continue;
			}
			
			keywordsIndex.replace(key, collection);
		}
	}

	// private method to help strip the string
	private String stripper(String word) {
		String unnecc = ".,?:;!";
		if ( unnecc.indexOf(word.substring(word.length() - 2+1, word.length())) >= 0 && word.length() >= 1) {
			
			return stripper(word.substring(0, word.length() - 2+1));
			
		}
		return word;
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!' NO
	 * OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be
	 * stripped So "word!!" will become "word", and "word?!?!" will also become
	 * "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		String solution = stripper(word.toLowerCase());
		
		for (int i = 0; i <= solution.length()-1; i++) {
			if (Character.isLetter(solution.charAt(i))==false) {
				return null;
			}
		}
		solution = (solution.length() == 0) || (noiseWords.contains(solution)) ? null:solution;
		
		return solution;
	}


	private static ArrayList<Integer> searching(ArrayList<Occurrence> occs, int target, int low, int high,ArrayList<Integer> list) {

		int middle = (low + high) / 2;
		
		list.add(middle);
		
		
		if ((occs.get(middle).frequency > target)==true) {
			if (middle > high-1) {
				return list;
			}
			return searching(occs, target, middle + 1, high, list);
		} 
		else if ((low == high)==true || (occs.get(middle).frequency == target)==true ) {
			return list;
		} 
		else {
			if (middle < low + 1) {
				return list;
			}
			
			return searching(occs, target, low, middle - 1, list);
		}

	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		Occurrence reach = occs.get(occs.size() - 1);
		
		ArrayList<Integer> arr = searching(occs, reach.frequency, 0, occs.size() - 2, new ArrayList<Integer>());
		int index = arr.get(arr.size() - 1);
		
		if (occs.size()-1 == 0) {
			return null;
		}
		
		if (occs.get(index).frequency<=reach.frequency) {
			occs.add(index, occs.get(occs.size() - 1));
		} 
		else {
			occs.add(index + 1, occs.get(occs.size() - 1));
		}
		
		occs.remove(occs.size() - 1);
		
		return arr;
	}


	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(noiseWordsFile));
		
		while (sc.hasNext()==true) {
			String phrase = sc.next();
			noiseWords.add(phrase);
		}
		sc = new Scanner(new File(docsFile));
		
		while (sc.hasNext()==true) {
			String doc = sc.next();
			
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(doc);
			mergeKeywords(kws);
			
		}
		
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies.
	 * 
	 * Note that a matching document will only appear once in the result.
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. That is,
	 * if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all,
	 * result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<String> first = new ArrayList<String>();
		ArrayList<Occurrence> val1;
		ArrayList<Occurrence>val2;
		int ind1 = 0;
		int ind2 = 0;
		val1 = keywordsIndex.containsKey(kw1) ? keywordsIndex.get(kw1) : null;
		val2 = keywordsIndex.containsKey(kw2) ? keywordsIndex.get(kw2) : null;
		
		if ((keywordsIndex.containsKey(kw2)==false) && (keywordsIndex.containsKey(kw1)==false)) {
			return null;
		}
		
		while (val2 != null && ind1 < val1.size() && val1 != null && first.size() < 5 && ind2 < val2.size()) {
			Occurrence one = val1.get(ind1);
			Occurrence two = val2.get(ind2);
			 
			if (two.frequency > one.frequency) {
				if (first.contains(two.document)==false) {
					first.add(two.document);
				}
				ind2++;
			} 
			else if (one.frequency > two.frequency) {
				if (first.contains(one.document)==false) {
					first.add(one.document);
				}
				ind1++;
			}
			else {
				if (first.contains(one.document)==false) {
					first.add(one.document);
				}
				ind2++;
				ind1++;
				
			}
		}
		
		val1 = val1 != null ? val1 : val2;
		
		ind1 = val1 != null ? ind1 : ind2;
		
		while ( ind1 < val1.size() && first.size()+1 <= 5 && val1 != null) {
			if (first.contains(val1.get(ind1).document)==false) {
				first.add(val1.get(ind1).document);
			}
			ind1++;
		}
		
		return first;

	}
}
