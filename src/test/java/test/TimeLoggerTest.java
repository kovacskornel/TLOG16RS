/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.kovacskornel.tlog16rs.resources.TimeLogger;
import com.kovacskornel.tlog16rs.resources.Task;
import com.kovacskornel.tlog16rs.resources.WorkDay;
import com.kovacskornel.tlog16rs.resources.WorkMonth;
import com.kovacskornel.tlog16rs.core.NotNewMonthException;
/**
 * @author Kovács Kornél
 * @version 0.1.0
 * @since 2016-11-03
 */
public class TimeLoggerTest {
    
    @Test
    public void SameSumTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 4, 14));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 4));
        Task t = new Task("7:30","10:30");
        wd.addTask(t);
        wm.addWorkDay(wd);
        TimeLogger tl = new TimeLogger("a");
        tl.addMonth(wm);
        assertEquals(t.getMinPerTask(),tl.getMonths().get(0).getSumPerMonth());
    }
    
    @Test(expected = NotNewMonthException.class)
    public void NotNewMonthTest()
    {
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 4));
        WorkMonth wm2 = new WorkMonth(YearMonth.of(2016, 4));
        TimeLogger tl = new TimeLogger("a");
        tl.addMonth(wm);
        tl.addMonth(wm2);
    }
    
    @Test
    public void IsNewMonthTest()
    {
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 4));
        WorkMonth wm2 = new WorkMonth(YearMonth.of(2016, 9));
        TimeLogger tl = new TimeLogger("a");
        tl.addMonth(wm);
        assertEquals(tl.isNewMonth(wm2),true);
    }
    
    @Test
    public void IsNewMonthTest2()
    {
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 4));
        WorkMonth wm2 = new WorkMonth(YearMonth.of(2016, 4));
        TimeLogger tl = new TimeLogger("a");
        tl.addMonth(wm);
        assertEquals(tl.isNewMonth(wm2),false);
    }
    
}
