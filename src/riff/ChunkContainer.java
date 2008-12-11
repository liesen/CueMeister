package riff;

import java.util.LinkedList;
import java.util.List;

/**
 * A chunk container is a chunk that can contain sub-chunks.
 * @author liesen
 *
 */
public class ChunkContainer extends Chunk {
  /** Contained chunks */
  private final List<Chunk> chunks;

  /** Content type of the contained chunks */
  protected String contentType;


  public ChunkContainer(Chunk chunk) {
    this(chunk.getIdentifier(), chunk.getLength());
  }

  /**
   * @param identifier
   * @param length
   */
  public ChunkContainer(String identifier, int length) {
    this(identifier, length, "");
  }

  /**
   * @param identifier
   * @param length
   * @param data
   */
  public ChunkContainer(String identifier, int length, String contentType) {
    super(identifier, length, new byte[length]);

    this.contentType = contentType;
    this.chunks = new LinkedList<Chunk>();
  }

  /* (non-Javadoc)
   * @see riff.Chunk#canContainSubchunks()
   */
  @Override
  public boolean canContainSubchunks() {
    return true;
  }

  /**
   * @return
   */
  public List<Chunk> getChunks() {
    return chunks;
  }

  /**
   * @return
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @param contentType
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * @param contentType
   */
  public void setContentType(final byte[] contentType) {
    this.contentType = new String(contentType);
  }

  @Override
  public String toString() {
    StringBuffer str = new StringBuffer();

    str.append(identifier);
    str.append("/");
    str.append(contentType);
    str.append(" (");
    str.append(length);
    str.append("):");

    for (Chunk chunk : chunks) {
      str.append("\n\t");

      if (!chunk.canContainSubchunks()) {
        str.append("\t");
      }

      str.append(chunk.toString());
    }

    return str.toString();
  }
}
