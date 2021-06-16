package flowsets;

import soot.toolkits.scalar.AbstractBoundedFlowSet;
import soot.Local;

import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-10 18:54
 **/


public class LiveVariableFlowSet extends AbstractBoundedFlowSet<Local> {

    private Set<Local> liveVariableSet = new HashSet<>();
    public LiveVariableFlowSet(){
        super();
    }

    @Override
    public LiveVariableFlowSet clone() {
        LiveVariableFlowSet myClone = new LiveVariableFlowSet();
        myClone.liveVariableSet.addAll(this.liveVariableSet);
        return myClone;
    }

    @Override
    public boolean isEmpty() {
        return liveVariableSet.isEmpty();
    }

    @Override
    public int size() {
        return liveVariableSet.size();
    }

    @Override
    public void add(Local local) {
        liveVariableSet.add(local);
    }

    @Override
    public void remove(Local local) {
        if(liveVariableSet.contains(local)) {
            liveVariableSet.remove(local);
        }
    }

    @Override
    public boolean contains(Local local) {
        return liveVariableSet.contains(local);
    }

    @Override
    public Iterator<Local> iterator() {
        return liveVariableSet.iterator();
    }

    @Override
    public List<Local> toList() {
        return new ArrayList<>(liveVariableSet);
    }
}
