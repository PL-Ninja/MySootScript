package ifds;


import java.util.Map;
import java.util.Set;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.scalar.Pair;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-07-20 16:36
 **/


// Pair just somthing in pair 01-02

public class IFDSDataFlowTransformer extends SceneTransformer {


    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();

        IFDSTabulationProblem<
                Unit,
                Pair<Value, Set<DefinitionStmt>>,
                SootMethod,
                InterproceduralCFG<Unit, SootMethod>> problem = new IFDSReachingDefinitions(icfg);

//        IFDSSolver<
//                Unit,
//                Pair<Value, Set<DefinitionStmt>>,
//                SootMethod,
//                InterproceduralCFG<Unit, SootMethod>> solver = new IFDSSolver<>(problem);

        JimpleIFDSSolver<Pair<Value, Set<DefinitionStmt>>, InterproceduralCFG<Unit, SootMethod>> solver = new JimpleIFDSSolver<>(problem);


        System.out.println("Starting solver");
        solver.solve();
        solver.dumpResults();
        System.out.println("Done");
    }
}
