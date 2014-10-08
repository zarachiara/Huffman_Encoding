import java.io.*;
import java.util.*;
import java.awt.Point;

public class HuffmanEncoding {
	
	public static void main (String [] args) throws IOException{
		String method = args[0];
		String target = args[1];
		String destination = args[2];
		if (args.length == 3) {
			if (method.equals("encode")) {
				HuffmanEncoding.encode(target, destination);
			} else if (method.equals("decode")) {
				HuffmanEncoding.decode(target, destination);
			} else {
				throw new IllegalArgumentException("No method for " + method);
			}
		}
		else {
			if (method.equals("encode")) {
				HuffmanEncoding.encode2(target, destination, Integer.parseInt(args[3]));
			}
		}
	}
	
	// The Root of the Huffman Encoding Tree
	HuffmanEncodingNode myRoot;
	
	// The Encode method. Takes in a file name, compresses the file, and outputs the compressed file with a codemap header.
	public static File encode(String target, String destination) throws IOException {
		File destinationFile = new File(destination);
		// Errors if there is already a file with the name destination
//		if (!destinationFile.createNewFile()) {
//			throw new IllegalArgumentException("Cannot create a file of name " + destination + " because a file of name " + destination + " already exists.");
//		}
		HuffmanEncoding h = new HuffmanEncoding(target);
		FileOutputStream fos = new FileOutputStream(destinationFile);
		h.writeCodeMap(fos);
		fos.close();
		h.compressFile(destination, target);
		return destinationFile;	
	}
	
	// The Decode method. Takes in an output file from encode and reproduces the original file. If Encode takes in A and returns B, Decode will return A when
	// it is given B as an argument.  It decompresses a compressed file.    
	public static File decode(String target, String destination) throws IOException{
		File destinationFile = new File(destination);
		// Errors if there is already a file with the name destination
//		if (!destinationFile.createNewFile()) {
//			throw new IllegalArgumentException("Cannot create a file of name " + destination + " because a file of name " + destination + " already exists.");
//		}
		HuffmanEncoding h = new HuffmanEncoding();
		FileCharIterator iter = new FileCharIterator(target);
		StringBuilder stringToWrite = new StringBuilder();
		HuffTrie conversionKey = h.codeMapReader(iter);
		HuffTrie.HuffTrieNode[] currNodeBranch = conversionKey.myHuffs;
		while (iter.hasNext()) {
			stringToWrite.append(iter.next());
		}
		StringBuilder toAddToFile = new StringBuilder();
		for (int i = 0; i < stringToWrite.length(); i++) {
			int trieIndex = stringToWrite.charAt(i) - 48;
			HuffTrie.HuffTrieNode currNode = currNodeBranch[trieIndex];
			if (currNode.myChar != null) {
				if (currNode.myChar.equals("EOF")) {
					break;
				}
				toAddToFile.append(currNode.myChar);
				currNodeBranch = conversionKey.myHuffs;
			} else {
				currNodeBranch = currNode.myDescendants;
			}
		}
		FileOutputHelper.writeBinStrToFile(toAddToFile.toString(), destination);
		return destinationFile;
	}
	
	public static File encode2(String target, String destination, int n) throws IOException {
		File destinationFile = new File(destination);
		// Errors if there is already a file with the name destination
//		if (!destinationFile.createNewFile()) {
//			throw new IllegalArgumentException("Cannot create a file of name " + destination + " because a file of name " + destination + " already exists.");
//		}
		HuffmanEncoding h = new HuffmanEncoding(target, n);
		FileOutputStream fos = new FileOutputStream(destinationFile);
		h.writeCodeMap(fos);
		fos.close();
		h.compressFile2(destination, target, n);
		return destinationFile;	
	}
	
	
	// A no argument constructor will produce a huffman encoding with myRoot set to null
	HuffmanEncoding() {
		myRoot = null;
	}
	
	// The Constructor which will produce a Huffman Encoding Tree for a given file. It will do this by creating an ArrayList of HuffmanEncondingNodes by calling
	// weightEval on the file, and then using the ArrayList HuffmanEncoding constructor to generate the huffmanEncoding tree using 
	HuffmanEncoding(String toEncode) {
		myRoot = nodeCombiner(weightEval(toEncode));
		huffRepSetter(); 
	}
	
	// takes an integer which is the number of frequent words we want to use. 
	HuffmanEncoding(String toEncode, int n)
	{
		myRoot = nodeCombiner(weightEval2(toEncode, n));
		huffRepSetter(); 
	}
	
