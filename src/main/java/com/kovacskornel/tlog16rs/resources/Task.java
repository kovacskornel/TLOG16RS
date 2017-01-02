/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.resources;
import com.kovacskornel.tlog16rs.core.EmptyTimeFieldException;
import com.kovacskornel.tlog16rs.core.InvalidTaskIDException;
import com.kovacskornel.tlog16rs.core.NegativeMinutesOfWorkException;
import com.kovacskornel.tlog16rs.core.NoTaskIDException;
import com.kovacskornel.tlog16rs.core.NotExpectedTimeOrderException;
import com.kovacskornel.tlog16rs.core.NotMultipleQuarterHourException;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author precognox
 */
@Entity
@lombok.Getter
@lombok.Setter
public class Task{
    
    @Id @GeneratedValue
    int id;
    private String task_id;
    private LocalTime start_time;
    private LocalTime end_time;
    private String comment;
    private int min_per_task;

    /**
     * <h3>isValidRedmineTaskId</h3>
     * Check if a Task's Id only contains 4 numbers
     * 
     * @param ID Gets the ID from user input
     * @return boolean<br>true if it is a valid redmine ID<br>false if it is not a valid redmine
     */
    public final boolean isValidRedmineTaskId(String ID)
    {
        return ID.matches("\\d{4}");
    }
    /**
     * <h3>isValidLTTaskId</h3>
     * Check if a Task's Id only contains "LT-" and 4 numbers
     * 
     * @param ID Gets the ID from user input
     * @return boolean<br>true if it is a valid LT task ID<br>false if it is not a valid LT task
     */    
    public final boolean isValidLTTaskId(String ID)
    {
        return ID.matches("LT-\\d{4}");
    }
    
    /**
     * <h3>isValidTaskID</h3>
     * check if the ID got from user input is an LT or redmine task
     * @param ID Gets the ID from user input
     * @return boolean<br>true if it is a valid redmine or LT task ID<br>false if it is not a valid redmine or LT task
     */
    public boolean isValidTaskID(String ID)
    {
        return isValidRedmineTaskId(ID) || isValidLTTaskId(ID);
    }
 
    /**
     *<h3>stringToLocalTime</h3>
     * Converts a string to LocalTime<br>Format: (HH:MM)
     * @param a Gets a string from user input
     * @return LocalTime<br>Hour from HH<br>Minutes from MM
     */
    public final LocalTime stringToLocalTime(String a){
    String[] parts = a.split(":");
    LocalTime x = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    return x;
    }

    /**
     * <h3>GetMinPerTask</h3>
     * Gets working minutes of the task
     * @return long - the working minutes of the task
     * @throws NotExpectedTimeOrderException if a tasks ends before it starts
     */
    
    
    public long getMinPerTask()
    {
        long x=0;
        if(end_time != null && end_time.getHour()*60+end_time.getMinute() > start_time.getHour()*60+start_time.getMinute())
        {
        x += (end_time.getHour()*60+end_time.getMinute())-(start_time.getHour()*60+start_time.getMinute());
        }
        else if(end_time!=null && end_time.getHour()*60+end_time.getMinute() > start_time.getHour()*60+start_time.getMinute()) throw new NotExpectedTimeOrderException();
        return x;
        
    }

    public Task(String task_id, LocalTime start_time, String comment) {
        if(isValidLTTaskId(task_id) || isValidRedmineTaskId(task_id))
        {
            this.task_id = task_id;
        }else throw new InvalidTaskIDException("Invalid task ID!");
        setStartTime(start_time);
        this.comment = comment;
    }
    
     public Task(String task_id, String sstring, String comment) {
        if(isValidLTTaskId(task_id) || isValidRedmineTaskId(task_id))
        {
            this.task_id = task_id;
        }else throw new InvalidTaskIDException("Invalid task ID!");
        this.comment = comment;
        setStartTime(sstring);
    }   

