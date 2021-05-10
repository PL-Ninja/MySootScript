package FlowSets;

import soot.jimple.internal.AbstractBinopExpr;
import soot.toolkits.scalar.AbstractFlowSet;

import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-10 16:57
 **/


public class VeryBusyExpressionFlowSet extends AbstractFlowSet<AbstractBinopExpr> {
    private Set<AbstractBinopExpr> busyExprs = new HashSet<>();
    public VeryBusyExpressionFlowSet(){
        super();
    }

    @Override
    public VeryBusyExpressionFlowSet clone() {
        VeryBusyExpressionFlowSet myClone = new VeryBusyExpressionFlowSet();
        myClone.busyExprs.addAll(this.busyExprs);
        return myClone;
    }

    @Override
    public boolean isEmpty() {
        return busyExprs.isEmpty();
    }

    @Override
    public int size() {
        return busyExprs.size();
    }

    @Override
    public void add(AbstractBinopExpr abstractBinopExpr) {
        busyExprs.add(abstractBinopExpr);
    }

    @Override
    public void remove(AbstractBinopExpr abstractBinopExpr) {
        if(busyExprs.contains(abstractBinopExpr)) {
            busyExprs.remove(abstractBinopExpr);
        }
    }

    @Override
    public boolean contains(AbstractBinopExpr abstractBinopExpr) {
        return busyExprs.contains(abstractBinopExpr);
    }

    @Override
    public Iterator<AbstractBinopExpr> iterator() {
        return busyExprs.iterator();
    }

    @Override
    public List<AbstractBinopExpr> toList() {
        return new ArrayList<>(busyExprs);
    }
}
