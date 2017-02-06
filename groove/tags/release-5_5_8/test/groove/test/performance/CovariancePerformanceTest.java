/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.test.performance;

/** Test to show the performance hit of overriding with return type covariance. */
public class CovariancePerformanceTest {
    /** Main method. */
    static public void main(String[] args) {
        System.out.println("Covariance test (m1)");
        test1(new A());
        test1(new B());
        System.out.println("Overriding with super test (m2)");
        test2(new A());
        test2(new B());
        System.out.println("Overriding w/o super test (m3)");
        test3(new A());
        test3(new B());
        System.out.println("Casting test (m3)");
        test4(new A());
        test4(new B());
    }

    static void test1(A a) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            for (int j = 0; j < 100; j++) {
                a.m1();
            }
        }
        System.out.printf("%s: %d%n", a.getClass()
            .getName(), System.currentTimeMillis() - time);
    }

    static void test2(A a) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            for (int j = 0; j < 100; j++) {
                a.m2();
            }
        }
        System.out.printf("%s: %d%n", a.getClass()
            .getName(), System.currentTimeMillis() - time);
    }

    static void test3(A a) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            for (int j = 0; j < 100; j++) {
                a.m3();
            }
        }
        System.out.printf("%s: %d%n", a.getClass()
            .getName(), System.currentTimeMillis() - time);
    }

    static void test4(A a) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            for (int j = 0; j < 100; j++) {
                a.m4();
            }
        }
        System.out.printf("%s: %d%n", a.getClass()
            .getName(), System.currentTimeMillis() - time);
    }

    static class A {
        A m1() {
            this.i = this.i + 1;
            return this.a;
        }

        A m2() {
            this.i = this.i + 1;
            return this.a;
        }

        A m3() {
            this.i = this.i + 1;
            return this.a;
        }

        A m4() {
            this.i = this.i + 1;
            return this.a;
        }

        private int i;
        private A a = this;
    }

    static class B extends A {
        @Override
        B m1() {
            return (B) super.m1();
        }

        @Override
        A m2() {
            return super.m2();
        }

        @Override
        B m3() {
            this.i = this.i + 1;
            return this.c;
        }

        @Override
        A m4() {
            this.i = this.i + 1;
            return this.b;
        }

        private int i;
        private A b = this;
        private B c = this;
    }
}
