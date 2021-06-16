package pta.toolkit;

import com.google.gson.Gson;
import jas.Var;
import soot.Local;
import soot.SootFieldRef;
import soot.jimple.FieldRef;

import java.util.*;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-06-23 09:46
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 * <p>
 * 单例分析器
 */

/**
 * 单例分析器
 */

/**
 *
 * @author xinjiang
 * 用于根据函数中使用的allocId(), test(id, v)得到相应的结果
 * 功能及相关分析：
 * 1. 全局只有一个分析器，因此，采用单例模式
 * 2. 需要根据local找到相应的variable，其中对于对象local，会对应一组variable（包括field）
 * 3. 能够设定allocId
 * 4. 添加查询的local
 * 5. 以字符串的形式，返回相应答案
 * 6. 添加（local, variable）对
 */
public class Analyzer {
    private int allocId; // 记录allocId
    private Map<Local, Set<Variable>> wholeLocalMap; // 记录所有context中 出现过的local 对应的var
    private Map<Integer, Local> queryMap; // allocId 对应的local
    private Map<Local, Variable> localCheckMap; // 记录出现过的local
    private Map<Variable, Variable> transferMap; // 记录assign的变化
    //private Map<Member, Member> transferMap; // 记录assign的变化
    private Map<Local, Local> equalMap;//记录a=$stack4的匹配关系

    public StringBuilder tracker = new StringBuilder();


    private Analyzer() {
        allocId = 0;
        wholeLocalMap = new LinkedHashMap<>();
        queryMap = new LinkedHashMap<>();
        localCheckMap = new LinkedHashMap<>();
        transferMap = new LinkedHashMap<>();
        equalMap = new LinkedHashMap<>();
    }

    // singleton
    private static class LoadAnalyzer {
        // 单例模式 static final 需要一直跟着
        private final static Analyzer instance = new Analyzer();
    }

    public static Analyzer getAnalyzer() {
        return LoadAnalyzer.instance;
    }

    /**
     * 如果localMap中不存在当前local的话，添加一个新的entry <local,set.add(var)>
     * @param local
     * @param var
     */
    public void addLocal(Local local, Variable var) {
        Set<Variable> vars = wholeLocalMap.computeIfAbsent(local, k -> new HashSet<>());
        vars.add(var);
    }

    /**
     * 增加行号索引local  <allocId local>
     * @param id
     * @param l
     */
    public void addQuery(int id, Local l) {
        queryMap.put(id, l);
    }


    public void addLocalCheckMap(Local local, Variable var) {
        if (localCheckMap.get(local) != null && localCheckMap.get(local).getAllocIdSet().size() > var.getAllocIdSet().size()) {
            return;
        } else {
            localCheckMap.put(local, var);
        }
    }

