package org.teneted.taiyitist.injection.commands;

public interface InjectionCommandNode {

    default void removeCommand(String name) {
        throw new AssertionError("Not implemented");
    }
}
