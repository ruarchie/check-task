package org.ruarchie.dev;

import java.util.List;

@FunctionalInterface
public interface Queryable {
    List<?> getData();
}