    public void addEqualMap(Local lvar, Local rvar) {
        equalMap.put(lvar, rvar);
    }

//    // 打印记录下来的queryMap <allockId,local>
//    public String run() {
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<Integer, Local> entry : queryMap.entrySet()) {
//            // 先打印 alloc：
//            sb.append(entry.getKey() + ":");
//            Local local = entry.getValue();
//            Set<Variable> vars = wholeLocalMap.get(local);//通过local去localMap找对应的var
//            Set<Integer> allocIdSet = new HashSet<>();
//            // 通过var的
//            if (vars != null) {
//                for (Variable var : vars) {
//                    allocIdSet.addAll(var.getAllocIdSet());
//                }
//            }
//            // 遍历对应的allocId
//            for (Integer id : allocIdSet) {
//                if (id > 0) {
//                    sb.append(" " + id);
//                }
//            }
//            sb.append("\n");
//        }
//        System.out.println("Result : \n" + sb);
//        return sb.toString();
//    }

    // 打印记录下来的localCheckMap <local,variable>
    public String run() {
        StringBuilder sb = new StringBuilder();
        sb.append("Result : \n");
        for (Map.Entry<Local, Variable> entry : localCheckMap.entrySet()) {
            // 去除$stack这种的中间变量
            if (entry.getKey().getName().charAt(0) != '$') {
                sb.append(entry.getValue().getClassName()).append(" : ")
                        .append(entry.getKey().getName()).append(" : ")
                        .append(entry.getValue().getAllocIdSet())
                        .append("\n");
                // pta.ptademo.Hello : a : [14, 16]
            }
        }

        sb.append("\nObj's Fields : \n");
        for (Map.Entry<Local, Variable> entry : localCheckMap.entrySet()) {
            if (entry.getKey().getName().charAt(0) != '$') {
                sb.append(entry.getValue().getClassName()).append(" : ")
                        .append(entry.getKey().getName()).append(" : ");
                Map<SootFieldRef, Variable> fieldMap = entry.getValue().getMember().getFieldMap();
                for (Map.Entry<SootFieldRef, Variable> fieldMapEntry : fieldMap.entrySet()) {
                    if (fieldMapEntry.getValue().getName().contains("ConstantValue")) {
                        sb.append(fieldMapEntry.getKey() + " value " + fieldMapEntry.getValue().getMember().getValue() + " from " + fieldMapEntry.getValue().getClassName() + " : " + fieldMapEntry.getValue().getAllocIdSet()).append(" , ");
                    } else {
//                        sb.append(fieldMapEntry.getKey() + " from " + fieldMapEntry.getValue().getMember().getType() + " : " + fieldMapEntry.getValue().getAllocIdSet()).append(" ");
                        sb.append(fieldMapEntry.getKey() + " from " + fieldMapEntry.getValue().getClassName() + " : " + fieldMapEntry.getValue().getAllocIdSet()).append(" ");
                    }
                }
                sb.append("\n");
            }
        }

        sb.append("\nTrack Changes : \n");
        for (Map.Entry<Variable, Variable> memberEntry : transferMap.entrySet()) {
//            if (memberEntry.getKey().getLocal() != null && memberEntry.getKey().getLocal().getName().charAt(0) != '$') {
//            if (memberEntry.getKey().getValue().toString() != null && memberEntry.getKey().getLocal().getName().charAt(0) != '$') {
//                if (memberEntry.getKey().getFieldMap() != null && memberEntry.getKey().getFieldMap().size() != 0) {// 修改了值的member
//                    Map<SootFieldRef, Variable> memberValueMap = memberEntry.getValue().getFieldMap();
//                    for (Map.Entry<SootFieldRef, Variable> fieldEntry : memberEntry.getKey().getFieldMap().entrySet()) {
//                        Variable variable = memberValueMap.get(fieldEntry.getKey());
//                        if (!fieldEntry.getValue().toString().equals(variable.toString())) {
//                            //sb.append(memberEntry.getValue().getLocal().getName()+" 's "+fieldEntry.getKey() + " : " + fieldEntry.getValue() + " --> " + variable).append("\n");
//                            sb.append(fieldEntry.getValue().getLocal().getName()+" 's "+fieldEntry.getKey() + " : " + fieldEntry.getValue() + " --> " + variable).append("\n");
//                        }
//                    }
//                    //sb.append(memberEntry.getKey().getFieldMap() + " ---> " + memberEntry.getValue().getFieldMap()).append("\n");
//                }
//            }

//            // 可以跑的版本
//            if (memberEntry.getKey().getValue() != null && memberEntry.getKey().getValue().toString().charAt(0) != '$') {
//                if (memberEntry.getKey().getFieldMap() != null && memberEntry.getKey().getFieldMap().size() != 0) {
//                    Map<SootFieldRef, Variable> memberValueMap = memberEntry.getValue().getFieldMap();
//                    for (Map.Entry<SootFieldRef, Variable> fieldEntry : memberEntry.getKey().getFieldMap().entrySet()) {
//                        Variable variable = memberValueMap.get(fieldEntry.getKey());
//                        if (!fieldEntry.getValue().toString().equals(variable.toString())) {
//                            //sb.append(memberEntry.getKey() + " : " + memberEntry.getValue()).append("\n");
////                            sb.append(fieldEntry.getValue().getLocal().getName()+" 's "+fieldEntry.getKey() + " : " + fieldEntry.getValue() + " --> " + variable).append("\n");
//                            sb.append(equalMap.get(memberEntry.getKey().getLocal()) + " 's " + fieldEntry.getKey() + " : " + fieldEntry.getValue() + " --> " + variable).append("\n");
//                        }
//                    }
//                    //sb.append(memberEntry.getKey().getValue() + " : " + memberEntry.getValue().getValue()).append("\n");
//                }
//            }
        }

        for (Map.Entry<Variable, Variable> memberEntry : transferMap.entrySet()) {

        }




//        for (Map.Entry<Member, Member> variableEntry : transferMap.entrySet()) {
//            System.out.println(variableEntry.getKey().getLocal().getName() + " : " + variableEntry.getValue().getName());
//        }


//        StringBuilder mm = new StringBuilder();
//        for (Map.Entry<Variable, Variable> variableEntry : transferMap.entrySet()) {
//            if(variableEntry.getKey().getName().charAt(0)!='$'){
//                mm.append(variableEntry.getKey()+ " ---> "+variableEntry.getValue()).append('\n');
//            }
//        }

        System.out.println(sb.toString());
        return sb.toString();
    }


//    public void trackTransferMap(Context context, Member lvar, Member rvar) {
//        transferMap.put(lvar, rvar);
//    }
    public void trackTransferMap(Context context, Variable lvar, Variable rvar) {
        transferMap.put(lvar, rvar);
    }

    public void trackTransferMap(Context context, Variable lvar, Variable rvar, Variable baseVar,SootFieldRef sfr) {
        Variable tempVar = baseVar;
        Variable variable = tempVar.getMember().getFieldMap().get(sfr);
        rvar = variable;
        transferMap.put(lvar, rvar);
    }


    public Map<Local, Local> getEqualMap() {
        return equalMap;
    }

    public int getAllocId() {
        return allocId;
    }

    public void setAllocId(int allocId) {
        this.allocId = allocId;
    }

    @Override
    public String toString() {
        return "Analyzer{ "
                + wholeLocalMap + " "
                + queryMap + " "
                + "}";
    }
}
