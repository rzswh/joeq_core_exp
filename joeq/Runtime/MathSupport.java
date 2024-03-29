// MathSupport.java, created Mon Feb  5 23:23:21 2001 by joewhaley
// Copyright (C) 2001-3 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package joeq.Runtime;

import joeq.Class.PrimordialClassLoader;
import joeq.Class.jq_Class;
import joeq.Class.jq_StaticField;
import joeq.Class.jq_StaticMethod;

/*
 * @author  John Whaley <jwhaley@alum.mit.edu>
 * @version $Id: MathSupport.java,v 1.11 2004/03/09 22:01:46 jwhaley Exp $
 */
public abstract class MathSupport {

    public static boolean ucmp(/*unsigned*/int a, /*unsigned*/int b) {
        if ((b < 0) && (a >= 0)) return true;
        if ((b >= 0) && (a < 0)) return false;
        return a<b;
    }
    public static boolean ulcmp(/*unsigned*/long a, /*unsigned*/long b) {
        if ((b < 0L) && (a >= 0L)) return true;
        if ((b >= 0L) && (a < 0L)) return false;
        return a<b;
    }
    public static /*unsigned*/int udiv(/*unsigned*/int a, /*unsigned*/int b) {
        if (b < 0) {
            if (a < 0) {
                if (a >= b) return 1;
            }
            return 0;
        }
        if (a >= 0) return a/b;
        // hard case
        int a2 = a >>> 1;
        int d = a2/b;
        int m = ((a2%b) << 1) + (a&1);
        return (d<<1) + m/b;
    }
    
    public static /*unsigned*/int urem(/*unsigned*/int a, /*unsigned*/int b) {
        if (b < 0) {
            if (a < 0) {
                if (a >= b) return a-b;
            }
            return a;
        }
        if (a >= 0) return a%b;
        // hard case
        int a2 = a >>> 1;
        int m = ((a2%b) << 1) + (a&1);
        return m%b;
    }
    
