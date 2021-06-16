package pta;

import pta.toolkit.Analyzer;
import pta.toolkit.Context;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.util.List;
import java.util.Map;

import pta.toolkit.*;
import soot.util.Chain;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-17 17:46
 **/


public class MyPTATransformer extends SceneTransformer {
    @Override
    protected void internalTransform(String arg0, Map<String, String> arg1) {
        // TODO Auto-generated method stub
        try {
            Analyzer analyzer = Analyzer.getAnalyzer();
            SootMethod mainMethod = Scene.v().getSootClass(Starter.className).getMethodByName("main");
            String initialClassName = mainMethod.getDeclaringClass().getName();
            // 从mainMethod开始解， 创建主域
            solveMethod(mainMethod, Context.getInstance(analyzer, initialClassName));
            Printer.printResult("result.txt", analyzer.run());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // 根据当前方法开始解
    private void solveMethod(SootMethod method, Context contex) {
        Body body = method.getActiveBody();
        UnitGraph graph = new BriefUnitGraph(body);
        Chain<Local> locals = body.getLocals();
        String declaringClassName = method.getDeclaringClass().getName();
        Unit head = graph.getHeads().iterator().next();
        // 从第一个unit开始解
        solveBlock(head, contex, graph, locals, declaringClassName);
    }

    private void solveBlock(Unit u, Context contex, UnitGraph graph, Chain<Local> localChain, String className) {
        while (true) {
            // 解unit
            solveUnit(u, contex, localChain, className);
            List<Unit> succs = graph.getSuccsOf(u);
            int succCount = succs.size();
            if (succCount == 1) {
                u = succs.iterator().next();
            } else if (succCount > 1) {// 说明接下来会发生分支
                //遍历分支语句
                for (Unit succ : succs) {
                    String branchStmt = succ.toString();
                    if (contex.isInBranchChain(branchStmt)) {
                        // 静态分析不处理循环
                        continue;
                    }
                    solveBlock(succ, contex.createBranchScope(branchStmt), graph, localChain, className);
                }
                return;
            } else {
                return;// 执行到了函数的末尾return语句
            }
        }
    }

    /**
     * 如果unit是给local赋值的语句
     * 拿到unit的lop和rop
     * 如果rop是方法参数，将lop和rop绑定
     * 如果rop是this，将lop和this绑定
     * <p>
     * 如果unit是正常assig语句
     * 拿到unit的lop和rop
     *
     * @param u
     * @param context
     */

    private void solveUnit(Unit u, Context context, Chain<Local> localChain, String className) {
        try {
            // 拿到analyzer单例
            Analyzer analyzer = context.getAnalyzer();
            System.out.println(u.toString());
            // 如果unit是this、方法参数赋值
            if (u instanceof IdentityStmt) {
                IdentityStmt is = (IdentityStmt) u;
                Value lop = is.getLeftOp();
                Value rop = is.getRightOp();
                // 如果rop是方法参数，将lop和rop绑定
                if (rop instanceof ParameterRef) {
                    ParameterRef pr = (ParameterRef) rop;
                    context.bindArg((Local) lop, pr.getIndex());
                } else if (rop instanceof ThisRef) {// 如果rop是this，将lop和this绑定
                    context.bindThis((Local) lop);
                }
            } else if (u instanceof AssignStmt) {// 其余正常赋值
                AssignStmt as = (AssignStmt) u;
                Value lop = as.getLeftOp();
                Value rop = as.getRightOp();

                Variable rvar = null;
                // 如果rop是new语句，生成一个var变量，并且指向这里
                if (rop instanceof AnyNewExpr) {
                    rvar = context.createVariable(rop);// 创建一个引用rvar
                    analyzer.setAllocId(u.getJavaSourceStartLineNumber());// 将当前的linenumber设置为allocId
                    rvar.addAllocId(analyzer.getAllocId());
                    analyzer.setAllocId(0);// 将allocId重置为0
                } else if (rop instanceof Local) {
                    // 如果rop是local
                    rvar = context.getOrAdd((Local) rop);
                } else if (rop instanceof FieldRef) {
                    // 如果rop是fieldref
                    FieldRef rfref = (FieldRef) rop;
                    SootFieldRef rfield = rfref.getFieldRef();
                    // 如果rop是实例类型field引用
                    if (rop instanceof InstanceFieldRef) {
                        InstanceFieldRef rifref = (InstanceFieldRef) rop;
                        Local rbase = (Local) rifref.getBase();
                        Variable rbaseVar = context.getOrAdd(rbase);
                        rvar = rbaseVar.getMember().getVariable(rfield);
                    }
                } else if (rop instanceof Constant){// rop是常量
                    rvar = context.createConstant((Constant) rop);
                    analyzer.setAllocId(u.getJavaSourceStartLineNumber());
                    rvar.addAllocId(analyzer.getAllocId());
                    analyzer.setAllocId(0);
                    //return;
                }else{
                    return;
                }

                // 如果rvar不为空，开始分析lop
                if (rvar != null) {
                    // 如果lop为local，local=local
                    if (lop instanceof Local) {
                        Variable lvar = context.getOrAdd((Local) lop);//lvar a rvar b
                        lvar.assign(context,rvar,analyzer);// 将allocId，member分配给lvar
//                        analyzer.trackTransferMap(context,lvar,rvar);
                        if (localChain.contains(lop)) {
                            analyzer.addLocalCheckMap((Local) lop, lvar);// diy加入到localCheckMap<local,var>
                        }
                        // 添加a= $stack4赋值,通过$去找正常的
                        if(rop.toString().charAt(0)=='$'){
                            analyzer.addEqualMap((Local)rop,(Local)lop);
                        }
                    } else if (lop instanceof FieldRef) {
                        // 如果lop是field引用
                        FieldRef lfref = (FieldRef) lop;
                        SootFieldRef lfield = lfref.getFieldRef();
                        // 如果lop是实例field引用 a.f = b
                        if (lop instanceof InstanceFieldRef) {
                            InstanceFieldRef lifref = (InstanceFieldRef) lop;
                            Local lbaseLocal = (Local) lifref.getBase();
                            Variable lbaseVar = context.getOrAdd(lbaseLocal);
                            lbaseVar.getMember().addField(lfield, rvar,context,analyzer,lbaseVar);
//                            lbaseVar.getMember().addField(lfref, rvar,context,analyzer);
                            analyzer.trackTransferMap(context,lbaseVar,rvar);
                        }
                    }
                }

            } else if (u instanceof InvokeStmt) {// 如果unit是调用语句
                InvokeStmt is = (InvokeStmt) u;
                InvokeExpr ie = is.getInvokeExpr(); // 获取invoke Expr
                if (ie != null) {
                    SootMethod invokeMethod = ie.getMethod();
                    String methodSignature = invokeMethod.getSignature();
                    List<Value> invokeArgs = ie.getArgs(); // 参与调用的参数
                    // 如果interface、virtual、special
                    if (ie instanceof InstanceInvokeExpr) {
                        if ("<java.lang.Object: void <init>()>".equals(methodSignature)) {
                            return;
                        }
                        if (context.isInRecursion(methodSignature)) {
                            return;
                        }
                        //开始创建下一个context
                        InstanceInvokeExpr sie = (InstanceInvokeExpr) ie;
                        Local baseLocal = (Local) sie.getBase();// 拿到baseLocal
                        Variable baseVar = context.getOrAdd(baseLocal);// 基于baseLocal拿到baseVar
                        Context invokeContex = context.createInvokeContext(methodSignature, invokeArgs, baseVar); // baseVar这个时候就是thisVar,生成下一层上下文，depth+1
                        solveMethod(invokeMethod, invokeContex);
                    } else {// static invoke
                        switch (methodSignature) {
//                            case "<benchmark.internal.Benchmark: void alloc(int)>": {
                            case "<pta.ptademo.Benchmark: void alloc(int)>": {
                                int allocId = ((IntConstant) invokeArgs.get(0)).value;// 获取第0个参数，值为1，也就是分配点
                                analyzer.setAllocId(allocId); // 设置analyzer.allocId
                                break;
                            }
                            case "<pta.ptademo.Benchmark: void test(int,java.lang.Object)>": {
                                int id = ((IntConstant) invokeArgs.get(0)).value;
                                Local local = (Local) invokeArgs.get(1);
                                // 加入到查询表，分别是id和想要查询的local
                                analyzer.addQuery(id, local);
                                break;
                            }
                            default:
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

