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
    //public static final String className = "Demo.Circle";

    public static final String methodName = "begin";
    //public static final String methodName = "main";

    public static void main(String[] args) throws Exception {
        SootConfig.setupSoot(className);
        parseGadgets();
    }

    public static void parseGadgets() {
        SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);
        SootMethod srcMethod = sc.getMethodByName(methodName);

        List<SootMethod> entrypoint = new ArrayList<>();
        entrypoint.add(srcMethod);
        Scene.v().setEntryPoints(entrypoint);


        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", new SceneTransformer() {
            @Override
            protected void internalTransform(String s, Map<String, String> map) {

                List<SootMethod> gadgetList = new LinkedList<>();

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

                        ReachableMethods directTargets = new ReachableMethods(cg, new Targets(cg.edgesOutOf(srcMethod)));
                        TransitiveTargets transitiveTargets = new TransitiveTargets(cg);

                        Iterator<MethodOrMethodContext> transitiveIterator = transitiveTargets.iterator(srcMethod);

                        while (transitiveIterator.hasNext()) {
                            SootMethod transitiveTarget = (SootMethod) transitiveIterator.next();
                            // 添加到list
                            gadgetList.add(transitiveTarget);
                        }

                        System.out.println(gadgetList);
                    }
                }
            }
        }));
//
//
        PackManager.v().runPacks();


//        CallGraph cg = Scene.v().getCallGraph();
//
//        List<SootMethod> entrypoint = new ArrayList<>();
//        entrypoint.add(srcMethod);
//        Scene.v().setEntryPoints(entrypoint);
//
//        PackManager.v().runPacks();
//
//
//        QueueReader<Edge> listener = cg.listener();
//        while (listener.hasNext()){
//            Edge edge = listener.next();
//            SootClass declaringClass = edge.src().getDeclaringClass();
//            if(edge.src().getName().equals(methodName) && edge.src().getDeclaringClass().equals(className)){
//                System.out.println(edge.src().getDeclaringClass());
//            }
//        }
//
//
//
//        Targets targets = new Targets(cg.edgesOutOf(srcMethod));
//        Iterator<Edge> edgeIterator = cg.edgesOutOf(srcMethod);
//        while(edgeIterator.hasNext()){
//            Edge next = edgeIterator.next();
//            System.out.println("next = " + next.src().getName()+next.tgt().getName());
//        }


//        ReachableMethods directTargets = new ReachableMethods(cg, new Targets(cg.edgesOutOf(srcMethod)));
//        TransitiveTargets transitiveTargets = new TransitiveTargets(cg);
//
//        QueueReader<MethodOrMethodContext> listener = directTargets.listener();
//
//        while(listener.hasNext()){
//            SootMethod next = (SootMethod)listener.next();
//            System.out.println("next = " + next);
//        }
//
//        Iterator<MethodOrMethodContext> iterator = transitiveTargets.iterator(srcMethod);
//
//
//
//        while(iterator.hasNext()){
//            SootMethod next = (SootMethod)iterator.next();
//            System.out.println(next.getSubSignature());
//        }

//        List<SootMethod> entrypoint = new ArrayList<>();
//        entrypoint.add(srcMethod);
//        Scene.v().setEntryPoints(entrypoint);


    }
}
