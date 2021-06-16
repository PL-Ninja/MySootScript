package basic;

import config.SootConfig;
import soot.*;
import soot.jimple.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-27 15:53
 **/


public class ParseExpr {

    private static String className = "Demo.Circle";

    private static final String methodSubSignature = "int area(boolean)";

    public static void main(String[] args) {
        SootConfig.setupSoot(className);
        getExprInfo();
    }

    public static void getExprInfo() {

        SootClass sc = Scene.v().getSootClass(className);
        SootMethod areaMethod = sc.getMethod(methodSubSignature);

        //获得方法体
        final JimpleBody areaBody = (JimpleBody)areaMethod.retrieveActiveBody();

        // 获取第一个非赋值语句
        Stmt firstNonIdentitiyStmt = areaBody.getFirstNonIdentityStmt();

        for(Unit u : areaBody.getUnits()){
            Stmt stmt = (Stmt) u;

            if(stmt.equals(firstNonIdentitiyStmt)){
                System.out.println("First invoke expr : \n"+ stmt.getJavaSourceStartLineNumber() + " : " + stmt);
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            }

            // invoke expr
            if(stmt.containsInvokeExpr()){
                System.out.println(stmt.getJavaSourceStartLineNumber() + " : " + stmt);
                InvokeExpr invokeExpr = stmt.getInvokeExpr();
                invokeExpr.apply(new AbstractExprSwitch() {
                    @Override
                    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
                        super.caseInterfaceInvokeExpr(v);
                    }

                    @Override
                    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
                        super.caseSpecialInvokeExpr(v);
                    }

                    @Override
                    public void caseStaticInvokeExpr(StaticInvokeExpr v) {
                        System.out.printf("[StaticInvoke] StaticInvokeExpr '%s' from class '%s'%n", v, v.getMethod().getDeclaringClass());
                        System.out.println("[MethodRef] " + v.getMethodRef());//被调用的方法引用
                        System.out.println("[Args] " + v.getArgs());//获取参数
                    }

                    @Override
                    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                        System.out.printf("[VirtualInvoke] VirtualInvokeExpr '%s' from local '%s' with type %s %n", v, v.getBase(), v.getBase().getType());
                        /**
                         * 只有virtualInvoke才会有getBase这样的方法
                         * v.getBase() 获取方法调用的起始者 会是个变量
                         * v.getBase().getType() 获取调用者的所属引用
                         * v.getMethod() 获取被调用的方法
                         * v.getMethodRef() 获取被调用的方法的引用
                         * v.getArgCount() 获取参数个数
                         **/
                    }

                    @Override
                    public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
                        super.caseDynamicInvokeExpr(v);
                    }
                    @Override
                    public void defaultCase(Object v) {
                        super.defaultCase(v);
                    }
                });
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            }

            // check 'if' stmt
            stmt.apply(new AbstractStmtSwitch() {
                @Override
                public void caseIfStmt(IfStmt stmt) {
                    System.out.println(stmt);
                    /**
                     * stmt.getCondition() 获取条件
                     * stmt.getTarget() 获取目标语句
                     */
                    System.out.printf("[Before change] if condition '%s' is true goes to stmt '%s'%n", stmt.getCondition(), stmt.getTarget());
                    //改变跳转流程 直接跳到紧挨着stmt的下一个语句 视为直接忽略if,顺序执行了
                    stmt.setTarget(areaBody.getUnits().getSuccOf(stmt));
                    System.out.printf("[After change] if condition '%s' is true goes to stmt '%s'%n", stmt.getCondition(), stmt.getTarget());
                }
            });

            // stmt change fields
            if(stmt.containsFieldRef()){
                // fieldRef就是被调用的field
                FieldRef fieldRef = stmt.getFieldRef();
                fieldRef.apply(new AbstractRefSwitch() {
                    @Override
                    public void caseStaticFieldRef(StaticFieldRef v) {
                        //如果是静态属性的引用 啥也不做
                    }

                    @Override
                    public void caseInstanceFieldRef(InstanceFieldRef v) {
                        //如果是实例属性引用
                        //那就获得实例所属变量 实例属性 实例属性引用 field数据类型
                        Value base = v.getBase();
                        SootField field = v.getField();
                        SootFieldRef fieldRef = v.getFieldRef();
                        Type fieldType = v.getType();
                        System.out.println("baseVar = " + base+"; field = "+ field+"; fieldRef = "+ fieldRef +"; fieldType = "+ fieldType);
                    }
                });
            }

        }

    }




}
