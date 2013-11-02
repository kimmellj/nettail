package main.java.com.fluid;

import java.util.Comparator;

/**
 * This comparator is used to sort a list of log files by their time stamp descending
 * 
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class LogListDateComparator implements Comparator<Log> {

    @Override
    public int compare(Log o1, Log o2) {
        if (o1.getLastModifiedTimeStamp() == o2.getLastModifiedTimeStamp()) {
            return 0;
        }

        return o1.getLastModifiedTimeStamp() > o2.getLastModifiedTimeStamp() ? -1 : 1;
    }
}