package Graph;

import Config.SootConfig;
import soot.*;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.BriefUnitGraph;
import soot.util.dot.DotGraph;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 20:47
 **/


public class BuildCFG {

    public static final String className = "Demo.CFGDemo";

    public static final String methodName = "begin";

    public static void main(String[] args) {
        SootConfig.setupSoot(className);
        buildCFG();
    }

    public static void buildCFG() {

        SootClass sc = Scene.v().getSootClass(className);
        SootMethod beginmethod = sc.getMethodByName(methodName);
        //SootMethod beginmethod = sc.getMethod("String begin(String)");
        JimpleBody body = (JimpleBody)beginmethod.retrieveActiveBody();

        DotGraph dg = new DotGraph(methodName);
        BriefUnitGraph bug = new BriefUnitGraph(body);

        for(Unit u : bug){

        }

        //打印输出内容
        PackManager.v().writeOutput();


    }

}
