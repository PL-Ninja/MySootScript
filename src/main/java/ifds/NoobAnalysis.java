package ifds;

import soot.*;
import soot.options.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-07-20 16:23
 **/

public class NoobAnalysis {
    public static final String className = "demo.IFDSDemo";

    public static final String methodName = "main";

    public static final String sourceDirectory = System.getProperty("user.dir") + File.separator + "target" + File.separator + "classes";


    public static void setupSoot() {
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_process_dir(Collections.singletonList(sourceDirectory));
        Options.v().set_whole_program(true);
        Options.v().set_app(true);
        Scene.v().loadNecessaryClasses();

        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();

        // Call-graph options
        Options.v().setPhaseOption("cg", "safe-newinstance:true");
        Options.v().setPhaseOption("cg.cha", "enabled:false");

        // Enable SPARK call-graph construction
        Options.v().setPhaseOption("cg.spark", "enabled:true");
        Options.v().setPhaseOption("cg.spark", "verbose:true");
        Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");

        Options.v().set_allow_phantom_refs(true);

    }


    public static void main(String[] args) {

        setupSoot();

        //Options.v().set_main_class(className);
        Options.v().set_main_class(className);


        SootClass c = Scene.v().forceResolve(className, SootClass.BODIES);

        System.out.println(c.getMethods());

        // Load the "main" method of the main class and set it as a Soot entry point
        SootMethod entryPoint = c.getMethodByName(methodName);
        List<SootMethod> entryPoints = new ArrayList<>();
        entryPoints.add(entryPoint);
        Scene.v().setEntryPoints(entryPoints);


        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new IFDSDataFlowTransformer()));

        PackManager.v().runPacks();

    }
}
