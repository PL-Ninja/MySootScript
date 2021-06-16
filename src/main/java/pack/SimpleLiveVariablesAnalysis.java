package pack;

import flowsets.LiveVariableFlowSet;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

import java.util.Iterator;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-01 17:44
 **/


public class SimpleLiveVariablesAnalysis extends BackwardFlowAnalysis<Unit, LiveVariableFlowSet>{

    private LiveVariableFlowSet emptySet;

    public SimpleLiveVariablesAnalysis(DirectedGraph graph) {
        super(graph);
        // do fixed-point
        doAnalysis();
    }


    @Override
    protected LiveVariableFlowSet newInitialFlow() {
        return new LiveVariableFlowSet();
    }

    @Override
    protected LiveVariableFlowSet entryInitialFlow() {
        return new LiveVariableFlowSet();
    }


    @Override
    protected void merge(LiveVariableFlowSet srcSet1, LiveVariableFlowSet srcSet2, LiveVariableFlowSet destSet) {
        // union
        srcSet1.union(srcSet2,srcSet2);
    }

    @Override
    protected void copy(LiveVariableFlowSet srcSet, LiveVariableFlowSet destSet) {
        //backward copy
        srcSet.copy(destSet);

    }

    @Override
    protected void flowThrough(LiveVariableFlowSet srcSet, Unit u, LiveVariableFlowSet destSet) {
        //transfer function
        // 1.1 kill leftOp
        kill(srcSet,u,destSet);
        // 1.2 gen rightOps
        gen(u,destSet);

        // 2. something more beauty
        // first kill all Locals
        // Second gen use guys
//        FlowSet<Local> killSet = (FlowSet<Local>) new ArraySet<Local>();
//        for (ValueBox useAndDefBox : u.getUseAndDefBoxes()) {
//            useAndDefBox.getValue().apply(new AbstractJimpleValueSwitch() {
//                @Override
//                public void caseLocal(Local v) {
//                    killSet.add((Local)useAndDefBox.getValue());
//                }
//            });
//        }
//        src.intersection(killSet, dest);
//        for (ValueBox useBox : u.getUseBoxes()) {
//            useBox.getValue().apply(new AbstractJimpleValueSwitch() {
//                @Override
//                public void caseLocal(Local v) {
//                    dest.add((Local)useBox.getValue());
//                }
//            });
//        }

        // 3. something basical
        // first kill leftOp
        // second gen rightOps
//        for (ValueBox defBox : u.getDefBoxes()) {
//            defBox.getValue().apply(new AbstractJimpleValueSwitch() {
//                @Override
//                public void caseLocal(Local v) {
//                   dest.remove(defBox.getValue());
//                }
//            });
//        }
//
//        for (ValueBox useBox : u.getUseBoxes()) {
//            useBox.getValue().apply(new AbstractJimpleValueSwitch() {
//                @Override
//                public void caseLocal(Local v) {
//                    dest.add(useBox.getValue());
//                }
//            });
//        }



    }


    private void kill(LiveVariableFlowSet inSet, Unit u, LiveVariableFlowSet destSet) {
        LiveVariableFlowSet kills = new LiveVariableFlowSet();
        Iterator<ValueBox> defIt = u.getDefBoxes().iterator();
        while(defIt.hasNext()){
            ValueBox defBox = defIt.next();
            defBox.getValue().apply(new AbstractJimpleValueSwitch() {
                @Override
                public void caseLocal(Local v) {
                    Iterator inIt = inSet.iterator();
                    while(inIt.hasNext()){
                        Local inValue = (Local)inIt.next();
                        if(inValue.equivTo(defBox.getValue())){
                            kills.add((Local) defBox.getValue());
                        }
                    }
                }
            });
        }
        // inSet - kills = destSet
        inSet.difference(kills,destSet);
    }

    private void gen(Unit u, LiveVariableFlowSet destSet) {
        Iterator<ValueBox> it = u.getUseBoxes().iterator();
        while(it.hasNext()){
            ValueBox useBox = it.next();
            useBox.getValue().apply(new AbstractJimpleValueSwitch() {
                @Override
                public void caseLocal(Local v) {
                    destSet.add((Local) useBox.getValue());
                }
            });
        }
    }
}
