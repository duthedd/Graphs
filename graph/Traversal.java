package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.TreeSet;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular collection of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.
 *
 *  Generally, the client will extend Traversal.  By overriding the visit
 *  method, the client can determine what happens when a node is visited.
 *  By supplying an appropriate type of Queue object to the constructor,
 *  the client can control the behavior of the fringe. By overriding the
 *  shouldPostVisit and postVisit methods, the client can arrange for
 *  post-visits of a node (as in depth-first search).  By overriding
 *  the reverseSuccessors and processSuccessor methods, the client can control
 *  the addition of neighbor vertices to the fringe when a vertex is visited.
 *
 *  Traversals may be interrupted or restarted, remembering the previously
 *  marked vertices.
 *  @author Dasheng Chen
 */
public abstract class Traversal {

    /** A Traversal of G, using FRINGE as the fringe. */
    protected Traversal(Graph G, Queue<Integer> fringe) {
        _G = G;
        _fringe = fringe;
        _visited = new ArrayList<Integer>();
        _postVisited = new TreeSet<Integer>();
    }

    /** Unmark all vertices in the graph. */
    public void clear() {
        _visited.clear();
    }

    /** Initialize the fringe to V0 and perform a traversal. */
    public void traverse(Collection<Integer> V0) {
        _fringe.addAll(V0);
        while (!_fringe.isEmpty()) {
            int v = _fringe.peek();
            if (marked(v)) {
                _fringe.poll();
                if (shouldPostVisit(v) && !_postVisited.contains(v)) {
                    _postVisited.add(v);
                    if (!this.postVisit(v)) {
                        return;
                    }
                }
            } else {
                this.mark(v);
                if (!shouldPostVisit(v)) {
                    _fringe.poll();
                }
                if (!this.visit(v)) {
                    return;
                }
                ArrayDeque<Integer> suc = new ArrayDeque<Integer>();
                Iteration<Integer> iterSuc = this._G.successors(v);
                if (reverseSuccessors(v)) {
                    for (Integer i : iterSuc) {
                        suc.addFirst(i);
                    }
                    iterSuc = Iteration.iteration(suc.iterator());
                }
                for (Integer i : iterSuc) {
                    if (processSuccessor(v, i)) {
                        _fringe.add(i);
                    }
                }
            }
        }
    }

    /** Initialize the fringe to { V0 } and perform a traversal. */
    public void traverse(int v0) {
        traverse(Arrays.<Integer>asList(v0));
    }

    /** Returns true iff V has been marked. */
    protected boolean marked(int v) {
        return _visited.contains(v);
    }

    /** Mark vertex V. */
    protected void mark(int v) {
        _visited.add(v);
    }

    /** Perform a visit on vertex V.  Returns false iff the traversal is to
     *  terminate immediately. */
    protected boolean visit(int v) {
        return true;
    }

    /** Return true if we should postVisit V after traversing its
     *  successors.  (Post-visiting generally is useful only for depth-first
     *  traversals, although we define it for all traversals.) */
    protected boolean shouldPostVisit(int v) {
        return false;
    }

    /** Revisit vertex V after traversing its successors.  Returns false iff
     *  the traversal is to terminate immediately. */
    protected boolean postVisit(int v) {
        return true;
    }

    /** Return true if we should schedule successors of V in reverse order. */
    protected boolean reverseSuccessors(int v) {
        return false;
    }

    /** Process successor V to U.  Returns true iff V is then to
     *  be added to the fringe.  By default, returns true iff V is unmarked. */
    protected boolean processSuccessor(int u, int v) {
        return !marked(v);
    }

    /** The graph being traversed. */
    private final Graph _G;
    /** The fringe. */
    protected final Queue<Integer> _fringe;
    /** Vertex that visited. */
    private ArrayList<Integer> _visited;
    /** Post visit set. */
    private TreeSet<Integer> _postVisited;
}
