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
public class StatisticsJSON {
    
    @JsonProperty
    private String text = "";
    @JsonProperty
    private TimeLogger tl;
    
    public StatisticsJSON(TimeLogger tl) {
        this.tl = tl;
    }

    public StatisticsJSON(String text, TimeLogger tl) {
        this.text = text;
        this.tl = tl;
    }
    
    public String getStat()
    {
        text = "";
        int m,i,j;
        if(tl.getMonths().isEmpty()) return "No months available";
        else {
        for(m=0;m<tl.getMonths().size();m++)
        {
            
            WorkMonth WM = tl.getMonths().get(m);
            text += ("\t\t\t\t\t" + WM.getExtraMinPerMonth()+"\n");
            if(WM.getDays().isEmpty()) text += WM.getDate() + ": No days this month\n";
            else{            
            for(i=0;i<WM.getDays().size();i++)
            {
                WorkDay WD = WM.getDays().get(i);
                if(WD.getTasks().isEmpty()) text+= WD.getActualDay() + ": No tasks for this day";
                else
                {
                text+=(WD.getActualDay() + "\t" + WD.getSumPerDay() + "\t" + WD.getTasks().get(0).getStartTime() + "\t" + WD.getExtraMinPerDay()+"\n");
                for (j=0;j<WD.getTasks().size();j++)
                {
                    Task t = WD.getTasks().get(j);
                    text+=(t.getMinPerTask() + "\t" + t.getTaskId() + "\t" + t.getComment() + "\t" + t.getEndTime()+"\n");
                }
                }
           text+="\n";
            }
           }

        text+="\n";
        }
        return text;
        }
    }
}
