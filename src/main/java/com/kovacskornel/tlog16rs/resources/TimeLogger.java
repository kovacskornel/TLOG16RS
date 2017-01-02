package com.kovacskornel.tlog16rs.resources;

import com.kovacskornel.tlog16rs.core.NotNewMonthException;
import com.kovacskornel.tlog16rs.core.NotNewMonthException;
import java.util.List;

import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
/**
 * TimeLogger is the class which contains the working months
 * @author Kovács Kornél
 * @version 0.1.0
 * @since 2016-11-03
 */
@Entity
public class TimeLogger{
    @Id @GeneratedValue
    private int id;
    
    @lombok.Getter
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<WorkMonth> months = new ArrayList<>();
    
    /**
     * Checks if the given working month exists
     * @param wm Working month
     * @return true if the WorkMonth already added to TimeLogger<br>false if it is a new WorkMonth
     */

    public boolean isNewMonth(WorkMonth wm) {
        boolean isnew = true;
        int i;
        for (i = 0; i < months.size(); i++) {
            if (months.get(i).date.equals(wm.date)) {
                isnew = false;
                break;
            }
        }
        return isnew;
    }
    
    /**
     *Adds a month to the TimeLogger
     * @param wm Working month
     * @exception NotNewMonthException if the month already exists
     */
    public void addMonth(WorkMonth wm) {
        if (isNewMonth(wm)) {
            if (months.add(wm)) {
                System.out.println("Successfully added a WorkMonth");
            } else {   
                System.out.println("Not added!");
            }
        } else {
            throw new NotNewMonthException();
        }
    }
    
}