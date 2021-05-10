package Pack;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.internal.AbstractBinopExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.lang.reflect.AccessibleObject;
import java.util.Iterator;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-08 13:13
 **/

/**
 * Forward & MUST
 */


public class AvailableExpressionAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<AbstractBinopExpr>> {

    private FlowSet<AbstractBinopExpr> emptySet;

    public AvailableExpressionAnalysis(DirectedGraph g, SootMethod m){
        super(g);

        doAnalysis();
    }

    @Override
    protected void merge(FlowSet<AbstractBinopExpr> in1,
                         FlowSet<AbstractBinopExpr> in2,
                         FlowSet<AbstractBinopExpr> out) {
        in1.intersection(in2, out);
    }

    @Override
    protected void copy(FlowSet<AbstractBinopExpr> source,
                        FlowSet<AbstractBinopExpr> dest) {
        source.copy(dest);
    }

    @Override
    protected FlowSet<AbstractBinopExpr> newInitialFlow() {
        //TODO: try to hold top
        return emptySet.clone();
    }

    @Override
    protected FlowSet<AbstractBinopExpr> entryInitialFlow() {
        return emptySet.clone();
    }


    @Override
    protected void flowThrough(FlowSet<AbstractBinopExpr> in, Unit unit, FlowSet<AbstractBinopExpr> out) {
        // out <- (in - expr containing locals defined in d) union out
        kill(in, unit, out);
        // out <- out union expr used in d
        gen(out, unit);
    }

    private void kill(FlowSet<AbstractBinopExpr> inSet, Unit u,
                      FlowSet<AbstractBinopExpr> outSet) {
        FlowSet<AbstractBinopExpr> kills = emptySet.clone();
        for (ValueBox defBox : u.getDefBoxes()) {

            if (defBox.getValue() instanceof Local) {
                Iterator<AbstractBinopExpr> inIt = inSet.iterator();
                while (inIt.hasNext()) {
                    AbstractBinopExpr e = inIt.next();
                    Iterator<ValueBox> eIt = e.getUseBoxes().iterator();
                    while (eIt.hasNext()) {
                        ValueBox useBox = eIt.next();
                        if (useBox.getValue() instanceof Local &&
                                useBox.getValue().equivTo(defBox.getValue()))
                            kills.add(e);
                    }
                }
            }
        }
        inSet.difference(kills, outSet);
    }

    private void gen(FlowSet<AbstractBinopExpr> outSet, Unit u) {
        for (ValueBox useBox : u.getUseBoxes()) {
            if (useBox.getValue() instanceof AbstractBinopExpr)
                outSet.add((AbstractBinopExpr) useBox.getValue());
        }
    }


}
