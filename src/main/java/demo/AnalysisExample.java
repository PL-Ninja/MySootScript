package demo;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-29 10:47
 **/


public class AnalysisExample {
    public void methodA(int n){
        String s = String.valueOf(n);
        methodC(s);
    }

    public void methodB(int n){
        String s = String.valueOf(n);
        methodC(s);
    }

    public void methodC(String str){
        System.out.println(str);
    }
}
