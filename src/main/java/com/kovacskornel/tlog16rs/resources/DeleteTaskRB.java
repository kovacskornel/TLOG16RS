/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.resources;

/**
 *
 * @author Kovacs Kornel
 */

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class DeleteTaskRB {
    private int year;
    private int month;
    private int day;
    private String taskId;
    private String startTime;
}
