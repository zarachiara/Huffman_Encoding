import java.io.*;
import java.util.*;

public class FileFreqWordsIterator implements Iterator{
	
	//TODO: Instance variable declaration
	String[] freqWords;
	private String inputFileName;
	// keeps track of whether the current string in the next() method can possibly be a frequent word.  
	private boolean possibleWord = true;
	// keeps track of the characters we've seen so far in each word
	private StringBuilder storedChars = new StringBuilder();
	FileCharIterator charIter;
	
	//TODO: Implement the constructor, might be like fileCharIterator, but this class might just use FileCharIterator
	FileFreqWordsIterator(String inputFileName, int n) {
		freqWords = new String[n];
		this.inputFileName = inputFileName;
		charIter = new FileCharIterator(inputFileName);
		freqWordsHelper(fileWordIterator());
	}
	
	//TODO: Implement, will be like FileCharIterator, but also check the built up string to see if it's empty
	public boolean hasNext() {
		return charIter.hasNext() || storedChars.length() > 0;
	}
	
	//TODO: will build up a string as long as the current string is a possible word.  If the string becomes a word, return the 
	// binary string representation of that word, and if possible words becomes false, begin to return the characters in the built up 
	// string one by one until the string is empty again, at which point possible words will be true again.  
	public String next() {
		if (storedChars.length() == 0) {
			possibleWord = true;
		}
		if (possibleWord) {
			String output = charIter.next();
			while (!output.equals("00100000") && !output.equals("00001010") && charIter.hasNext()) {
				storedChars.append(output);
				for (String word : freqWords) {
					if (word.equals(storedChars.toString())) {
						String ret = storedChars.toString();
						storedChars = new StringBuilder();
						return ret;
					}
				}
				output = charIter.next();
			}
			storedChars.append(output);
			possibleWord = false;
		}
		// take out the first 8 bits of the string 
		String ret = storedChars.substring(0, 8);
		storedChars.delete(0, 8);
		return ret;
	}
	
	public void remove() {
        throw new UnsupportedOperationException("FileCharIterator does not delete from files.");
	}
	
	
	//TODO: add tests 
	// method will alter freqWords to contain the most frequent words in the file
	void freqWordsHelper(HashMap<String, Integer> toSort) {
		// make a comparator to pass in the priority queue
		WordNumber compare = new WordNumber("", 0);
		PriorityQueue<WordNumber> wordQueue = new PriorityQueue<WordNumber>(16, compare);
		for (Map.Entry<String, Integer> n : toSort.entrySet()) {
			wordQueue.add(new WordNumber(n.getKey(), n.getValue()));
		}
		for(int i = 0; i < freqWords.length; i++){
			freqWords[i] = wordQueue.remove().getWord();
		}
	}
	
	//TODO:add tests 
	// a method which will iterate through all of the words in the file, returning them in an ArrayList of HuffmanEncodingNodes
	// which will contain the binary string representation of the word and the words weight
	HashMap<String, Integer> fileWordIterator() {
		HashMap<String, Integer> words = new HashMap<String, Integer>();
		FileCharIterator charIter = new FileCharIterator(inputFileName);
		StringBuilder word = new StringBuilder();
		// take characters one at a time. 
		while (charIter.hasNext()) {
			// store next from the current character 
			String currChar = charIter.next(); 
			// if find a space or a new line, don't add to the end of the string. Instead, see if the words are already in the arrayList. 
			if ((currChar.equals("00100000") || currChar.equals("00001010"))){
				if (word.length() > 8) {	
					if (words.containsKey(word.toString())) {
						words.put(word.toString(), words.get(word.toString()) + 1);
					}else{
						words.put(word.toString(), 1);
					}
					word = new StringBuilder();
	//				boolean seen = false;
	//					for (WordNumber w : words) {
	//						// increase the weight
	//						if (word.toString().equals(w.myWord)) {
	//							//increment frequency 
	//							w.frequency++;
	//							seen = true;
	//							break;
	//						}
	//					}
	//				
	//				// add to arrayList a new word
	//				if (!seen) {
	//					words.add(new WordNumber(word.toString()));
	//				} 
	//				word = new StringBuilder();
				} else {
					word = new StringBuilder();
				}
			} else{
				word.append(currChar);
				// deals with the EOF
				if (!charIter.hasNext()) {
					if (words.containsKey(word.toString())) {
						words.put(word.toString(), words.get(word.toString()) + 1);
					} else {
						words.put(word.toString(), 1);
					}
				}
			}
		}
		return words;
	}

	
	class WordNumber implements Comparator<WordNumber> {
		
		// Instance variables
		private String myWord;
		private int frequency;
		
		// Constructor
		private WordNumber(String word, int f) {
			myWord = word;
			frequency = f;
		}
		
		// Comparator methods will be used for freqWordsHelper
		
		public int compare(WordNumber w1, WordNumber w2) {
			if (w1.frequency < w2.frequency) {
				return 1;
			} else if (w1.frequency > w2.frequency) {
				return -1;
			} else {
				return 0;
			}
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof WordNumber && myWord.equals(((WordNumber) obj).myWord)) {
				return true;
			}
			return false;
		}
		
		// for testing purposes
		
		String getWord() {
			return myWord;
		}
		
		int getFrequency() {
			return frequency;
		}

	}
	
