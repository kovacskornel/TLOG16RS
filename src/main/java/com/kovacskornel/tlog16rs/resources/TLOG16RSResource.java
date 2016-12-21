package com.kovacskornel.tlog16rs.resources;

import com.kovacskornel.tlog16rs.core.JsonDay;
import com.kovacskornel.tlog16rs.core.JsonMonth;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.kovacskornel.tlog16rs.core.StatisticsJSON;
import com.kovacskornel.tlog16rs.core.TimeLogger;
import com.kovacskornel.tlog16rs.core.WorkMonth;
import com.kovacskornel.tlog16rs.core.WorkDay;
import com.kovacskornel.tlog16rs.core.Task;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;


@Path("/timelogger")
public class TLOG16RSResource {    
    
    private final TimeLogger tl = new TimeLogger();
    
    @Path ("/workmonths")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatistics()
    {
        return new StatisticsJSON(tl).getStat();
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
    public String MonthData(@PathParam(value = "year") int year, @PathParam(value="month") int month)
    {
        int m;
        boolean exists = false;
        if(!tl.getMonths().isEmpty())
        {
            for(m=0;m<tl.getMonths().size();m++)
            {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month) exists = true;
            }
        }
        if(exists == false)
        {
            WorkMonth MYWM = new WorkMonth(YearMonth.of(year, month));
            tl.addMonth(MYWM);
        }
        return new JsonMonth(year,month,tl).getText();
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
    public String DayData(@PathParam(value = "year") int year, @PathParam(value="month") int month, @PathParam(value = "day") int day)
    {
        int m;
        boolean exists = false, dexists = false;
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
        return new JsonDay(year,month,day,tl).getText();
    }
    
    
}