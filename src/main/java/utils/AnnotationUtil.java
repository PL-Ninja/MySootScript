package utils;

import config.SysConfig;
import soot.SootClass;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationUtil {
    public static boolean  isControllerClass(SootClass sootClass){

        VisibilityAnnotationTag visibilityAnnotationTag = (VisibilityAnnotationTag)sootClass.getTag("VisibilityAnnotationTag");
        if ( visibilityAnnotationTag!= null){

            for ( AnnotationTag tag:visibilityAnnotationTag.getAnnotations()){
                if (SysConfig.controllerRules.contains(tag.getType())){
                    return true;
                }
            }
        }
        return false;
    }
    // VisibilityAnnotationTag
//                  ->AnnotationTag
//                          ->type
//                          ->Elems
//                              -> 根据kind的类型有不同的结构（string/Array） name value都有实现
    public static void formatVisibilityAnnotationTag(VisibilityAnnotationTag visibilityAnnotationTag){
        if (visibilityAnnotationTag != null && visibilityAnnotationTag.hasAnnotations()){
            for (AnnotationTag tag : visibilityAnnotationTag.getAnnotations()){
                System.out.println(tag.getType());
                if (tag.getElems().size() != 0){
                    for (AnnotationElem elem : tag.getElems()){
                        if (elem.getKind() == '['){
                            AnnotationElemSwitch re = new AnnotationElemSwitch();
                            elem.apply(re);
                            AnnotationElemSwitch.AnnotationElemResult arrayResult = (AnnotationElemSwitch.AnnotationElemResult) re.getResult();
                            System.out.println("[Elem key]: " + arrayResult.getKey());
                            AnnotationElemSwitch.AnnotationElemResult[] arrayElems = (AnnotationElemSwitch.AnnotationElemResult[]) arrayResult.getValue();
                            for (AnnotationElemSwitch.AnnotationElemResult r : arrayElems){
                                System.out.println("[ ArrayElem key]: " + r.getKey());
                                System.out.println("[ArrayElem Value]: " + r.getValue());
                            }
                        }else {
                            AnnotationElemSwitch re = new AnnotationElemSwitch();
                            elem.apply(re);
                            AnnotationElemSwitch.AnnotationElemResult result = (AnnotationElemSwitch.AnnotationElemResult) re.getResult();
                            System.out.println("[Elem key]: " + result.getKey());
                            System.out.println("[Elem Value]: " + result.getValue());
                        }

                    }
                }
            }
        }
    }
}
