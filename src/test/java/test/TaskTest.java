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
        NoEndTime().getEndTime();
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
        assertEquals(validRedmineTask().isValidRedmineTaskId(validRedmineTask().getTaskId()),true);
    }
    // Test 5
    @Test(expected = InvalidTaskIDException.class)
    public void isInValidRedmineTask()
    {
        InvalidRedmineTask().isValidRedmineTaskId(validRedmineTask().getTaskId());
    }
    // Test 6
    @Test(expected = NoTaskIDException.class)
    public void NoRedmineTaskIDtest()
    {
        noTaskID().isValidRedmineTaskId(noTaskID().getTaskId());
    }
    // Test 7
    @Test
    public void testIsValidTaskID()
    {
        assertEquals(validLTTaskID().isValidLTTaskId(validLTTaskID().getTaskId()),true);
    }
    
    // Test 8
    @Test(expected = InvalidTaskIDException.class)
    public void testIsInvalidLTTaskID()
    {
        InvalidLTTaskID().isValidLTTaskId(InvalidLTTaskID().getTaskId());
    }
    
    // Test 9
    @Test(expected = NoTaskIDException.class)
    public void NoLTTaskIDtest()
    {
        noTaskID().isValidLTTaskId(noTaskID().getTaskId());
    }
    
    // Test 10
    @Test
    public void testIsValidTestID()
    {
        validLTTaskID().isValidTaskID(validLTTaskID().getTaskId());
    }
    
    // Test 11
    @Test(expected = InvalidTaskIDException.class)
    public void testIsInvalidTaskID()
    {
        InvalidRedmineTask().isValidTaskID(InvalidRedmineTask().getTaskId());
    }
    
    // Test 12
    @Test(expected = NoTaskIDException.class)
    public void NoTaskIDValidTask()
    {
        noTaskID().isValidTaskID(noTaskID().getTaskId());
    }
    
    // Test 13
    @Test
    public void testValidMultipleQuarter()
    {
        assertEquals(getMinTaskGood().isMultipleQuarterHour(getMinTaskGood().getStartTime().getMinute()) && getMinTaskGood().isMultipleQuarterHour(getMinTaskGood().getEndTime().getMinute()) ,true);          
    }
    
    // Test 14
    @Test
/*    public void testInvalidMultipleQuarter()
    {
        assertEquals(notQuarterHour().isMultipleQuarterHour(notQuarterHour().getStartTime().getMinute()) && notQuarterHour().isMultipleQuarterHour(notQuarterHour().getEndTime().getMinute()) ,false);       
    }    
*/  (expected = NotMultipleQuarterHourException.class) 
    public void testInvalidMultipleQuarter()
    {
        notQuarterHour().isMultipleQuarterHour(notQuarterHour().getStartTime().getMinute());
        notQuarterHour().isMultipleQuarterHour(notQuarterHour().getEndTime().getMinute());
    }
    
    // Test 15
    @Test(expected = NoTaskIDException.class)
    public void NoTaskIDgetTaskId()
    {
        noTaskID().getTaskId();
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