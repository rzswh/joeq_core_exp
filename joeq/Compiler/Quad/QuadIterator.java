// QuadIterator.java, created Fri Jan 11 16:42:38 2002 by joewhaley
// Copyright (C) 2001-3 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package joeq.Compiler.Quad;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Set;
import java.util.NoSuchElementException;
import joeq.Class.jq_Class;
import jwutil.graphs.Navigator;

/**
 *
 * @author  John Whaley <jwhaley@alum.mit.edu>
 * @version $Id: QuadIterator.java,v 1.14 2004/09/22 22:17:27 joewhaley Exp $
 */
public class QuadIterator implements ListIterator<Quad> {

    /** A reference to the control flow graph that we are iterating over. */
    protected final ControlFlowGraph cfg;
    /** The reverse post order iteration of basic blocks in the control flow graph.
     * When going forward, nextBasicBlock should always be the last basic block
     * returned by this iterator. */
    protected final ListIterator<BasicBlock> rpoBasicBlocks;
    /** References to the previous non-empty basic block, the current basic block,
     * and the next non-empty basic block. */
    protected BasicBlock previousBasicBlock, currentBasicBlock, nextBasicBlock;
    /** An iteration of the quads in the current basic block. */
    protected ListIterator<Quad> quadsInCurrentBasicBlock;
    
    /** The index of the last quad that was returned. */
    protected int lastIndex;
    /** The last quad that was returned. */
    protected Quad lastQuad;
    
    /**
     * Initialize the iterator to iterate over the quads in the
     * given control flow graph in reverse post order.
     * @param cfg  the control flow graph
     */
    public QuadIterator(ControlFlowGraph cfg) { this(cfg, true); }
    /**
     * Initialize the iterator to iterate over the quads in the
     * given control flow graph.
     * If direction is true, the order is reverse post order and the
     * iteration starts at the first quad.
     * If direction is false, the order is post order on the
     * reverse graph and the iteration starts at the last quad.
     * @param direction  the direction to iterate, forward==true
     * @param cfg  the control flow graph
     */
    public QuadIterator(ControlFlowGraph cfg, boolean direction) {
        // cache control flow graph
        this.cfg = cfg;
        // initialize list of basic blocks in reverse post order
        this.rpoBasicBlocks = direction ? cfg.reversePostOrderIterator() : cfg.postOrderOnReverseGraphIterator();
        // skip entry basic block
        this.rpoBasicBlocks.next();
        this.previousBasicBlock = null;
        this.currentBasicBlock = this.rpoBasicBlocks.next();
        updateNextBB();
        // initialize quad iterator
        this.quadsInCurrentBasicBlock = this.currentBasicBlock.iterator();
        // go to the end, if we are doing the reverse direction.
        if (!direction) {
            while (hasNext()) next();
        }
        // initialize the last index and the last quad
        this.lastIndex = -1; this.lastQuad = null;
    }
    
    /** Update the nextBasicBlock field to point to the next non-empty basic
     * block from the reverse post order, or null if there are no more
     * non-empty basic blocks. */
    protected void updateNextBB() {
        for (;;) {
            if (!this.rpoBasicBlocks.hasNext()) {
                this.nextBasicBlock = null;
                break;
            }
            this.nextBasicBlock = this.rpoBasicBlocks.next();
            if (this.nextBasicBlock.size() > 0) break;
        }
    }
    /** Update the previousBasicBlock field to point to the previous non-empty basic
     * block from the reverse post order, or null if there are no more previous
     * non-empty basic blocks. */
    protected void updatePreviousBB() {
        // the rpoBasicBlocks iterator points just past nextBasicBlock, so
        // we need to back up to before previousBasicBlock.
        for (;;) {
            BasicBlock p = this.rpoBasicBlocks.previous();
            if (p == this.previousBasicBlock) break;
        }
        for (;;) {
            if (!this.rpoBasicBlocks.hasPrevious()) {
                this.previousBasicBlock = null;
                break;
            }
            this.previousBasicBlock = this.rpoBasicBlocks.previous();
            if (this.previousBasicBlock.size() > 0) break;
        }
        // now reset the rpoBasicBlocks iterator to point just past nextBasicBlock.
        for (;;) {
            BasicBlock p = this.rpoBasicBlocks.next();
            if (p == this.nextBasicBlock) break;
        }
    }
    
    public BasicBlock getCurrentBasicBlock() { return this.currentBasicBlock; }
    
    public Quad getCurrentQuad() { return lastQuad; }
    
    /** Return the next quad in the iteration. */
    public Quad next() {
        if (!this.quadsInCurrentBasicBlock.hasNext()) {
            // end of basic block, go to next basic block.
            if (this.nextBasicBlock == null)
                throw new NoSuchElementException();
            this.previousBasicBlock = this.currentBasicBlock;
            this.currentBasicBlock = this.nextBasicBlock;
            this.quadsInCurrentBasicBlock = this.currentBasicBlock.iterator();
            // update nextBasicBlock
            updateNextBB();
        }
        ++this.lastIndex;
        return this.lastQuad = this.quadsInCurrentBasicBlock.next();
    }
    /** Returns whether there is a next quad in this iteration. */
    public boolean hasNext() {
        if (this.quadsInCurrentBasicBlock.hasNext()) return true;
        return this.nextBasicBlock != null;
    }
    
