//package pta;
//
//import soot.Local;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///**
// *
// * @author xinjiang
// * 用于根据函数中使用的allocId(), test(id, v)得到相应的结果
// * 功能及相关分析：
// * 1. 全局只有一个分析器，因此，采用单例模式
// * 2. 需要根据local找到相应的variable，其中对于对象local，会对应一组variable（包括field）
// * 3. 能够设定allocId
// * 4. 添加查询的local
// * 5. 以字符串的形式，返回相应答案
// * 6. 添加（local, variable）对
// */
//public class Analyzer {
//	private int allocId;
//	// 变量表 Local-出现过的引用(引用就是变量)
//	private Map<Local, Set<Variable>> localMap;
//	// 查询表
//	private Map<Integer, Local> queries;
//
//	private Analyzer() {
//		allocId = 0;
//		localMap = new HashMap<>();
//		queries = new HashMap<>();
//	}
//
//	//singleton
//	private static class LoadAnalyzer {
//		// 单例模式 static final 需要一直跟着
//		private final static Analyzer instance = new Analyzer();
//	}
//
//	public static Analyzer getAnalyzer() {
//		return LoadAnalyzer.instance;
//	}
//
//	public void setAllocId(int id) {
//		allocId = id;
//	}
//
//	public int getAllocId() {
//		return allocId;
//	}
//
//	// 如果localMap中不存在当前local的话，添加一个新的entry
//	public void addLocal(Local local, Variable var) {
//		Set<Variable> vars = localMap.computeIfAbsent(local, k -> new HashSet<>());
//		vars.add(var);
//	}
//
//	public void addQuery(int id, Local l) {
//		queries.put(id, l);
//	}
//
//	// 开始分析
//	public String run() {
//		StringBuilder sb = new StringBuilder();
//		for (Map.Entry<Integer, Local> entry : queries.entrySet()) {
//			sb.append(entry.getKey() + ":");
//			Local l = entry.getValue();
//			Set<Variable> vars = localMap.get(l);
//			Set<Integer> sourceId = new HashSet<>();
//			if (vars != null) {
//				for (Variable var : vars) {
//					sourceId.addAll(var.getSourceId());
//				}
//			}
//			for (Integer id : sourceId) {
//				if (id > 0) {
//					sb.append(" " + id);
//				}
//			}
//			sb.append("\n");
// 		}
//		System.out.println("Result: \n" + sb);
//		return sb.toString();
//	}
//
//	@Override
//	public String toString() {
//		return "Analyzer{ "
//				+ localMap + " "
//				+ queries + " "
//				+ "}";
//	}
//}
