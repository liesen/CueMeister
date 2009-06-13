package riff;

import java.util.Arrays;

/**
 * RIFF chunk. A chunk contains an identifier field and a data field.
 * 
 * @author liesen
 * 
 */
public class Chunk {
  protected String identifier;

  protected int length;

  protected byte[] data;

  /**
   * Creates a (deep) copy of a chunk.
   * @param chunk
   */
  public Chunk(Chunk chunk) {
    this(chunk.getIdentifier(), chunk.getLength(), chunk.getData());
  }

  public Chunk(byte[] identifier, int length) {
    this(new String(identifier), length);
  }

  public Chunk(String identifier, int length) {
    this(identifier, length, new byte[length]);
  }

  public Chunk(byte[] identifier, int length, byte[] data) {
    this(new String(identifier), length, data);
  }

  public Chunk(String identifier, int length, byte[] data) {
    this.identifier = identifier;
    this.length = length;
    this.data = Arrays.copyOf(data, length);
  }

  /**
   * Returns whether this chunk is able to contain sub-chunks or not, i.e. if
   * this chunk is a list chunk (see {@link ListChunk}).
   * @return
   */
  public boolean canContainSubchunks() {
    return false;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public String toString() {
    StringBuffer str = new StringBuffer(identifier + " (" + length + "): ");

    if (data != null) {
      str.append(data.toString());
      str.append(" (" + data.length + ")");
    }

    return str.toString();
  }
}
