package analysis.InterAnalysis;

import config.SootConfig;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-29 15:48
 **/


public class FindMethodCaller {
    public static final String className = "Demo.AnalysisExample";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        findMCaller();
    }

    public static void findMCaller() {
        SootClass sc = Scene.v().getSootClass(className);

        // 查看哪些方法调用了C方法
        SootMethod method = sc.getMethod("void methodC(java.lang.String)");
        System.out.println("Analysis the method [" + method.getName() + "] from class " + "{"+sc.getName() + "}");

        // 遍历类中的方法
        for(SootMethod m : sc.getMethods()){
            List<Stmt> stmtList = new ArrayList<>();
            JimpleBody body = (JimpleBody)m.retrieveActiveBody();
            //遍历方法的每一个语句
            for(Unit unit :body.getUnits()){
                Stmt stmt = (Stmt)unit;
                //如果当前语句调用了methodC
                if(doesinvoke(stmt,method)){
                    stmtList.add(stmt);
                }
            }


            if(stmtList.size() > 0){
                System.out.println("============================");
                System.out.println(stmtList.size() + " Usage(s) found in the method " + m.getSignature());
                for(Stmt stmt : stmtList){
                    InvokeExpr invokeExpr = stmt.getInvokeExpr();
                    System.out.println("invokeExpr = " + invokeExpr);
                    //获取参数个数 参数
                    int parameterCount = invokeExpr.getMethod().getParameterCount();
                    List<Value> args = invokeExpr.getArgs();
                    System.out.println("args count = " + parameterCount);
                    System.out.println("args = " + args);

                    invokeExpr.apply(new AbstractExprSwitch() {
                        @Override
                        public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                            //receiver object & it's class & method's type
                            System.out.println("receiver object = " + v.getBase());
                            System.out.println("receiver object's class = " + v.getBase().getType());
                            System.out.println("method ref = " + v.getMethodRef());
                        }
                    });

                }
            }
        }


    }

    public static boolean doesinvoke(final Unit u, final SootMethod method) {
        final AtomicBoolean result = new AtomicBoolean(false);
        u.apply(new AbstractStmtSwitch() {
            @Override
            public void caseInvokeStmt(InvokeStmt stmt) {
                if(stmt.getInvokeExpr().getMethod().equals(method)){
                    result.set(true);
                };
            }
        });
        return result.get();
    }
}
