package graph;

import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-07-02 19:18
 **/


public class DoStuffExample {

    public static final String className = "demo.CallGraph";

    public static final String methodName = "doStuff";

    public static final String sourceDirectory = System.getProperty("user.dir")+ File.separator + "target" + File.separator + "classes";

    public static void setupSoot(){
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_process_dir(Collections.singletonList(sourceDirectory));
        Options.v().set_whole_program(true);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();
    }
    public static void main(String[] args) throws IOException {
        setupSoot();

//        SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);也可以
        SootClass sc = Scene.v().getSootClass(className);
        SootMethod srcMethod = sc.getMethodByName(methodName);

        //设置入口点
        List<SootMethod> entrypoint = new ArrayList<>();
        entrypoint.add(srcMethod);
        Scene.v().setEntryPoints(entrypoint);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", new SceneTransformer() {

            @Override
            protected void internalTransform(String s, Map<String, String> map) {
                CHATransformer.v().transform();
                SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);
                SootMethod srcMethod = sc.getMethodByName(methodName);
                CallGraph cg = Scene.v().getCallGraph();
                Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(srcMethod));

                while(targets.hasNext()){
                    SootMethod tgt = (SootMethod) targets.next();
                    System.out.println(srcMethod+" may call "+tgt);

                }
            }
        }));

        PackManager.v().runPacks();

    }
}
