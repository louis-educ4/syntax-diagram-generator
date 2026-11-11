package org.louis.ebnfcompiler.Visitors;

import java.util.Optional;

public interface OpenToVisitors {
    /*
    * Opens an AST node to a visitor, allowing the visitor to visit the node
    * Returns a data transfer object to allow the visitor to have data transits as it visits the tree.
    */

    Object accept(Visitor visitor, Object... args);
}
