package com.kovacskornel.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;


@Path("/timelogger")
public class TLOG16RSResource {    
    
    static String myname = "test";
    private TimeLogger tl = gettl();
    private TimeLogger gettl(){
        TimeLogger tilo= null;
        for (TimeLogger til : Ebean.find(TimeLogger.class).findList()) {
        if(til.getName().equals(myname))
        {
            tilo = til;
        }
        }
        if(tilo == null)
        {
            tilo = new TimeLogger(myname);
        }
        return tilo;
    }
    
    
    
    private WorkMonth createMonthIfNotExists(int year, int month)
    {
        int m;
        WorkMonth wm = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth workMonth = tl.getMonths().get(m);
            if(workMonth.getDate().getYear() == year && workMonth.getDate().getMonthValue() == month)
            {
                wm = workMonth;
            }
            }
        }
        if(wm == null)
        {
            wm = new WorkMonth(YearMonth.of(year, month));
        }
        tl.addMonth(wm);
        Ebean.save(tl);
        return wm;
    }
    
    private WorkDay createDayIfNotExists(int year,int month, int day)
    {
        WorkMonth mywm = createMonthIfNotExists(year, month);
        WorkDay mywd = null;
        if(!mywm.getDays().isEmpty())
        {
            int d;
            for(d=0;d<mywm.getDays().size();d++)
            {
                WorkDay wd = mywm.getDays().get(d);
                if(wd.getActualDay().getDayOfMonth() == day)
                {
                    mywd = wd;
                }
            }
        }
        if(mywd == null)
        {
            mywd = new WorkDay(LocalDate.of(year,month,day));
            mywm.addWorkDay(mywd);
            Ebean.save(tl);
        }
        return mywd;
    }
    
    private Task createTaskIfNotExists(WorkDay mywd, String taskid, String stime, String comment)
    {
        Task myTask = null;
        if(!mywd.getTasks().isEmpty())
        {
            int t;
            for(t=0;t<mywd.getTasks().size();t++)
            {
                if(mywd.getTasks().get(t).getTaskId().equals(taskid) && mywd.getTasks().get(t).getStartTime() == mywd.getTasks().get(t).stringToLocalTime(stime))
                {
                    myTask = mywd.getTasks().get(t);
                }
            }
        }
        if(myTask == null)
        {
            myTask = new Task(taskid,stime,comment);
            mywd.addTask(myTask);
            Ebean.save(tl);
        }
        return myTask;
    }
    
    @Path ("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List getStatistics()
    {
        return tl.getMonths(); 
    }

    /**
     * Adds a workmonth
     * @param month int year, int month
     * @return workMonth
     */
    @POST
    @Path("/workmonths")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewMonth(WorkMonthRB month) {
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        tl.addMonth(workMonth);
        Ebean.save(tl);
        return workMonth;
    }
    
	
    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List monthData(@PathParam(value = "year") int year, @PathParam(value="month") int month)
    {
        WorkMonth mywm;
        mywm = createMonthIfNotExists(year, month);
        return mywm.getDays();
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    @Produces(MediaType.APPLICATION_JSON)
    public void delAll()
    {
        Ebean.delete(tl);
        tl = new TimeLogger(myname);
        Ebean.save(tl);
    }

    
    @POST
    @Path("/workmonths/workdays")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewDay(WorkDayRB day) {
        WorkMonth mywm;
        mywm = createMonthIfNotExists(day.getYear(), day.getMonth());
        WorkDay wd = new WorkDay(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()),day.getRequiredHours());
        mywm.addWorkDay(wd);
        Ebean.save(tl);
        return wd;
    }
 
    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List dayData(@PathParam(value = "year") int year, @PathParam(value="month") int month, @PathParam(value = "day") int day)
    {
        WorkDay mywd = createDayIfNotExists(year, month, day);
        return mywd.getTasks();
    }
    
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task startTask(StartTaskRB task) {
        WorkDay mywd = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay());        
        Task myTask = new Task(task.getTaskId(),task.getStartTime(),task.getComment());
        mywd.addTask(myTask);
        Ebean.save(tl);
        return myTask;
    }
    
    @PUT
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishTaskRB task) {
        WorkDay MYWD = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay());
        Task MyTask = createTaskIfNotExists(MYWD,task.getTaskId(),task.getStartTime(),task.getComment());
        MyTask.setEndTime(task.getEndTime());
        Ebean.save(tl);
        return MyTask;
    }
    
    @PUT
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task modifyTask(ModifyTaskRB task) {
        WorkDay MYWD = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay());
        Task MyTask = createTaskIfNotExists(MYWD, task.getTaskId(), task.getStartTime(), task.getNewComment()); 
        MyTask.setEndTime(task.getNewEndTime());
        MyTask.setStartTime(task.getNewStartTime());
        MyTask.setTaskId(task.getNewTaskId());
        Ebean.save(tl);
        return MyTask;
        }
    
    private WorkMonth isMonthExists(int year, int month)
    {
        int m;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month)
            {
                return WM;
            }
            }
        }
        return null;
    }
    
    private WorkDay isDayExists(WorkMonth MYWM, int day)
    {
            if(!MYWM.getDays().isEmpty())
            {
                int d;
                for(d=0;d<MYWM.getDays().size();d++)
                {
                    WorkDay WD = MYWM.getDays().get(d);
                    if(WD.getActualDay().getDayOfMonth() == day)
                    {
                        return WD;
                    }
                }
            }
        return null;
    }
    
    @PUT
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task deleteTask(DeleteTaskRB task) {
        Task delTask=null;
            WorkMonth MYWM = isMonthExists(task.getYear(), task.getMonth());
            if(MYWM == null)
            {
                return delTask;
            }
            WorkDay MYWD = isDayExists(MYWM, task.getDay());
            if(MYWD == null)
            {
                return delTask;
            }
            if(MYWD.getTasks().isEmpty())
            {
                int t;
                for(t=0;t<MYWD.getTasks().size();t++)
                {

                    if(MYWD.getTasks().get(t).getTaskId().equals(task.getTaskId()) && MYWD.getTasks().get(t).getStartTime() == MYWD.getTasks().get(t).stringToLocalTime(task.getStartTime()))
                    {
                        delTask = MYWD.getTasks().get(t);
                    }
                }
            }
            MYWD.getTasks().remove(delTask);
            Ebean.delete(delTask);
            return delTask;
    }
    
}