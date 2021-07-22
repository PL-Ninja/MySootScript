package demo;

import java.io.IOException;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-07-20 17:30
 **/


public class IFDSDemo {
//    public static void main(String[] args) {
//        String t = "taint";
//        String cmd = new AA().method1(t);
//        String str = new BB().method2(cmd);
//        System.out.println("str = " + str);
//    }
//    static class AA{
//        public String method1(String param) {
//            return param;
//        }
//    }
//    static class BB {
//        public String method2(String param) {
//            return new CC().method3(param);
//        }
//    }
//    static class CC {
//        public String method3(String param) {
//            return param;
//        }
//    }

    public static void main(String[] args) {
        int x = secret();
        int y = 0;
        y = foo(x);
        print(y);
    }

    private static void print(int y) {
        System.out.println(y);
    }

    private static int secret() {
        return 88;
    }
    private static int foo(int p){
        return p;
    }
}







