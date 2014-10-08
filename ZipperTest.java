import junit.framework.TestCase;


public class ZipperTest extends TestCase {
	
	// Zipper tests
	public void testZipper() throws Exception{
		Zipper.zipper("testDir", "zipFileTest.txt");
	}
	
	public void testZipper2() throws Exception{
		Zipper.zipper("testDirSherlock", "zipSherlockTest.txt");
	}
	public void testZipper3() throws Exception{
		Zipper.zipper("testDirDeep", "testDirDeeps.txt");
	}
	// Unzipper tests
	public void testUnzipper() throws Exception {
		Zipper.unzipper("zipFileTest.txt", "EndDir1");
	}
	
	// Unzipper test2
	public void testUnzipper2() throws Exception {
		Zipper.unzipper("zipSherlockTest.txt", "EndSherDir");
	}
	
	// Unzipper test3
	public void testUnzipper3() throws Exception {
		Zipper.unzipper("testDirDeeps.txt", "EnderShererDirer");
	}
	public void testUnzipper4() throws Exception {
		Zipper.zipper("testsForUnzipper", "unzipperTest.txt");
		Zipper.unzipper("unzipperTest.txt", "unzipperDirectory");
	}
}
