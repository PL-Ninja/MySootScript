package Graph;

import Config.SootConfig;
import Pack.MyAnalysis;
import soot.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: MySootScript
 * @description: callgraoh parse by soot
 * @author: 0range
 * @create: 2021-04-28 19:38
 **/


public class SootParseCallGraph {

    public static final String className = "Demo.CGDemo";

    public static final String methodName = "begin";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        parseCG();
    }

    public static void parseCG() {
        SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);
        SootMethod srcMethod = sc.getMethodByName(methodName);
        List<SootMethod> entrypoint = new ArrayList<>();
        entrypoint.add(srcMethod);
        Scene.v().setEntryPoints(entrypoint);

        // my own pack
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myRecorder", new MyAnalysis(className,methodName)));

        // use my own pack
        PackManager.v().runPacks();




    }

}
