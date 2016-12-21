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
public class JsonDay {
    @JsonProperty
    private int year;
    @JsonProperty
    private int month;
    @JsonProperty
    private int day;
    @JsonProperty
    private TimeLogger tl;
    @JsonProperty
    private String text = "";

    public JsonDay(int year ,int month,int day, TimeLogger tl ) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.tl = tl;
    }

    public JsonDay() {
    }
    
    public JsonDay(String text) {
        this.text = text;
    }
    
    public String getText()
    {
        text = "";
        int m,i,j;
        for(m=0;m<tl.getMonths().size();m++)
        {   
            WorkMonth WM = tl.getMonths().get(m);
            if(WM.getDate().getYear() == year && WM.getDate().getMonthValue() == month){
                for(i=0;i<WM.getDays().size();i++)
                {
                    WorkDay WD = WM.getDays().get(i);
                    if(WD.getActualDay().getDayOfMonth() == day)
                    {
                        if(!WD.getTasks().isEmpty())
                        {
                            text+=(WD.getActualDay() + "\t" + WD.getSumPerDay() + "\t" + WD.getTasks().get(0).getStartTime() + "\t" + WD.getExtraMinPerDay()+"\n");
                            for (j=0;j<WD.getTasks().size();j++)
                            {
                                Task t = WD.getTasks().get(j);
                                if(t.getEndTime() != null)
                                {

                                
                                text+=(t.getMinPerTask() + "\t" + t.getTaskId() + "\t" + t.getComment() + "\t" + t.getEndTime());

                                } else text+= t.getTaskId() + ": Unfinished Task!";
                                text+="\n";
                            }
                        }else text+= WD.getActualDay() + ": No tasks for this day";
                    text+="\n";
                    }
                }
        }
        text+="\n";
        }       
    return text;
    }
}