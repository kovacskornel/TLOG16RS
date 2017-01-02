package test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.kovacskornel.tlog16rs.core.EmptyTimeFieldException;
import com.kovacskornel.tlog16rs.core.InvalidTaskIDException;
import com.kovacskornel.tlog16rs.core.NoTaskIDException;
import com.kovacskornel.tlog16rs.core.NotExpectedTimeOrderException;
import com.kovacskornel.tlog16rs.core.NotMultipleQuarterHourException;
import java.time.LocalTime;
import com.kovacskornel.tlog16rs.resources.Task;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author precognox
 */
public class TaskTest {
    
    private Task WrongTimeOrder()
    {
     return new Task("08:45", "07:30");
    }
    
    private Task NoEndTime()
    {
        return new Task("08:00", null);
    }
    
    private Task getMinTaskGood()
    {
        return new Task("07:30","08:45");
    }
    
    private Task notQuarterHour()
    {
        return new Task("07:35","08:45");
    }
    
    private Task validRedmineTask()
    {
        return new Task("1548");
    }
    
    
    private Task InvalidRedmineTask()
    {
        return new Task("154858");
    }
    
    private Task noTaskID()
    {
        return new Task(null);
    }
    
    private Task validLTTaskID()
    {
        return new Task("LT-1548");
    }
    
    private Task InvalidLTTaskID()
    {
        return new Task("LT-154858");
    }
    
    private Task NoComment()
    {
        return new Task("LT-1548","07:30","08:45","");
    }
    
    // Test 1
    @Test(expected = NotExpectedTimeOrderException.class)
    public void isWrongTimeOrderTest()
    {
         WrongTimeOrder().getMinPerTask();
    }
    // Test 2
    @Test(expected = EmptyTimeFieldException.class)
    public void isEmptyTimeFieldTest()
    {
        NoEndTime().getEnd_time();
    }
    // Test 3
    @Test
    public void isGetMinPerTaskOK()
    {
        assertEquals(getMinTaskGood().getMinPerTask(),75);
    }
    // Test 4
    @Test
    public void isValidRedmineTask()
    {
        assertEquals(validRedmineTask().isValidRedmineTaskId(validRedmineTask().getTask_id()),true);
    }
    // Test 5
    @Test(expected = InvalidTaskIDException.class)
    public void isInValidRedmineTask()
    {
        InvalidRedmineTask().isValidRedmineTaskId(validRedmineTask().getTask_id());
    }
    // Test 6
    @Test(expected = NoTaskIDException.class)
    public void NoRedmineTaskIDtest()
    {
        noTaskID().isValidRedmineTaskId(noTaskID().getTask_id());
    }
    // Test 7
    @Test
    public void testIsValidTaskID()
    {
        assertEquals(validLTTaskID().isValidLTTaskId(validLTTaskID().getTask_id()),true);
    }
    
    // Test 8
    @Test(expected = InvalidTaskIDException.class)
    public void testIsInvalidLTTaskID()
    {
        InvalidLTTaskID().isValidLTTaskId(InvalidLTTaskID().getTask_id());
    }
    
    // Test 9
    @Test(expected = NoTaskIDException.class)
    public void NoLTTaskIDtest()
    {
        noTaskID().isValidLTTaskId(noTaskID().getTask_id());
    }
    
    // Test 10
    @Test
    public void testIsValidTestID()
    {
        validLTTaskID().isValidTaskID(validLTTaskID().getTask_id());
    }
    
    // Test 11
    @Test(expected = InvalidTaskIDException.class)
    public void testIsInvalidTaskID()
    {
        InvalidRedmineTask().isValidTaskID(InvalidRedmineTask().getTask_id());
    }
    
    // Test 12
    @Test(expected = NoTaskIDException.class)
    public void NoTaskIDValidTask()
    {
        noTaskID().isValidTaskID(noTaskID().getTask_id());
    }
    
    // Test 13
    @Test
    public void testValidMultipleQuarter()
    {
        assertEquals(getMinTaskGood().isMultipleQuarterHour(getMinTaskGood().getStart_time().getMinute()) && getMinTaskGood().isMultipleQuarterHour(getMinTaskGood().getEnd_time().getMinute()) ,true);          
    }
    
    // Test 14
    @Test
/*    public void testInvalidMultipleQuarter()
    {
        assertEquals(notQuarterHour().isMultipleQuarterHour(notQuarterHour().getStart_time().getMinute()) && notQuarterHour().isMultipleQuarterHour(notQuarterHour().getEnd_time().getMinute()) ,false);       
    }    
*/  (expected = NotMultipleQuarterHourException.class) 
    public void testInvalidMultipleQuarter()
    {
        notQuarterHour().isMultipleQuarterHour(notQuarterHour().getStart_time().getMinute());
        notQuarterHour().isMultipleQuarterHour(notQuarterHour().getEnd_time().getMinute());
    }
    
    // Test 15
    @Test(expected = NoTaskIDException.class)
    public void NoTaskIDgetTask_id()
    {
        noTaskID().getTask_id();
    }
    
    // Test 16   
    @Test
    public void NoCommentTest()
    {
        assertEquals("",NoComment().getComment());
    }
    
    // Test 17
    @Test(expected = InvalidTaskIDException.class)
    public void InvalidTaskIDEx()
    {
        InvalidLTTaskID();
    }
    
    // Test 18
    @Test(expected = NoTaskIDException.class)
    public void NoTaskIDEx()
    {
        noTaskID();
    }
    
    // Test 19
    @Test
    public void StrToLclTm()
    {
        Task t = NoComment();
        assertEquals(t.stringToLocalTime("09:00"),LocalTime.of(9, 0));
    }
    
    // Test +1
    @Test(expected = NotMultipleQuarterHourException.class)
    public void Multiple()
    {
        getMinTaskGood().setEndTime(LocalTime.of(0, 35));
    }
}