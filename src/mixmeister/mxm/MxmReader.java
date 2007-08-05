package mixmeister.mxm;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import mixmeister.mmp.MixmeisterPlaylist;
import mixmeister.mmp.Track;
import riff.Chunk;
import riff.ChunkContainer;
import riff.RiffFile;

public class MxmReader {
    public static void main(String... args) throws IOException {
        PrintStream log = System.out;
        
        MixmeisterPlaylist mmp = MixmeisterPlaylist.open(new File("test.mmp"));
        
        log.println(mmp);
        
        Track track = mmp.getTracks().get(0);
        String trackId = track.getHeader().getArchiveKey();
        
        System.out.println(trackId);
        
        File archiveFile = new File("C:\\Documents and Settings\\johan\\Application Data\\MixMeister Technology\\MixMeister Engine 6\\Archive\\" + trackId + ".mxm");
        RiffFile riff = new RiffFile(archiveFile);
        
        riff.read();
        
        Chunk plot = null;
        
        for (Chunk p : ((ChunkContainer) riff).getChunks()) {
            if ("plot".equalsIgnoreCase(p.getIdentifier())) {
                plot = p;
                
                break;
            }
        }
        
        System.out.println(plot);
        
        display(riff);
        /*
        final BufferedImage plotImg = createPlot(plot);
        
        JFrame frame = new JFrame();
        frame.add(new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                g.drawImage(plotImg, 0, 0, this);
            }
        });
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);*/
    }
    
    private static void display(Chunk ch) {
        System.out.println(ch.getIdentifier() + "\t");
        
        if (ch.canContainSubchunks()) {
            for (Chunk ch2 : ((ChunkContainer) ch).getChunks()) {
                display(ch2);
                System.out.println();
            }
        }
        
        if (ch.getData() == null || ch.getLength() == 0) {
            System.out.println();
            
            return;
        }
        
        IntBuffer buf = ByteBuffer.wrap(ch.getData()).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int length = buf.capacity();
        int[] data = new int[length];
        
        buf.get(data);
        
        for (int i = 0; i < data.length; ++i) {
            System.out.printf("%12s", data[i]);
        }
        
        System.out.println();
    }
    
    public static BufferedImage createPlot(Chunk plot) {
        IntBuffer buf = ByteBuffer.wrap(plot.getData()).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int npoints = buf.capacity();
        int[] ypoints = new int[npoints];
        
        buf.get(ypoints);
        
        int[] xpoints = new int[npoints];
        
        for (int i = 0; i < npoints; ++i) {
            xpoints[i] = i;
        }
        
        Polygon p = new Polygon(xpoints, ypoints, npoints);
        int width = npoints;
        int height = max(ypoints) >> 3;
        
        BufferedImage im = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D g = (Graphics2D) im.getGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        
        g.draw(p);
        
        return im;
    }
    
    public static int max(int... values) {
        int max = values[0];
        
        for (int i = 1; i < values.length; ++i) {
            max = Math.max(max, values[i]);
        }
        
        return max;
    }
}
