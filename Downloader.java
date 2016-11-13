import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class Downloader {

	final String fileToReceive;

	public static int testercounter = 0;

	boolean fileDownloaded = false;
	Socket clientSocket;

	public Downloader(String fileToReceive) {
		this.fileToReceive = fileToReceive;
	}

	public void download(String ServerIP) {
		int counter=0;
		try {
			clientSocket = new Socket();
			final int timeOut = (int) TimeUnit.MILLISECONDS.toMillis(50); // 100 msec wait period
			clientSocket
					.connect(new InetSocketAddress(ServerIP, 3248), timeOut);
			System.out.println("Connect to port 3248");
			System.out.println("Sending the file to be recieved " + fileToReceive);

			BufferedWriter BuffWriter = new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream()));
			BuffWriter.write(fileToReceive + "\n");
			BuffWriter.flush();
			InputStream is = clientSocket.getInputStream();

			File root = Environment.getExternalStorageDirectory();
			File dir = new File(root.getAbsolutePath() + "/vid/");
			dir.mkdirs();
			byte[] longBytes = new byte[8];
			readFully(clientSocket, longBytes);
			ByteBuffer bb = ByteBuffer.wrap(longBytes);
			long fileLength = bb.getLong();

			// save
			@SuppressWarnings("resource")
			FileOutputStream f = new FileOutputStream(new File(dir, fileToReceive));

			byte[] buffer = new byte[1024];
			int len1 = 0;
			int readBytes = 0;
			while ((len1 = is.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
				readBytes += len1;
			}
			if (readBytes == fileLength) {
				this.fileDownloaded = true;
				System.out.println("File Recieved " + fileToReceive);
			}

		} catch (Exception ex) {
			System.out.println("Client stopped");
			ex.printStackTrace();
		} finally {
			try {
				if (clientSocket != null && !clientSocket.isClosed()) {
					clientSocket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void readFully(Socket clientSocket, byte[] buffer)	throws IOException {
		InputStream inputStream = clientSocket.getInputStream();
		int remaining = buffer.length;
		int read = 0;
		System.out.println("Receiving the file " + fileToReceive);
		while (remaining > 0) {
			read = inputStream.read(buffer, buffer.length - remaining,remaining);
			if (read < 0) {
				throw new IOException();
			}
			remaining -= read;
		}
	}

	public boolean IsDownloaded() {

		return this.fileDownloaded;
	}

}
