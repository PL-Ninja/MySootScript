package Utils;

import soot.Modifier;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 10:12
 **/


public class SootUtil {
    //check modifier
    public static String checkModifiers(int modifier) {
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
