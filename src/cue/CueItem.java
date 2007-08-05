package cue;

public abstract class CueItem {
    protected File file;
    protected String rem;
    protected String performer;
    protected String songwriter;
    protected String title;


    /**
     * @param file
     * @param performer
     * @param title
     */
    public CueItem(File file, String performer, String title) {
        this.file = file;
        this.performer = performer;
        this.title = title;
        this.rem = null;
        this.songwriter = null;
    }

    /**
     * @param performer
     * @param title
     */
    public CueItem(String performer, String title) {
        this(null, performer, title);
    }

    /**
     * @param file
     */
    public CueItem(File file) {
        this(file, null, null);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getRem() {
        return rem;
    }

    public void setRem(String rem) {
        this.rem = rem;
    }

    public String getSongwriter() {
        return songwriter;
    }

    public void setSongwriter(String songwriter) {
        this.songwriter = songwriter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
