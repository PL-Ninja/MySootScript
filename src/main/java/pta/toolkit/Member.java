package pta.toolkit;

import soot.*;
import soot.jimple.FieldRef;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-23 09:57
 **/

/**
 * @author xinjiang
 * <p>
 * 模拟一个对象所拥有的成员变量。
 * <p>
 * 对于单独的对象，虽然其没有成员变量，但是其仍旧有对应的Member对象，只是fieldMap为空。
 * <p>
 * 对于一个对象的成员变量，最主要的是其类型，在Jimple中，常常用
 * <benchmark.objects.A: benchmark.objects.B f> 表示这个是 A对象中的f成员，其类型为 B
 * <p>
 * 该对象需要完成：
 * 1. 根据SootFieldRef获取其对应的Variable。 比如表示式 A.f，知道.f，就能够找到对应的变量表示
 * 2. 添加<SootFieldRef, Variable>对
 * 3. 由于存在Map，如果需要复制相应变量，需要深复制函数
 */
public class Member implements Data {
    private String id;
    private final Value value;
    private Type type; // field类型
    private String typeName;
    private Local local;

    private Map<SootFieldRef, Variable> fieldMap; // // 存储fields对应的Varible，能够根据field描述，得到相应的Variable对象。
//    private Map<FieldRef, Variable> fieldMap; // // 存储fields对应的Varible，能够根据field描述，得到相应的Variable对象。

    /**
     * 构造函数
     * 两种情况会来这：
     * 1. 创建一个新的var的时候，v就是value，
     * 2.
     *
     * @param v
     */
    public Member(Value v) {
        id = UUID.randomUUID().toString();
        value = v;
        type = v.getType();
        typeName = v.getType().toString();
        // 如果该field是一个对象类型的field，那么他就是RefLikeType, 例如<benchmark.objects.A: benchmark.objects.B f>
        // field为空
        if (type instanceof RefLikeType) {
            fieldMap = new LinkedHashMap<>();
        }
    }

    /**
     * 深复制，要将当前的
     *
     * @return
     */
    public Member copyDeep() {
        Member m = new Member(value);
        if (fieldMap != null) {
            for (Map.Entry<SootFieldRef, Variable> entry : this.fieldMap.entrySet()) {
                m.fieldMap.put(entry.getKey(), entry.getValue().copyDeep(true));
            }
        }
        return m;
    }

    /**
     * 通过fieldref获取对应的var
     *
     * @param sfr
     * @return
     */
    public Variable getVariable(SootFieldRef sfr) {
        return fieldMap.get(sfr);
    }

    /**
     * 为相应的field，添加相应的var
     * 如果fieldMap已经有相应的var了，那么就说明是赋值操作，需要进行assign操作；
     *
     * @param sfr
     * @param rvar
     */
    public void addField(SootFieldRef sfr, Variable rvar, Context context, Analyzer analyzer,Variable var) {
        Variable v = fieldMap.get(sfr);
//        if(v == null && var.getLocal()==null){
//            // constant
//            fieldMap.put(sfr,var);
//        } else if(v == null && var.getLocal()!=null){
//            fieldMap.put(sfr,var.copyDeep(false));// 浅复制
//        } else{
//            v.assign(context,var); // 如果已经有了，说明这次是assign
//        }
        if (v == null) {
            fieldMap.put(sfr, rvar.copyDeep(false));// 浅复制
        } else {
            v.assign(context, rvar, analyzer,var,sfr); // 如果已经有了，说明这次是assign
        }
    }


    public Value getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

//    public Map<SootFieldRef, Variable> getFieldMap() {
//        return fieldMap;
//    }
    public Map<SootFieldRef, Variable> getFieldMap() {
        return fieldMap;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Member{ "
                + value.toString() + " "
                + (local == null ? "Constant" : local) + " "
                + typeName + " "
                + fieldMap + " "
                + "}";
    }

    @Override
    public String getName() {
        return value.toString();
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

}
