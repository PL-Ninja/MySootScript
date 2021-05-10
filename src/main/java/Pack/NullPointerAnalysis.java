package Pack;

import FlowSets.NullPointerFlowSets;
import soot.Local;
import soot.Unit;
import soot.jimple.*;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * @program: MySootScript
 * @description:
 * @author: Dr.Navid
 * @create: 2021-05-10 15:11
 **/


public class NullPointerAnalysis extends ForwardFlowAnalysis<Unit, NullPointerFlowSets> {


    public enum AnalysisMode {MUST, MAY_P, MAY_O}

    public AnalysisMode analysisMode;

    public NullPointerAnalysis(DirectedGraph graph, AnalysisMode analysisMode) {
        super(graph);
        this.analysisMode = analysisMode;
        doAnalysis();
    }

    @Override
    protected NullPointerFlowSets newInitialFlow() {
        return new NullPointerFlowSets();
    }

    @Override
    protected void flowThrough(NullPointerFlowSets inSet, Unit unit, NullPointerFlowSets outSet) {
        inSet.copy(outSet);
        kill(inSet, unit, outSet);
        gen(inSet, unit, outSet);
    }

    @Override
    protected void merge(NullPointerFlowSets inSet1, NullPointerFlowSets inSet2, NullPointerFlowSets outSet) {
        if(analysisMode != AnalysisMode.MUST) {
            inSet1.union(inSet2, outSet);
        } else {
            inSet1.intersection(inSet2, outSet);
        }
    }

    @Override
    protected void copy(NullPointerFlowSets source, NullPointerFlowSets dest) {
        source.copy(dest);
    }

    protected void kill(NullPointerFlowSets inSet, Unit unit, NullPointerFlowSets outSet){
        unit.apply(new AbstractStmtSwitch() {
            @Override
            public void caseAssignStmt(AssignStmt stmt) {
                Local leftOp = (Local) stmt.getLeftOp();
                outSet.remove(leftOp);
            }
        });
    }

    protected void gen(NullPointerFlowSets inSet, Unit unit, NullPointerFlowSets outSet){
        unit.apply(new AbstractStmtSwitch() {
            @Override
            public void caseAssignStmt(AssignStmt stmt) {
                Local leftOp = (Local) stmt.getLeftOp();
                stmt.getRightOp().apply(new AbstractJimpleValueSwitch() {
                    @Override
                    public void caseLocal(Local v) {
                        if (inSet.contains(v)) {
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
