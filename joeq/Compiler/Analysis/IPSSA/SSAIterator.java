/*
 * Created on Dec 4, 2003
 *
 * Define some typed iterators.
 */
package joeq.Compiler.Analysis.IPSSA;

import java.util.Iterator;
import jwutil.collections.UnmodifiableIterator;

/**
 * @author V.Benjamin Livshits
 * @version $Id: SSAIterator.java,v 1.8 2004/09/22 22:17:33 joewhaley Exp $
 */
public class SSAIterator {
    public static class DefinitionIterator extends UnmodifiableIterator {
        private Iterator<SSADefinition> _iter;
        
        public DefinitionIterator(Iterator<SSADefinition> iter) {
            this._iter = iter;
        }
        
        public boolean hasNext() {
            return _iter.hasNext();
        }
    
        public SSADefinition next() {
            return _iter.next();
        }
    }
    
    public static class ValueIterator extends UnmodifiableIterator {
        private Iterator<SSAValue> _iter;
        
        public ValueIterator(Iterator<SSAValue> iter) {
            this._iter = iter;
        }
        
        public boolean hasNext() {
            return _iter.hasNext();
        }
    
        public SSAValue next() {
            return _iter.next();
        }
    }
    
    public static class BindingIterator extends UnmodifiableIterator {
        private Iterator<SSABinding> _iter;
    
        public BindingIterator(Iterator<SSABinding> iter) {
            this._iter = iter;
        }
    
        public boolean hasNext() {
            return _iter.hasNext();
        }

        public SSABinding next() {
            return _iter.next();
        }
    }
}