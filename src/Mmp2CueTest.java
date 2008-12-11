import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import mixmeister.mmp.Marker;
import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.Track;
import mixmeister.mmp.TrackType;
import cue.CueSheet;
import cue.Index;

public class Mmp2CueTest {

  public Mmp2CueTest() {
    super();
  }

  public static void main(String... args) throws FileNotFoundException, IOException {
    File file = new File("mxmfusion2.mmp");
    MixmeisterPlaylist mmp = MixmeisterPlaylist.open(file);

    CueSheet cueSheet = new CueSheet("VA", "Test");
    cueSheet.setFile(new cue.File(file.getName()));

    double position = 0;
    String performer, songTitle;
    int trackNo = 1;

    for (Track currentTrack : mmp.getTracks()) {
      switch (currentTrack.getHeader().getTrackType()) {
        case TrackType.OVERLAY:
        case TrackType.OVERLAY_WITH_BEATSYNC:
        case TrackType.OVERLAY_WITHOUT_BEATSYNC:
          break;

        default:
          cue.Track cueTrack = new cue.Track(trackNo++);

          performer = "Unknown artist";
          songTitle = "Track " + cueTrack.getNumber();

          cueTrack.setPerformer(performer);
          cueTrack.setTitle(songTitle);
          cueTrack.getIndices().add(new Index(position));

          cueSheet.getTracks().add(cueTrack);

          Marker outroAnchor =
              new LinkedList<Marker>(currentTrack.getMarkers(Marker.OUTRO_RANGE)).getLast();
          Marker introAnchor =
              new LinkedList<Marker>(currentTrack.getMarkers(Marker.INTRO_RANGE)).getLast();
          
//          Set<Marker> bpmMarkers = new TreeSet<Marker>(new MarkerPositionComparator());
//          
//          bpmMarkers.addAll(currentTrack.getMarkers(0x1000000));
//          bpmMarkers.addAll(currentTrack.getMarkers(0x2000000));
//          
          Set<Set<Marker>> markers = new HashSet<Set<Marker>>(currentTrack.getMetaData());
          
          for (Set<Marker> ms : markers) {
            System.out.printf("%x\t", ms.iterator().next().getType());
            
            for (Marker m : ms) {
              System.out.printf("%d\t", m.getVolume());
            }
            
            System.out.println();
          }

          position += (outroAnchor.getPosition() - introAnchor.getPosition()) / 1000000.0;
          break;
      }
    }

//    new CueSheetWriter(new PrintWriter(System.out)).write(cueSheet);
  }
}
