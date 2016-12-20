package com.kovacskornel.tlog16rs.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonGreeting {

    @JsonProperty
    private String greeting;

    public JsonGreeting() {
    }

    public JsonGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }
}