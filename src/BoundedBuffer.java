import javax.swing.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**Class representing a boundedbuffer holding strings, where each position can have three different
 * status, Empty, New or Changed, which is saved in a BufferStatus array. It has methods for writing to, modify, and read
 * from positions depending on status of the position.
 * Created by Jimmy on 2015-12-15.
 */
public class BoundedBuffer {
    private String[] buffer;
    private BufferStatus[] status;
    private int size;

    private int writePos = 0, readPos = 0, findPos = 0;
    private boolean notify;
    private String findString;
    private String replaceString;

    private final Lock lock = new ReentrantLock();
    //Conditions the different methods wait for and signal.
    private final Condition Empty = lock.newCondition();
    private final Condition Checked = lock.newCondition();
    private final Condition New = lock.newCondition();

    /**
     * Enum for the 3 different status on the posisitions in the buffer
     */
    private enum BufferStatus { EMPTY, CHECKED, NEW}

    /**
     * Constructor taking 4 parameters
     * @param elements how many positions the buffer should be able to hold
     * @param notify boolean if user want to be notified on every match
     * @param find the string to to check buffer against
     * @param replace the string to replace find with if found
     */
    public BoundedBuffer (int elements, boolean notify, String find, String replace) {
        size = elements;
        this.notify = notify;
        findString = find;
        replaceString = replace;
        buffer = new String[elements];
        status = new BufferStatus[elements];
        // Initiate all the positions in the status array to EMPTY
        for (int i = 0; i < status.length; i++){
            status[i] = BufferStatus.EMPTY;
        }
    }

    /**
     * Writes a string to the buffer
     * @param str the string to write to buffer
     * @throws InterruptedException
     */
    public void writeData (String str) throws InterruptedException{
        lock.lock();
        try {
            // Only writes if writePos in status array is EMPTY, else it waits for signal
            if (!status[writePos].equals(BufferStatus.EMPTY)){
                Empty.await();
            }
            buffer[writePos] = str;
            status[writePos] = BufferStatus.NEW;
            writePos = (writePos + 1) % size;
            New.signal();
        }finally {
            lock.unlock();
        }
    }

    /**
     * Modifies a string in the buffer after checking that it is the findString
     * @throws InterruptedException
     */
    public void modify() throws InterruptedException {
        lock.lock();
        try {
            //Only tries to modify if findPos is NEW, else it waits for signal
            if (!status[findPos].equals(BufferStatus.NEW)) {
                New.await();
            }
            if (buffer[findPos].equals(findString)) {
                if (notify) {
                    int a = JOptionPane.showConfirmDialog(null, "Replace " + buffer[findPos] + " with " + replaceString + "?");
                    if (a == 0) {
                        System.out.format("MODIFIED %s to %s\n", buffer[findPos], replaceString);
                        buffer[findPos] = replaceString;
                    }
                } else {
                    System.out.format("MODIFIED %s to %s\n", buffer[findPos], replaceString);
                    buffer[findPos] = replaceString;
                }
            }
            status[findPos] = BufferStatus.CHECKED;
            findPos = (findPos + 1) % size;
            Checked.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reads a string in the buffer
     * @return the string
     * @throws InterruptedException
     */
    public String readData () throws InterruptedException{
        lock.lock();
        String newString = "";
        try {
            // Only reads the position if status is CHECKED, else it waits for signal
            if (!status[readPos].equals(BufferStatus.CHECKED)){
                Checked.await();
            }
            newString = buffer[readPos];
            status[readPos] = BufferStatus.EMPTY;
            readPos = (readPos + 1) % size;
            Empty.signal();
        }finally {
            lock.unlock();
        }
        return newString;
    }
}