    public static /*unsigned*/long umul(/*unsigned*/int a, /*unsigned*/int b) {
        char a_1 = (char)a;
        char a_2 = (char)(a>>16);
        char b_1 = (char)b;
        char b_2 = (char)(b>>16);
        int r1;
        long result = 0L;
        r1 = a_1*b_1; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)    ; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)   ;
        r1 = a_2*b_1; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<16; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<16;
        r1 = a_1*b_2; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<16; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<16;
        return result;
    }
    
    public static long imul(int u, int v) {
        /*unsigned*/int u1, u0, v1, v0, udiff, vdiff, high, mid, low;
        /*unsigned*/int prodh, prodl, was;
        boolean neg;

        u1 = HHALF(u);
        u0 = LHALF(u);
        v1 = HHALF(v);
        v0 = LHALF(v);

        low = u0 * v0;

        /* This is the same small-number optimization as before. */
        if (u1 == 0 && v1 == 0) return ((long)low)&0xFFFFFFFF;

        if (!ucmp(u1, u0)) {
            udiff = u1 - u0; neg = false;
        } else {
            udiff = u0 - u1; neg = true;
        }
        if (!ucmp(v0, v1)) {
            vdiff = v0 - v1;
        } else {
            vdiff = v1 - v0; neg = !neg;
        }
        mid = udiff * vdiff;

        high = u1 * v1;

        /* prod = (high << 2N) + (high << N); */
        prodh = high + HHALF(high);
        prodl = LHUP(high);

        /* if (neg) prod -= mid << N; else prod += mid << N; */
        if (neg) {
            was = prodl;
            prodl -= LHUP(mid);
            prodh -= HHALF(mid) + (ucmp(was, prodl)?1:0);
        } else {
            was = prodl;
            prodl += LHUP(mid);
            prodh += HHALF(mid) + (ucmp(prodl, was)?1:0);
        }

        /* prod += low << N */
        was = prodl;
        prodl += LHUP(low);
        prodh += HHALF(low) + (ucmp(prodl, was)?1:0);
        /* ... + low; */
        if (ucmp((prodl += low), low))
            prodh++;

        /* return 4N-bit product */
        return COMBINEQ(prodh, prodl);
    }
    
    public static long lmul(long a, long b) {
        long u, v, low, prod;
        /*unsigned*/int high, mid, udiff, vdiff;
        boolean negall, negmid;
        if (a >= 0) { u = a; negall = false; }
        else { u = -a; negall = true; }
        if (b >= 0) { v = b; }
        else { v = -b; negall = !negall; }

        /*unsigned*/int u1, u0, v1, v0;
        u1 = HHALFQ(u);
        u0 = LHALFQ(u);
        v1 = HHALFQ(v);
        v0 = LHALFQ(v);
        if (u1 == 0 && v1 == 0) {
            prod = imul(u0, v0);
        } else {
            /*
             * Compute the three intermediate products, remembering
             * whether the middle term is negative.  We can discard
             * any upper bits in high and mid, so we can use native
             * u_long * u_long => u_long arithmetic.
             */
            low = imul(u0, v0);
            if (!ucmp(u1, u0)) {
                negmid = false; udiff = u1 - u0;
            } else {
                negmid = true; udiff = u0 - u1;
            }
            if (!ucmp(v0, v1)) {
                vdiff = v0 - v1;
            } else {
                vdiff = v1 - v0; negmid = !negmid;
            }
            mid = udiff * vdiff;
            
            high = (/*unsigned*/ int)umul(u1, v1);

            /*
             * Assemble the final product.
             */
            prod = COMBINEQ(high + (negmid ? -mid : mid) + LHALFQ(low) + HHALFQ(low), HHALFQ(low));
        }
        return (negall ? -prod : prod);
    }
    
    public static long ulmul(long a, long b) {
        char a_1 = (char)a;
        char a_2 = (char)(a>>16);
        char a_3 = (char)(a>>32);
        char a_4 = (char)(a>>48);
        char b_1 = (char)b;
        char b_2 = (char)(b>>16);
        char b_3 = (char)(b>>32);
        char b_4 = (char)(b>>48);
        int r1;
        long result = 0L;
        r1 = a_1*b_1; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)    ; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)   ;
        r1 = a_2*b_1; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<16; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<16;
        r1 = a_1*b_2; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<16; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<16;
        r1 = a_3*b_1; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<32; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<32;
        r1 = a_2*b_2; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<32; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<32;
        r1 = a_1*b_3; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<32; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<32;
        r1 = a_4*b_1; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<48; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<48;
        r1 = a_3*b_2; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<48; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<48;
        r1 = a_2*b_3; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<48; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<48;
        r1 = a_1*b_4; if (r1 < 0) { result += ((long)Integer.MAX_VALUE)<<48; r1 -= Integer.MAX_VALUE; }
        result += ((long)r1)<<48;
        return result;
    }
    
    public static long ldiv(long uq, long vq) {
        boolean neg = false;
        if (uq < 0L) { uq = -uq; neg = !neg; }
        if (vq < 0L) { vq = -vq; neg = !neg; }
        uq = uldivrem(uq, vq, false);
        if (neg) uq = -uq;
        return uq;
    }

    public static long lrem(long uq, long vq) {
        boolean neg = false;
        if (uq < 0L) { uq = -uq; neg = !neg; }
        if (vq < 0L) { vq = -vq; /*neg = !neg;*/ }
        uq = uldivrem(uq, vq, true);
        if (neg) uq = -uq;
        return uq;
    }

    private static int HHALFQ(long v) { return (int)(v>>32); }
    private static int LHALFQ(long v) { return (int)v; }
    private static char HHALF(int v) { return (char)(v>>16); }
    private static char LHALF(int v) { return (char)v; }
    private static int LHUP(int v) { return v<<16; }
    private static /*unsigned*/int COMBINE(char hi, char lo) { return (hi<<16) | lo; }
    private static /*unsigned*/long COMBINEQ(/*unsigned*/ int hi, /*unsigned*/ int lo) {
        return (((long)hi)<<32) | (((long)lo) & 0xFFFFFFFFL);
    }

    /*
     * Multiprecision divide.  This algorithm is from Knuth vol. 2 (2nd ed),
     * section 4.3.1, pp. 257--259.
     *
     * NOTE: the version here is adapted from NetBSD C source code (author unknown).
     */
    public static /*unsigned*/long uldivrem(/*unsigned*/long uq, /*unsigned*/long vq, boolean rem) {
        final int HALF_BITS = 16;
        final int B = 1 << HALF_BITS;
        
        char u[], v[], q[];
        char v1, v2;
        /*unsigned*/int qhat, rhat, t;
        int m, n, d, j, i;
        int ui=0, vi=0, qi=0;
        if (vq == 0) throw new ArithmeticException();
        if (ulcmp(uq, vq)) {
            if (rem) return uq;
            return 0L;
        }
        u = new char[5];
        v = new char[5];
        q = new char[5];
        /*
         * Break dividend and divisor into digits in base B, then
         * count leading zeros to determine m and n.  When done, we
         * will have:
         *      u = (u[1]u[2]...u[m+n]) sub B
         *      v = (v[1]v[2]...v[n]) sub B
         *      v[1] != 0
         *      1 < n <= 4 (if n = 1, we use a different division algorithm)
         *      m >= 0 (otherwise u < v, which we already checked)
         *      m + n = 4
         * and thus
         *      m = 4 - n <= 2
         */
        u[0] = 0;
        u[1] = HHALF(HHALFQ(uq));
        u[2] = LHALF(HHALFQ(uq));
        u[3] = HHALF(LHALFQ(uq));
        u[4] = LHALF(LHALFQ(uq));
        v[1] = HHALF(HHALFQ(vq));
        v[2] = LHALF(HHALFQ(vq));
        v[3] = HHALF(LHALFQ(vq));
        v[4] = LHALF(LHALFQ(vq));
        for (n = 4; v[vi+1] == 0; vi++) {
            if (--n == 1) {
                /*unsigned*/int rbj;    /* r*B+u[j] (not root boy jim) */
                char q1, q2, q3, q4;

                /*
                 * Change of plan, per exercise 16.
                 *      r = 0;
                 *      for j = 1..4:
                 *              q[j] = floor((r*B + u[j]) / v),
                 *              r = (r*B + u[j]) % v;
                 * We unroll this completely here.
                 */
                t = v[vi+2];    /* nonzero, by definition */
                q1 = (char)udiv(u[ui+1], t);
                rbj = COMBINE((char)urem(u[ui+1], t), u[ui+2]);
                q2 = (char)udiv(rbj, t);
                rbj = COMBINE((char)urem(rbj, t), u[ui+3]);
                q3 = (char)udiv(rbj, t);
                rbj = COMBINE((char)urem(rbj, t), u[ui+4]);
                q4 = (char)udiv(rbj, t);
                if (rem) return (long)urem(rbj, t);
                return COMBINEQ(COMBINE(q1, q2), COMBINE(q3, q4));
            }
        }

        /*
         * By adjusting q once we determine m, we can guarantee that
         * there is a complete four-digit quotient at &qspace[1] when
         * we finally stop.
         */
        for (m = 4 - n; u[ui+1] == 0; ++ui)
            m--;
        for (i = 4 - m; --i >= 0;)
            q[qi+i] = 0;
        qi += 4 - m;

        /*
         * Here we run Program D, translated from MIX to C and acquiring
         * a few minor changes.
         *
         * D1: choose multiplier 1 << d to ensure v[1] >= B/2.
         */
        d = 0;
        for (t = v[vi+1]; ucmp(t, B/2); t <<= 1)
            d++;
        if (d > 0) {
            shl(u, ui, m + n, d);               /* u <<= d */
            shl(v, vi+1, n - 1, d);             /* v <<= d */
        }
        /*
         * D2: j = 0.
         */
        j = 0;
        v1 = v[vi+1];   /* for D3 -- note that v[1..n] are constant */
        v2 = v[vi+2];   /* for D3 */
        do {
            char uj0, uj1, uj2;
                
            /*
             * D3: Calculate qhat (\^q, in TeX notation).
             * Let qhat = min((u[j]*B + u[j+1])/v[1], B-1), and
             * let rhat = (u[j]*B + u[j+1]) mod v[1].
             * While rhat < B and v[2]*qhat > rhat*B+u[j+2],
             * decrement qhat and increase rhat correspondingly.
             * Note that if rhat >= B, v[2]*qhat < rhat*B.
             */
            uj0 = u[ui+j + 0];  /* for D3 only -- note that u[j+...] change */
            uj1 = u[ui+j + 1];  /* for D3 only */
            uj2 = u[ui+j + 2];  /* for D3 only */
            boolean sim_goto = false;
            if (uj0 == v1) {
                qhat = B;
                rhat = uj1;
                //goto qhat_too_big;
                sim_goto = true;
            } else {
                /*unsigned*/int n2 = COMBINE(uj0, uj1);
                qhat = udiv(n2, v1);
                rhat = urem(n2, v1);
            }
            while (sim_goto || (ucmp(COMBINE((char)rhat, uj2), (int)umul(v2, qhat)))) {
        //qhat_too_big:
                sim_goto = false;
                qhat--;
                if ((rhat += v1) >= B)
                    break;
            }
            /*
             * D4: Multiply and subtract.
             * The variable `t' holds any borrows across the loop.
             * We split this up so that we do not require v[0] = 0,
             * and to eliminate a final special case.
             */
            for (t = 0, i = n; i > 0; i--) {
                t = u[ui+i + j] - (int)umul(v[vi+i], qhat) - t;
                u[ui+i + j] = LHALF(t);
                t = (B - HHALF(t)) & (B - 1);
            }
            t = u[ui+j] - t;
            u[ui+j] = LHALF(t);
            /*
             * D5: test remainder.
             * There is a borrow if and only if HHALF(t) is nonzero;
             * in that (rare) case, qhat was too large (by exactly 1).
             * Fix it by adding v[1..n] to u[j..j+n].
             */
            if (HHALF(t) != 0) {
                qhat--;
                for (t = 0, i = n; i > 0; i--) { /* D6: add back. */
                    t += u[ui+i + j] + v[vi+i];
                    u[ui+i + j] = LHALF(t);
                    t = HHALF(t);
                }
                u[ui+j] = LHALF(u[ui+j] + t);
            }
            q[qi+j] = (char)qhat;
        } while (++j <= m);             /* D7: loop on j. */

        /*
         * If caller wants the remainder, we have to calculate it as
         * u[m..m+n] >>> d (this is at most n digits and thus fits in
         * u[m+1..m+n], but we may need more source digits).
         */
        if (rem) {
            if (d != 0) {
                for (i = m + n; i > m; --i)
                    u[ui+i] = (char)((u[ui+i] >>> d) |
                        LHALF(u[ui+i - 1] << (HALF_BITS - d)));
                u[ui+i] = 0;
            }
            return COMBINEQ(COMBINE(u[1], u[2]), COMBINE(u[3], u[4]));
        }
        return COMBINEQ(COMBINE(q[1], q[2]), COMBINE(q[3], q[4]));
    }
    private static void shl(char[] p, int off, int len, int sh)
    {
        int i;
        final int HALF_BITS = 16;
        for (i = 0; i < len; i++)
                p[off+i] = (char) (LHALF(p[off+i] << sh) | (p[off+i + 1] >>> (HALF_BITS - sh)));
        p[off+i] = LHALF(p[off+i] << sh);
    }

    // greatest double that can be rounded to an int
    public static double maxint = (double)Integer.MAX_VALUE;
    // least double that can be rounded to an int
    public static double minint = (double)Integer.MIN_VALUE;
  
    // greatest double that can be rounded to a long
    public static double maxlong = (double)Long.MAX_VALUE;
    // least double that can be rounded to a long
    public static double minlong = (double)Long.MIN_VALUE;
    
    public static final jq_Class _class;
    public static final jq_StaticMethod _lmul;
    public static final jq_StaticMethod _ldiv;
    public static final jq_StaticMethod _lrem;
    public static final jq_StaticField _maxint;
    public static final jq_StaticField _minint;
    public static final jq_StaticField _maxlong;
    public static final jq_StaticField _minlong;
    static {
        _class = (jq_Class)PrimordialClassLoader.loader.getOrCreateBSType("Ljoeq/Runtime/MathSupport;");
        _lmul = _class.getOrCreateStaticMethod("lmul", "(JJ)J");
        _ldiv = _class.getOrCreateStaticMethod("ldiv", "(JJ)J");
        _lrem = _class.getOrCreateStaticMethod("lrem", "(JJ)J");
        _maxint = _class.getOrCreateStaticField("maxint", "D");
        _minint = _class.getOrCreateStaticField("minint", "D");
        _maxlong = _class.getOrCreateStaticField("maxlong", "D");
        _minlong = _class.getOrCreateStaticField("minlong", "D");
    }
    
}
