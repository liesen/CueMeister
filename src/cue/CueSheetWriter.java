package cue;

import java.io.IOException;
import java.io.Writer;

public class CueSheetWriter {
  Writer out;

  public CueSheetWriter(Writer out) {
    this.out = out;
  }

  public void write(CueSheet cue) throws IOException {
    if (cue.getFile() != null) {
      out.append(String.format("%s%n", cue.getFile()));
    }

    if (cue.getPerformer() != null) {
      out.append(String.format("PERFORMER \"%s\"%n", cue.getPerformer()));
    }

    if (cue.getTitle() != null) {
      out.append(String.format("TITLE \"%s\"%n", cue.getTitle()));
    }

    // Add tracks
    for (Track track : cue.getTracks()) {
      out.write("  TRACK ");
      out.write(String.format("%02d %s%n", track.getNumber(), track.getMode()));

      if (track.getFile() != null) {
        out.write(String.format("    %s%n", track.getFile()));
      }

      if (track.getIsrc() != null && track.getIsrc().length() > 0) {
        out.write(String.format("    ISRC %s%n", track.getIsrc()));
      }

      out.write(String.format("    PERFORMER \"%s\"%n", track.getPerformer()));
      out.write(String.format("    TITLE \"%s\"%n", track.getTitle()));

      if (track.getFlags().size() > 0) {
        out.write("    FLAGS");

        for (Flag f : track.getFlags()) {
          out.write(" ");
          out.write(f.toString());
        }

        out.write(String.format("%n"));
      }

      for (int i = 0; i < track.getIndices().size(); ++i) {
        Index index = track.getIndices().get(0);

        out.write(String.format("    INDEX %02d %s%n", i + 1, index));
      }
    }

    out.flush();
  }

  public void close() throws IOException {
    out.close();
  }
}
