package demo;

import java.io.IOException;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 20:58
 **/


public class CGDemo {
    public String begin(String args) throws IOException {
        String cmd = new A().method1(args);
        return new B().method2(cmd);
    }
}

class A{
    public String method1(String param) {
        return param;
    }
}

class B {
    public String method2(String param) {
        return new C().method3(param);
    }
}

class C {
    public String method3(String param) {
        return param;
    }
}
