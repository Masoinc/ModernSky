package me.masonic.mc.Objects;

import java.util.HashMap;

public class Function {
    HashMap<String, String> columns = new HashMap<>();

    String name;

    public Function(String name, HashMap<String, String> columns) {
        this.columns = columns;
        this.name = name;
    }

    public HashMap<String, String> getColumns() {
        return columns;
    }
}
