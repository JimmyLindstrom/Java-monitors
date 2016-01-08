import java.util.List;

/** Class that writes strings to a bounded buffer
 * Created by Jimmy on 2015-12-15.
 */
public class Writer implements Runnable{
    private BoundedBuffer buffer;
    private List<String> textToWrite;

    /**
     * Constructor
     * @param buf the buffer
     * @param textIn list of the strings to write
     */
    public Writer (BoundedBuffer buf, List<String> textIn) {
        buffer = buf;
        textToWrite = textIn;
    }

    /**
     * Method that runs as many times as it has strings
     */
    @Override
    public void run() {

        for (String str: textToWrite) {
            try {
                buffer.writeData(str);
            } catch (Exception e) {
                System.out.println("Error Writing: " + str);
            }
        }
        System.out.println("WRITER FINISHED");
    }
}
