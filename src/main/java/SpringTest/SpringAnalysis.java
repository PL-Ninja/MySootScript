package SpringTest;

import config.SootConfig;

import soot.*;
import soot.tagkit.*;
import soot.util.*;

import utils.AnnotationElemSwitch;
import utils.AnnotationElemSwitch.AnnotationElemResult;
import utils.AnnotationUtil;


public class SpringAnalysis {
    public static void main(String[] args) {
        SootConfig.setupSoot();
//        Chain<SootClass> classChain = Scene.v().getClasses();
        Chain<SootClass> classChain = Scene.v().getApplicationClasses();
        for (SootClass sootClass : classChain){
            if (AnnotationUtil.isControllerClass(sootClass)){
                System.out.println("[接口类]: " + sootClass.getName());
                System.out.println("[开始解析类外定义的注释]: ");
                VisibilityAnnotationTag annotationTagClass = (VisibilityAnnotationTag) sootClass.getTag("VisibilityAnnotationTag");
                AnnotationUtil.formatVisibilityAnnotationTag(annotationTagClass);
                for (SootMethod sootMethod : sootClass.getMethods()) {

                    System.out.println("[开始解析方法]：" + sootMethod.getSubSignature());
                    // 处理方法名、类名外注释解析
                    System.out.println("[开始解析方法外定义的注解]：");
                    VisibilityAnnotationTag visibilityAnnotationTag = (VisibilityAnnotationTag)sootMethod.getTag("VisibilityAnnotationTag");
                    AnnotationUtil.formatVisibilityAnnotationTag(visibilityAnnotationTag);
                    System.out.println("[开始解析方法参数列表中定义的注解]：");
                    //处理参数列表中的注解信息
                    VisibilityParameterAnnotationTag parameterAnnotationTag = (VisibilityParameterAnnotationTag)sootMethod.getTag("VisibilityParameterAnnotationTag");
                    if (parameterAnnotationTag != null){
                        for (VisibilityAnnotationTag tag : parameterAnnotationTag.getVisibilityAnnotations()){
                            AnnotationUtil.formatVisibilityAnnotationTag(tag);
                        }
                    }
                }
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            }
        }
    }
}
