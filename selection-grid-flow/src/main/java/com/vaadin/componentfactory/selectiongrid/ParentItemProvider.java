package com.vaadin.componentfactory.selectiongrid;

import java.util.Optional;

/**
 * A functional interface, that allows an object based on a different item.
 *
 * @author Stefan Uebe
 */
public interface ParentItemProvider<T, P> {

    /**
     * Returns the parent for a given item. If there is no parent item for the given one, the method
     * should return an empty optional.
     *
     * @return optional parent instance
     */
    Optional<P> getParent(T item);
}
