package Pack;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-05-01 17:44
 **/


public class SimpleLiveVariablesAnalysis extends BackwardFlowAnalysis<DirectedGraph,FlowSet>{

    public SimpleLiveVariablesAnalysis(DirectedGraph graph) {
        super(graph);
    }


    @Override
    protected FlowSet newInitialFlow() {
        return null;
    }



    @Override
    protected void merge(FlowSet flowSet, FlowSet a1, FlowSet a2) {

    }

    @Override
    protected void copy(FlowSet flowSet, FlowSet a1) {

    }

    @Override
    protected void flowThrough(FlowSet flowSet, DirectedGraph directedGraph, FlowSet a1) {

    }
}
