/**Class that replaces the sought word with it's replacement word
 * Created by Jimmy on 2015-12-15.
 */
public class Modifier implements Runnable{

    private BoundedBuffer buffer;
    private int count;

    /**
     * Constructor
     * @param buf the buffer
     * @param nbrOfStr the number of strings to search through
     */
    public Modifier (BoundedBuffer buf, int nbrOfStr) {
        buffer = buf;
        count = nbrOfStr;
    }

    /**
     * Method that searched through every string in the buffer and modifies
     * if needed
     */
    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
             try {
                 buffer.modify();
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }
        System.out.println("Modifier finished");

    }
}
