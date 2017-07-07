package com.kovacskornel.tlog16rs.resources;

import com.kovacskornel.tlog16rs.core.NotNewMonthException;
import java.util.List;

import java.util.ArrayList;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * TimeLogger = User
 *
 * @author Kovács Kornél
 * @since 2016-11-03
 */
@Entity
@lombok.Getter
@lombok.Setter
public class TimeLogger {

    private static final Logger LOGGER = Logger.getLogger(TimeLogger.class.getName());
    @Id
    @GeneratedValue
    private int id;
    private final String name;
    private String password;
    private String salt;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkMonth> months = new ArrayList();

    
    /**
     * Creates a TimeLogger element
     * @param name The user's name whose the TimeLogger is 
     */
    public TimeLogger(String name) {
        months = new ArrayList();
        this.name = name;
        this.password = "";
        this.salt = "";       
    }

    /**
     * Checks if the given working month exists
     *
     * @param wm Working month
     * @return true if the WorkMonth already added to TimeLogger<br>false if it
     * is a new WorkMonth
     */
    public boolean isNewMonth(WorkMonth wm) {
        boolean isItNewMonth = true;
        int i;
        for (i = 0; i < months.size(); i++) {
            if (months.get(i).getDate().equals(wm.getDate())) {
                isItNewMonth = false;
                break;
            }
        }
        return isItNewMonth;
    }

    /**
     * Adds a month to the TimeLogger
     *
     * @param wm Working month
     * @exception NotNewMonthException if the month already exists
     */
    public void addMonth(WorkMonth wm) {
        if (isNewMonth(wm)) {
            months.add(wm);
        } else {
            throw new NotNewMonthException();
        }
    }

}
