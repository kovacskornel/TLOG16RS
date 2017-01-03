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

    private TimeLogger gettl(){
        TimeLogger tilo= null;
        int i;
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
    
    TimeLogger tl = gettl();
    
    private WorkMonth createMonthIfNotExists(int year, int month)
    {
        int m;
        WorkMonth wm = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month)
            {
                wm = WM;
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
        WorkMonth MYWM = createMonthIfNotExists(year, month);
        WorkDay MYWD = null;
        if(!MYWM.getDays().isEmpty())
        {
            int d;
            for(d=0;d<MYWM.getDays().size();d++)
            {
                WorkDay WD = MYWM.getDays().get(d);
                if(WD.getActualDay().getDayOfMonth() == day)
                {
                    MYWD = WD;
                }
            }
        }
        if(MYWD == null)
        {
            MYWD = new WorkDay(LocalDate.of(year,month,day));
            MYWM.addWorkDay(MYWD);
            Ebean.save(tl);
        }
        return MYWD;
    }
    
    private Task CreateTaskIfNotExists(WorkDay MYWD, String taskid, String stime, String comment)
    {
        Task MyTask = null;
        if(!MYWD.getTasks().isEmpty())
        {
            int t;
            for(t=0;t<MYWD.getTasks().size();t++)
            {
                if(MYWD.getTasks().get(t).getTask_id().equals(taskid) && MYWD.getTasks().get(t).getStart_time() == MYWD.getTasks().get(t).stringToLocalTime(stime))
                {
                    MyTask = MYWD.getTasks().get(t);
                }
            }
        }
        if(MyTask == null)
        {
            MyTask = new Task(taskid,stime,comment);
            MYWD.addTask(MyTask);
            Ebean.save(tl);
        }
        return MyTask;
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
    public List MonthData(@PathParam(value = "year") int year, @PathParam(value="month") int month)
    {
        int m;
        WorkMonth MYWM;
        MYWM = createMonthIfNotExists(year, month);
        return MYWM.getDays();
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
        int m;
        WorkMonth MYWM;
        MYWM = createMonthIfNotExists(day.getYear(), day.getMonth());
        WorkDay wd = new WorkDay(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()),day.getRequiredHours());
        MYWM.addWorkDay(wd);
        Ebean.save(tl);
        return wd;
    }
 
    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List DayData(@PathParam(value = "year") int year, @PathParam(value="month") int month, @PathParam(value = "day") int day)
    {
        WorkDay MYWD = createDayIfNotExists(year, month, day);
        return MYWD.getTasks();
    }
    
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task startTask(StartTaskRB task) {
        WorkDay MYWD = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay());        
        Task MyTask = new Task(task.getTaskId(),task.getStartTime(),task.getComment());
        MYWD.addTask(MyTask);
        Ebean.save(tl);
        return MyTask;
    }
    
    @PUT
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishTaskRB task) {
        WorkDay MYWD = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay());
        Task MyTask = CreateTaskIfNotExists(MYWD,task.getTaskId(),task.getStartTime(),task.getComment());
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
        Task MyTask = CreateTaskIfNotExists(MYWD, task.getTaskId(), task.getStartTime(), task.getNewComment()); 
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
            int m;
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

                    if(MYWD.getTasks().get(t).getTask_id().equals(task.getTaskId()) && MYWD.getTasks().get(t).getStart_time() == MYWD.getTasks().get(t).stringToLocalTime(task.getStartTime()))
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