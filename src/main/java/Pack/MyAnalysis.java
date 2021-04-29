package Pack;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.Map;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-28 19:52
 **/


public class MyAnalysis extends SceneTransformer {

    public String classname;
    public String methodname;

    public MyAnalysis(String classname,String methodname){
        this.classname = classname;
        this.methodname = methodname;
    }

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        for(SootClass sootClass: Scene.v().getApplicationClasses()){
            if(sootClass.getName().equals(this.classname)){
                SootClass sc = Scene.v().forceResolve(sootClass.getName(), SootClass.BODIES);
//                SootMethod src = sc.getMethodByName(this.methodname);
                CallGraph cg = Scene.v().getCallGraph();

                for (Edge edge : cg) {
                    if (edge.src().getDeclaringClass().getName().equals(this.classname) && edge.src().getName().equals(this.methodname)) {
                        System.out.println(edge);
                    }
                }
                

            }

        }
    }
}
