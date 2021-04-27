package Basic;

import Config.SootConfig;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 15:33
 **/


public class MethodJimple2File {

    private static String className = "Demo.Circle";

    public static final String methodSubSignature = "int area(boolean)";

    public static void main(String[] args) throws Exception{

        SootConfig.setupSoot(className);
        method2JimpleFile();

    }

    public static void method2JimpleFile() throws Exception{

        SootClass sc = Scene.v().getSootClass(className);
        SootMethod areaMethod = sc.getMethod(methodSubSignature);
        JimpleBody areaBody = (JimpleBody)areaMethod.retrieveActiveBody();
        //获取jimple-stmt 写入到文件中
        FileWriter writer = new FileWriter(System.getProperty("user.dir") + File.separator + "MyJimpleFiles"+ File.separator + className + ".jimple");
        int c = 0;
        for (Unit unit : areaBody.getUnits()) {
            c++;
            Stmt stmt = (Stmt) unit;
            System.out.printf("(%d): %s%n", c, stmt );
            writer.write(String.format("(%d): %s%n",c,stmt));
            writer.flush();
        }
        writer.close();
        System.out.printf("Method %s 's jimple file is transformed. \n",areaMethod.getName());

    }
}
