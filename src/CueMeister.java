import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mixmeister.mmp.Marker;
import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.TrackType;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jaudiotagger.audio.InvalidAudioFrameException;
import org.jaudiotagger.audio.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import riff.RiffException;
import cue.CueSheet;
import cue.CueSheetWriter;
import cue.Index;
import cue.Track;

public class CueMeister extends ApplicationWindow {
    public static final String DEFAULT_PERFORMER = "CueMeister";

    public static final String DEFAULT_TITLE = "In the mix";

    private CueSheet cueSheet;

    private MixmeisterPlaylist mmp;

    private TableViewer tableViewer;

    private Text title;

    private Text performer;

    private Text file;

    public CueMeister() {
        super(null);

        addMenuBar();

        cueSheet = new CueSheet("", "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell sh) {
        super.configureShell(sh);

        sh.setText("CueMeister");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#initializeBounds()
     */
    @Override
    protected void initializeBounds() {
        getShell().setSize(540, 480);
    }

    @Override
    protected MenuManager createMenuManager() {
        MenuManager menu = new MenuManager();

        menu.add(createLoadAction());
        menu.add(createSaveAction());
        menu.add(createAboutAction());

        return menu;
    }

    /**
     * @return
     */
    private Action createLoadAction() {
        return new Action("Load") {
            @Override
            public void run() {
                super.run();

                FileDialog dialog = new FileDialog(getShell());
                dialog.setFilterExtensions(new String[] { "*.mmp" });

                try {
                    final String path = dialog.open();

                    if (path == null) {
                        return;
                    }

                    File file = new File(path);

                    try {
                        mmp = MixmeisterPlaylist.open(file);
                        cueSheet = getCueSheetFromMixmeisterPlaylist(mmp, true);
                        tableViewer.setInput(cueSheet);
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

    /**
     * @return
     */
    private Action createAboutAction() {
        return new Action("About") {
            @Override
            public void run() {
                super.run();

                MessageDialog
                        .openInformation(
                                getShell(),
                                "About CueMeister",
                                "CueMeister converts Mixmeister playlists to cue sheets\n"
                                        + "\n"
                                        + "Author: Johan Liesén, johanliesen@gmail.com, "
                                        + "http://www.itstud.chalmers.se/~liesen/");
            }
        };
    }

    /**
     * @return
     */
    private Action createSaveAction() {
        return new Action("Save") {
            @Override
            public void run() {
                super.run();

                final String path = new FileDialog(getShell()).open();

                try {
                    CueSheet cue = gatherCueSheet();
                    new CueSheetWriter(new FileWriter(path)).write(cue);
                } catch (IOException e) {
                    MessageDialog.openError(getShell(), "Write error",
                            "Could not write to file");
                }

            }
        };
    }

    /**
     * Converts MMP to CUE
     * 
     * @param mmp
     * @param readID3 use ID3 lookup
     * @return
     */
    private static CueSheet getCueSheetFromMixmeisterPlaylist(
            MixmeisterPlaylist mmp, boolean readID3) {
        CueSheet cueSheet = new CueSheet("", "");
        double position = 0;
        String performer, songTitle;
        mixmeister.mmp.Track mmpTrack;

        for (int i = 0; i < mmp.getTracks().size(); ++i) {
            mmpTrack = mmp.getTracks().get(i);

            switch (mmpTrack.getHeader().getTrackType()) {
            case TrackType.OVERLAY:
            case TrackType.OVERLAY_WITH_BEATSYNC:
            case TrackType.OVERLAY_WITHOUT_BEATSYNC:
                break;

            default:
                cue.Track cueTrack = new cue.Track(i + 1);

                performer = "Unknown artist";
                songTitle = "Track " + cueTrack.getNumber();

                String fileName = mmpTrack.getFileName();

                // Fetch ID3
                if (readID3) {
                    try {
                        MP3File mp3 = new MP3File(new File(fileName),
                                MP3File.LOAD_ALL, true);

                        performer = ID3Helper.getArtist(mp3);
                        songTitle = ID3Helper.getTitle(mp3);
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

                cueTrack.setPerformer(performer);
                cueTrack.setTitle(songTitle);
                cueTrack.getIndices().add(new Index(position));

                cueSheet.getTracks().add(cueTrack);

                Marker outroAnchor = new LinkedList<Marker>(mmpTrack
                        .getMarkers(Marker.OUTRO_RANGE)).getLast();
                Marker introAnchor = new LinkedList<Marker>(mmpTrack
                        .getMarkers(Marker.INTRO_RANGE)).getLast();

                position += (outroAnchor.getPosition() - introAnchor
                        .getPosition()) / 1000000.0;
                break;
            }
        }

        return cueSheet;
    }

    private TableViewer createTableViewer(Composite parent, int nColumns) {
        Table table = new Table(parent, SWT.V_SCROLL | SWT.BORDER
                | SWT.HIDE_SELECTION);

        GridData tableData = new GridData();

        tableData.horizontalAlignment = GridData.FILL;
        tableData.grabExcessHorizontalSpace = true;
        tableData.horizontalSpan = nColumns;

        tableData.verticalAlignment = GridData.CENTER;
        tableData.grabExcessVerticalSpace = true;

        return new TableViewer(table);
    }

    @Override
    protected Control createContents(Composite parent) {
        if (mmp != null) {
            cueSheet = getCueSheetFromMixmeisterPlaylist(mmp, true);
        }

        Composite comp = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().applyTo(comp);

        createForm(comp);

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
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new CueSheetContentProvider());
        // tableView.setLabelProvider(new CueSheetLabelProvider());
        tableViewer.setInput(cueSheet);

        return comp;
    }

    protected void createForm(final Composite parent) {
        // Composite comp = new Composite(parent, SWT.NONE);
        Group comp = new Group(parent, SWT.NONE);
        comp.setText("Cue sheet information");

        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        new Label(comp, SWT.NONE).setText("Performer");

        performer = new Text(comp, SWT.BORDER);
        performer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        performer.setText(DEFAULT_PERFORMER);

        new Label(comp, SWT.NONE).setText("Title");

        title = new Text(comp, SWT.BORDER);
        title.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        title.setText(DEFAULT_TITLE);

        new Label(comp, SWT.NONE).setText("File");

        Composite fileComposite = new Composite(comp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(fileComposite);
        fileComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                false));

        file = new Text(fileComposite, SWT.BORDER);
        file.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Button browseButton = new Button(fileComposite, SWT.PUSH);
        browseButton.setText("Browse...");
        browseButton.setLayoutData(new GridData(SWT.RIGHT));
        browseButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                final String path = new FileDialog(parent.getShell()).open();

                if (path != null) {
                    file.setText(path);
                }
            }

        });
    }

    protected CueSheet gatherCueSheet() {
        CueSheet cue = getCueSheetFromMixmeisterPlaylist(mmp, true); // new CueSheet(performer.getText(), title.getText());

        cue.setPerformer(performer.getText());
        cue.setTitle(title.getText());

        if (file.getText() != null && !file.getText().isEmpty()) {
            cue.setFile(new cue.File(file.getText()));
        }

        return cue;
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
                MixmeisterPlaylist mmp = MixmeisterPlaylist.open(new File(
                        mmpFile));

                if (outFile == null) {
                    new CueSheetWriter(new PrintWriter(System.out))
                            .write(getCueSheetFromMixmeisterPlaylist(mmp,
                                    readID3));
                } else {
                    new CueSheetWriter(new FileWriter(outFile))
                            .write(getCueSheetFromMixmeisterPlaylist(mmp,
                                    readID3));
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
}