    /** Returns the first quad reachable from the start of the given basic block. */
    protected Quad getFirstQuad(BasicBlock bb) {
        for (;;) {
            if (bb.isExit()) return null;
            if (bb.size() > 0) return bb.getQuad(0);
            bb = bb.getFallthroughSuccessor();
        }
    }

    /** Returns the last quad reachable from the end of the given basic block. */
    protected Quad getLastQuad(BasicBlock bb) {
        for (;;) {
            if (bb.isEntry()) return null;
            if (bb.size() > 0) return bb.getLastQuad();
            if (bb.getPredecessors().isEmpty()) return null; // block is unreachable.
            bb = bb.getFallthroughPredecessor();
        }
    }
    
    /** Sets the current quad. */
    public void set(Quad obj) { this.quadsInCurrentBasicBlock.set(obj); }
    /** Returns the index of the next quad to be returned. */
    public int nextIndex() { return this.lastIndex+1; }
    
    /** Returns the previous quad in the iteration. */
    public Quad previous() {
        if (!this.quadsInCurrentBasicBlock.hasPrevious()) {
            // beginning of basic block, go to previous basic block.
            if (this.previousBasicBlock == null)
                throw new NoSuchElementException();
            this.nextBasicBlock = this.currentBasicBlock;
            this.currentBasicBlock = this.previousBasicBlock;
            this.quadsInCurrentBasicBlock = this.currentBasicBlock.iterator();
            // go to end of iterator
            while (this.quadsInCurrentBasicBlock.hasNext())
                this.quadsInCurrentBasicBlock.next();
            // update previousBasicBlock
            updatePreviousBB();
        }
        this.lastQuad = this.quadsInCurrentBasicBlock.previous();
        --this.lastIndex;
        return this.lastQuad;
    }
    
    /** Removes the last-returned-quad from the underlying list. */
    public void remove() { this.quadsInCurrentBasicBlock.remove(); lastQuad = null; --lastIndex; }

    /** Returns the index of the previous quad. */
    public int previousIndex() { return this.lastIndex; }
    
    /** Returns whether this iteration has a previous quad. */
    public boolean hasPrevious() {
        if (this.quadsInCurrentBasicBlock.hasPrevious()) return true;
        return this.previousBasicBlock != null;
    }
    
    /** Adds a quad to the underlying quad list. */
    public void add(Quad obj) { this.quadsInCurrentBasicBlock.add(obj); lastQuad = null; ++lastIndex; }
    
    /**
     * Return an iterator of the possible successor quads of the most recently returned quad.
     * If a possible successor is the method exit, it includes the "null" value in the iteration.
     * @throws IllegalStateException  if the nextQuad method has not yet been called.
     */
    public java.util.Iterator<Quad> successors() {
        return successors1().iterator();
    }
    public Collection<Quad> successors1() {
        // if lastQuad is invalid, throw an exception.
        if (lastQuad == null) throw new IllegalStateException();
        // allocate the result set.
        java.util.Set<Quad> result = new java.util.HashSet<Quad>();

        // Start with case 3:
        // Case 3: Iterate through the types of exceptions that this quad
        //         may throw.
        Iterator<jq_Class> exceptions = lastQuad.getThrownExceptions().iterator();
        while (exceptions.hasNext()) {
            jq_Class exception = exceptions.next();
            // Iterate over the list of exception handlers that may catch an
            // exception of this type.
            Iterator<ExceptionHandler> mayCatch = currentBasicBlock.getExceptionHandlers().mayCatch(exception).iterator();
            while (mayCatch.hasNext()) {
                ExceptionHandler exceptionHandler = mayCatch.next();
                // add the first quad of the exception handler entry to the set.
                result.add(getFirstQuad(exceptionHandler.getEntry()));
            }
            // if the exception is not definitely caught, add "null" for the exit.
            if (currentBasicBlock.getExceptionHandlers().mustCatch(exception) == null) {
                result.add(null);
            }
        }

        // note: we use next() and previous() rather than using
        // nextIndex() and getQuad(index), because accessing a linked list
        // via an index is not a constant time operation.

        // we need to check if we last called previous or next, because
        // the position of the iterator depends on which was most recently
        // called.
        if (this.quadsInCurrentBasicBlock.hasNext()) {
            Quad next = this.quadsInCurrentBasicBlock.next();
            if (next != lastQuad) {
                // We called next() last.
                // Case 1: if this is not the end of the basic block, add the next
                //         quad in the basic block.
                result.add(next); // add next quad.
                this.quadsInCurrentBasicBlock.previous(); // reset iterator position.
                return result;
            } else {
                // We called previous() last.
                if (this.quadsInCurrentBasicBlock.hasNext()) {
                    // Case 1: if this is not the end of the basic block, add the next
                    //         quad in the basic block.
                    next = this.quadsInCurrentBasicBlock.next();
                    result.add(next); // add next quad.
                    // reset iterator position.
                    this.quadsInCurrentBasicBlock.previous();
                    this.quadsInCurrentBasicBlock.previous();
                    return result;
                } else {
                    // this is the last quad in the basic block.
                    // reset iterator position and fallthrough to case 2, below.
                    this.quadsInCurrentBasicBlock.previous();
                }
            }
        }
        // Case 2: end of basic block, add the first quad of every
        //         successor basic block.
        Iterator<BasicBlock> succs = this.currentBasicBlock.getSuccessors().iterator();
        while (succs.hasNext())
            result.add(getFirstQuad(succs.next()));
        
        return result;
    }

