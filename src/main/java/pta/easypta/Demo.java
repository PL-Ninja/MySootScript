package pta.easypta;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-22 14:57
 **/


public class Demo {
    public Demo f;
    public static void main(String[] args) {
        Demo a = new Demo();
        Demo b = new Demo();
        Demo c = new Demo();
        Demo d = new Demo();
        Demo e = new Demo();

        a = b;
        c.f = a;
        d = c;
        c.f = d;
        e = d.f;
    }
}
