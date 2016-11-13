import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Append implements Runnable {

	public final Object usageLock = new Object();
	private ArrayList<String> server1chunks;
	final String fname1;
	private String fileToReceive;
	public int firstmeter = 0;

	public Append(ArrayList<String> server1chunks, String fname1, int firstmeter) {

		this.server1chunks = new ArrayList<String>(server1chunks);
		this.fname1 = fname1;
		this.firstmeter = firstmeter;
	}

	public void run() {
		System.out.println("chunk size = " + server1chunks.size());
		for (int e = 0; e < firstmeter; e++) {
			fileToReceive = server1chunks.get(e);
			if (!fileToReceive.equals(" ")) {
				synchronized (usageLock) {
					try {
						ArrayList<String> files = new ArrayList<String>();
						files.add("/sdcard/vid/" + fileToReceive);
						String fname = "/sdcard/" + fname1;
						File ofile = new File(fname);
						FileOutputStream fos;
						FileInputStream fis;
						byte[] fileBytes;
						int bytesRead = 0;
						try {
							fos = new FileOutputStream(ofile, true);

							for (String filename : files) {
								File file = new File(filename);
								fis = new FileInputStream(file);
								fileBytes = new byte[(int) file.length()];
								bytesRead = fis.read(fileBytes, 0,
										(int) file.length());
								assert (bytesRead == fileBytes.length);
								assert (bytesRead == (int) file.length());
								fos.write(fileBytes);
								fos.flush();
								fileBytes = null;
								fis.close();
								fis = null;
							}

							fos.close();
							fos = null;
						} catch (Exception e1) {
							// Catch exception if any
							System.err.println("Error: " + e1.getMessage());
						}
					} finally {
					}
				}
			}
		}
	}
}

