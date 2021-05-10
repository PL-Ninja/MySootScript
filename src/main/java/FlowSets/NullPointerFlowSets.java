package FlowSets;

import soot.Local;
import soot.toolkits.scalar.AbstractBoundedFlowSet;

import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-10 15:13
 **/

/**
 * 这里来继承FlowSet来实现一个适合我们问题的FlowSet，自定义FlowSet内部方法
 */


public class NullPointerFlowSets extends AbstractBoundedFlowSet<Local> {
    private Set<Local> nullLocals = new HashSet<>();
    public NullPointerFlowSets() {
        super();
    }
    @Override
    public NullPointerFlowSets clone() {
        NullPointerFlowSets myClone = new NullPointerFlowSets();
        myClone.nullLocals.addAll(this.nullLocals);
        return myClone;
    }

    @Override
    public boolean isEmpty() {
        return nullLocals.isEmpty();
    }

    @Override
    public int size() {
        return nullLocals.size();
    }

    @Override
    public void add(Local local) {
        nullLocals.add(local);
    }

    @Override
    public void remove(Local local) {
        if(nullLocals.contains(local))
            nullLocals.remove(local);
    }

    @Override
    public boolean contains(Local local) {
        return nullLocals.contains(local);
    }

    @Override
    public Iterator<Local> iterator() {
        return nullLocals.iterator();
    }

    @Override
    public List<Local> toList() {
        return new ArrayList<>(nullLocals);
    }


}
