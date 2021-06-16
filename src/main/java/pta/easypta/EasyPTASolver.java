//package pta.easypta;
//
//import config.SootConfig;
//import heros.SolverConfiguration;
//
//import soot.*;
//import soot.jimple.*;
//import soot.toolkits.graph.BriefUnitGraph;
//import soot.toolkits.graph.UnitGraph;
//
//import java.util.*;
//
///**
// * @program: MySootScript
// * @description: only solve new、assign、store、load 不考虑上下文
// * @author: 0range
// * @create: 2021-06-22 15:00
// **/
//
//
//public class EasyPTASolver {
//    public static final String className = "pta.easypta.Demo";
//
//    public static Map<Variable, Set<Value>> workList = new LinkedHashMap<>();
//    public static Map<Value, Set<Integer>> easyWorkList = new LinkedHashMap<>();
//
//    public static void main(String[] args) {
//        SootConfig.setupSoot(className);
//        SootClass sootClass = Scene.v().getSootClass(className);
//        SootMethod entryMethod = sootClass.getMethodByName("main");
////        Solve(entryMethod);
////        for (Map.Entry<Value, Set<Integer>> entry : easyWorkList.entrySet()) {
////            Value key = entry.getKey();
////            Set<Integer> value = entry.getValue();
////            for (Integer allocId : value) {
////                System.out.println(key.getType().toString()+" "+allocId);
////            }
////        }
//
//        PointsToAnalysis pointsToAnalysis = Scene.v().getPointsToAnalysis();
//        for (Local local : entryMethod.retrieveActiveBody().getLocals()) {
//            System.out.println(local.toString());
//            PointsToSet pointsToSet = pointsToAnalysis.reachingObjects(local);
//            if(!pointsToSet.isEmpty()){
//                for (Type possibleType : pointsToSet.possibleTypes()) {
//                    System.out.println(possibleType);
//                }
//            }
//        }
//
//    }
//
//    private static void Solve(SootMethod entryMethod) {
//        // 拿到方法体
//        Body body = entryMethod.retrieveActiveBody();
//        UnitGraph graph = new BriefUnitGraph(body);
//        Unit headUnit = graph.getHeads().iterator().next();
//        for (Unit unit : body.getUnits()) {
//            solveUnit(unit);
//        }
////        // 从第一个unit开始解
////        solveBlock(headUnit, graph);
//
//
//    }
//
//    private static void solveBlock(Unit unit, UnitGraph graph){
//
////        while(true){
////            solveUnit(unit,graph);
////        }
//    }
//
////    public static void solveUnit(Unit unit, UnitGraph graph){
//    public static void solveUnit(Unit unit){
//        try{
//            unit.apply(new AbstractStmtSwitch() {
//                @Override
//                public void caseAssignStmt(AssignStmt stmt) {
//                    AssignStmt assignStmt = stmt;
//                    Value lop = assignStmt.getLeftOp();
//                    Value rop = assignStmt.getRightOp();
//                    if(rop instanceof AnyNewExpr){
//                        // add to Map
//                        int allodId = stmt.getJavaSourceStartLineNumber();
//                        Set<Integer> allocIdSet = easyWorkList.getOrDefault(lop, new LinkedHashSet<>());
//                        allocIdSet.add(allodId);
//                        easyWorkList.put(lop, allocIdSet);
//                    }
//                }
//            });
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//
//
//}
