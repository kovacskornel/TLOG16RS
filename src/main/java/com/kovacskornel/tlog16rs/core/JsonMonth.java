/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.core;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author precognox
 */
public class JsonMonth {
    @JsonProperty
    private int year;
    @JsonProperty
    private int month;
    @JsonProperty
    private TimeLogger tl;
    @JsonProperty
    private String text = "";

    public JsonMonth(int year ,int month, TimeLogger tl ) {
        this.year = year;
        this.month = month;
        this.tl = tl;
    }

    public JsonMonth() {
    }
    
    public JsonMonth(String text) {
        this.text = text;
    }
    
    public String getText()
    {
        text = "";
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
                    if(!WD.getTasks().isEmpty())
                    {
                    text+=(WD.getActualDay() + "\t" + WD.getSumPerDay() + "\t" + WD.getTasks().get(0).getStartTime() + "\t" + WD.getExtraMinPerDay()+"\n");
                    for (j=0;j<WD.getTasks().size();j++)
                    {
                        Task t = WD.getTasks().get(j);
                        text+=(t.getMinPerTask() + "\t" + t.getTaskId() + "\t" + t.getComment() + "\t" + t.getEndTime()+"\n");
                    }
                    }else text+= WD.getActualDay() + ": No tasks for this day";
                text+="\n";
                }
            }else text+= WM.getDate() + ": No days this month\n";
        }

        text+="\n";
        }
         
        }
    return text;
    }
}