package utils;

import soot.SootMethod;
import soot.jimple.Stmt;

import java.util.Map;
import java.util.Objects;

/**
 * @program: MySootScript
 * @description:
 * @author: 0range
 * @create: 2021-04-28 14:32
 **/


public class CallInfo {
    public Map<SootMethod,Integer> info;
    public Stmt stmt;

    public CallInfo(){

    }

    public CallInfo(Map<SootMethod,Integer> info,Stmt stmt){
        this.info = info;
        this.stmt = stmt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CallInfo callInfo = (CallInfo) o;
        return Objects.equals(info, callInfo.info) &&
                Objects.equals(stmt, callInfo.stmt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, stmt);
    }
}
