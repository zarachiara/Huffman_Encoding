import junit.framework.TestCase;

import java.io.IOException;
import java.util.*;

public class FileFreqWordsIteratorTest extends TestCase {
	
//	// file word iterator tests
//	public void testFileWordIterator() {
//		FileFreqWordsIterator f = new FileFreqWordsIterator("Cities.txt", 5);
//		ArrayList<FileFreqWordsIterator.WordNumber> testList = f.fileWordIterator();
//		assertEquals(17, testList.size());
//		assertTrue("0110100101110100".equals(testList.get(0).getWord()));
//		assertEquals(4, testList.get(0).getFrequency());
//	}
	
	// freqWordsHelper tests
	public void testFreqWordsHelper() {
		FileFreqWordsIterator f = new FileFreqWordsIterator("Cities.2.txt", 4);
		f.freqWordsHelper(f.fileWordIterator());
		assertEquals(4, f.freqWords.length);
		//System.out.println(f.freqWords[0]);
		assertTrue(f.freqWords[0].equals("011101000110100001100101"));
	}
	
	// iterator tests
	public void testNext() {
		FileFreqWordsIterator f = new FileFreqWordsIterator("Cities.2.txt", 4);
		System.out.println(f.next());
//		assertTrue(f.next().equals("011101000110100001100101")); 
		System.out.println(f.next());
		System.out.println(f.next());
	}
	public void testHasNext() {
		FileFreqWordsIterator f = new FileFreqWordsIterator("Short.txt", 1);
		assertTrue(f.hasNext());
		System.out.println(f.next());
		System.out.println(f.next());
		System.out.println(f.next());
		System.out.println(f.next());
		System.out.println(f.next());
		System.out.println(f.next());
		System.out.println(f.next());
		assertFalse(f.hasNext());
	}
	
	// encode2 test
	public void testencode2()throws IOException{
		HuffmanEncoding.encode2("Cities.2.txt", "CitiesTest.txt", 4);
	}
	// sherlock test
	public void testencode2Sherlock() throws IOException {
		HuffmanEncoding.encode2("Sherlock.txt", "SherlockFrequency.txt", 5);
	}
	// and the decode 
	public void testDecodeEncode2() throws IOException{
		HuffmanEncoding.decode("SherlockFrequency.txt", "SherlockDone.txt");
	}
	// test for a bigger number of frequent words
	public void testencode2BigSherlock() throws IOException {
		HuffmanEncoding.encode2("Sherlock.txt", "SherlockBig.txt", 100);
	}
	
	
}
