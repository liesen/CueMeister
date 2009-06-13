package cuemeister.ui;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import mixmeister.mmp.MixmeisterPlaylist;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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

import cue.CueSheet;
import cue.CueSheetWriter;
import cuemeister.CueMeister;

/**
 * SWT- and JFace-based UI for CueMeister.
 * 
 */
public class CueMeisterApp extends ApplicationWindow {
  /** */
  public static final String DEFAULT_PERFORMER = "CueMeister";

  /** */
  public static final String DEFAULT_TITLE = "In the mix";

  /** */
  private CueSheet cueSheet;

  /** */
  private MixmeisterPlaylist mmp;

  /** */
  private TableViewer tableViewer;

  /** */
  private Text title;

  /** Name of the performer of the mix */
  private Text performer;

  /** Path to the music file */
  private Text file;

  /** Indicator for whether to read the ID3 tags from the tracks' MP3 file */
  private boolean readID3;

  /**
   * @param parentShell
   */
  public CueMeisterApp(Shell parentShell, boolean readID3) {
    super(parentShell);
    addMenuBar();
    this.readID3 = readID3;
    cueSheet = new CueSheet("", "");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.
   * Shell)
   */
  @Override
  protected void configureShell(Shell sh) {
    super.configureShell(sh);
    sh.setText("CueMeister");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.window.Window#initializeBounds()
   */
  @Override
  protected void initializeBounds() {
    getShell().setSize(540, 480);
  }

  @Override
  protected MenuManager createMenuManager() {
    MenuManager menu = super.createMenuManager();
    
    MenuManager file = new MenuManager("&File");
    file.add(createLoadAction());
    file.add(createSaveAction());
    menu.add(file);
    
    MenuManager help = new MenuManager("&Help");
    help.add(createAboutAction());
    menu.add(help);
    
    menu.update(true);
    return menu;
  }

  /**
   * Creates action that loads a MixMeister playlist
   * 
   * @return
   */
  private Action createLoadAction() {
    return new Action("Open MixMeister playlist") {
      @Override
      public void run() {
        FileDialog dialog = new FileDialog(getShell());
        dialog.setFilterExtensions(new String[] {"*.mmp"});

        try {
          final String path = dialog.open();

          if (path == null) {
            return;
          }

          File file = new File(path);

          try {
            mmp = MixmeisterPlaylist.open(file);
            cueSheet = CueMeister.convertMixmeisterPlaylistToCueSheet(mmp, readID3);
            tableViewer.setInput(cueSheet);
          } catch (FileNotFoundException e) {
            MessageDialog.openInformation(getShell(), "File not found", e.getMessage());
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
        MessageDialog.openInformation(getShell(), "About CueMeister",
            "CueMeister converts Mixmeister playlists to cue sheets\n" + "\n"
                + "Author: Johan Lies\u00e9n, johanliesen@gmail.com, "
                + "http://www.itstud.chalmers.se/~liesen/");
      }
    };
  }

  /**
   * @return
   */
  private Action createSaveAction() {
    return new Action("Save Cue sheet") {
      @Override
      public void run() {
        final String path = new FileDialog(getShell()).open();

        try {
          CueSheet cue = getCueSheet();
          CueSheetWriter w = new CueSheetWriter(new FileWriter(path));
          w.write(cue);
          w.close();
        } catch (IOException e) {
          MessageDialog.openError(getShell(), "Write error", "Could not write to file: " + path);
        }
      }
    };
  }

  /**
   * Creates table and viewer that displays the tracks
   * 
   * @param parent
   * @param numColumns
   * @return
   */
  private TableViewer createTableViewer(Composite parent, int numColumns) {
    GridData tableLayout = new GridData();
    tableLayout.horizontalAlignment = GridData.FILL;
    tableLayout.grabExcessHorizontalSpace = true;
    tableLayout.horizontalSpan = numColumns;
    tableLayout.verticalAlignment = GridData.CENTER;
    tableLayout.grabExcessVerticalSpace = true;

    Table table = new Table(parent, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
    table.setLayoutData(tableLayout);

    return new TableViewer(table);
  }

  @Override
  protected Control createContents(Composite parent) {
    if (mmp != null) {
      cueSheet = CueMeister.convertMixmeisterPlaylistToCueSheet(mmp, readID3);
    }

    Composite comp = new Composite(parent, SWT.NONE);
    GridLayoutFactory.swtDefaults().applyTo(comp);
    
    // Create form for inputting information related to the cue sheet 
    createForm(comp);

    final String[] columnHeaders = new String[] {"Performer", "Title", "Index"};
    ColumnLayoutData[] columnLayouts =
        new ColumnLayoutData[] {new ColumnWeightData(3, true), new ColumnWeightData(4, true),
            new ColumnWeightData(1, true)};

    tableViewer = createTableViewer(comp, 3);
    tableViewer.setContentProvider(new CueSheetContentProvider());
    tableViewer.setLabelProvider(new CueSheetLabelProvider());
    tableViewer.setUseHashlookup(true);

    final Table table = tableViewer.getTable();
    TableLayout tableLayout = new TableLayout();

    for (int i = 0; i < columnHeaders.length; ++i) {
      tableLayout.addColumnData(columnLayouts[i]);

      TableColumn tc = new TableColumn(table, SWT.NONE, i);
      tc.setResizable(columnLayouts[i].resizable);
      tc.setText(columnHeaders[i]);
      tc.pack();
    }

    table.setLayout(tableLayout);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    CellEditor[] editors =
        new CellEditor[] {new TextCellEditor(table), new TextCellEditor(table),
            new TextCellEditor(table)};

    editors[2].setValidator(new CueIndexValidator());

    tableViewer.setColumnProperties(columnHeaders);
    tableViewer.setCellModifier(new CueTrackModifier(tableViewer));
    tableViewer.setCellEditors(editors);

    return comp;
  }

  /**
   * Creates form for cue sheet properties
   * 
   * @param parent
   */
  protected void createForm(final Composite parent) {
    Group comp = new Group(parent, SWT.NONE);
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
    fileComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    file = new Text(fileComposite, SWT.BORDER);
    file.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    Button browseButton = new Button(fileComposite, SWT.PUSH);
    browseButton.setText("Browse...");
    browseButton.setLayoutData(new GridData(SWT.RIGHT));
    browseButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
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

  protected CueSheet getCueSheet() {
    cueSheet.setPerformer(performer.getText());
    cueSheet.setTitle(title.getText());

    if (file.getText() != null && !file.getText().isEmpty()) {
      String path = file.getText();
      String extension = path.substring(path.lastIndexOf('.'));

      if (".WAV".equalsIgnoreCase(extension)) {
        cueSheet.setFile(new cue.File(path, cue.File.Type.WAVE));
      } else {
        cueSheet.setFile(new cue.File(path));
      }
    }

    return cueSheet;
  }

  public void run() {
    setBlockOnOpen(true);
    open();
    Display.getCurrent().dispose();
  }
}
