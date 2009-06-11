package cuemeister;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import mixmeister.mmp.MixmeisterPlaylist;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import cue.CueSheetWriter;
import cuemeister.ui.CueMeisterApp;

/**
 * Launcher for CueMeister. Brings up the UI by default, but can be used on the
 * command line as well.
 * 
 */
public class Main {
  @Option(name = "-debug", usage = "Enable debugging output")
  private boolean debug = false;

  @Option(name = "-no-id3", usage = "Disable ID3 lookup")
  private boolean useID3 = true;

  @Option(name = "-f", usage = "MixMeister playlist file")
  private File mmpFile = null;

  @Option(name = "-o", usage = "Output file")
  private String out = "-";

  private void run(String... args) {
    CmdLineParser parser = new CmdLineParser(this);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      parser.printUsage(System.err);
      System.err.println();
      return;
    }

    if (!debug) {
      Logger.getLogger("org.jaudiotagger.audio.mp3").setLevel(Level.OFF);
    }

    if (mmpFile == null) {
      // Open UI
      new CueMeisterApp(null, useID3).run();
    } else {
      try {
        MixmeisterPlaylist mmp = MixmeisterPlaylist.open(mmpFile);
        Writer writer;

        if ("-".equals(out) || out == null) {
          writer = new PrintWriter(System.out);
        } else {
          writer = new FileWriter(out);
        }

        new CueSheetWriter(writer).write(
            CueMeister.convertMixmeisterPlaylistToCueSheet(mmp, useID3));
      } catch (Exception e) {
        if (debug) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * @param args program arguments
   */
  public static void main(String[] args) {
    new Main().run(args);
  }
}
