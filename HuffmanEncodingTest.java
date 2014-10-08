import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import java.io.*;

public class HuffmanEncodingTest extends TestCase {
	
	//weightEval tests
	public void testWeightEval() {
		HuffmanEncoding h = new HuffmanEncoding();
		ArrayList<HuffmanEncoding.HuffmanEncodingNode> testArray = h.weightEval("SmallFile.txt");
		assertEquals(3, testArray.size());
		assertEquals(2, testArray.get(0).myWeight);
		assertEquals(1, testArray.get(1).myWeight);
		assertEquals(1, testArray.get(2).myWeight);
		assertTrue("EOF".equals(testArray.get(2).myChar));
		assertTrue("01100001".equals(testArray.get(0).myChar));
		assertTrue("01100010".equals(testArray.get(1).myChar));
	}
	
	// encode error test
//	public void testEncodeError() {
//		try {
//			HuffmanEncoding.encode("SmallFile.txt", "TestFile.txt");
//			fail();
//		} catch (IllegalArgumentException e) {
//			assertTrue(true);
//		} catch (IOException e) {
//			fail();
//		}
//	}
	
//	// decode error tests
//	public void testDecodeError1() {
//		try {
//			HuffmanEncoding.decode("SmallFile.txt", "TestFile.txt");
//			fail();
//		} catch (IllegalArgumentException e) {
//			assertTrue(true);
//		} catch (IOException e) {
//			fail();
//		}
//	}
	
	// HuffmanEncoding ArrayList Constructor tests
	public void testHuffmanEncodingArrayListConstructor() {
		// "mississippi river" size 
		HuffmanEncoding h = new HuffmanEncoding("ArrayListConstructorTest.txt");
		assertEquals(18, h.myRoot.myWeight);
		// general case "aba"
		h = new HuffmanEncoding("SmallFile.txt");
		assertEquals(4, h.myRoot.myWeight);
		assertTrue("01100010".equals(h.myRoot.myRight.myLeft.myChar));
		assertEquals(2, h.myRoot.myRight.myWeight);
		assertEquals(2, h.myRoot.myLeft.myWeight);
		assertEquals(1, h.myRoot.myRight.myRight.myWeight);
		assertEquals(1, h.myRoot.myRight.myLeft.myWeight);
		assertTrue("EOF".equals(h.myRoot.myRight.myRight.myChar));
		assertTrue("01100001".equals(h.myRoot.myLeft.myChar));		
	}
	
	public void testHuffRepSetter() {
		HuffmanEncoding h = new HuffmanEncoding("SmallFile.txt");
		h.huffRepSetter();
		assertTrue("0".equals(h.myRoot.myLeft.huffRep));
		assertTrue("10".equals(h.myRoot.myRight.myLeft.huffRep));
		assertTrue("11".equals(h.myRoot.myRight.myRight.huffRep));
	}
	
	public void testwriteCodeMap() throws IOException
	{
		HuffmanEncoding h = new HuffmanEncoding("SmallFile.txt");
		File testFile = new File("testFile");
		testFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(testFile);
		h.writeCodeMap(fos);
		fos.close();
	}
	
	// encode test
	public void testEncode() throws IOException {
		HuffmanEncoding.encode("SmallFile.txt", "SmallFileTest.txt" );
		HuffmanEncoding.encode("mis.txt", "misTest.txt");
	}
	
	//codemapReader test
	public void testcodeMapReader() throws IOException
	{
		HuffmanEncoding.encode("SmallFile.txt", "SmallFileTest1.txt" );
		FileCharIterator iter = new FileCharIterator("SmallFileTest1.txt");
		HuffmanEncoding h = new HuffmanEncoding();
		HuffmanEncoding.HuffTrie testTrie = h.codeMapReader(iter);
		assertTrue(testTrie.myHuffs[0].myChar.equals("01100001"));
		assertTrue(testTrie.myHuffs[1].myDescendants[0].myChar.equals("01100010"));
		assertTrue(testTrie.myHuffs[1].myDescendants[1].myChar.equals("EOF"));
	}
	
	//tests for the Huffman trie representation
	public void testHuffTrie() {
		HuffmanEncoding h = new HuffmanEncoding();
		HuffmanEncoding.HuffTrie trie = h.new HuffTrie();
		trie.insert("0", "01100001");
		trie.insert("10", "01100010");
		trie.insert("11", "EOF");
		assertTrue(trie.myHuffs[0].myChar.equals("01100001"));
		assertTrue(trie.myHuffs[1].myDescendants[0].myChar.equals("01100010"));
		assertTrue(trie.myHuffs[1].myDescendants[1].myChar.equals("EOF"));
	}
	
	public void testDecode() throws IOException{
		HuffmanEncoding.encode("SmallFile.txt", "SmallFileEncoded.txt");
		HuffmanEncoding.decode("SmallFileEncoded.txt", "SmallFileDecoded.txt");
		HuffmanEncoding.encode("mis.txt", "misEncoded.txt");
		HuffmanEncoding.decode("misEncoded.txt", "misDecoded.txt");
		HuffmanEncoding.encode("Sherlock.txt", "SherlockEncoded.txt");
		HuffmanEncoding.decode("SherlockEncoded.txt", "SherlockDecoded.txt");
	}
}
