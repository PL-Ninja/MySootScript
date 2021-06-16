//package pta;
//
//import soot.RefLikeType;
//import soot.SootFieldRef;
//import soot.Type;
//import soot.Value;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// *
// * @author xinjiang
// *
// * 模拟一个对象所拥有的成员变量。
// *
// * 对于单独的对象，虽然其没有成员变量，但是其仍旧有对应的Member对象，只是fieldMap为空。
// *
// * 对于一个对象的成员变量，最主要的是其类型，在Jimple中，常常用
// * <benchmark.objects.A: benchmark.objects.B f> 表示这个是 A对象中的f成员，其类型为 B
// *
// * 该对象需要完成：
// * 	1. 根据SootFieldRef获取其对应的Variable。 比如表示式 A.f，知道.f，就能够找到对应的变量表示
// *  2. 添加<SootFieldRef, Variable>对
// *  3. 由于存在Map，如果需要复制相应变量，需要深复制函数
// */
//public class Member {
//	private final Value value;
//	private Type type; // 类型
//	private String typename;
//	private Map<SootFieldRef, Variable> fieldMap;	// 该变量用于存储field对应的Varible，能够根据field描述，得到相应的Variable对象。
//
//	public Member(Value val) {
//		value = val;
//		type = value.getType();
//		typename = type.toString();
//		// 如果该对象是成员变量，则其为RefLikeType，例：<benchmark.objects.A: benchmark.objects.B f>
//		if (type instanceof RefLikeType) {
//			fieldMap = new HashMap<>();
//		}
//	}
//
//	/**
//	 * 深复制，主要就是要深复制Map变量
//	 * @return
//	 */
//	public Member copyDeep() {
//		Member m = new Member(value);
//		if (fieldMap != null) {
//			for (Map.Entry<SootFieldRef, Variable> entry : this.fieldMap.entrySet()) {
//                m.fieldMap.put(entry.getKey(), entry.getValue().copyDeep(true));
//            }
//		}
//		return m;
//	}
//
//	public Variable getVariable(SootFieldRef sfr) {
//		return fieldMap.get(sfr);
//	}
//
//	/**
//	 * 为相应的成员变量，添加相应的Variable
//	 * 如果已经有对应的Variable变量了，则说明是一个赋值操作，对其进行assign操作
//	 * @param sfr 成员引用定义量
//	 * @param var 成员对应的Variable变量
//	 */
//	public void addField(SootFieldRef sfr, Variable var) {
//		Variable v = fieldMap.get(sfr);
//		if (v == null) {
//			fieldMap.put(sfr, var.copyDeep(false));
//		} else {
//			v.assign(var);
//		}
//	}
//
//	public Value getValue() {
//		return value;
//	}
//
//	public Type getType() {
//		return type;
//	}
//
//	@Override
//	public String toString() {
//		return "Member{ "
//				+ value.toString() + " "
//				+ typename + " "
//				+ fieldMap + " "
//				+ "}";
//	}
//}
