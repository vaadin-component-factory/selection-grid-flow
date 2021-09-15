package com.vaadin.componentfactory.selectiongrid;

/*
 * #%L
 * selection-grid-flow
 * %%
 * Copyright (C) 2020 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Optional;

/**
 * A functional interface, that allows an object based on a different item.
 *
 * @author Stefan Uebe
 */
public interface ParentItemProvider<T> {

    /**
     * Returns the parent for a given item. If there is no parent item for the given one, the method
     * should return an empty optional.
     *
     * @return optional parent instance
     */
    Optional<T> getParent(T item);
}
