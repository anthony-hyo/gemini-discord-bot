package dev.anthhyo.discord.events;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EventArgs {

    private final List<String> args = new LinkedList<>();
    public int length;

    public static EventArgs parse(Object[] params) {
        EventArgs args = new EventArgs();

        Arrays.stream(params).forEach(param -> {
            if (param instanceof String && NumberUtils.isCreatable((String) param)) {
                try {
                    args.add(Integer.parseInt((String) param));
                } catch (NumberFormatException ex) {
                    try {
                        if (((String) param).contains(".") || ((String) param).contains(",")) {
                            args.add(Double.parseDouble((String) param));
                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex2) {
                        args.add(String.valueOf(param));
                    }
                }
            } else {
                args.add(String.valueOf(param));
            }
        });

        args.length = args.list().size();

        return args;
    }

    public boolean has(int argIndex) {
        return argIndex >= 0 && argIndex < args.size();
    }

    public Integer getInt(int argIndex) {
        return Integer.parseInt(args.get(argIndex));
    }

    public Double getDouble(int argIndex) {
        return Double.parseDouble(args.get(argIndex));
    }

    public String getStr(int argIndex) {
        return String.valueOf(args.get(argIndex));
    }

    public boolean getBoolean(int argIndex) {
        return Boolean.parseBoolean(args.get(argIndex));
    }

    public List<String> list() {
        return Collections.unmodifiableList(args);
    }

    @Override
    public String toString() {
        return Arrays.toString(list().toArray());
    }

    private void add(Object argObj) {
        args.add(argObj.toString());
    }

}
