//package pta;
//
//import soot.Local;
//import soot.Value;
//
//import java.util.HashSet;
//import java.util.LinkedHashSet;
//import java.util.Set;
//
///**
// *
// * @author xinjiang
// * 类描述：
// * 1. 用于表示指针分析中的半格变量，也就是变量
// * 2. 代码中的每一个Local对象都对应有一个对应的Variable变量
// * 3. Variable对象存储了该Local对象对应的指针位置(allocID)
// * 4. 如果是一个对象Local对应的Varible，比如A a = new A() 其含有相应的成员函数 a.name，则其Variable对象中
// * 	包含Member对象，该Member对象存储了该对象的成员对应的Variable对象
// *
// * 类功能：
// * 1. 能够为相应的Variable添加相应的allocId
// * 2. 由于存在Set变量，因此需要实现深赋值
// * 3. 为了便于调试，查看日记，需要重写toString函数
// * 4. 只复制sourceId
// */
//public class Variable {
//	private Local local;
//	private String name;
//	private Member member; // 用于存储当前local的成员变量
//	private Set<Integer> sourceId = new LinkedHashSet<>(); // allocID 集
//
//	public Variable(Value value, Member member) {
//		name = value.toString();
//		// 如果是Local，需要赋值给Variable的local属性
//		if (value instanceof Local) {
//			this.local = (Local)value;
//		}
//		this.member = member;
//	}
//
//	/**
//	 * 给定一个Local变量，其本身就是成员变量
//	 * 已经可以创建一个相应的Variable对象
//	 * @param value
//	 * @return
//	 */
//	public static Variable getInstance(Value value) {
//		Member m = new Member(value);
//		return new Variable(value, m);
//	}
//
//	public void addAllocId(Integer id) {
//		if (sourceId != null) {
//			sourceId.add(id);
//		}
//	}
//
//	public Variable copyDeep(boolean flag) {
//		Variable var = new Variable(local, flag ? this.member.copyDeep() : this.member);
//		var.sourceId.addAll(this.sourceId);
//		return var;
//	}
//
//	/**
//	 *
//	 * @param var
//	 * 只用于var1 = var2操作，但是只改变其sourceId，并不是实际中的赋值
//	 */
//	public void assign(Variable var) {
//		sourceId.clear();
//		sourceId.addAll(var.sourceId);
//		member = var.member;
//	}
//
//	public Local getLocal() {
//		return local;
//	}
//
//	public Member getMember() {
//		return member;
//	}
//
//	public Set<Integer> getSourceId() {
//		return sourceId;
//	}
//
//	@Override
//	public String toString() {
//		// 添加System.identityHashCode(member)用于查看相应的成员复制情况
//		return "Variable{ "
//				+ name + " "
//				+ System.identityHashCode(member) + " "
//				+ sourceId + " "
//				+ "}";
//	}
//
//	@Override
//	public int hashCode() {
//		return super.hashCode();
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		return super.equals(obj);
//	}
//}
