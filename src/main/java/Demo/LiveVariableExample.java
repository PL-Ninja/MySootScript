package Demo;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-10 17:20
 **/


public class LiveVariableExample {
    public static void main(String[] args) {
        int a = 0;
        int b = 95;
        func1(a);
        func1(b);
    }

    public static void func1 (int x) {
        if(x != 0){
            while(x % 4 != 0){
                x = x/4;
            }
        }
    }
}
