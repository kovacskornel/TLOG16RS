/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import com.kovacskornel.tlog16rs.resources.WorkDay;
import com.kovacskornel.tlog16rs.resources.Task;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.kovacskornel.tlog16rs.core.NegativeMinutesOfWorkException;
import com.kovacskornel.tlog16rs.core.FutureWorkException;
import com.kovacskornel.tlog16rs.core.NotMultipleQuarterHourException;
import com.kovacskornel.tlog16rs.core.NotSeparatedTimesException;

/**
 *
 * @author precognox
 */
public class WorkDayTest {
    
    private Task TaskWith75Mins()
    {
        return new Task("07:30","08:45");
    }
    
    private Task NextTask()
    {
        return new Task("08:45","09:45");
    }

    private Task notQuarterHour()
    {
        return new Task("07:35","08:45");
    }
    
    private Task MyTask()
    {
        return new Task("08:00","09:00");
    }
    
    private Task LastTask()
    {
        return new Task("09:30","11:45");
    }
    
    private Task SepTask1()
    {
        return new Task("07:30","08:45");
    }
    
    private Task SepTask2()
    {
        return new Task("08:45","9:45");
    }
    
    private Task SepTask3()
    {
        return new Task("08:30","9:45");
    }
    
    private WorkDay MyDay()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(MyTask());
        return a;
    }
    
    private WorkDay MyDayTwo()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(notQuarterHour());
        return a;       
    }
    
    private WorkDay twoTasks()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        return a;
    }
    
    private WorkDay req75()
    {
        WorkDay a = new WorkDay(LocalDate.now(),75);
        a.addTask(TaskWith75Mins());
        return a;
    }
    
    private WorkDay reqDef()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(TaskWith75Mins());
        return a;
    }
    
    private WorkDay req60()
    {
        WorkDay a = new WorkDay(LocalDate.now(),60);
        a.addTask(TaskWith75Mins());
        return a;
    }
    
    private WorkDay emptyWorkDay()
    {
        return new WorkDay(LocalDate.now());
    }
    
    private WorkDay NegativeWorkDay()
    {
        return new WorkDay(LocalDate.now(), -5);
    }
    
    private WorkDay Tomorrow()
    {
        return new WorkDay(LocalDate.now().plusDays(1));
    }
    
    private WorkDay Weekend()
    {
        return new WorkDay(LocalDate.of(2016, 12, 11));
    }
    
    private WorkDay notWeekend()
    {
        return new WorkDay(LocalDate.of(2016, 12, 12));
    }
    
    // Test 1    
    @Test
    public void getExtraTest()
    {
         assertEquals(req75().getExtraMinPerDay(),0);
    }
    
    // Test 2
    @Test
    public void getExtraTest2()
    {
        assertEquals(reqDef().getExtraMinPerDay(),-375);
    }
    
    // Test 3
    @Test
    public void getExtraTest3()
    {
        assertEquals(req60().getExtraMinPerDay(),15);
    }
    
    // Test 4
    @Test
    public void getExtraTest4()
    {
        WorkDay x = emptyWorkDay();
        assertEquals(x.getExtraMinPerDay(),-x.getRequiredMinPerDay());
    }
    
    // Test 5
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void NegativeTest()
    {
        NegativeWorkDay().getRequiredMinPerDay();
    }
    
    // Test 6
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void NegativeTest2()
    {
        NegativeWorkDay();
    }    
    
    // Test 7
    @Test(expected = FutureWorkException.class)
    public void FutureWork()
    {
        Tomorrow().setActualDay(LocalDate.of(5000, Month.MARCH, 3));
    }  
    
    // Test 8
    @Test(expected = FutureWorkException.class)
    public void FutureWork2()
    {
        Tomorrow();
    }
    
    // Test 9
    @Test
    public void sumOfTwo()
    {
        WorkDay a = emptyWorkDay();
        a.addTask(TaskWith75Mins());
        a.addTask(NextTask());
        assertEquals(a.getSumPerDay(),135);
    }
    
    // Test 10
    @Test
    public void sumOfZero()
    {
        assertEquals(emptyWorkDay().getSumPerDay(),0);
    }
    
    // Test 11
    @Test
    public void equalTest()
    {
        assertEquals(MyTask().getMinPerTask(),MyDay().getSumPerDay());
    }
    
    // Test 12
    @Test(expected = NotMultipleQuarterHourException.class)
    public void NotQuarterTest()
    {
        MyDayTwo();
    }
    
    // Test 13
    @Test
    public void NotWeekendTest()
    {
        assertEquals(notWeekend().isWeekDay(notWeekend().getActualDay()),true);
    }
    
    // Test 14
    @Test
    public void WeekendTest()
    {
        assertEquals(Weekend().isWeekDay(Weekend().getActualDay()),false);
    }
    
    // Test 15
    @Test
    public void LastTaskTest()
    {
        WorkDay a = emptyWorkDay();
        a.addTask(TaskWith75Mins());
        a.addTask(LastTask());
        assertEquals(a.endTimeOfTheLastTask(),LocalTime.of(11,45));
    }
    
    // Test 16
    @Test
    public void LastTaskTest2()
    {
        assertEquals(emptyWorkDay().endTimeOfTheLastTask(),null);
    }
    
    // Test 17
    @Test
    public void SepTest1()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(SepTask1());
        assertEquals(a.isSeparatedTime(SepTask2()),true);
    }
   
    // Test 18
    @Test
    public void SepTest2()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(SepTask1());
        assertEquals(a.isSeparatedTime(SepTask3()),false);
    }

    // Test 19
    @Test
    public void SepTest3()
    {   
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(SepTask3());
        assertEquals(a.isSeparatedTime(SepTask1()),false);
    }

    // Test 20
    @Test
    public void SepTest4()
    {
        WorkDay a = new WorkDay(LocalDate.now());
        a.addTask(SepTask1());
        assertEquals(a.isSeparatedTime(SepTask1()),false);
    }
    
    // Test 21
    @Test(expected = NotSeparatedTimesException.class)
    public void SepTest5()
    {
        WorkDay a = emptyWorkDay();
        a.addTask(SepTask1());
        a.addTask(SepTask3());
    }

}