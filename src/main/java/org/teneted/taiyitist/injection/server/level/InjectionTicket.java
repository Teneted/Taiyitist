package org.teneted.taiyitist.injection.server.level;

public interface InjectionTicket {

    default Object bridge$key() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setKey(Object key){
        throw new IllegalStateException("Not implemented");
    }
}
