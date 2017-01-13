package com.kovacskornel.tlog16rs.resources;

import com.avaje.ebean.Ebean;
import com.kovacskornel.tlog16rs.core.InvalidTaskIDException;
import com.kovacskornel.tlog16rs.core.NoTaskIDException;
import com.kovacskornel.tlog16rs.core.NotMultipleQuarterHourException;
import java.lang.annotation.Annotation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

@Path("/timelogger")
public class TLOG16RSResource {

    static String myname = "test";
    private TimeLogger tl = gettl();

    private TimeLogger gettl() {
        TimeLogger tilo = null;
        boolean ok = false;
        for (TimeLogger tlog : Ebean.find(TimeLogger.class).findList()) {
            if (tlog.getName().equals(myname)) {
                tilo = tlog;
                ok = true;
            }
        }
        if (!ok) {
            tilo = new TimeLogger(myname);
        }
        return tilo;
    }

    private WorkMonth createMonthIfNotExists(int year, int month) {
        int m;
        WorkMonth wm = null;
        if (!tl.getMonths().isEmpty()) {
            for (m = 0; m < tl.getMonths().size(); m++) {
                WorkMonth workMonth = tl.getMonths().get(m);
                if (workMonth.getDate().getYear() == year && workMonth.getDate().getMonthValue() == month) {
                    wm = workMonth;
                }
            }
        }
        if (wm == null) {
            wm = new WorkMonth(year, month);
            tl.addMonth(wm);
        }

        Ebean.save(tl);
        return wm;
    }

    private WorkDay createDayIfNotExists(int year, int month, int day, long req) {
        WorkMonth mywm = createMonthIfNotExists(year, month);
        WorkDay mywd = null;
        if (!mywm.getDays().isEmpty()) {
            int d;
            for (d = 0; d < mywm.getDays().size(); d++) {
                WorkDay wd = mywm.getDays().get(d);
                if (wd.getActualDay().getDayOfMonth() == day) {
                    mywd = wd;
                }
            }
        }
        if (mywd == null) {
            mywd = new WorkDay(LocalDate.of(year, month, day),req);
            mywm.addWorkDay(mywd);
            Ebean.save(tl);
        }
        return mywd;
    }

    private Task createTaskIfNotExists(WorkDay mywd, String taskid, String stime, String comment) {
        Task myTask = null;
        if (!mywd.getTasks().isEmpty()) {
            int t;
            for (t = 0; t < mywd.getTasks().size(); t++) {
                if (mywd.getTasks().get(t).getTaskId().equals(taskid) && mywd.getTasks().get(t).getStartTime() == mywd.getTasks().get(t).stringToLocalTime(stime)) {
                    myTask = mywd.getTasks().get(t);
                }
            }
        }
        if (myTask == null) {
            myTask = new Task(taskid, stime, comment);
            mywd.addTask(myTask);
            Ebean.save(tl);
        }
        return myTask;
    }

    @Path("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics() {
        return Response.ok(tl.getMonths()).build();
    }

