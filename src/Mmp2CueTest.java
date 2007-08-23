import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import mixmeister.mmp.Marker;
import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.Track;
import mixmeister.mmp.TrackType;
import cue.CueSheet;
import cue.CueSheetWriter;
import cue.Index;

public class Mmp2CueTest {

    public Mmp2CueTest() {
        super();
    }

    public static void main(String... args) throws FileNotFoundException,
            IOException {
        File file = new File(
        // "C:\\Documents and Settings\\johan\\Desktop\\mini\\do it again_star guitar_neo violence_25 years and running_toop toop.mmp");
                // "7.mmp"
                // "6.mmp"
                "C:\\DOCUME~1\\johan\\LOCALS~1\\Temp\\Two\\two - mixed by trancer.mmp"
        );
        MixmeisterPlaylist mmp = MixmeisterPlaylist.open(file);

        CueSheet cueSheet = new CueSheet("VA", "Test");
        cueSheet.setFile(new cue.File(file.getName()));

        double position = 0;
        String performer, songTitle;

        Track currentTrack;

        for (int i = 0; i < mmp.getTracks().size(); ++i) {
            currentTrack = mmp.getTracks().get(i);

            switch (currentTrack.getHeader().getTrackType()) {
            case TrackType.OVERLAY:
            case TrackType.OVERLAY_WITH_BEATSYNC:
            case TrackType.OVERLAY_WITHOUT_BEATSYNC:
                break;

            default:
                cue.Track cueTrack = new cue.Track(i + 1);

                performer = "Unknown artist";
                songTitle = "Track " + cueTrack.getNumber();

                cueTrack.setPerformer(performer);
                cueTrack.setTitle(songTitle);
                cueTrack.getIndices().add(new Index(position));

                cueSheet.getTracks().add(cueTrack);

                Marker outroAnchor = new LinkedList<Marker>(currentTrack.getMarkers(Marker.OUTRO_RANGE)).getLast();
                Marker introAnchor = new LinkedList<Marker>(currentTrack.getMarkers(Marker.INTRO_RANGE)).getLast();

                position += (outroAnchor.getPosition() - introAnchor.getPosition()) / 1000000.0;
                break;
            }
        }
        /*
        BufferedWriter wr = new BufferedWriter(new FileWriter(file.toString().concat(".cue")));
        */

        new CueSheetWriter(new PrintWriter(System.out)).write(cueSheet);
    }
}
