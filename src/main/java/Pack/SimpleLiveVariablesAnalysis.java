package Pack;

import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.AbstractRefSwitch;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;
import soot.util.ArraySet;

import java.util.Iterator;
import java.util.List;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-01 17:44
 **/


public class SimpleLiveVariablesAnalysis extends BackwardFlowAnalysis{

    private FlowSet emptySet;

    public SimpleLiveVariablesAnalysis(DirectedGraph graph) {
        super(graph);
        this.emptySet = new ArraySparseSet();
        // do fixed-point
        doAnalysis();
    }


    @Override
    protected Object newInitialFlow() {
        return emptySet.emptySet();
    }

    @Override
    protected Object entryInitialFlow() {
        return emptySet.emptySet();
    }


    @Override
    protected void merge(Object srcSet1, Object srcSet2, Object destSet) {
        FlowSet in1 = (FlowSet)srcSet1;
        FlowSet in2 = (FlowSet)srcSet2;
        FlowSet out = (FlowSet)destSet;
        // union
        in1.union(in2,out);
    }

    @Override
    protected void copy(Object srcSet, Object destSet) {
        //backward copy
        FlowSet src = (FlowSet)srcSet;
        FlowSet dest = (FlowSet)destSet;
        src.copy(dest);

    }

    @Override
    protected void flowThrough(Object srcSet, Object o, Object destSet) {
        //transfer function
        FlowSet src = (FlowSet)srcSet;
        FlowSet dest = (FlowSet)destSet;
        Unit u = (Unit)o;
        // 1.1 kill leftOp
        kill(src,u,dest);
        // 1.2 gen rightOps
        gen(u,dest);

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


    private void kill(FlowSet inSet, Unit u, FlowSet destSet) {
        FlowSet kills = emptySet.clone();
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
                            kills.add(defBox.getValue());
                        }
                    }
                }
            });
        }
        // inSet - kills = destSet
        inSet.difference(kills,destSet);
    }

    private void gen(Unit u, FlowSet destSet) {
        Iterator<ValueBox> it = u.getUseBoxes().iterator();
        while(it.hasNext()){
            ValueBox useBox = it.next();
            useBox.getValue().apply(new AbstractJimpleValueSwitch() {
                @Override
                public void caseLocal(Local v) {
                    destSet.add(useBox.getValue());
                }
            });
        }
    }
}
