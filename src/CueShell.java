import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.TrackMeta;

import org.jaudiotagger.audio.InvalidAudioFrameException;
import org.jaudiotagger.audio.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import cue.CueSheet;
import cue.CueSheetWriter;
import cue.Index;
import cue.Track;

public class CueShell {

    private static CueSheet getCueSheetFromMixmeisterPlaylist(
            MixmeisterPlaylist mmp) {
        CueSheet cueSheet = new CueSheet("CueMeister", "In the Mix");
        double position = 0;
        String performer, songTitle;
        boolean id3 = true;

        for (int i = 0; i < mmp.getTracks().size(); ++i) {
            Track track = new Track(i + 1);

            // Default performer and title
            performer = "Unknown artist";
            songTitle = "Track " + track.getNumber();

            String fileName = mmp.getTracks().get(i).getFileName();

            // Fetch ID3
            if (id3) {

                try {
                    MP3File mp3 = new MP3File(new File(fileName),
                            MP3File.LOAD_ALL, true);

                    if (mp3.hasID3v1Tag()) {
                        ID3v1Tag tag = mp3.getID3v1Tag();

                        performer = tag.getArtist();
                        songTitle = tag.getTitle();
                    }

                    // Try v2
                    if (mp3.hasID3v2Tag()) {
                        AbstractID3v2Tag tag = mp3.getID3v2TagAsv24();

                        // Get performer
                        String value = extractID3v2TagValue(tag, "TCOM",
                                "TPE1", "TOPE", "TEXT", "TOLY");

                        if (value != null) {
                            performer = value;
                        }

                        // Get song title
                        value = extractID3v2TagValue(tag, "TIT2", "TOAL");

                        if (value != null) {
                            songTitle = value;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TagException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                }
            }

            track.setPerformer(performer);
            track.setTitle(songTitle);
            track.getIndices().add(new Index(position));

            cueSheet.getTracks().add(track);

            TrackMeta outroAnchor = new LinkedList<TrackMeta>(mmp.getTracks()
                    .get(i).getMarkers(TrackMeta.OUTRO_RANGE)).getLast();
            position += outroAnchor.getPosition() / 1000000.0;
        }

        return cueSheet;
    }

    private static String extractID3v2TagValue(AbstractID3v2Tag tag,
            String... identifiers) {
        for (String identifier : identifiers) {
            if (tag.hasFrameAndBody(identifier)) {
                Object frame = tag.getFrame(identifier);

                if (frame != null && frame instanceof AbstractID3v2Frame) {
                    String value = ((AbstractID3v2Frame) frame).getBody()
                            .getObjectValue("Text").toString();

                    if (value != null && value.trim().length() > 0) {
                        return value.trim();
                    }
                }
            }
        }

        return null;
    }

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        Logger.getLogger("org.jaudiotagger.audio.mp3").setLevel(Level.OFF);
        
        if (args.length != 1) {
            System.err.println("Specify input file...");
            System.exit(-1);
        }

        MixmeisterPlaylist mmp = MixmeisterPlaylist.open(new File(args[0]));
        CueSheet cueSheet = getCueSheetFromMixmeisterPlaylist(mmp);

        new CueSheetWriter(new PrintWriter(System.out)).write(cueSheet);
    }
}
