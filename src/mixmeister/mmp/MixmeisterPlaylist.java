package mixmeister.mmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import riff.Chunk;
import riff.ChunkContainer;
import riff.RiffFile;

/**
 *
 * @author johan
 */
public class MixmeisterPlaylist {
    private List<Track> tracks;

    /**
     * Creates an empty playlist
     */
    public MixmeisterPlaylist() {
        this.tracks = new LinkedList<Track>();
    }

    /**
     * Reads a playlist from a file
     * 
     * @param file mmp file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static MixmeisterPlaylist open(File file)
            throws FileNotFoundException, IOException {
        RiffFile riff = new RiffFile(file);
        riff.read();

        MixmeisterPlaylist mmp = new MixmeisterPlaylist();

        mmp.getTracks().addAll(
                readTrackList((ChunkContainer) riff.getChunks().get(0)));

        return mmp;
    }

    private static List<Track> readTrackList(ChunkContainer trkl) {
        List<Track> tracks = new ArrayList<Track>();
        TrackMeta trks = null;

        for (Chunk chunk : trkl.getChunks()) {
            TrackHeader header = new TrackHeader();
            String file = "";
            List<TrackMeta> meta = new LinkedList<TrackMeta>();

            if (chunk.canContainSubchunks()) {
                for (Chunk chunk2 : ((ChunkContainer) chunk).getChunks()) {
                    if ("TRKH".equals(chunk2.getIdentifier())) {
                        header = readTrackHeader(chunk2);
                    } else if ("TRKF".equals(chunk2.getIdentifier())) {
                        file = readTrackFile(chunk2);
                    } else {
                        if (chunk2.canContainSubchunks()) {
                            for (Chunk chunk3 : ((ChunkContainer) chunk2)
                                    .getChunks()) {
                                if ("TRKM".equals(chunk3.getIdentifier())) {
                                    meta.add(readTrackMarker(chunk3));
                                } else if ("TRKS"
                                        .equals(chunk3.getIdentifier())) {
                                    trks = readTrackStartMarker(chunk3);
                                }
                            }
                        }
                    }
                }
            }

            Track tr = new Track(header, file, meta);
            tr.setStopMarker(trks);

            tracks.add(tr);
        }

        return tracks;
    }

    private static TrackMeta readTrackStartMarker(Chunk trks) {
        int[] data = wrapChunk(trks);

        int type = data[1];
        int position = data[2];
        int changeFlag = data[3];
        int value = data[4];

        return new TrackMeta(type, position, changeFlag, value);
    }

    private static TrackHeader readTrackHeader(Chunk trkh) {
        int[] data = wrapChunk(trkh);

        String archiveKey = String.format("%08x-%08x-%08x-%08x", data[3],
                data[2], data[5], data[4]);
        double bpm = data[6] / 1000.0;
        double masterVolume = data[7] / 100.0;
        int trackType = data[8];
        int mixingType = data[9];
        double keyAdjustment = data[10] / 100.0;
        int beatBlendType = data[11];

        return new TrackHeader(archiveKey, bpm, masterVolume, trackType,
                mixingType, keyAdjustment, beatBlendType);
    }

    private static String readTrackFile(Chunk trkf) {
        try {
            byte[] data = trkf.getData();
            String file = new String(data, "UTF-16LE");

            return file.substring(0, file.length() - 1);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * Wraps the buffered bytes (big endian) into a int-array (little endian) 
     * 
     * @param ch
     * @return
     */
    private static int[] wrapChunk(Chunk ch) {
        IntBuffer buf = ByteBuffer.wrap(ch.getData()).order(
                ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int length = buf.capacity();
        int[] data = new int[length];

        buf.get(data);

        return data;
    }

    private static TrackMeta readTrackMarker(Chunk trkm) {
        int[] data = wrapChunk(trkm);

        /* System.out.print(trkm.getIdentifier() + ":");
        
        for (int i = 0; i < data.length; ++i) {
            System.out.printf("%12d", data[i]);
        }
        
        System.out.println(); */

        int type = data[1];
        int position = data[2];
        int changeFlag = data[3];
        int value = data[5];

        return new TrackMeta(type, position, changeFlag, value);
    }

    public List<Track> getTracks() {
        return tracks;
    }
}