/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.resources;

/**
 *
 * @author precognox
 */
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class WorkDayRB {
    private int year;
    private int month;
    private int day;
    private int requiredHours;    
}
