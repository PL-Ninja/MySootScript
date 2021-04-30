package Utils.NullPointerUtils;

import heros.flowfunc.Kill;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-29 16:48
 **/


public class NullPointerAnalysis extends ForwardFlowAnalysis<Unit,NullFlowSet> {

    enum AnalysisMode {MUST, MAY_P, MAY_O}

    AnalysisMode analysisMode;

    public NullPointerAnalysis(DirectedGraph<Unit> graph,AnalysisMode analysisMode) {
        super(graph);
        this.analysisMode = analysisMode;
        doAnalysis();
    }

    @Override
    protected NullFlowSet newInitialFlow() {
        return new NullFlowSet();
    }

    @Override
    protected void copy(NullFlowSet source, NullFlowSet dest) {
        // source -> dest
        source.copy(dest);
    }

    @Override
    protected void flowThrough(NullFlowSet inSet, Unit unit, NullFlowSet outSet) {
        inSet.copy(outSet);
        kill(inSet,unit,outSet);
        generate(inSet, unit, outSet);
    }

    protected void kill(NullFlowSet inSet, Unit unit, NullFlowSet outSet) {
        // 赋值语句
        unit.apply(new AbstractStmtSwitch() {
            @Override
            public void caseAssignStmt(AssignStmt stmt) {
                //获取左边变量
                Local leftOp = (Local)stmt.getLeftOp();
                //把它从outSet中取走
                outSet.remove(leftOp);
            }
        });
    }


    @Override
    protected void merge(NullFlowSet inSet1, NullFlowSet inSet2, NullFlowSet outSet) {
        if(analysisMode != AnalysisMode.MUST){
            // inSet1 u inSet2 = outSet
            inSet1.union(inSet2,outSet);
        }else{
            // inSet1 n inSet2 = outSet
            inSet1.intersection(inSet2, outSet);
        }
    }

    protected void generate(NullFlowSet inSet, Unit unit, NullFlowSet outSet){
        unit.apply(new AbstractStmtSwitch() {

            @Override
            public void caseAssignStmt(AssignStmt stmt) {
                Local leftOp = (Local)stmt.getLeftOp();
                stmt.getRightOp().apply(new AbstractJimpleValueSwitch() {
                    @Override
                    public void caseLocal(Local v) {
                        // local val
                        if(inSet.contains(v)){
                            outSet.add(leftOp);
                        }
                    }

                    @Override
                    public void caseNullConstant(NullConstant v) {
                        outSet.add(leftOp);
                    }

                    @Override
                    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
                        if(analysisMode == AnalysisMode.MAY_P) {
                            outSet.add(leftOp);
                        }
                    }

                    @Override
                    public void caseStaticInvokeExpr(StaticInvokeExpr v) {
                        if(analysisMode == AnalysisMode.MAY_P) {
                            outSet.add(leftOp);
                        }
                    }

                    @Override
                    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                        if(analysisMode == AnalysisMode.MAY_P) {
                            outSet.add(leftOp);
                        }
                    }

                });
            }

            @Override
            public void caseIdentityStmt(IdentityStmt stmt) {

                Local leftOp = (Local) stmt.getLeftOp();
                if(analysisMode == AnalysisMode.MAY_P) {
                    if(!(stmt.getRightOp() instanceof ThisRef)) {
                        outSet.add(leftOp);
                    }
                }
            }
        });
    }



}
