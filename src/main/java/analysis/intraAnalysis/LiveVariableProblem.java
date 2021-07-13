package analysis.intraAnalysis;

import config.SootConfig;
import pack.SimpleLiveVariablesAnalysis;
import soot.*;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.TrapUnitGraph;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-10 17:29
 **/


public class LiveVariableProblem {
    public static String className = "Demo.LiveVariableExample";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        varialbleAnalysis();
        PackManager.v().writeOutput();
    }

    private static void varialbleAnalysis() {
        SootClass sc = Scene.v().getSootClass(className);
        for (SootMethod method : sc.getMethods()) {
            if(!"<init>".equals(method.getName())){
                System.out.println("Method : " + method.getName());
                System.out.println("==================");
                JimpleBody body = (JimpleBody) method.retrieveActiveBody();
                TrapUnitGraph g = new TrapUnitGraph(body);
                SimpleLiveVariablesAnalysis lva = new SimpleLiveVariablesAnalysis(g);
                for (Unit unit : body.getUnits()) {
                    System.out.println(unit.toString() + "[entry]" +lva.getFlowBefore(unit));
                    System.out.println(unit.toString() + "[exit]" +lva.getFlowBefore(unit));
                }
                System.out.println("==================");
            }
        }
    }


}
