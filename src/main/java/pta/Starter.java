package pta;

import config.SootConfig;
import soot.PackManager;
import soot.Scene;
import soot.Transform;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-17 17:21
 **/


public class Starter {

    public static final String className = "pta.ptademo.Hello";

    public static void main(String[] args) {
        SootConfig.setupSoot(className);

        String sootClassPath = Scene.v().getSootClassPath();
        // 打印soot的classpath
        System.out.println("sootClassPath = " + sootClassPath);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.MyPTA",new MyPTATransformer()));

        PackManager.v().runPacks();
    }
}
