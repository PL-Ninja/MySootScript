package basic;

import config.SootConfig;
import utils.SootUtil;
import soot.*;
import soot.util.Chain;

import java.util.List;

/**
 * @program: MySootScript
 * @description: try to parse class information
 * @author: 0range
 * @create: 2021-04-26 23:20
 **/


public class ParseClass {

    private static String className = "Demo.Circle";


    public static void main(String[] args) {
        SootConfig.setupSoot(className);
        getClassInfo();
    }

    public static void getClassInfo(){

        SootClass sc = Scene.v().getSootClass(className);

        String classFullName = sc.getName();

        String packageName = sc.getJavaPackageName();

        RefType classType = sc.getType();

        int modifier = sc.getModifiers();
        String modifyType = SootUtil.checkModifiers(modifier);

        SootClass superClass = sc.getSuperclass();

        boolean isapplicationClass = sc.isApplicationClass();

        int fieldCount = sc.getFieldCount();

        Chain<SootField> fields = sc.getFields();

        int methodCount = sc.getMethodCount();

        List<SootMethod> methods = sc.getMethods();

        SootMethod mainMethod = Scene.v().getMainMethod();

        System.out.print("[class full name] = " + classFullName +'\n'
                        +"[package name] = " + packageName + '\n'
                        +"[class type] = " + classType + '\n'
                        +"[modifier type] = " + modifyType + '\n'
                        +"[super class] = " + superClass + '\n'
                        +"[application class] = " + isapplicationClass + '\n'
                        +"--------------------------------------"+'\n'
                        +"[field count] = " + fieldCount + '\n'
                        +"[field list] = " + fields + '\n'
                        +"--------------------------------------"+'\n'
                        +"[method count] = " + methodCount + '\n'
                        +"[method list] : " + methods + '\n'
                        +"[main method] =  " + mainMethod + '\n'
        );
    }

    //check modifier
    private static String checkModifiers(int modifier) {
        if(Modifier.isPublic(modifier)){
            return "public";
        }
        if(Modifier.isPrivate(modifier)){
            return "private";
        }
        if(Modifier.isAbstract(modifier)){
            return "abstract";
        }
        if(Modifier.isFinal(modifier)){
            return "final";
        }
        if(Modifier.isStatic(modifier)){
            return "static";
        }
        return "error!";
    }

}
