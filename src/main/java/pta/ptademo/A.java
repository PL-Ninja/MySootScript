package pta.ptademo;



/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-17 17:23
 **/


public class A {
    public int i = 5;
    public B f = new B();
    public B g = new B();
    public B h;

    public A() {
    }

    public A(B b) {
        this.f = b;
    }

    public B getF() {
        return this.f;
    }

    public B getH() {
        return this.h;
    }

    public B id(B b) {
        return b;
    }
}
