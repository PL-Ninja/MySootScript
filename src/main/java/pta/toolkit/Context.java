package pta.toolkit;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-23 09:57
 **/

import soot.*;

import java.util.*;

/**
 * 上下文
 */

/**
 * @author xinjiang
 * 模拟一个函数上下文
 * 对于一个函数内部，有其相应的Local（变量），不同的函数内部，Local可以具有相同的名字
 * 对于该类需要解决：
 * 1. 在某个函数体A内，调用了函数B，如何将A中变量与B中变量相联系?
 * - 由于每个local都对应了一个Variable，那么我们可以看做添加对应的
 * - 赋值操作，A.r1 = B.p1，由于在指针分析中，赋值操作，是在r1的sourceId中
 * - 添加相应的p1的sourceId，因此，我们可以在r1对应的variables中，添加一个p1对应的variable
 * - ！！！！因此，我们需要保留调用该函数的所在的函数域“! ! ! !，便于将该函数的参数与调用域中的参数对应
 * 2. 为了便于调试，明朗化，需要知道当前调用的深度
 * 3. 返回值，怎么处理？（暂时未处理！）
 * 4. 如何判断是否进入了递归函数？是否要处理递归函数？如果按照我们的添加variable的想法，
 * 处理递归函数会导致内存爆炸。因此，暂时不处理递归函数，如果要处理，需要修改一些机制。
 * 5. 还要处理分支函数，即If !!!!，第一次忽略了这个，一直跑不了
 * 6. 对于函数调用，需要将调用函数体内的变量与当前函数的参数进行联系。注意，函数调用后，只会影响对象的内部对象的指针指向！！
 * <p>
 * <p>
 * <p>
 * 注意点：
 * 注意点，创建分支区域与创建函数子区域是不同的。
 * 创建一个分支域，需要复制半格，并且是deep复制，因为在分支内，对象及其内部成员都会受到影响。
 * 而创建函数子区域，则无需deep复制操作。
 * 在进行函数调用后，需要将参数与上层函数域内变量对应，简单的想，只要将上层对应变量的Variable与该层的Local对应就行了，
 * 这样在该域内，对该域的Local操作，然后相应进行半格操作。
 * 但是由于函数调用，不会改变对象本身的指针，只能改变对象成员的指针，
 * 因此，我们此时与该域对应的参数的Local对应的应该是上层相应变量的Variable的浅复制，这样该域内可以改变对象的成员指针，但是不能改变对象本身指针；
 */
public class Context {
    private Analyzer analyzer;
    private int depth;
    private String className; // 类名
    private Context invokeContext;// 调用该函数域的上级函数域
    private String methodSignature; // 当前函数签名
    private List<Value> argsList; // 当前函数的参数列表
    private Context preContext;    // 分支时刻的域
    private String branchStmt; // 分支到的方法签名
    private Variable thisVar; // this var
    private Map<Local, Variable> localMap; // 在当前域内部，local和variable的对应表

    private Context(Analyzer analyzer,
                    int depth,
                    String className,
                    Context invokeContext,
                    String methodSignature,
                    List<Value> argsList,
                    Context preContext,
                    String branchStmt,
                    Variable thisVar) {
        this.analyzer = analyzer;
        this.depth = depth;
        this.className = className;
        this.invokeContext = invokeContext;
        this.methodSignature = methodSignature;
        this.argsList = argsList;
        this.preContext = preContext;
        this.branchStmt = branchStmt;
        this.thisVar = thisVar;
        localMap = new LinkedHashMap<>();
    }

    /**
     * 创建一个新域
     */
    public static Context getInstance(Analyzer analyzer, String className) {
        return new Context(analyzer, 0, className,null, null, Collections.emptyList(), null, null, null);
    }

    /**
     * 创建一个函数调用的新context
     *
     * @param methodSignature
     * @param args
     * @param thisVar
     * @return
     */
    public Context createInvokeContext(String methodSignature, List<Value> args, Variable thisVar) {
        SootMethod targetMethod = Scene.v().getMethod(methodSignature);
        String targetClassName = targetMethod.getDeclaringClass().getName();
        return new Context(analyzer, depth + 1, targetClassName,this, methodSignature, args, null, null, thisVar);
    }


    /**
     * 创建分支域，需要全体var deepCopy
     *
     * @param branchStmt
     * @return
     */
    public Context createBranchScope(String branchStmt) {
        Context context = new Context(analyzer, depth, className,invokeContext, methodSignature, argsList, this, branchStmt, thisVar);
        // 将context环境下的localMap，copy一份，给分支的context
        for (Map.Entry<Local, Variable> entry : this.localMap.entrySet()) {
            Variable copy = entry.getValue().copyDeep(true); // 深拷贝
            context.bindLocalAndVariable(copy.getLocal(), copy);
//            context.bindLocalAndVariable(entry.getKey(), copy);
        }
        return context;
    }


    /**
     * 在当前context下，将local和var绑定
     *
     * @param local
     * @param var
     */
    public void bindLocalAndVariable(Local local, Variable var) {
        localMap.put(local, var);
        analyzer.addLocal(local, var);
    }


    public Analyzer getAnalyzer() {
        return analyzer;
    }

    // 通过local获取对应的var
    public Variable getVariable(Local local) {
        return localMap.get(local);
    }


    // 将实参与形参绑定
    // 将上层函数域中的对应local的var浅复制一下，与当前域的local进行对应
    public void bindArg(Local local, int paramIndex) {
        // args.size()初始是0，如果第二个条件是false，说明是main方法
        if (paramIndex >= 0 && paramIndex < argsList.size()) {
            Value argValue = argsList.get(paramIndex);
            if (argValue instanceof Local) {
                Variable argVar = invokeContext.getVariable((Local) argValue);
                Variable var;
                if (argVar != null) {
                    var = argVar.copyDeep(false);// 浅复制
                } else {
                    var = Variable.getInstance(argValue,this);
                }
                bindLocalAndVariable(local, var);
            }
        }
    }

    /**
     * 将local
     *
     * @param local
     * @return
     */
    public Variable getOrAdd(Local local) {
        // 在当前context环境下，获取local对应的var
        Variable var = getVariable(local);
        // 如果local还没有对应的var，那么就为他生成一个var
        if (var == null) {
            var = Variable.getInstance(local,this);
            // 将local与var绑定
            bindLocalAndVariable(local, var);
        }
        return var;
    }


    // 将this与当前local绑定
    public void bindThis(Local local) {
        bindLocalAndVariable(local, thisVar.copyDeep(false));
    }

    // 当出现new的时候，在当前context中创建var，当前的val就是new expr
    public Variable createVariable(Value val) {
        return Variable.getInstance(val,this);
    }

    // 将常量包装为var
    public Variable createConstant(Value val){
//        return Variable.getConstantInstance(val,this);
        return Variable.getInstance(val,this);
    }


    // 判断是否递归
    public boolean isInRecursion(String invokeSignature) {
        if (invokeSignature.equals(methodSignature)) {
            return true;
        }
        if (invokeContext != null) {
            return invokeContext.isInRecursion(invokeSignature);
        }
        return false;
    }

    // 判断是否分支
    public boolean isInBranchChain(String branchStmt) {
        if (branchStmt.equals(this.branchStmt)) {
            return true;
        }
        if (preContext != null) {
            return preContext.isInBranchChain(branchStmt);
        }
        return false;
    }

    public int getDepth() {
        return depth;
    }

    public String getClassName() {
        return className;
    }

    public Context getPreContext() {
        return preContext;
    }

    @Override
    public String toString() {
        return "Contex{ "
                + this.localMap
                + "}";
    }


}
