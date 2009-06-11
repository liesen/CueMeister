package cuemeister;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mixmeister.mmp.Marker;
import mixmeister.mmp.MarkerPositionComparator;
import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.Track;
import mixmeister.mmp.TrackType;

import org.jaudiotagger.audio.mp3.MP3File;

import cue.CueSheet;
import cue.Index;

public class CueMeister {
  /**
   * Tries to read the ID3 tags off of {@code mp3File} and apply the information
   * gathered to the metadata for {@code track}.
   * 
   * @param track
   * @param mp3File
   */
  private static void applyMetadataFromID3(cue.Track track, File mp3File) {
    if (!mp3File.exists()) {
      return;
    }
    
    try {
      MP3File mp3 = new MP3File(mp3File, MP3File.LOAD_ALL, true);
      track.setPerformer(ID3Helper.getArtist(mp3));
      track.setTitle(ID3Helper.getTitle(mp3));
    } catch (Exception e) {
    }
  }

  /**
   * Converts MMP to CUE
   * 
   * @param mmp
   * @param readID3 use ID3 lookup
   * @return
   */
  public static CueSheet convertMixmeisterPlaylistToCueSheet(MixmeisterPlaylist mmp, boolean readID3) {
    final CueSheet cueSheet = new CueSheet("", "");
    double position = 0;
    int trackNo = 1;
    final List<Track> tracks = mmp.getTracks();
  
    if (tracks.isEmpty()) {
      return cueSheet;
    }
  
    final Comparator<Marker> markerPositionComparator = new MarkerPositionComparator();
  
    double previousBpmMarkerPosition = 0;
    double previousBpm = tracks.iterator().next().getHeader().getBpm();
  
    for (int i = 0; i < mmp.getTracks().size(); ++i) {
      mixmeister.mmp.Track mmpTrack = mmp.getTracks().get(i);
  
      switch (mmpTrack.getHeader().getTrackType()) {
        case TrackType.OVERLAY:
        case TrackType.OVERLAY_WITH_BEATSYNC:
        case TrackType.OVERLAY_WITHOUT_BEATSYNC:
          break;
  
        default:
          cue.Track cueTrack = new cue.Track(trackNo++);
          cueTrack.setPerformer("Unknown artist");
          cueTrack.setTitle("Track " + cueTrack.getNumber());
  
          // Fetch metadata from ID3 tags
          if (readID3) {
            applyMetadataFromID3(cueTrack, new File(mmpTrack.getFileName()));
          }
          
          cueTrack.getIndices().add(new Index(position));
          cueSheet.getTracks().add(cueTrack);
  
          Marker outroAnchor =
              new LinkedList<Marker>(mmpTrack.getMarkers(Marker.OUTRO_RANGE)).getLast();
          Marker introAnchor =
              new LinkedList<Marker>(mmpTrack.getMarkers(Marker.INTRO_RANGE)).getLast();
  
          double trackLength = (outroAnchor.getPosition() - introAnchor.getPosition()) / 1000000.0;
          double trackBpm = mmpTrack.getHeader().getBpm();
  
          // Adjust the track's length when taking BPM markers into account
          Set<Marker> tempoMarkers = new TreeSet<Marker>(markerPositionComparator);
          tempoMarkers.addAll(mmpTrack.getMarkers(Marker.INTRO_TEMPO_MARKER));
          tempoMarkers.addAll(mmpTrack.getMarkers(Marker.TEMPO_MARKER));
  
          for (Marker tempoMarker : tempoMarkers) {
            double markerPosition = tempoMarker.getPosition() / 1000000.0;
            double bpm = Float.intBitsToFloat(tempoMarker.getVolume());
  
            // Calculate the actual time elapsed since the last marker
            double timeElapsed = position + markerPosition - previousBpmMarkerPosition;
            double averageBpm = (bpm + previousBpm) / 2;
            double bpmModifier = trackBpm / averageBpm;
            
            trackLength -= timeElapsed - (timeElapsed * bpmModifier);
  
            previousBpmMarkerPosition = markerPosition;
            previousBpm = bpm;
          }
  
          position += trackLength;
          break;
      }
    }
  
    return cueSheet;
  }
}
