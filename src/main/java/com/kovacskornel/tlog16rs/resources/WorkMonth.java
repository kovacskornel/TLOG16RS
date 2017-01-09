package com.kovacskornel.tlog16rs.resources;

import com.kovacskornel.tlog16rs.core.NotNewDateException;
import com.kovacskornel.tlog16rs.core.NotTheSameMonthException;
import com.kovacskornel.tlog16rs.core.WeekendNotEnabledException;
import static java.lang.Integer.parseInt;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * <h1>WorkMonth</h1>
 * This class contains methods and variables to work with WorkDays
 *
 * @author Kovács Kornél
 * @version 0.1.0
 * @since 2016-11-03
 */
@Entity
@lombok.Getter
public class WorkMonth {

    @Id
    @GeneratedValue
    int id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<WorkDay> days = new ArrayList<>();
    private transient YearMonth date;
    private String sdate;
    private long sumPerMonth;
    private long requiredMinPerMonth;
    private long extraMinPerMonth;
    private boolean isWeekendEnabled = false;

    public WorkMonth(YearMonth date) {
        this.date = date;
        sdate = date.toString();
    }

    public WorkMonth(int year, int month) {
        date = YearMonth.of(year, month);
        sdate = date.toString();
    }

    /**
     *
     * @param x A workday to check
     * @return boolean<br>true if the month not contains this workday
     * <br>false if the month already contains this workday
     */
    public boolean isNewDate(WorkDay x) {
        int i;
        boolean a = false;
        if (days.isEmpty()) {
            return true;
        }
        for (i = 0; i < days.size(); i++) {
            int day1 = days.get(i).getActualDay().getDayOfMonth();
            int day2 = x.getActualDay().getDayOfMonth();
            if (day1 != day2) {
                a = true;
            }
        }
        return a;
    }

    /**
     *
     * @param isWeekendEnabled boolean - Sets the weekend work
     * <br> true - Weekend work enabled
     * <br> false - Weekend work disabled
     */
    public void setIsWeekendEnabled(boolean isWeekendEnabled) {
        this.isWeekendEnabled = isWeekendEnabled;
    }

    /**
     * Sets the minutes of working hours this month
     *
     * @param sumPerMonth minutes of working hours this month
     */
    public void setSumPerMonth(long sumPerMonth) {
        this.sumPerMonth = sumPerMonth;
    }

    /**
     * Sets the required minutes to work this month
     *
     * @param requiredMinPerMonth required minutes to work this month
     */
    public void setRequiredMinPerMonth(long requiredMinPerMonth) {
        this.requiredMinPerMonth = requiredMinPerMonth;
    }

    /**
     * Creates a {@link WorkDay}
     *
     * @param wd {@link WorkDay}
     * @param isWeekendEnabled boolean true if you want to add a weekend day
     * <br>false (default) You can only add weekdays
     * @exception NotTheSameMonthException if the {@link WorkDay} not fits to
     * the month
     * @exception NotNewDateException if the {@link WorkDay} already exists
     */
    public void addWorkDay(WorkDay wd, boolean isWeekendEnabled) {
        if (isNewDate(wd)) {
            if (isSameMonth(wd)) {
                if (wd.isWeekDay(wd.getActualDay()) || isWeekendEnabled) {
                    days.add(wd);
                } else {
                    throw new WeekendNotEnabledException();
                }
            } else {
                throw new NotTheSameMonthException();
            }
        } else {
            throw new NotNewDateException();
        }
    }

    /**
     * The same as
     * {@link addWorkDay(WorkDay wd, boolean isWeekendEnabled) addWorkDay} but
     * isWeekendEnabled has a default false value
     *
     * @param wd {@link WorkDay}
     */
    public void addWorkDay(WorkDay wd) {
        addWorkDay(wd, isWeekendEnabled);
    }

    /**
     *
     * @return Gets the extra minutes worked this month
     */
    public long getExtraMinPerMonth() {
        int i;
        long h = 0;
        for (i = 0; i < days.size(); i++) {
            h += days.get(i).getExtraMinPerDay();
        }
        return h;
    }

    /**
     *
     * @return List of the WorkDays in this month
     */
    public List<WorkDay> getDays() {
        return days;
    }

    /**
     *
     * @return The date of this month
     */
    public String getSDate() {
        return sdate;
    }

    public YearMonth getDate() {
        if (sdate != null && !"".equals(sdate)) {
            return stringToYearMonth();
        } else {
            return date;
        }
    }

    private YearMonth stringToYearMonth() {
        String[] mydate = sdate.split("-");
        return YearMonth.of(parseInt(mydate[0]), parseInt(mydate[1]));
    }

    /**
     *
     * @param date The date of this month
     */
    public void setSDate(String date) {
        this.sdate = date;
    }

    public void setDate(YearMonth date) {
        this.sdate = date.toString();
        this.date = date;
    }

    /**
     *
     * @return The minutes worked this WorkMonth
     */
    public long getSumPerMonth() {
        long spm = 0;
        int i;
        for (i = 0; i < days.size(); i++) {
            spm += days.get(i).getSumPerDay();
        }
        return spm;
    }

    /**
     *
     * @return The required minutes to work this WorkMonth
     */
    public long getRequiredMinPerMonth() {
        long rpm = 0;
        int i;
        for (i = 0; i < days.size(); i++) {
            rpm += days.get(i).getRequiredMinPerDay();
        }
        this.requiredMinPerMonth = rpm;
        return rpm;
    }

    /**
     *
     * @param wd {@link WorkDay}
     * @return boolean<br>true if the workday fits to this month<br>false if it
     * is not
     */
    public boolean isSameMonth(WorkDay wd) {
        if (date == null) {
            date = stringToYearMonth();
        }
        return wd.getActualDay().getMonth().equals(date.getMonth()) && wd.getActualDay().getYear() == date.getYear();
    }

}
