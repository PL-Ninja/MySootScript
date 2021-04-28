package Graph;

import Config.SootConfig;
import Utils.CallInfo;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.BriefUnitGraph;


import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 20:21
 **/


public class BuildCallGraph {
    public static final String className = "Demo.CGDemo";

    public static final String methodName = "begin";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        buildCG();
    }

    public static void buildCG() throws Exception {

        SootClass sc = Scene.v().getSootClass(className);
        SootMethod beginmethod = sc.getMethodByName(methodName);

        List<SootMethod> WL = new ArrayList<>();
        Map<CallInfo,SootMethod> CG = new LinkedHashMap<>();
        List<SootMethod> RM = new ArrayList<>();

        WL.add(beginmethod);

        while(!WL.isEmpty()){
            SootMethod m = WL.get(0);
            WL.remove(0);
            if(!RM.contains(m)){
                RM.add(m);
                List<Stmt> callSite = getCallSite(m);
                for (Stmt stmt : callSite) {
                    List<SootMethod> T = findtargetMethods(stmt);
                    for (SootMethod method : T) {
                        Map<SootMethod,Integer> info = new LinkedHashMap<>();
                        info.put(m,stmt.getJavaSourceStartLineNumber());
                        CallInfo ci = new CallInfo(info,stmt);
                        CG.put(ci,method);
                        WL.add(method);
                    }
                }
            }
        }




        for(Map.Entry<CallInfo, SootMethod> entry : CG.entrySet()){
            for(Map.Entry<SootMethod,Integer> en : entry.getKey().info.entrySet()){
                System.out.println("{RESULT} : { (Location : " + en.getValue() + ") : " + en.getKey() + " -invoke-> " + entry.getValue() + " }");
            }
        }

        //打印输出内容
        PackManager.v().writeOutput();

    }

    public static List<Stmt> getCallSite(SootMethod m) throws Exception {
        List<Stmt> invokeExprList = new ArrayList<Stmt>();
        try{
            if(m.isConcrete()){
                JimpleBody body = (JimpleBody)m.retrieveActiveBody();
                BriefUnitGraph bug = new BriefUnitGraph(body);
                for(Unit u : bug){
                    Stmt stmt = (Stmt) u;
                    if(stmt.containsInvokeExpr()){
                        invokeExprList.add(stmt);
                    }
                }
            }
        }catch (Exception e){
            throw e;
        }
        return invokeExprList;

    }

    public static List<SootMethod> findtargetMethods(Stmt stmt){
        final List<SootMethod> targetMethodsList = new ArrayList<SootMethod>();
        final String signature = stmt.getInvokeExpr().getMethod().getSubSignature();
        stmt.getInvokeExpr().apply(new AbstractExprSwitch() {
            @Override
            public void caseStaticInvokeExpr(StaticInvokeExpr v) {
                targetMethodsList.add(v.getMethod());
            }

            @Override
            public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
                // CHA
                SootClass declaringClass = v.getMethod().getDeclaringClass();
                SootMethod dispatchMethod = Dispatch(declaringClass, signature);
                targetMethodsList.add(dispatchMethod);
            }

            @Override
            public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                // implement in CHA
                SootClass baseClass = Scene.v().getSootClass(v.getBase().getType().toString());

                Hierarchy h = Scene.v().getActiveHierarchy();
                baseClass.checkLevel(SootClass.HIERARCHY);

                List<SootClass> subClasses = new ArrayList<>(h.getSubclassesOf(baseClass));
                subClasses.add(baseClass);

                for (SootClass c : subClasses) {
                    SootMethod dispatchMethod = Dispatch(c, signature);
                    targetMethodsList.add(dispatchMethod);
                }

            }
        });
        return targetMethodsList;
    }

    public static SootMethod Dispatch(SootClass sc,String signature) {

        List<SootMethod> methods = sc.getMethods();
        List<String> signatureList = new ArrayList<>();
        for (SootMethod method : methods) {
            signatureList.add(method.getSubSignature());
        }

        for (SootMethod method : methods) {
            //1. no abstract method or interface method
            if(method.isConcrete()){
                if(method.getSubSignature().equals(signature)){
                    return method;
                }
            }
        }

        // still not found
        if("java.lang.Object".equals(sc.getName())){
            return null;
        }

        Dispatch(sc.getSuperclassUnsafe(),signature);

        return null;
    }


}
