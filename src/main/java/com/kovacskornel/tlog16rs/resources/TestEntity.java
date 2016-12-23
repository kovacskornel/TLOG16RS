/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.resources;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author precognox
 */
@Entity
@lombok.Getter
@lombok.Setter
public class TestEntity{
    private String text = "";
    @Id @GeneratedValue
    private int id;
}

