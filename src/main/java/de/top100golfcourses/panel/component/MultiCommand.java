package de.top100golfcourses.panel.component;

import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MultiCommand implements Command {

    private final Command[] commands;

    @Override
    public void menuSelected(MenuItem selectedItem) {
        for (Command cmd : commands) {
            cmd.menuSelected(selectedItem);
        }
    }

    public MultiCommand(Command... cmd) {
        this.commands = cmd;
    }

}
