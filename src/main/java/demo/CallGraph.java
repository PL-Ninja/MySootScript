package demo;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-07-02 19:18
 **/


public class CallGraph {
    public static void main(String[] args) {
        doStuff();
    }

    public static void doStuff() {
        new D().foo();
    }
}

class D
{
    public void foo() {
        bar();
    }

    public void bar() {
    }
}