    /**
     * Adds a workmonth
     *
     * @param month int year, int month
     * @return workMonth
     */
    @POST
    @Path("/workmonths")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewMonth(WorkMonthRB month) {
        try {
            WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
            tl.addMonth(workMonth);
            Ebean.save(tl);
            return Response.ok(workMonth).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @Path("/workmonths/{year}/{month}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response monthData(@PathParam(value = "year") int year, @PathParam(value = "month") int month) {
        WorkMonth mywm;
        mywm = createMonthIfNotExists(year, month);
        return Response.ok(mywm.getDays()).build();
    }

    @PUT
    @Path("/workmonths/deleteall")
    @Produces(MediaType.APPLICATION_JSON)
    public void delAll() {
        int i = 1;
        while (!Ebean.find(TimeLogger.class).findList().isEmpty()) {
            Ebean.delete(TimeLogger.class, i);
            i++;
        }
        tl = new TimeLogger(myname);
        Ebean.save(tl);
    }

    @POST
    @Path("/workmonths/workdays")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewDay(WorkDayRB day) {
        try {
            createDayIfNotExists(day.getYear(), day.getMonth(), day.getDay(),day.getRequiredHours());
            return Response.status(200).build();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/workmonths/workdays/weekend")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewWeekendDay(WorkDayRB day) {
        try {
            WorkMonth mywm;
            mywm = createMonthIfNotExists(day.getYear(), day.getMonth());
            WorkDay wd = new WorkDay(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()), day.getRequiredHours());
            mywm.addWorkDay(wd, true);
            Ebean.save(tl);
            return Response.ok(day).build();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @Path("/workmonths/{year}/{month}/{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response dayData(@PathParam(value = "year") int year, @PathParam(value = "month") int month, @PathParam(value = "day") int day) {
        WorkDay mywd = createDayIfNotExists(year, month, day,450);
        return Response.ok(mywd.getTasks()).build();
    }

    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startTask(StartTaskRB task) {
        try {
            WorkDay mywd = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay(),450);
            Task myTask = new Task(task.getTaskId(), task.getStartTime(), task.getComment());
            mywd.addTask(myTask);
            Ebean.save(tl);
            return Response.ok(myTask).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }

    }

    @PUT
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response finishTask(FinishTaskRB task) {
        try {
            WorkDay mywd = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay(),450);
            Task myTask = createTaskIfNotExists(mywd, task.getTaskId(), task.getStartTime(), task.getComment());
            myTask.setEndTime(task.getEndTime());
            Ebean.save(tl);
            return Response.ok(myTask).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

    @PUT
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTask(ModifyTaskRB task) {
        try {
            WorkDay mywd = createDayIfNotExists(task.getYear(), task.getMonth(), task.getDay(),450);
            Task myTask = createTaskIfNotExists(mywd, task.getTaskId(), task.getStartTime(), task.getNewComment());
            myTask.setEndTime(task.getNewEndTime());
            myTask.setStartTime(task.getNewStartTime());
            myTask.setTaskId(task.getNewTaskId());
            Ebean.save(tl);
            return Response.ok(myTask).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }

    }

    private WorkMonth isMonthExists(int year, int month) {
        int m;
        if (!tl.getMonths().isEmpty()) {
            for (m = 0; m < tl.getMonths().size(); m++) {
                WorkMonth wm = tl.getMonths().get(m);
                if (wm.getDate().getYear() == year && wm.getDate().getMonthValue() == month) {
                    return wm;
                }
            }
        }
        return null;
    }

    private WorkDay isDayExists(WorkMonth mywm, int day) {
        if (!mywm.getDays().isEmpty()) {
            int d;
            for (d = 0; d < mywm.getDays().size(); d++) {
                WorkDay wd = mywm.getDays().get(d);
                if (wd.getActualDay().getDayOfMonth() == day) {
                    return wd;
                }
            }
        }
        return null;
    }

    @PUT
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(DeleteTaskRB task) {
        try {
            Task delTask = null;
            WorkMonth mywm = isMonthExists(task.getYear(), task.getMonth());
            if (mywm == null) {
                Response.ok(delTask).build();
            }
            WorkDay mywd = isDayExists(mywm, task.getDay());
            if (mywd == null) {
                Response.ok(delTask).build();
            }
            if (mywd.getTasks().isEmpty()) {
                int t;
                for (t = 0; t < mywd.getTasks().size(); t++) {

                    if (mywd.getTasks().get(t).getTaskId().equals(task.getTaskId()) && mywd.getTasks().get(t).getStartTime() == mywd.getTasks().get(t).stringToLocalTime(task.getStartTime())) {
                        delTask = mywd.getTasks().get(t);
                    }
                }
            }
            mywd.getTasks().remove(delTask);
            Ebean.delete(delTask);
            return Response.ok(delTask).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.status(400).build();
        }
    }

}
