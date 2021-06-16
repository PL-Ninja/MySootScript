package pta.ptademo;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-17 17:24
 **/


public class Hello {
    public static void main(String[] args) {
        //Benchmark.alloc(1);
        A a = new A();
        //Benchmark.alloc(2);
        A b = new A();
        //Benchmark.alloc(3);
        A c = new A();
//        if (args.length > 1) {
//            a = b;
//        }

//        if (args.length > 2) {
//            c = a;
//        }

        a=b;
        c=a;
//
        c.i = 6;
//



        //Benchmark.test(1, a);
        //Benchmark.test(2, b);
        //Benchmark.test(3, c);
    }
}