	// The Constructor which will produce a Huffman Encoding Tree for a given ArrayList containing Huffman Encoding Nodes
	private HuffmanEncodingNode nodeCombiner(ArrayList<HuffmanEncodingNode> toEncode) {
		while (toEncode.size() > 1){
			HuffmanEncodingNode min1 = toEncode.get(0);
			HuffmanEncodingNode min2 = toEncode.get(1);
			if (min1.myWeight > min2.myWeight){
				min1 = toEncode.get(1);
				min2 = toEncode.get(0);
			}for (int i= 2; i<toEncode.size(); i++){
				if (toEncode.get(i).myWeight < min2.myWeight){
					min2 = toEncode.get(i);
					if (min2.myWeight < min1.myWeight){
						min2 = min1;
						min1 = toEncode.get(i);
					}
				}
			}
			HuffmanEncodingNode toAdd = new HuffmanEncodingNode (min1, min2);
			toEncode.remove(min1);
			toEncode.remove(min2);
			toEncode.add(toAdd);
		}
		return toEncode.get(0); // tree created with whatever is left at the end
	}
	
	// Translates the tree w/ the new Huffman binary representation
	void huffRepSetter()
	{
		if(myRoot != null){
			myRoot.huffRepSetter("");
		}
	}
	
	// Takes in an empty file supplied by encode and writes the huffman encoding codemap at the top of the file
	void writeCodeMap(FileOutputStream fos) throws IOException {
		myRoot.writeCodeMap(fos);
		fos.write(10);
	}
	
	// Compresses the file, converting each byte of the original file to it's corresponding huffman representation byte
	void compressFile(String destination, String fileToIterate) throws IOException {
		FileCharIterator iter = new FileCharIterator(fileToIterate);
		StringBuilder stringToWrite = new StringBuilder();
		while(iter.hasNext()){
			String toEval = iter.next();
			stringToWrite.append(getHuffRep(toEval));
		}
		stringToWrite.append(getHuffRep("EOF"));
		while (stringToWrite.length() % 8 != 0) {
			stringToWrite.append("0");
		}
		FileOutputHelper.writeBinStrToFile(stringToWrite.toString(), destination);
	}
	
	// Compresses the file, converting each byte of the original file to it's corresponding huffman representation byte
	void compressFile2(String destination, String fileToIterate, int n) throws IOException {
		FileFreqWordsIterator iter = new FileFreqWordsIterator(fileToIterate, n);
		StringBuilder stringToWrite = new StringBuilder();
		while(iter.hasNext()){
			String toEval = iter.next();
			stringToWrite.append(getHuffRep(toEval));
		}
		stringToWrite.append(getHuffRep("EOF"));
		while (stringToWrite.length() % 8 != 0) {
			stringToWrite.append("0");
		}
		FileOutputHelper.writeBinStrToFile(stringToWrite.toString(), destination);
	}
	
	// Returns the huffman representation of a character by finding its binary string representation in the tree
	String getHuffRep(String character){
		return myRoot.getHuffRep(character);
	}
	
	// Gives each character in a file a weight based upon the number of times it appears. Returns an ArrayList of HuffmanEncodingNodes which contain the string 
	// representation of each character and the weight, i.e. number of times the character appears in the file.  
	ArrayList<HuffmanEncodingNode> weightEval(String inputFileName) {
		ArrayList<HuffmanEncodingNode> result = new ArrayList<HuffmanEncodingNode>();
		FileCharIterator iterator = new FileCharIterator(inputFileName);
		while (iterator.hasNext()) {
			String currentChar = iterator.next();
			boolean containsNode = false;
			for (HuffmanEncodingNode node : result) {
				if (node.myChar.equals(currentChar)) {
					node.myWeight++;
					containsNode = true;
					break;
				}
			}
			if (!containsNode) {
				result.add(new HuffmanEncodingNode(currentChar));
			}
		}
		result.add(new HuffmanEncodingNode("EOF"));
		return result;
	}
	
	// Similar to weightEval but uses the freqWordsIterator
	ArrayList<HuffmanEncodingNode> weightEval2(String inputFileName, int n) {
		ArrayList<HuffmanEncodingNode> result = new ArrayList<HuffmanEncodingNode>();
		FileFreqWordsIterator iterator = new FileFreqWordsIterator(inputFileName, n);
		while (iterator.hasNext()) {
			String currentChar = iterator.next();
			boolean containsNode = false;
			for (HuffmanEncodingNode node : result) {
				if (node.myChar.equals(currentChar)) {
					node.myWeight++;
					containsNode = true;
					break;
				}
			}
			if (!containsNode) {
				result.add(new HuffmanEncodingNode(currentChar));
			}
		}
		result.add(new HuffmanEncodingNode("EOF"));
		return result;
	}
	
	
	// Reads the codemap at the beginning of an encoded file, and returns an ArrayList which contains Nodes relating the ASCII binary representation to the 
	// huffman representation used in the file so that the decoder can convert the file back into its original state.  
	HuffTrie codeMapReader(FileCharIterator toRead) {
		HuffTrie ret = new HuffTrie();
		String prev = "";
		String curr = "";
		StringBuilder charString = new StringBuilder(); 
		while(!prev.equals("00001010") || !curr.equals("00001010")) {
			// set curr & prev
			prev = curr;
			curr = toRead.next();
			if (curr.equals("00001010") && charString.length() > 0) {
				// add to the arraylist
				int commaLocation = charString.indexOf(",");
				String character = charString.substring(0, commaLocation);
				String huffRep = charString.substring(commaLocation +1);
				ret.insert(huffRep,character);
				charString = new StringBuilder();
			}
			// adding to charString
			else if (curr.equals("00110000")) {
				charString.append("0");
			} else if (curr.equals("00110001")) {
				charString.append("1");
			} else if (curr.equals("01000101")){
				charString.append("E");
			} else if (curr.equals("01001111")) {
				charString.append("O");
			} else if (curr.equals("01000110")) {
				charString.append("F");
			} else {
				charString.append(",");
			}
		}
		return ret;
	}
	
