package cue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class CueSheet extends CueItem {
  private static final String QUOTATION_MARK = "\"";

  private SortedSet<Track> tracks;

  private String catalog;

  private String cdTextFile;

  public static CueSheet open(java.io.File cueFile) throws IOException {
    CueSheet cueSheet = new CueSheet("", "");

    BufferedReader reader = new BufferedReader(new FileReader(cueFile));
    CueItem currentItem = cueSheet;
    String token;

    for (String line; (line = reader.readLine()) != null;) {
      Scanner tok = new Scanner(line);

      if (!tok.hasNext()) {
        continue;
      }

      token = tok.next();

      if ("CATALOG".equalsIgnoreCase(token)) {

      } else if ("CDTEXTFILE".equalsIgnoreCase(token)) {

      } else if ("FLAGS".equalsIgnoreCase(token)) {

      } else if ("INDEX".equalsIgnoreCase(token) && currentItem instanceof Track) {
        // int indexNo =
        Integer.parseInt(tok.next()); // Slurp index index
        Scanner sc = tok.useDelimiter(Pattern.compile(":| "));
        int minutes = sc.nextInt(), seconds = sc.nextInt(), frames = sc.nextInt();
        Index index = new Index(minutes, seconds, frames);

        ((Track) currentItem).getIndices().add(index);
      } else if ("ISRC".equalsIgnoreCase(token)) {

      } else if ("TITLE".equalsIgnoreCase(token)) {
        String nextToken = tok.next();

        if (nextToken.startsWith(QUOTATION_MARK)) {
          if (nextToken.endsWith(QUOTATION_MARK)) {
            nextToken = nextToken.substring(1, nextToken.length() - 1);
          } else {
            nextToken = nextToken.substring(1) + tok.useDelimiter(QUOTATION_MARK).next();
          }
        }

        currentItem.setTitle(nextToken);
      } else if ("PERFORMER".equalsIgnoreCase(token)) {
        String nextToken = tok.next();

        if (nextToken.startsWith(QUOTATION_MARK)) {
          if (nextToken.endsWith(QUOTATION_MARK)) {
            nextToken = nextToken.substring(1, nextToken.length() - 1);
          } else {
            nextToken = nextToken.substring(1) + tok.useDelimiter(QUOTATION_MARK).next();
          }
        }

        currentItem.setPerformer(nextToken);
      } else if ("FILE".equalsIgnoreCase(token) && currentItem instanceof CueSheet) {
        String nextToken = tok.next();

        if (nextToken.startsWith(QUOTATION_MARK)) {
          if (nextToken.endsWith(QUOTATION_MARK)) {
            nextToken = nextToken.substring(1, nextToken.length() - 1);
          } else {
            nextToken = nextToken.substring(1) + tok.useDelimiter(QUOTATION_MARK).next();
          }
        }

        File file = new File(nextToken, File.Type.getType(tok.next()));

        currentItem.setFile(file);
      } else if ("TRACK".equalsIgnoreCase(token)) {
        int trackNo = tok.nextInt();
        String nextToken = tok.nextLine().trim().toUpperCase();
        Mode trackMode = Mode.getMode(nextToken);

        currentItem = new Track(trackNo, trackMode);

        cueSheet.getTracks().add((Track) currentItem);
      } else if ("POSTGAP".equalsIgnoreCase(token)) {

      } else if ("PREGAP".equalsIgnoreCase(token)) {

      } else if ("REM".equalsIgnoreCase(token) && tok.hasNextLine()) {
        System.out.println(tok.nextLine());
      } else if ("SONGWRITER".equalsIgnoreCase(token)) {

      }
    }

    reader.close();

    return cueSheet;
  }

  public CueSheet(String performer, String title) {
    super(performer, title);

    tracks = new TreeSet<Track>();
  }

  /**
   * Returns the tracks in the cue sheet
   * 
   * @return set of tracks
   */
  public SortedSet<Track> getTracks() {
    return tracks;
  }

  public String getCatalog() {
    return catalog;
  }

  public void setCatalog(String catalog) {
    if (catalog.length() > 13) {
      throw new IllegalArgumentException("Catalog can not contain more than 13 characters.");
    }

    this.catalog = catalog;
  }

  public String getCdTextFile() {
    return cdTextFile;
  }

  public void setCdTextFile(String cdTextFile) {
    this.cdTextFile = cdTextFile;
  }
}
