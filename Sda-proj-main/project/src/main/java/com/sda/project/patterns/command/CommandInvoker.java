package com.sda.project.patterns.command;

import java.util.ArrayList;
import java.util.List;

public class CommandInvoker {
    private final List<Command> history = new ArrayList<>();

    public void execute(Command command) {
        command.execute();
        history.add(command);
    }

    public List<Command> getHistory() {
        return List.copyOf(history);
    }
}
