
package com.kovacskornel.tlog16rs.resources;

import com.kovacskornel.tlog16rs.core.FutureWorkException;
import com.kovacskornel.tlog16rs.core.NegativeMinutesOfWorkException;
import com.kovacskornel.tlog16rs.core.NotSeparatedTimesException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
/**
 * <h1>WorkDay class</h1>
 * It contains the data of the workday and methods for {@link Task}s
 * @author Kovács Kornél
 * @version 0.1.0
 * @since 2016-11-03
 */
@Entity
@lombok.Getter
public class WorkDay{

    @Id @GeneratedValue
    private int id;
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Task> tasks = new ArrayList<>();
    private long requiredMinPerDay=450;
    private long extra_min_per_day;
    private LocalDate actualDay;
    private long sumPerDay;
    
    /**
     * @return the extra minutes worked this day
     */
    public long getExtraMinPerDay()
    {
        long x;
        x = getSumPerDay() - requiredMinPerDay;
        return x;
    }
    
    /**
     * setActualDay is a method to set the date of the WorkDay
     * @param date the date of the working day
     * @throws FutureWorkException if the date is in the future
     */
    public final void setActualDay(LocalDate date)
    {
        if(date.isAfter(LocalDate.now())) throw new FutureWorkException();
        else actualDay = date;
    }

    /**
     *
     * @return minutes worked today
     */
    public long getSumPerDay() {
        long x=0;
        int i;
        for (i=0;i<tasks.size();i++)
        {
            x+=tasks.get(i).getMinPerTask();
        }
        return x;
    }

    /**
     *
     * @param requiredMinPerDay set the required minutes to work this workday
     */
    public void setRequiredMinPerDay(long requiredMinPerDay) {
        this.requiredMinPerDay = requiredMinPerDay;
    }

    /**
     *
     * @param sumPerDay set the minutes worked this workday
     */
    public void setSumPerDay(long sumPerDay) {
        this.sumPerDay = sumPerDay;
    }
    
    /**
     * Adds a {@link Task} to the list
     * @param t A Task from user input
     */
    public void addTask(Task t)
    {
        if(tasks.isEmpty())
        {
            tasks.add(t);
        }
        else if(!isSeparatedTime(t)) throw new NotSeparatedTimesException();
        else tasks.add(t);
        
    }
    
    /**
     * Decides if the workday is a weekday or not
     * @param date The date of WorkDay
     * @return true if it is a weekday<br>false if it is not a weekday
     */
    public boolean isWeekDay(LocalDate date)
    {
        return !((date.getDayOfWeek() == DayOfWeek.SATURDAY) || (date.getDayOfWeek() == DayOfWeek.SUNDAY));
    }
    
    public WorkDay(LocalDate date, long reqmin)
    {
       setActualDay(date);
       if(reqmin < 0) throw new NegativeMinutesOfWorkException();
       else requiredMinPerDay = reqmin;
    }
    
    public WorkDay(LocalDate date)
    {
       setActualDay(date);
       requiredMinPerDay = 450;
    }
    
    /**
     *
     * @return The finishing time of the WorkDay's last task
     */
    public LocalTime endTimeOfTheLastTask()
    {
        if(tasks.isEmpty()) return null;
        return tasks.get(tasks.size()-1).getEnd_time();
    }
    
    /**
     *
     * @param t Task
     * @return true if it is separated from other task
     * <br> false if it is not
     */
    public final boolean isSeparatedTime(Task t)
    {       
        int i,j;
        LocalTime a,b,c,d;
        if(tasks.isEmpty()) return true;

            a = t.getStart_time();
            b = t.getEnd_time();
            if(t.getEnd_time()== null) return true;
            for(j=0;j<tasks.size();j++)
            {
                    boolean after,before;
                    c = tasks.get(j).getStart_time();
                    d = tasks.get(j).getEnd_time();
                    after = (a.isBefore(c) && a.isBefore(d)) ||( a.isAfter(c) && a.isAfter(d));
                    before = (b.isBefore(c) && b.isBefore(d)) || (b.isAfter(c) && b.isAfter(d));
                    if(!after || !before) {
                        if(c.equals(b) || d.equals(a)){
                        }else return false;
                    } else {
                    }
            }
        
        return true;
    }
    
}
