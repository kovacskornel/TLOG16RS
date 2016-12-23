package com.kovacskornel.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import com.kovacskornel.tlog16rs.CreateDatabase;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.kovacskornel.tlog16rs.core.TimeLogger;
import com.kovacskornel.tlog16rs.core.WorkMonth;
import com.kovacskornel.tlog16rs.core.WorkDay;
import com.kovacskornel.tlog16rs.core.Task;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;


@Path("/timelogger")
public class TLOG16RSResource {    
    
    private final TimeLogger tl = new TimeLogger();
    

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
        try{
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        tl.addMonth(workMonth);
        return workMonth;
        }
        catch(Exception a){
            System.out.println(a.getMessage());
            return new WorkMonth(month.getYear(), month.getMonth());
        }
    }
    
	
    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List MonthData(@PathParam(value = "year") int year, @PathParam(value="month") int month)
    {
        int m;
        WorkMonth MYWM = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month) MYWM = WM;
            }
        }
        if(MYWM == null)
        {
            MYWM = new WorkMonth(YearMonth.of(year, month));
            tl.addMonth(MYWM);
        }
        return MYWM.getDays();
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    @Produces(MediaType.APPLICATION_JSON)
    public void delAll()
    {
        tl.getMonths().clear();
    }

    
    @POST
    @Path("/workmonths/workdays")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDayRB addNewDay(WorkDayRB day) {
        int m;
        WorkMonth MYWM = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == day.getYear() && WM.getDate().getMonthValue() == day.getMonth()) MYWM = WM;
            }
        }
        if(MYWM == null)
        {
            MYWM = new WorkMonth(YearMonth.of(day.getYear(), day.getMonth()));
            tl.addMonth(MYWM);
        }
        WorkDay wd = new WorkDay(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()),day.getRequiredHours());
        MYWM.addWorkDay(wd);
        return day;
    }
 
    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List DayData(@PathParam(value = "year") int year, @PathParam(value="month") int month, @PathParam(value = "day") int day)
    {
        int m;
        WorkMonth MYWM = null;
        WorkDay MYWD = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month) MYWM = WM;
            }
        }
        if(MYWM == null)
        {
            MYWM = new WorkMonth(YearMonth.of(year, month));
            tl.addMonth(MYWM);
        }
        if(!MYWM.getDays().isEmpty())
        {
            int d;
            for(d=0;d<MYWM.getDays().size();d++)
            {
                WorkDay WD = MYWM.getDays().get(d);
                if(WD.getActualDay().getDayOfMonth() == day) MYWD = WD;
            }
        }
        if(MYWD == null)
        {
            MYWD = new WorkDay(LocalDate.of(year,month,day));
            MYWM.addWorkDay(MYWD);
        }
        return MYWD.getTasks();
    }
    
    
    @POST
    @Path("/save/test")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String test(String text)
    {

        TestEntity test = new TestEntity();
        test.setText(text);
        Ebean.save(test);
        return text;
    } 
    
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task startTask(StartTaskRB task) {
        int m;
        WorkMonth MYWM = null;
        WorkDay MYWD = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == task.getYear() && WM.getDate().getMonthValue() == task.getMonth()) MYWM = WM;
            }
        }
        if(MYWM == null)
        {
            MYWM = new WorkMonth(YearMonth.of(task.getYear(), task.getMonth()));
            tl.addMonth(MYWM);
        }
        if(!MYWM.getDays().isEmpty())
        {
            int d;
            for(d=0;d<MYWM.getDays().size();d++)
            {
                WorkDay WD = MYWM.getDays().get(d);
                if(WD.getActualDay().getDayOfMonth() == task.getDay()) MYWD = WD;
            }
        }
        if(MYWD == null)
        {
            MYWD = new WorkDay(LocalDate.of(task.getYear(),task.getMonth(),task.getDay()));
            MYWM.addWorkDay(MYWD);
        }
        Task MyTask = new Task(task.getTaskId(),task.getStartTime(),task.getComment());
        MYWD.addTask(MyTask);
        return MyTask;
    }
    
    @PUT
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task finishTask(FinishTaskRB task) {
        int m;
        WorkMonth MYWM = null;
        WorkDay MYWD = null;
        Task MyTask = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == task.getYear() && WM.getDate().getMonthValue() == task.getMonth()) MYWM = WM;
            }
        }
        if(MYWM == null)
        {
            MYWM = new WorkMonth(YearMonth.of(task.getYear(), task.getMonth()));
            tl.addMonth(MYWM);
        }
        if(!MYWM.getDays().isEmpty())
        {
            int d;
            for(d=0;d<MYWM.getDays().size();d++)
            {
                WorkDay WD = MYWM.getDays().get(d);
                if(WD.getActualDay().getDayOfMonth() == task.getDay()) MYWD = WD;
            }
        }
        if(MYWD == null)
        {
            MYWD = new WorkDay(LocalDate.of(task.getYear(),task.getMonth(),task.getDay()));
            MYWM.addWorkDay(MYWD);
        }
        if(!MYWD.getTasks().isEmpty())
        {
            int t;
            for(t=0;t<MYWD.getTasks().size();t++)
            {
                if(MYWD.getTasks().get(t).getTaskId().equals(task.getTaskId()) && MYWD.getTasks().get(t).getStartTime() == MYWD.getTasks().get(t).stringToLocalTime(task.getStartTime())) MyTask = MYWD.getTasks().get(t);
            }
        }
        if(MyTask == null)
        {
            MyTask = new Task(task.getTaskId(),task.getStartTime(),task.getComment());
            MYWD.addTask(MyTask);
        }
        MyTask.setEndTime(task.getEndTime());
        return MyTask;
    }
    
    @PUT
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task modifyTask(ModifyTaskRB task) {
        int m;
        WorkMonth MYWM = null;
        WorkDay MYWD = null;
        Task MyTask = null;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == task.getYear() && WM.getDate().getMonthValue() == task.getMonth()) MYWM = WM;
            }
        }
        if(MYWM == null)
        {
            MYWM = new WorkMonth(YearMonth.of(task.getYear(), task.getMonth()));
            tl.addMonth(MYWM);
        }
        if(!MYWM.getDays().isEmpty())
        {
            int d;
            for(d=0;d<MYWM.getDays().size();d++)
            {
                WorkDay WD = MYWM.getDays().get(d);
                if(WD.getActualDay().getDayOfMonth() == task.getDay()) MYWD = WD;
            }
        }
        if(MYWD == null)
        {
            MYWD = new WorkDay(LocalDate.of(task.getYear(),task.getMonth(),task.getDay()));
            MYWM.addWorkDay(MYWD);
        }
        if(!MYWD.getTasks().isEmpty())
        {
            int t;
            for(t=0;t<MYWD.getTasks().size();t++)
            {
                if(MYWD.getTasks().get(t).getTaskId().equals(task.getTaskId()) && MYWD.getTasks().get(t).getStartTime() == MYWD.getTasks().get(t).stringToLocalTime(task.getStartTime())) MyTask = MYWD.getTasks().get(t);
            }
        }
        if(MyTask == null){
            MyTask = new Task(task.getTaskId(),task.getStartTime(),task.getComment());
            MYWD.addTask(MyTask);
        }
        MyTask.setComment(task.getNewComment());
        MyTask.setEndTime(task.getNewEndTime());
        MyTask.setStartTime(task.getNewStartTime());
        MyTask.setTaskId(task.getNewTaskId());
        return MyTask;
        }
    
    @PUT
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Task deleteTask(DeleteTaskRB task) {
        Task delTask=null;
            int m;
            WorkMonth MYWM = null;
            WorkDay MYWD = null;
            if(!tl.getMonths().isEmpty())
            {
                for(m=0;m<tl.getMonths().size();m++)
                {   
                WorkMonth WM = tl.getMonths().get(m);
                if(WM.getDate().getYear() == task.getYear() && WM.getDate().getMonthValue() == task.getMonth()) MYWM = WM;
                }
            }
            else return delTask;
            if(!MYWM.getDays().isEmpty())
            {
                int d;
                for(d=0;d<MYWM.getDays().size();d++)
                {
                    WorkDay WD = MYWM.getDays().get(d);
                    if(WD.getActualDay().getDayOfMonth() == task.getDay()) MYWD = WD;
                }
            }
            else return delTask;
            int t;
            for(t=0;t<MYWD.getTasks().size();t++)
            {
                if(MYWD.getTasks().get(t).getTaskId().equals(task.getTaskId()) && MYWD.getTasks().get(t).getStartTime() == MYWD.getTasks().get(t).stringToLocalTime(task.getStartTime())) delTask = MYWD.getTasks().get(t);
            }
            MYWD.getTasks().remove(delTask);
            return delTask;
    }
    
}