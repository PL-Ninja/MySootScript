package Graph;

import Config.SootConfig;
import soot.*;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.queue.QueueReader;

import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-16 12:48
 **/

//
public class GadgetsParser {
    public static final String className = "Demo.CGDemo";

    public static final String methodName = "begin";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        parseGadgets();
    }

    public static void parseGadgets() {
        SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);
        SootMethod srcMethod = sc.getMethodByName(methodName);

        // 设置入口
        List<SootMethod> entrypoint = new ArrayList<>();
        entrypoint.add(srcMethod);
        Scene.v().setEntryPoints(entrypoint);


        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", new SceneTransformer() {
            @Override
            protected void internalTransform(String s, Map<String, String> map) {

                List<SootMethod> directList = new LinkedList<>();
                List<SootMethod> transitiveList = new LinkedList<>();

                for (SootClass sootClass : Scene.v().getApplicationClasses()) {
                    if (sootClass.getName().equals(className)) {
                        CallGraph cg = Scene.v().getCallGraph();

//                        QueueReader<Edge> listener = cg.listener();
//                        while(listener.hasNext()){
//                            Edge edge = listener.next();
//                            if (edge.src().getDeclaringClass().getName().equals(className) && edge.src().getName().equals(methodName)) {
//                                System.out.println(edge);
//                            }
//                        }


//                        Iterator targets = new Targets(cg.edgesOutOf(srcMethod));
//                        while(targets.hasNext()){
//                            SootMethod method = (SootMethod)targets.next();
//                            System.out.println(method.getName());
//                        }

                        // directTargets是方法内直接能走到的方法
                        ReachableMethods directTargets = new ReachableMethods(cg, new Targets(cg.edgesOutOf(srcMethod)));
                        // transitiveTargets是间接跳转的
                        TransitiveTargets transitiveTargets = new TransitiveTargets(cg);

                        Iterator<MethodOrMethodContext> transitiveIterator = transitiveTargets.iterator(srcMethod);

                        while (transitiveIterator.hasNext()) {
                            SootMethod transitiveTarget = (SootMethod) transitiveIterator.next();

                            transitiveList.add(transitiveTarget);

                            //
                            if(directTargets.contains(transitiveTarget)){
                                directList.add(transitiveTarget);
                            }
                        }

                        System.out.println(srcMethod.getName()+ " may reach "+ transitiveList);
                        System.out.println(srcMethod.getName()+ " direct to "+ directList);

                    }
                }
            }
        }));

        PackManager.v().runPacks();

    }
}
