/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import java.time.LocalDate;
import java.time.YearMonth;
import com.kovacskornel.tlog16rs.core.WorkMonth;
import com.kovacskornel.tlog16rs.core.WorkDay;
import com.kovacskornel.tlog16rs.core.Task;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.kovacskornel.tlog16rs.core.NotNewDateException;
import com.kovacskornel.tlog16rs.core.WeekendNotEnabledException;
import com.kovacskornel.tlog16rs.core.NotTheSameMonthException;
/**
 *
 * @author precognox
 */
public class WorkMonthTest {
    
    // Test 1
    @Test
    public void getSumTest1()
    {
        Task t = new Task("7:30","8:45");
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 5),420);
        wd.addTask(t);
        Task t2 = new Task("8:45","9:45");
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 1),420);
        wd2.addTask(t2);
        WorkMonth wm = new WorkMonth(YearMonth.of(2016,9));
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        assertEquals(wm.getSumPerMonth(),135);      
    }
    
    // Test 2
    @Test
    public void getSumTest2()
    {
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        assertEquals(wm.getSumPerMonth(),0);
    }
    
    // Test 3
    @Test
    public void getExtraTest()
    {
        Task t = new Task("7:30","8:45");
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 5),420);
        wd.addTask(t);
        Task t2 = new Task("8:45","9:45");
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 1),420);
        wd2.addTask(t2);
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        assertEquals(wm.getExtraMinPerMonth(),-705);         
    }
    
    // Test 4
    @Test
    public void getExtraTest2()
    {
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        assertEquals(wm.getExtraMinPerMonth(),0);
    }
    
    // Test 5
    @Test
    public void getReqTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 5),420);
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 1),420);
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        assertEquals(wm.getRequiredMinPerMonth(),840);  
    }
    
    // Test 6
    @Test
    public void getReqTest2()
    {
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        assertEquals(wm.getRequiredMinPerMonth(),0);
    }
    
    // Test 7
    @Test
    public void sumTest()
    {
        Task t = new Task("7:30","8:45");
        WorkDay wd = new WorkDay(LocalDate.of(2016,9,9));
        wd.addTask(t);
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        assertEquals(wd.getSumPerDay(),wm.getSumPerMonth());
    }
    
    // Test 8
    @Test
    public void weekendTest()
    {
        Task t = new Task("7:30","8:45");
        WorkDay wd = new WorkDay(LocalDate.of(2016,8,28));
        wd.addTask(t);
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 8));
        wm.setIsWeekendEnabled(true);
        wm.addWorkDay(wd);
        assertEquals(wd.getSumPerDay(),wm.getSumPerMonth() );
    }
    
    // Test 9
    @Test(expected = WeekendNotEnabledException.class)
    public void weekendTest2()
    {
        Task t = new Task("7:30","8:45");
        WorkDay wd = new WorkDay(LocalDate.of(2016,8,28));
        wd.addTask(t);
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 8));
        wm.addWorkDay(wd);
    }
    
    // Test 10
    @Test
    public void isSameTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 2));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        assertEquals(wm.isSameMonth(wd2), true);
    }
    
    // Test 11
    @Test
    public void isSameMonth()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 8, 30));
         WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        assertEquals(wm.isSameMonth(wd2), false);
    }
    
    // Test 12
    @Test
    public void isSameMonth2()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        assertEquals(wm.isSameMonth(wd), true);
    }
    
    // Test 13
    @Test
    public void isSameMonth3()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 8));
        assertEquals(wm.isSameMonth(wd), false);
    }
    
    // Test 14
    @Test
    public void isNewDateTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        assertEquals(wm.IsNewDate(wd2),false);
    }
    
    // Test 15
    @Test
    public void isNewDateTest2()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 2));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        assertEquals(wm.IsNewDate(wd2),true);        
    }
    
    // Test 16
    @Test
    public void isNewDateTest3()
    {
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 2));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        assertEquals(wm.IsNewDate(wd2),true);       
    }
    
    // Test 17
    @Test(expected = NotNewDateException.class)
    public void inNewExcTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
    }
    
    // Test 18
    @Test
    public void isReqSumSameTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 9, 2));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
        long sumofreq = wd.getRequiredMinPerDay() + wd2.getRequiredMinPerDay();
        assertEquals(sumofreq,wm.getRequiredMinPerMonth());
    }
    
    // Test 19
    @Test(expected = NotTheSameMonthException.class)
    public void NotSameMonthTest()
    {
        WorkDay wd = new WorkDay(LocalDate.of(2016, 9, 1));
        WorkDay wd2 = new WorkDay(LocalDate.of(2016, 8, 30));
        WorkMonth wm = new WorkMonth(YearMonth.of(2016, 9));
        wm.addWorkDay(wd);
        wm.addWorkDay(wd2);
    }
}