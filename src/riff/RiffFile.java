package riff;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps a file that is formatted according to the RIFF (Resource Interchange
 * File Format) file format. RIFF is a generic meta-format for storing data in
 * tagged chunks.
 * 
 * <p>
 * Calling {@code #read()} or {@code #open(File)} will recursively read and
 * parse the entire contents of a RIFF file.
 * 
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Resource_Interchange_File_Format">
 * http://en.wikipedia.org/wiki/Resource_Interchange_File_Format</a>.
 * 
 * @author liesen
 */
public class RiffFile extends RiffChunk implements Closeable {
  /**
   * The channel that's being read from.
   */
  private final FileInputStream fin;

  /** Flag is set if the file has already been parsed. */
  private boolean isRead = false;

  /**
   * Opens--but does not read--a RIFF file. Call {@code #read()} to parse the
   * file.
   * 
   * @param file
   * @throws FileNotFoundException
   * @throws IOException
   */
  public RiffFile(File file) throws FileNotFoundException, IOException {
    super(0);
    this.fin = new FileInputStream(file);
  }

  /**
   * Factory method to open--and fully read--a RIFF file. *
   * 
   * @param file
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static RiffFile open(File file) throws FileNotFoundException, IOException {
    RiffFile riff = new RiffFile(file);
    riff.read();

    return riff;
  }

  /**
   * Reads the "current" chunk and recursively reads any sub-chunks.
   * 
   * @return
   * @throws IOException
   */
  public int read() throws IOException {
    if (!isRead) {
      Chunk chunk = readChunkHeader();
      setLength(chunk.getLength());
      handleChunk(this);
      isRead = true;
    }

    return 8 + getLength();
  }

  /**
   * Reads the raw data associated with {@code chunk}.
   * 
   * @param chunk
   * @return the number of bytes read.
   * @throws IOException
   */
  protected int handleChunk(Chunk chunk) throws IOException {
    int len = chunk.getLength();
    byte[] data = new byte[len];
    fin.read(data);
    chunk.setData(data);

    // Squash pad byte
    if ((len & 1) == 1) {
      len += len % 2;
      fin.skip(1);
    }

    return len;
  }

  /**
   * Recursively expands {@code container}.
   * 
   * @param container
   * @return the number of bytes read.
   * @throws IOException
   */
  protected int handleChunk(ChunkContainer container) throws IOException {
    // Read content type
    byte[] contentType = new byte[4];
    fin.read(contentType);
    container.setContentType(contentType);

    int numBytesRead = 4; // Content type length

    while (numBytesRead < container.getLength()) {
      Chunk chunk = readChunkHeader();

      numBytesRead += 8; // Header length

      if ("LIST".equals(chunk.getIdentifier())) {
        chunk = new ChunkContainer(chunk);

        numBytesRead += handleChunk((ChunkContainer) chunk);
      } else {
        numBytesRead += handleChunk(chunk);
      }

      container.getChunks().add(chunk);
    }

    return numBytesRead;
  }

  /**
   * Reads a chunk header. 4 bytes identifier plus 4 bytes (unsigned int)
   * content length.
   * 
   * @return
   * @throws IOException
   */
  protected Chunk readChunkHeader() throws IOException {
    byte[] identifier = new byte[4];
    fin.read(identifier);

    byte[] lengthData = new byte[4];
    ByteBuffer lengthBuf = ByteBuffer.wrap(lengthData).order(ByteOrder.LITTLE_ENDIAN);
    fin.read(lengthData);
    int length = lengthBuf.getInt();

    return new Chunk(identifier, length);
  }

  /**
   * Closes the underlying file input stream.
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    fin.close();
  }
}