    public Task(String start_time, String end_time)
    {
        LocalTime etime,stime;
        if(start_time != null && end_time != null)
        {
            etime = stringToLocalTime(end_time);
            stime = stringToLocalTime(start_time);
        }
        else throw new EmptyTimeFieldException("Empty time field!");
        if(stime.getHour()*60+stime.getMinute() < etime.getHour()*60+etime.getMinute())
        {
            if(isMultipleQuarterHour(stime.getMinute()) && isMultipleQuarterHour(etime.getMinute()))
            {
                setStartTime(start_time);
                setEndTime(end_time);
            }
        }
        else if(stime == null || etime == null) throw new EmptyTimeFieldException("Empty time field!");
        else if((stime.getHour()*60+stime.getMinute()) > (etime.getHour()*60+etime.getMinute())) throw new NotExpectedTimeOrderException();        
    }
     
    public Task(LocalTime start_time, LocalTime end_time) {
        if(start_time != null && end_time != null && start_time.getHour()*60+start_time.getMinute() < end_time.getHour()*60+end_time.getMinute())
        {
                setStartTime(start_time);
                setEndTime(end_time);
        }
        else if(start_time == null || end_time == null) throw new EmptyTimeFieldException("Empty time field!");
        else if(start_time.getHour()*60+start_time.getMinute() > end_time.getHour()*60+end_time.getMinute()) throw new NotExpectedTimeOrderException();
    }
     
    public Task(String task_id, LocalTime start_time, LocalTime end_time, String comment) {
        this.task_id = task_id;
        setStartTime(start_time);
        setEndTime(end_time);
        this.comment = comment;
    }
    
    public Task(String task_id, String start_time, String end_time, String comment) {
        this.task_id = task_id;
        setStartTime(stringToLocalTime(start_time));
        setEndTime(stringToLocalTime(end_time));
        this.comment = comment;
    }

    public Task(String task_id) {
        if(task_id == null || "".equals(task_id) || task_id.isEmpty()) throw new NoTaskIDException();
        else if(isValidLTTaskId(task_id) || isValidRedmineTaskId(task_id) && !"".equals(task_id))
        {
            this.task_id = task_id;
        }else if(!isValidRedmineTaskId(task_id) || !isValidRedmineTaskId(task_id)) throw new InvalidTaskIDException();
    }

    /**
     * Sets the Task's ID
     * @param task_id the Task's ID from user input
     * @throws InvalidTaskIDException if the Task's ID is invalid
     */
    public void setTaskId(String task_id) {
        if(isValidLTTaskId(task_id) || isValidRedmineTaskId(task_id))
        {
        this.task_id = task_id;
        }
        else throw new InvalidTaskIDException("Not a valid task ID!");
        
    }

    /**
     * Sets the starting time of a task
     * @param start_time Starting time from user input
     * @exception EmptyTimeFieldException if the user leaves the time field empty
     */
    public final void setStartTime(LocalTime start_time) {
        if(start_time.getHour() !=0 || start_time.getMinute() != 0 && isMultipleQuarterHour(start_time.getMinute()))
        {
            this.start_time = start_time;
        } else throw new EmptyTimeFieldException("Empty start time!");
    }
    /**
     * Sets the finishing time of a task
     * @param end_time Finishing time from user input
     * @exception EmptyTimeFieldException if the user leaves the time field empty
     */
    public final void setEndTime(LocalTime end_time) {

        if(end_time.getHour() !=0 || end_time.getMinute() != 0 && isMultipleQuarterHour(end_time.getMinute()))
        {
        this.end_time = end_time;
        } else throw new EmptyTimeFieldException("Empty end time!");
        if(start_time.getHour()*60+start_time.getMinute() > end_time.getHour()*60+end_time.getMinute())
        {
            throw new NegativeMinutesOfWorkException();
        }
    }
    
    /**
     * String input version of {@link setEndTime(LocalTime end_time)}
     * @param start_time
     */
    public final void setStartTime(String start_time) {
        setStartTime(stringToLocalTime(start_time));
    }
    /**
     * String input version of {@link setStartTime(LocalTime end_time)}
     * @param end_time
     */
    public final void setEndTime(String end_time) {
        setEndTime(stringToLocalTime(end_time));
    }

    /**
     *Sets the comment of a task
     * @param comment string from user input
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     *
     * @param min Minutes of a time
     * @return boolean<br>true if it is the multiple of quarter hour
     * <br>false if it is not the multiple of quarter hour
     */
    public final boolean isMultipleQuarterHour(long min)
    {
        if(min%15!=0) throw new NotMultipleQuarterHourException();
        else return true;
    }
}

