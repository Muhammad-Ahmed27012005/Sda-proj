package com.sda.project.patterns.command;

import java.util.ArrayDeque;
import java.util.Deque;
import org.springframework.stereotype.Component;

@Component
public class VideoCommandInvoker {
	private final Deque<Command> history = new ArrayDeque<>();

	public void executeCommand(Command command) {
		command.execute();
		history.push(command);
	}

	public void undoLast() {
		if (!history.isEmpty()) {
			history.pop().undo();
		}
	}
}
