import java.util.ArrayList;
import java.util.List;

/**Class that reads strings from the bounded buffer
 * Created by Jimmy on 2015-12-15.
 */
public class Reader implements Runnable{
    private BoundedBuffer buffer;
    private int count;
    private List<String> stringList;
    private Controller controller;

    /**
     * Constructor
     * @param buf the buffer
     * @param nbrOfStr the number of strings to read
     * @param controller the controller
     */
    public Reader (BoundedBuffer buf, int nbrOfStr, Controller controller) {
        buffer = buf;
        count = nbrOfStr;
        this.controller = controller;
        stringList = new ArrayList<>();
    }

    /**
     * Method that runs as long as it has more strings to read
     */
    @Override
    public void run() {
        String newString = "";
        for (int i = 0; i < count; i++) {
            try {
                newString = buffer.readData();
                stringList.add(newString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("FINISHED!!!");
        controller.updateDest(stringList); // updates the destination window when done reading
    }
}
