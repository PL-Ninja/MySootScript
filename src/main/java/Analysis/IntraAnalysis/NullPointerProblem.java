package Analysis.IntraAnalysis;

import Config.SootConfig;
import soot.*;
import soot.jimple.JimpleBody;

/**
 * @program: MySootScript
 * @description: view Dr. Navid
 * @author: 0range
 * @create: 2021-04-29 15:54
 **/


public class NullPointerProblem {
    public static String className = "Demo.NullPointerExample";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        findNullPointer();
    }

    public static void findNullPointer() {
        SootClass sc = Scene.v().getSootClass(className);
        for (SootMethod method : sc.getMethods()) {
            System.out.println("Method: " + method.getSignature());
            JimpleBody body = (JimpleBody)method.retrieveActiveBody();

        }
    }
}
