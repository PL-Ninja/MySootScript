package pack;

import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.Iterator;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-08 13:40
 **/


public class ReachingDefinationsAnalysis extends ForwardFlowAnalysis {
    private FlowSet emptySet;
    public ReachingDefinationsAnalysis(DirectedGraph g){
        super(g);
        this.emptySet = new ArraySparseSet();
        doAnalysis();
    }


    @Override
    protected Object newInitialFlow() {
        return emptySet.clone();
    }

    @Override
    protected Object entryInitialFlow() {
        return emptySet.clone();
    }

    @Override
    protected void merge(Object in1, Object in2, Object out) {
        FlowSet inSeta = (FlowSet)in1,
                inSetb = (FlowSet)in2,
                outSet = (FlowSet)out;

        inSeta.union(inSetb,outSet);
    }

    @Override
    protected void copy(Object src, Object dest) {
        FlowSet srcSet = (FlowSet)src,
                outSet = (FlowSet)dest;

        srcSet.copy(outSet);
    }

    @Override
    protected void flowThrough(Object src, Object u, Object dest) {
        //transfer function
        FlowSet srcSet = (FlowSet)src;
        FlowSet destSet = (FlowSet)dest;
        if(u instanceof Unit){
            // 1.1 kill leftOp
            kill(srcSet,(Unit)u,destSet);
            // 1.2 gen leftOps
            gen(destSet,(Unit)u);
        }
    }

    private void kill(FlowSet inSet, Unit u, FlowSet outSet) {
        FlowSet<ValueBox> kills = emptySet.clone();
        Iterator<ValueBox> defIt = u.getDefBoxes().iterator();
        while(defIt.hasNext()){
            ValueBox defBox = defIt.next();
            defBox.getValue().apply(new AbstractJimpleValueSwitch() {
                @Override
                public void caseLocal(Local v) {
                    Iterator inIt = inSet.iterator();
                    while(inIt.hasNext()){
                        Local inValue = (Local) defIt.next();
                        if(inValue.equivTo(defBox.getValue())){
                            kills.add((ValueBox) defBox.getValue());
                        }
                    }
                }
            });
        }

        inSet.difference(kills, outSet);
    }

    private void gen(FlowSet destSet, Unit u) {
        Iterator<ValueBox> it = u.getDefBoxes().iterator();
        while(it.hasNext()){
            ValueBox defBox = it.next();
            defBox.getValue().apply(new AbstractJimpleValueSwitch() {
                @Override
                public void caseLocal(Local v) {
                    destSet.add(defBox.getValue());
                }
            });
        }
    }

}
