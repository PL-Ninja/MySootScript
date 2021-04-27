package Basic;

import Config.SootConfig;
import soot.PackManager;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 15:45
 **/


public class ClassJimple2File {
    private static String className = "Demo.Circle";



    public static void main(String[] args) throws Exception{

        SootConfig.setupSoot(className);
        // gen jimple file by soot , located in user.dir/sootOutput
        PackManager.v().writeOutput();

    }

}
