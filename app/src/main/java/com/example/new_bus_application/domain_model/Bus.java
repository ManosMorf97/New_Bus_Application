package com.example.new_bus_application.domain_model;

import java.util.ArrayList;

public class Bus extends HelpComparator{
    private final String code;
    public Bus(String code,String name){
        super(name);
        routes=new ArrayList<>(2);
        this.code=code;
    }
    public String getCode(){
        return  code;
    }

    public String Info(){
        return "The bus' name is: "+getName()+ " the bus' code is: "+code;
    }
}