//	public class FreqWordTrie {
//
//	    // Indices 0, 1, 2 represent the letters a, b, c, etc.
//	    // This array contains the nodes at the top level of the FreqWordTrie.
//	    private FreqWordTrieNode[] myWords;
//	    public FreqWordTrie() {
//	        myWords = new FreqWordTrieNode[100];
//	    }
//
//	    public void insertWord(String word) {
//	        // YOUR CODE HERE
//	    	String prefix;
//	    	String toGo;
//	    	if (word.length() > 1) {
//	    		toGo = word.substring(1);
//	    		prefix = word.substring(0, 1);
//	    	} else {
//	    		prefix = word;
//	    		toGo = "";
//	    	}
//	    	insertWord(myWords, prefix, toGo);
//	    }
//	    
//	    public void insertWord(FreqWordTrieNode[] nodes, String prefix, String toGo) {
//	    	int index = charToIndex(prefix.charAt(prefix.length() - 1));
//	    	if (nodes[index] != null) {
//	    		if (toGo.length() > 1) {
//	    			prefix += toGo.substring(0, 1);
//	    			toGo = toGo.substring(1);
//	    		} else {
//	    			prefix += toGo;
//	    			toGo = "";
//	    		}
//	    		insertWord(nodes[index].myDescendants, prefix, toGo);
//	    	} else {
//	    		nodes[index] = new FreqWordTrieNode(prefix);
//	    		if (toGo.equals("")) {
//	    			nodes[index].isCompleteWord = true;
//	    		} else {
//	        		if (toGo.length() > 1) {
//	        			prefix += toGo.substring(0, 1);
//	        			toGo = toGo.substring(1);
//	        		} else {
//	        			prefix += toGo;
//	        			toGo = "";
//	        		}
//	        		insertWord(nodes[index].myDescendants, prefix, toGo);
//	    		}
//	    	}
//	    }
//
//	    // Returns true only for words that were inserted.
//	    public boolean containsWord(String word) {
//	        // YOUR CODE HERE
//	    	String prefix;
//	    	String toGo;
//	    	if (word.length() > 1) {
//	    		prefix = word.substring(0, 1);
//	    		toGo = word.substring(1);
//	    	} else {
//	    		prefix = word;
//	    		toGo = "";
//	    	}
//	        return containsWord(myWords, prefix, toGo);
//	    }
//	    
//	    public boolean containsWord(FreqWordTrieNode[] nodes, String prefix, String toGo) {
//	    	int index = charToIndex(prefix.charAt(prefix.length() - 1));
//	    	if (nodes[index] != null) {
//	    		if (toGo.equals("")) {
//	    			return true;
//	    		} else {
//					if (toGo.length() > 1) {
//					prefix += toGo.substring(0, 1);
//					toGo = toGo.substring(1);
//				} else {
//					prefix += toGo;
//					toGo = "";
//				}
//				return containsWord(nodes[index].myDescendants, prefix, toGo);
//	    		}
//	    	}
//	    	return false;
//	    }
//	    
//	    public void increaseFrequency(String word){
//	    	String prefix;
//	    	String toGo;
//	    	if (word.length() > 1) {
//	    		prefix = word.substring(0, 1);
//	    		toGo = word.substring(1);
//	    	} else {
//	    		prefix = word;
//	    		toGo = "";
//	    	}
//	        increaseFrequencyHelper(myWords, prefix, toGo);
//	    }
//	    
//		public void increaseFrequencyHelper(FreqWordTrieNode[] nodes,
//				String prefix, String toGo) {
//			int index = charToIndex(prefix.charAt(prefix.length() - 1));
//			if (nodes[index] != null) {
//				if (toGo.equals("")) {
//					nodes[index].frequency++;
//				} else {
//					if (toGo.length() > 1) {
//						prefix += toGo.substring(0, 1);
//						toGo = toGo.substring(1);
//					} else {
//						prefix += toGo;
//						toGo = "";
//					}
//					containsWord(nodes[index].myDescendants, prefix, toGo);
//				}
//			}
//		}
//		
//		public ArrayList<FreqWordTrieNode> toArray(FreqWordTrieNode[] toSearch) {
//			ArrayList<FreqWordTrieNode> ret = new ArrayList<FreqWordTrieNode>();
//			for (int i = 0; i < 100; i++) {
//				if (toSearch[i] != null) {
//					if (toSearch[i].isCompleteWord) {
//						ret.add(toSearch[i]);
//					}
//					ret.addAll(toArray(toSearch[i].myDescendants));
//				}
//			}
//			return ret;
//		}
//	    
//
//	    // Returns the index of myWords that the input char corresponds to.
//	    // You can also use this in FreqWordTrieNode.
//	    // Assumes that the input is a lowercase alphabetic char.
//	    public int charToIndex(char c) {
//	        return ((int) c) - 33;
//	    }
//
//	    private class FreqWordTrieNode implements Comparator<FreqWordTrieNode>{
//	        private String myPrefix;
//	        
//	        // This array contains the nodes just below this FreqWordTrie node.
//	        private FreqWordTrieNode[] myDescendants;
//	        private boolean isCompleteWord;
//	        private int frequency;
//
//	        private FreqWordTrieNode(String prefix){
//	            myPrefix = prefix;
//	            myDescendants = new FreqWordTrieNode[100];
//	        }
//	        
//	        
//			public int compare(FreqWordTrieNode w1, FreqWordTrieNode w2) {
//				if (w1.frequency < w2.frequency) {
//					return 1;
//				} else if (w1.frequency > w2.frequency) {
//					return -1;
//				} else {
//					return 0;
//				}
//			}
//			
//			public boolean equals(Object obj) {
//				if (obj instanceof FreqWordTrieNode && myPrefix.equals(((FreqWordTrieNode) obj).myPrefix)) {
//					return true;
//				}
//				return false;
//			}
//			
//			
//	    }
//	}
//
//}
}
