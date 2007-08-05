import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.TrackMeta;
import cue.CueSheet;
import cue.CueSheetWriter;
import cue.Index;

public class MmpTest {

    public MmpTest() {
        super();
    }

    public static void main(String... args) throws FileNotFoundException,
            IOException {
        File file = new File(
                "C:\\Documents and Settings\\johan\\Desktop\\mini\\do it again_star guitar_neo violence_25 years and running_toop toop.mmp");
        MixmeisterPlaylist mmp = MixmeisterPlaylist.open(file);

        CueSheet cueSheet = new CueSheet("VA", "Test");
        cueSheet.setFile(new cue.File(file.getName()));

        double position = 0;
        // MP3File mp3;
        String performer, songTitle;

        for (int i = 0; i < mmp.getTracks().size(); ++i) {
            cue.Track track = new cue.Track(i + 1);

            performer = "Unknown artist";
            songTitle = // "Track " + track.getNumber();
            mmp.getTracks().get(i).getFileName();

            /*
            try {
                mp3 = new MP3File(new File(mmp.getTracks().get(i)
                        .getFileName()), false);

                if (mp3.hasID3v2Tag()) {
                    AbstractID3v2 tag = mp3.getID3v2Tag();

                    performer = tag.getLeadArtist();
                    songTitle = tag.getSongTitle();
                } else if (mp3.hasID3v1Tag()) {
                    ID3v1 tag = mp3.getID3v1Tag();

                    performer = tag.getArtist();
                    songTitle = tag.getSongTitle();
                }
            } catch (IOException e) {

            } catch (TagException e) {

            }
            */

            track.setPerformer(performer);
            track.setTitle(songTitle);

            track.getIndices().add(new Index(position));

            cueSheet.getTracks().add(track);

            TrackMeta outroAnchor = new LinkedList<TrackMeta>(mmp.getTracks()
                    .get(i).getMarkers(TrackMeta.OUTRO_RANGE)).getLast();

            position += outroAnchor.getPosition() / 1000000.0;
        }
        /*
        BufferedWriter wr = new BufferedWriter(new FileWriter(file.toString().concat(".cue")));
        */

        System.out.println(cueSheet);

        new CueSheetWriter(new PrintWriter(System.out)).write(cueSheet);
    }
}