	// A HuffmanEncodingNode, which works a lot like a BinaryTreeNode.  It contains the information needed to determine the weight of a character in a file, the 
	// ASCII binary code of the character, and the left and right for the node, which is needed for nodes that don't contain characters.  
	class HuffmanEncodingNode {
		// Instance variable declaration
		String myChar;
		int myWeight;
		HuffmanEncodingNode myLeft;
		HuffmanEncodingNode myRight;
		String huffRep = null;
		
		// Constructor for a node which will actually contain a character. These nodes will be the leaves of the tree.  The input character will be the binary
		// string representation of a character. 
		private HuffmanEncodingNode(String character) {
			myChar = character;
			myWeight = 1;
			myLeft = null;
			myRight = null;
		}
		
		// Constructor for a node which does not contain a character, and must have at least one child node
		private HuffmanEncodingNode(HuffmanEncodingNode left, HuffmanEncodingNode right) {
			myChar = null;
			myWeight = left.myWeight + right.myWeight;
			myLeft = left;
			myRight = right;
		}
		
		// returns the current set of numbers assigned for new Huffman binary representation.  
		private void huffRepSetter(String prefix)
		{
			if (myChar != null){
				huffRep = prefix; // reached a leaf of the tree. 
			}
			else{
				String prefixLeft = prefix + "0";
				String prefixRight = prefix + "1";
				myLeft.huffRepSetter(prefixLeft);
				myRight.huffRepSetter(prefixRight);	
			}
		}
		
		// writes the codemap for an encoded file
		private void writeCodeMap(FileOutputStream fos) throws IOException{
			if (myChar != null) {
				String stringToWrite = myChar + "," + huffRep;
				byte[] toWrite = stringToWrite.getBytes();
				fos.write(toWrite);
				fos.write(10);
			} else {
				myLeft.writeCodeMap(fos);
				myRight.writeCodeMap(fos);
			}
		}
		
		// get's the huffman representation of a given character, searching through the tree until the node with the character is found
		private String getHuffRep(String character) {
			if(myChar == null){
				if(myLeft.getHuffRep(character) != null){
					return myLeft.getHuffRep(character);
				}else {
					return myRight.getHuffRep(character);
				}
			} else {
				if(myChar.equals(character)){
					return huffRep;
				} else{
					return null;
				}
			}	
		}		
	}
	// Trie representation of a huffman tree, used for decoding purposes
	class HuffTrie {
		// The root of the trie, which contains two different branching paths for 0 and 1
		HuffTrieNode[] myHuffs;
		
		HuffTrie() {
			myHuffs = new HuffTrieNode[2];
		}
		
		// Inserts a new huffman representation into the trie, and binds it to the ascII representation input.
		void insert(String huffRep, String charRep) {
	    	String prefix;
	    	String toGo;
	    	if (huffRep.length() > 1) {
	    		toGo = huffRep.substring(1);
	    		prefix = huffRep.substring(0, 1);
	    	} else {
	    		prefix = huffRep;
	    		toGo = "";
	    	}
	    	insert(myHuffs, prefix, toGo, charRep);
		}
		
		private void insert(HuffTrieNode[] nodes, String prefix, String toGo, String charRep) {
			int index = Integer.parseInt(prefix);
			if (nodes[index] != null) {
	    		if (toGo.length() > 1) {
	    			prefix = toGo.substring(0, 1);
	    			toGo = toGo.substring(1);
	    		} else {
	    			prefix = toGo;
	    			toGo = "";
	    		}
	    		insert(nodes[index].myDescendants, prefix, toGo, charRep);
			} else {
				nodes[index] = new HuffTrieNode(prefix);
				if (toGo.equals("")) {
					nodes[index].myChar = charRep;
				} else {
		    		if (toGo.length() > 1) {
		    			prefix = toGo.substring(0, 1);
		    			toGo = toGo.substring(1);
		    		} else {
		    			prefix = toGo;
		    			toGo = "";
		    		}
		    		insert(nodes[index].myDescendants, prefix, toGo, charRep);
				}
			}
		}
		
		class HuffTrieNode {
			// Instance variable declaration
			String huffPrefix;
			HuffTrieNode[] myDescendants;
			// true if this node contains a complete huffman representation, which signals the decoder to write a new byte to the file.
			String myChar = null;
			
			private HuffTrieNode(String prefix) {
				huffPrefix = prefix;
				myDescendants = new HuffTrieNode[2];
			}
		}
	}
	
	
	
	
}