    public java.util.Iterator<Quad> predecessors() {
        return predecessors1().iterator();
    }
    public Collection<Quad> predecessors1() {
        // if lastQuad is invalid, throw an exception.
        if (lastQuad == null) throw new IllegalStateException();

        // figure out if we last called previous() or next().
        Quad previous;
        if (this.quadsInCurrentBasicBlock.hasPrevious()) {
            previous = this.quadsInCurrentBasicBlock.previous();
            if (previous != lastQuad) {
                // we called previous() last;
                // Case 1: if this is not the beginning of the basic block, add the previous
                //         quad in the basic block.
                this.quadsInCurrentBasicBlock.next(); // reset iterator position.
                return Collections.singletonList(previous);
            } else {
                // we called next() last.
                if (this.quadsInCurrentBasicBlock.hasPrevious()) {
                    // Case 1: if this is not the beginning of the basic block, add the previous
                    //         quad in the basic block.
                    previous = this.quadsInCurrentBasicBlock.previous();
                    // reset iterator position.
                    this.quadsInCurrentBasicBlock.next(); 
                    this.quadsInCurrentBasicBlock.next();
                    return Collections.singletonList(previous);
                } else {
                    // this is the first quad in the basic block.
                    // reset iterator position and fallthrough to case 2 and 3, below.
                    this.quadsInCurrentBasicBlock.next();
                }
            }
        }

        // allocate the result set.
        Set<Quad> result = new java.util.HashSet<Quad>();

        // Case 2: beginning of basic block, add the first quad of every
        //         predecessor basic block.
        Iterator<BasicBlock> preds = this.currentBasicBlock.getPredecessors().iterator();
        while (preds.hasNext())
            result.add(getLastQuad(preds.next()));
        // Case 3: if this is the entry point to an exception handler, find
        //         all quads that can trigger this handler.
        if (currentBasicBlock.isExceptionHandlerEntry()) {
            Iterator<ExceptionHandler> ex_handlers = cfg.getExceptionHandlersMatchingEntry(currentBasicBlock);
            while (ex_handlers.hasNext()) {
                ExceptionHandler eh = (ExceptionHandler)ex_handlers.next();
                Iterator<BasicBlock> handled = eh.getHandledBasicBlocks().iterator();
                while (handled.hasNext()) {
                    BasicBlock bb = handled.next();
                    addQuadsThatReachHandler(bb, result, eh);
                }
            }
        }
        return result;
    }

    private static void addQuadsThatReachHandler(BasicBlock bb, Set<Quad> result, ExceptionHandler eh) {
        Iterator<Quad> quads = bb.iterator();
        while (quads.hasNext()) {
            Quad q = quads.next();
            Iterator<jq_Class> exceptions = q.getThrownExceptions().iterator();
            while (exceptions.hasNext()) {
                jq_Class exception = (jq_Class)exceptions.next();
                List<ExceptionHandler> mayCatch = bb.getExceptionHandlers().mayCatch(exception);
                if (mayCatch.contains(eh))
                    result.add(q);
            }
        }
    }
    
    public Navigator getNavigator() {
        return new Navigator() {
            public Collection<Quad> next(Object node) {
                if (node == lastQuad) return successors1();
                return search(node, true);
            }
            public Collection<Quad> prev(Object node) {
                if (node == lastQuad) return predecessors1();
                return search(node, false);
            }
            private Collection<Quad> search(Object node, boolean dir) {
                Quad oldLocation = lastQuad;
                Collection<Quad> result = null;
                if (searchBackward((Quad)node)) {
                    result = dir?successors1():predecessors1();
                }
                searchForward(oldLocation);
                if (result == null) {
                    if (searchForward((Quad)node)) {
                        result = dir?successors1():predecessors1();
                    }
                    if (result == null) {
                        throw new UnsupportedOperationException();
                    }
                    searchBackward(oldLocation);
                }
                return result;
            }
        };
    }
    public boolean searchForward(Quad node) {
        while (hasNext()) {
            if (node == next()) return true;
        }
        return false;
    }
    public boolean searchBackward(Quad node) {
        while (hasPrevious()) {
            if (node == previous()) return true;
        }
        return false;
    }
}
