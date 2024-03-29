// UnmodifiableList.java, created Wed Mar  5  0:26:32 2003 by joewhaley
// Copyright (C) 2001-3 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package joeq.Util.Templates;

/**
 * @author John Whaley <jwhaley@alum.mit.edu>
 * @version $Id: UnmodifiableList.java,v 1.7 2004/09/22 22:17:45 joewhaley Exp $
 */
public abstract class UnmodifiableList {
    public static class jq_Class extends java.util.AbstractList<joeq.Class.jq_Class> {
        private final joeq.Class.jq_Class[] a;
        public jq_Class(joeq.Class.jq_Class c) { a = new joeq.Class.jq_Class[] { c }; }
        public jq_Class(joeq.Class.jq_Class c1, joeq.Class.jq_Class c2) { a = new joeq.Class.jq_Class[] { c1, c2 }; }
        public jq_Class(joeq.Class.jq_Class c1, joeq.Class.jq_Class c2, joeq.Class.jq_Class c3) { a = new joeq.Class.jq_Class[] { c1, c2, c3 }; }
        public jq_Class(joeq.Class.jq_Class[] c) { a = c; }
        public int size() { return a.length; }
        public joeq.Class.jq_Class get(int index) { return getClass(index); }
        public joeq.Class.jq_Type getType(int index) { return a[index]; }
        public joeq.Class.jq_Reference getReference(int index) { return a[index]; }
        public joeq.Class.jq_Class getClass(int index) { return a[index]; }
        public static final jq_Class EMPTY = new jq_Class(new joeq.Class.jq_Class[0]);
        public static jq_Class getEmptyList() { return EMPTY; }
    }
        
    public static class RegisterOperand extends java.util.AbstractList<joeq.Compiler.Quad.Operand.RegisterOperand> {
        private final joeq.Compiler.Quad.Operand.RegisterOperand[] a;
        public RegisterOperand(joeq.Compiler.Quad.Operand.RegisterOperand c) { a = new joeq.Compiler.Quad.Operand.RegisterOperand[] { c }; }
        public RegisterOperand(joeq.Compiler.Quad.Operand.RegisterOperand c1, joeq.Compiler.Quad.Operand.RegisterOperand c2) { a = new joeq.Compiler.Quad.Operand.RegisterOperand[] { c1, c2 }; }
        public RegisterOperand(joeq.Compiler.Quad.Operand.RegisterOperand c1, joeq.Compiler.Quad.Operand.RegisterOperand c2, joeq.Compiler.Quad.Operand.RegisterOperand c3) { a = new joeq.Compiler.Quad.Operand.RegisterOperand[] { c1, c2, c3 }; }
        public RegisterOperand(joeq.Compiler.Quad.Operand.RegisterOperand c1, joeq.Compiler.Quad.Operand.RegisterOperand c2, joeq.Compiler.Quad.Operand.RegisterOperand c3, joeq.Compiler.Quad.Operand.RegisterOperand c4) { a = new joeq.Compiler.Quad.Operand.RegisterOperand[] { c1, c2, c3, c4 }; }
        public RegisterOperand(joeq.Compiler.Quad.Operand.RegisterOperand[] c) { a = c; }
        public int size() { return a.length; }
        public joeq.Compiler.Quad.Operand.RegisterOperand get(int index) { return getRegisterOperand(index); }
        public joeq.Compiler.Quad.Operand getOperand(int index) { return getRegisterOperand(index); }
        public joeq.Compiler.Quad.Operand.RegisterOperand getRegisterOperand(int index) { return a[index]; }
        public static final RegisterOperand EMPTY = new RegisterOperand(new joeq.Compiler.Quad.Operand.RegisterOperand[0]);
        public static RegisterOperand getEmptyList() { return EMPTY; }
    }
        
    public static class Operand extends java.util.AbstractList<joeq.Compiler.Quad.Operand> {
        private final joeq.Compiler.Quad.Operand[] a;
        public Operand(joeq.Compiler.Quad.Operand c) { a = new joeq.Compiler.Quad.Operand[] { c }; }
        public Operand(joeq.Compiler.Quad.Operand c1, joeq.Compiler.Quad.Operand c2) { a = new joeq.Compiler.Quad.Operand[] { c1, c2 }; }
        public Operand(joeq.Compiler.Quad.Operand c1, joeq.Compiler.Quad.Operand c2, joeq.Compiler.Quad.Operand c3) { a = new joeq.Compiler.Quad.Operand[] { c1, c2, c3 }; }
        public Operand(joeq.Compiler.Quad.Operand c1, joeq.Compiler.Quad.Operand c2, joeq.Compiler.Quad.Operand c3, joeq.Compiler.Quad.Operand c4) { a = new joeq.Compiler.Quad.Operand[] { c1, c2, c3, c4 }; }
        public Operand(joeq.Compiler.Quad.Operand[] c) { a = c; }
        public int size() { return a.length; }
        public joeq.Compiler.Quad.Operand get(int index) { return getOperand(index); }
        public joeq.Compiler.Quad.Operand getOperand(int index) { return a[index]; }
        public static final Operand EMPTY = new Operand(new joeq.Compiler.Quad.Operand[0]);
        public static Operand getEmptyList() { return EMPTY; }
    }
   
    public static class BasicBlock extends java.util.AbstractList<joeq.Compiler.Quad.BasicBlock> {
        private final joeq.Compiler.Quad.BasicBlock[] a;
        public BasicBlock(joeq.Compiler.Quad.BasicBlock[] c) { a = c; }
        public int size() { return a.length; }
        public joeq.Compiler.Quad.BasicBlock get(int index) { return getBasicBlock(index); }
        public joeq.Compiler.Quad.BasicBlock getBasicBlock(int index) { return a[index]; }
        public static final BasicBlock EMPTY = new BasicBlock(new joeq.Compiler.Quad.BasicBlock[0]);
        public static BasicBlock getEmptyList() { return EMPTY; }
    }
        
    public static class Quad extends java.util.AbstractList<joeq.Compiler.Quad.Quad> {
        private final joeq.Compiler.Quad.Quad[] a;
        public Quad(joeq.Compiler.Quad.Quad[] c) { a = c; }
        public Quad(joeq.Compiler.Quad.Quad c) { a = new joeq.Compiler.Quad.Quad[] { c }; }
        public int size() { return a.length; }
        public joeq.Compiler.Quad.Quad get(int index) { return getQuad(index); }
        public joeq.Compiler.Quad.Quad getQuad(int index) { return a[index]; }
        public static final Quad EMPTY = new Quad(new joeq.Compiler.Quad.Quad[0]);
        public static Quad getEmptyList() { return EMPTY; }
    }
}
