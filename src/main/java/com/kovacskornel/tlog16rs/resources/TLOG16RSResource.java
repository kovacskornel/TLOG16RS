package com.kovacskornel.tlog16rs.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.kovacskornel.tlog16rs.core.TimeLogger;
import com.kovacskornel.tlog16rs.core.WorkMonth;
import com.kovacskornel.tlog16rs.core.WorkDay;
import com.kovacskornel.tlog16rs.core.Task;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import javax.ws.rs.Consumes;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;


@Path("/timelogger")
public class TLOG16RSResource {    
    
    private TimeLogger tl = new TimeLogger();
    
    @Path ("/workmonths")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatistics()
    {
    String text = "";
        int m,i,j;
        if(tl.getMonths().isEmpty()) return "No months available";
        else {
        for(m=0;m<tl.getMonths().size();m++)
        {
            
            WorkMonth WM = tl.getMonths().get(m);
            text += ("\t\t\t\t\t" + WM.getExtraMinPerMonth()+"\n");
            for(i=0;i<WM.getDays().size();i++)
            {
                WorkDay WD = WM.getDays().get(i);
                text+=(WD.getActualDay() + "\t" + WD.getSumPerDay() + "\t" + WD.getTasks().get(0).getStartTime() + "\t" + WD.getExtraMinPerDay()+"\n");
            for (j=0;j<WD.getTasks().size();j++)
            {
                Task t = WD.getTasks().get(j);
                text+=(t.getMinPerTask() + "\t" + t.getTaskId() + "\t" + t.getComment() + "\t" + t.getEndTime()+"\n");
            }
           text+="\n";
        }

        text+="\n";
        }
        return text;
        }    
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
    @Produces(MediaType.TEXT_PLAIN)
    public String MonthData(@PathParam(value = "year") int year, @PathParam(value="month") int month)
    {
        String text = "";
        int m,i,j;
        if(tl.getMonths().isEmpty()) return "No months available";
        else {
        for(m=0;m<tl.getMonths().size();m++)
        {   
            
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month){
            if(!WM.getDays().isEmpty())
            {
                text += ("\t\t\t\t\t" + WM.getExtraMinPerMonth()+"\n");
                for(i=0;i<WM.getDays().size();i++)
                {
                    WorkDay WD = WM.getDays().get(i);
                    text+=(WD.getActualDay() + "\t" + WD.getSumPerDay() + "\t" + WD.getTasks().get(0).getStartTime() + "\t" + WD.getExtraMinPerDay()+"\n");
                    if(!WD.getTasks().isEmpty())
                    {
                    for (j=0;j<WD.getTasks().size();j++)
                    {
                        Task t = WD.getTasks().get(j);
                        text+=(t.getMinPerTask() + "\t" + t.getTaskId() + "\t" + t.getComment() + "\t" + t.getEndTime()+"\n");
                    }
                    }else text+= "No tasks this day\n";
                text+="\n";
                }
            }else text+= "No days this month\n";
        }

        text+="\n";
        }
        return text;
        }    
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
    
/*    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello World!!";
    }

    @Path("/{name}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNameGreeting(@PathParam(value = "name") String name) {
        return "Hello " + name;
    }

    @Path("/query_param")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNamedStringWithParam(@DefaultValue("world") @QueryParam("name") String name) {
        return "Hello " + name;
    }

    @Path("/hello_json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonGreeting getJSONGreeting() {
        return new JsonGreeting("Hello world!");
    }
*/
    
    
}