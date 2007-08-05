package riff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class RiffFile extends RiffChunk {
    private ByteBuffer buffer;


    public RiffFile(File file) throws FileNotFoundException, IOException {
        this(new FileInputStream(file));
    }

    public RiffFile(FileInputStream fis) throws IOException {
        this(fis.getChannel().map(
            FileChannel.MapMode.READ_ONLY, 0, fis.getChannel().size()));
    }

    public RiffFile(ByteBuffer buffer) {
        super(-1); // Length unknown

        this.buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public int read() throws RiffException {
        Chunk chunk = readChunkHeader();
/*
        if (!identifier.equals(chunk.getIdentifier())) {
            // File must start with RIFF
            throw new RiffException("Invalid RIFF file header");
        }
*/
        setLength(chunk.getLength());

        return handleChunk(this);
    }

    protected int handleChunk(Chunk chunk) {
        int len = chunk.getLength();
        byte[] data = new byte[len];
        buffer.get(data);

        chunk.setData(data);

        // Squash pad byte
        if (len % 2 == 1) {
            len += len % 2;
            buffer.get();
        }
        
        return len;
    }

    protected int handleChunk(ChunkContainer container) {
        // Read content type
        byte[] contentType = new byte[4];
        buffer.get(contentType);
        container.setContentType(contentType);

        int nbytes = 4; // Content type length

        while (nbytes < container.getLength()) {
            Chunk chunk = readChunkHeader();

            nbytes += 8; // Header length

            if ("LIST".equals(chunk.getIdentifier())) {
                chunk = new ChunkContainer(chunk);

                nbytes += handleChunk((ChunkContainer) chunk);
            } else {
                nbytes += handleChunk(chunk);
            }

            container.getChunks().add(chunk);
        }

        return nbytes;
    }

    /**
     * Reads a chunk header. 4 bytes identifier plus 4 bytes (unsigned
     * int) content length.
     * 
     * @return
     */
    protected Chunk readChunkHeader() {
        byte[] identifier = new byte[4];
        buffer.get(identifier);

        int length = buffer.getInt();

        return new Chunk(identifier, length);
    }
}
