package pta.toolkit;

import soot.Local;
import soot.SootFieldRef;
import soot.Value;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-23 09:57
 **/

/**
 *
 * @author xinjiang
 * 类描述：
 * 1. 用于表示指针分析中的半格变量，也就是变量
 * 2. 代码中的每一个Local对象都对应有一个对应的Variable变量
 * 3. Variable对象存储了该Local对象对应的指针位置(allocID)
 * 4. 如果是一个对象Local对应的Varible，比如A a = new A() 其含有相应的成员函数 a.name，则其Variable对象中
 * 	包含Member对象，该Member对象存储了该对象的成员对应的Variable对象
 *
 * 类功能：
 * 1. 能够为相应的Variable添加相应的allocId
 * 2. 由于存在Set变量，因此需要实现深赋值
 * 3. 为了便于调试，查看日记，需要重写toString函数
 * 4. 只复制sourceId
 */
public class Variable implements Data{
    private String id;
    private Context belongContext;
    private String className;
    private Local local;// 记录var代表的local
    private String name; // var名字
    private Member member; //
    private Set<Integer> allocIdSet = new LinkedHashSet<>(); // allocId集合 // sourceId

    public Variable(Value value,Member member,Context context){
        id = UUID.randomUUID().toString();
        belongContext = context;
//        className = context == null?"Initial Class":context.getClassName();
        className = context.getClassName();
        name = value.toString();
        this.member = member;
        // 如果value是Local，需要赋值给Variable的local属性
        if(value instanceof Local) {
            this.local = (Local) value;
            this.member.setLocal(this.local);
        }
    }


    /**
     * 给定一个Local变量，它本身就是成员变量
     * 已经可以创建一个相应的Variable对象
     * 3种情况：
     * 1. new expr：value是new expr,
     */
    public static Variable getInstance(Value value,Context context){
        Member member = new Member(value);
        return new Variable(value,member,context);
    }

    public static Variable getConstantInstance(Value cons,Context context){
        Member member = new Member(cons);
        return new Variable(cons,member,context);
    }

    public void addAllocId(Integer id) {
        if (allocIdSet != null) {
            allocIdSet.add(id);
        }
    }
    /**
     * 依据local新建一个var
     * flag == true 深复制
     */
    public Variable copyDeep(boolean flag) {
        Variable var = new Variable(local==null?new ConstantValue():local, flag ? this.member.copyDeep() : this.member,this.belongContext);
        var.allocIdSet.addAll(this.allocIdSet);
        return var;
    }

    /**
     *
     * @param rvar
     * 用于var1 = var2操作，把var1的allocIdSet清空，把var2的allocIdSet，并不是实际中的赋值
     * member也要拷贝过去
     */
//    public void assign(Variable var) {
//        allocIdSet.clear();
//        allocIdSet.addAll(var.allocIdSet);
//        member = var.member;
//    }

    public void assign(Context belongContext,Variable rvar,Analyzer analyzer) {
        if(belongContext.getPreContext()!=null && belongContext.getPreContext()!=belongContext){// 说明是分支
            allocIdSet.addAll(rvar.allocIdSet); //直接添加
//            analyzer.trackTransferMap(belongContext,member,var.member);
            analyzer.trackTransferMap(belongContext,this,rvar);
            //analyzer.trackTransferMap(belongContext,this.member,rvar.member);
            member = rvar.member;
        }else{
            // 说明没有跳转
            allocIdSet.clear();//清空，直接赋值
            allocIdSet.addAll(rvar.allocIdSet);
//            analyzer.trackTransferMap(belongContext,member,var.member);
            //analyzer.trackTransferMap(belongContext,this.member,rvar.member);
            analyzer.trackTransferMap(belongContext,this,rvar);
            member = rvar.member;// 赋值指针集
        }
    }

    // 用于修改值
    public void assign(Context belongContext, Variable rvar, Analyzer analyzer, Variable baseVar, SootFieldRef sfr){
        if(belongContext.getPreContext()!=null && belongContext.getPreContext()!=belongContext){// 说明是分支
            allocIdSet.addAll(rvar.allocIdSet); //直接添加
//            analyzer.trackTransferMap(belongContext,member,var.member);
            analyzer.trackTransferMap(belongContext,this,rvar);
            //analyzer.trackTransferMap(belongContext,this.member,rvar.member);
            member = rvar.member;
        }else{
            // 说明没有跳转
            allocIdSet.clear();//清空，直接赋值
            allocIdSet.addAll(rvar.allocIdSet);
//            analyzer.trackTransferMap(belongContext,member,var.member);
            //analyzer.trackTransferMap(belongContext,this.member,rvar.member);
            analyzer.trackTransferMap(belongContext,this,rvar,baseVar,sfr);
            member = rvar.member;// 赋值指针集
        }
    }

//    public String getLocalNameByMember(Member member){
//        return
//    }

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public Local getLocal() {
        return local;
    }

    public Member getMember() {
        return member;
    }

    public Set<Integer> getAllocIdSet() {
        return allocIdSet;
    }

    public String getClassName() { return className; }

    @Override
    public String toString() {
        // 添加System.identityHashCode(member)用于查看相应的成员复制情况
        return "Variable{ "
                + className + " "
                + name + " "
                + (local==null?"Constant":local.getName()) + " "
                + member + " "
                + allocIdSet + " "
                + "}";
    }



    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


}
