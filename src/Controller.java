import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Controller class that handles the communication between gui, threads and the buffer
 * Created by Jimmy on 2015-12-21.
 */
public class Controller {
    private GUIMonitor gui;
    private String inText;
    private String find;
    private String replace;
    private boolean notify;
    private int matches = 0;
    private MyHighLighter HL = new MyHighLighter(Color.GREEN);

    private JTextPane source;
    private JTextPane dest;
    private int size;

    /**
     * Constructor for the controller
     * @param gui the gui
     * @param source the source JTextPane
     * @param dest the destination JTextPane
     * @param notify boolean if user wants to be notified on matches
     * @param find the string to search for
     * @param replace the string to replace "find" with
     */
    public Controller (GUIMonitor gui, JTextPane source, JTextPane dest, Boolean notify, String find, String replace) {
        this.gui = gui;
        this.source = source;
        this.dest = dest;
        this.find = find;
        this.replace = replace;
        inText = source.getText();
        this.notify = notify;
        // Highlights the words in the source pane
        if (!this.find.equals("")) {
            HL.highlight(this.source, this.find);
        }
        gui.changes(matches); // updates gui with hits
        initThreads();
    }

    /**
     * Creates a buffer and creates/starts the writer/reader/modifier threads
     */
    public void initThreads () {
        ArrayList<String> inString;
        inString = new ArrayList<>(Arrays.asList(inText.split("( )|(?<=\r\n)")));
        size = inString.size();
        BoundedBuffer buffer = new BoundedBuffer(15, notify, find, replace);
        Thread write = new Thread(new Writer(buffer, inString));
        Thread read = new Thread(new Reader(buffer, size, this));
        Thread modify = new Thread(new Modifier(buffer, size));
        write.start();
        read.start();
        modify.start();
    }

    /**
     * Adds the text to the destination tab, after replacing the words you want replaced
     * @param listOfStr The text as a list of strings
     */
    public void updateDest (List<String> listOfStr) {
        String destString = "";
        Document destDoc = dest.getDocument();
        for (String s: listOfStr) {
            if (s.contains("\n")) {
                destString += s; // Ifall ordet slutar med radbryt lägg ej till mellanslag efter
            } else {
                destString += s + " "; //lägg till mellanslag efter alla strängar
            }
        }
        try {
            destDoc.insertString(destDoc.getLength(), destString, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (!this.find.equals("")) {
            MyHighLighter newHL = new MyHighLighter(Color.GREEN);
            newHL.highlight(dest, replace);
        }
    }

    /**
     * Removes the highlights in the text
     */
    public void removeHighlite () {
        if (!this.find.equals("")) {
            HL.remove();
        }
    }

    /**
     * Class for highlighting the word your looking for in the text
     */
    public class MyHighLighter extends DefaultHighlighter.DefaultHighlightPainter {
        Highlighter hilite = null;

        /**
         * Construnctor taking the color you want to use for highlighting as
         * parameter
         * @param color the highlight color
         */
        public MyHighLighter (Color color) {
            super(color);
        }

        /**
         * Highlights the pattern in the text
         * @param textComp the component holding the text
         * @param pattern the pattern your looking for
         */
        public void highlight (JTextComponent textComp, String pattern) {
            try {
                hilite = textComp.getHighlighter();
                Document doc = textComp.getDocument();
                String text = doc.getText(0, doc.getLength());

                int pos = 0;
                while ((pos = text.indexOf(pattern, pos)) >= 0) {
                    hilite.addHighlight(pos, pos+pattern.length(), HL);
                    matches++;
                    pos += pattern.length();
                }
            } catch (Exception e){}
        }

        /**
         * Removes the highlights in the text
         */
        public void remove() {
            hilite.removeAllHighlights();
        }
    }
}
