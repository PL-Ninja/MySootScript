package basic;

import config.SootConfig;
import utils.SootUtil;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.List;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 10:10
 **/


public class ParseMethod {
    private static String className = "demo.Circle";

    public static void main(String[] args) {
        SootConfig.setupSoot(className);
        getMethodInfo();
    }

    private static void getMethodInfo() {
        SootClass sc = Scene.v().getSootClass(className);
        List<SootMethod> methods = sc.getMethods();
        System.out.println("[class name] = " + sc.getName());
        System.out.println("[method] = " + sc.getMethodCount());
        System.out.println("---------------------------------");
        for (SootMethod method : methods) {
            System.out.println("[declaring class] = " + method.getDeclaringClass());
            String signature = method.getSignature();
            System.out.println("[modify type] = " + SootUtil.checkModifiers(method.getModifiers()));
            //<Circle: int area(boolean)>
            System.out.println("[signature] = " + signature);
            String subSignature = method.getSubSignature();
            //int area(boolean)
            System.out.println("[sub signature] = " + subSignature);
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        }
    }
}
