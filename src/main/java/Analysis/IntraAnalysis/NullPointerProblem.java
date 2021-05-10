package Analysis.IntraAnalysis;

import Config.SootConfig;
import Pack.NullPointerAnalysis;
import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.util.ArrayList;
import java.util.List;

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
            //System.out.println("Method: " + method.getSignature());
            JimpleBody body = (JimpleBody)method.retrieveActiveBody();
            UnitGraph unitGraph = new TrapUnitGraph(body);
            List<NullPointerAnalysis> npAnalyzers = new ArrayList<>();
            npAnalyzers.add(new NullPointerAnalysis(unitGraph, NullPointerAnalysis.AnalysisMode.MUST));
            npAnalyzers.add(new NullPointerAnalysis(unitGraph, NullPointerAnalysis.AnalysisMode.MAY_O));
            npAnalyzers.add(new NullPointerAnalysis(unitGraph, NullPointerAnalysis.AnalysisMode.MAY_P));
            doNullPointerAnalysis(body,npAnalyzers);
        }
    }

    public static void doNullPointerAnalysis(JimpleBody body,List<NullPointerAnalysis> npAnalyzers){
        int unitIndex = 0;
        for (Unit unit : body.getUnits()) {
            //获取unit的use值
            for (ValueBox useBox : unit.getUseBoxes()) {
                //如果是Local
                if(useBox.getValue() instanceof Local){
                    //获取Local值
                    Local usedLocal = (Local)useBox.getValue();
                    for (NullPointerAnalysis npa : npAnalyzers) {
                        //如果在这条unit之前的set中包含了当前local
                        if(npa.getFlowBefore(unit).contains(usedLocal)){
                            System.out.println("[+]Line " + unit.getJavaSourceStartLineNumber() +": " + npa.analysisMode + " NullPointer usage of local " + usedLocal + " in unit " + unit);
                        }
                    }
                }
                //如果是调用语句 并且 调用的是空的东西
                if(unit instanceof InvokeStmt && useBox.getValue().getType().equals(NullType.v())){
                    System.out.println("[*]Line " + unit.getJavaSourceStartLineNumber() +": MUST NullPointer usage in unit (" + unitIndex +") " + unit);
                }
            }
        }
    }
}
