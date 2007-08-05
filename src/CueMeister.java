import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.TrackMeta;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.jaudiotagger.audio.InvalidAudioFrameException;
import org.jaudiotagger.audio.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import riff.RiffException;
import cue.CueSheet;
import cue.CueSheetWriter;
import cue.Index;
import cue.Mode;
import cue.Track;

public class CueMeister extends ApplicationWindow {
    private CueSheet cueSheet;

    private MixmeisterPlaylist mmp;

    private TableViewer tableViewer;

    public CueMeister() {
        super(new Shell());

        cueSheet = new CueSheet("", "");
    }

    private static MixmeisterPlaylist loadMixmeisterPlaylist(File f)
            throws FileNotFoundException, IOException {
        MixmeisterPlaylist mmp = MixmeisterPlaylist.open(f);
        return mmp;
    }

    private static CueSheet getCueSheetFromMixmeisterPlaylist(
            MixmeisterPlaylist mmp, boolean readID3) {
        CueSheet cueSheet = new CueSheet("CueMeister", "In the Mix");
        double position = 0;
        String performer, songTitle;

        for (int i = 0; i < mmp.getTracks().size(); ++i) {
            Track track = new Track(i + 1, Mode.AUDIO);

            // Default performer and title
            performer = "Unknown artist";
            songTitle = "Track " + track.getNumber();

            String fileName = mmp.getTracks().get(i).getFileName();

            // Fetch ID3
            if (readID3) {
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

    /**
     * @param tag
     * @param identifiers
     * @return
     */
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

    private Action createLoadAction() {
        return new Action("Load") {
            @Override
            public void run() {
                super.run();

                FileDialog dialog = new FileDialog(getParentShell());

                dialog.setFilterExtensions(new String[] { "*.mmp" });

                try {
                    final String path = dialog.open();
                    
                    if (path == null) {
                        return;
                    }
                    
                    File file = new File(path);

                    try {
                        setMixmeisterPlaylist(MixmeisterPlaylist.open(file));
                        
                        tableViewer.setInput(getCueSheetFromMixmeisterPlaylist(mmp, true));
                    } catch (RiffException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SWTException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell sh) {
        super.configureShell(sh);

        sh.setLayout(new GridLayout());
        sh.setText("CueMeister");
        sh.setSize(640, 480);
    }

    private TableViewer createTableViewer(Composite parent, int nColumns) {
        Table table = new Table(parent, SWT.V_SCROLL | SWT.BORDER
                | SWT.HIDE_SELECTION);
        table.setLinesVisible(true);

        TableViewer tableViewer = new TableViewer(table);

        GridData tableData = new GridData();

        tableData.horizontalAlignment = GridData.FILL;
        tableData.grabExcessHorizontalSpace = true;
        tableData.horizontalSpan = nColumns;

        tableData.verticalAlignment = GridData.CENTER;
        tableData.grabExcessVerticalSpace = true;
        
        table.setLayoutData(tableData);

        return tableViewer;
    }

    @Override
    protected Control createContents(Composite comp) {
        if (mmp != null) {
            cueSheet = getCueSheetFromMixmeisterPlaylist(mmp, true);
        }
        
        createToolbar(comp);
        
        String[] columnHeaders = new String[] { "Performer", "Title", "Index" };

        ColumnLayoutData[] columnLayouts = new ColumnLayoutData[] {
                new ColumnWeightData(3, true), new ColumnWeightData(4, true),
                new ColumnWeightData(1, true) };

        CellLabelProvider[] labelProviders = new CellLabelProvider[] {
        // Performer column
                new CellLabelProvider() {
                    @Override
                    public void update(ViewerCell cell) {
                        Track tr = (Track) cell.getElement();
                        cell.setText(tr.getPerformer());
                    }
                },

                // Title column
                new CellLabelProvider() {
                    @Override
                    public void update(ViewerCell cell) {
                        Track tr = (Track) cell.getElement();
                        cell.setText(tr.getTitle());
                    }
                },

                // Index column
                new CellLabelProvider() {
                    @Override
                    public void update(ViewerCell cell) {
                        Track tr = (Track) cell.getElement();

                        if (tr.getIndices().size() > 0) {
                            cell.setText(tr.getIndices().get(0).toString());
                        }
                    }
                } };

        tableViewer = createTableViewer(comp, 3);
        Table table = tableViewer.getTable();
        TableLayout tableLayout = new TableLayout();

        for (int i = 0; i < columnHeaders.length; ++i) {
            tableLayout.addColumnData(columnLayouts[i]);

            TableColumn tc = new TableColumn(table, SWT.NONE, i);

            tc.setResizable(columnLayouts[i].resizable);
            tc.setText(columnHeaders[i]);

            TableViewerColumn tvc = new TableViewerColumn(tableViewer, tc);

            tvc.setLabelProvider(labelProviders[i]);
        }

        table.setLayout(tableLayout);
        table.setHeaderVisible(true);

        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new CueSheetContentProvider());
        // tableView.setLabelProvider(new CueSheetLabelProvider());
        tableViewer.setInput(cueSheet);

        return table;
    }

    private void createToolbar(final Composite parent) {
        ToolBar toolBar = new ToolBar(parent, SWT.RIGHT | SWT.FLAT);
        ToolBarManager manager = new ToolBarManager(toolBar);
        
        manager.add(createLoadAction());
        manager.update(true);
    }

    /**
     * 
     *
     */
    public void run() {
        setBlockOnOpen(true);
        open();

        Display.getCurrent().dispose();
    }

    public static void main(String[] args) {
        boolean debug = true;
        boolean readID3 = true;
        String mmpFile = null;
        String outFile = null;

        for (int i = 0; i < args.length; ++i) {
            if ("-debug".equalsIgnoreCase(args[i])) {
                debug = false;
            } else if ("-f".equalsIgnoreCase(args[i])) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException(
                            "Must specify file after -f flag");
                }

                mmpFile = args[++i];
            } else if ("-o".equalsIgnoreCase(args[i])) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException(
                            "Must specify file after -o flag");
                }

                outFile = args[++i];
            } else if ("-no-id3".equalsIgnoreCase(args[i])) {
                readID3 = false;
            }
        }

        if (!debug) {
            Logger.getLogger("org.jaudiotagger.audio.mp3").setLevel(Level.OFF);
        }

        if (mmpFile != null) {
            try {
                MixmeisterPlaylist mmp = loadMixmeisterPlaylist(new File(
                        mmpFile));
                
                if (outFile == null) {
                    new CueSheetWriter(new PrintWriter(System.out)).write(getCueSheetFromMixmeisterPlaylist(mmp, readID3));
                } else {
                    new CueSheetWriter(new FileWriter(outFile)).write(getCueSheetFromMixmeisterPlaylist(mmp, readID3));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new CueMeister().run();
        }
    }

    private void setMixmeisterPlaylist(MixmeisterPlaylist mmp)
            throws RiffException {
        this.mmp = mmp;
    }
}
