package team.zte.competition;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



public class Main {
	static final String file = "test/data.txt";
	static int nodeNum = 0, edgeNum = 0, nodeCstrntNum = 0, edgeCstrntNum = 0, limitNodeNum = Integer.MAX_VALUE;
	static int[][] graph;//邻接矩阵
	static int[][] allGraph;//拆点后的邻接矩阵
	static ArrayList<Integer> nodeCstrnt = new ArrayList<Integer>();//必须经过的点
	static ArrayList<Edge> edgeCstrnt = new ArrayList<Edge>();//必须经过的边
	static boolean[] visited;//判断是否访问过
	static int[] pre;//保留上一个节点,保留经过节点个数；
	static List<Integer> path;
	
	
	public static void main(String[] args){
		try {
			readData(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.printf("当前输入包含：%d个必须经过的点， %d条必须经过的边， 经过的点数限制为%d个。\n", nodeCstrntNum, edgeCstrntNum, limitNodeNum);
		doBestPath();
		if (path.size() > limitNodeNum){
			doLimitPath();
			if (path.size() > limitNodeNum){
				System.out.println("当前无法满足所有条件,当考虑经过必经点与边时，经过最少点数的次优解为：");
				showPath();
			}else{
				System.out.println("当考虑经过必经点与边时，满足条件的最优解为：");
				showPath();
			}
			System.out.println("若不考虑经过的点数，满足条件的次优解为：");
			doBestPath();
			showPath();
		}else{
			showPath();
		}
		
		
		
	}
	//忽略经过点数量的最优路径
	public static void doBestPath(){
		graphTrans(nodeCstrntNum+edgeCstrntNum);		
		dijkstra(allGraph);
		genPath();
		
	}
	//考虑最小点数的路径
	public static void doLimitPath(){
		graphTransPassNum(nodeCstrntNum+edgeCstrntNum);		
		//checkMatrix(allGraph);
		dijkstra(allGraph);
		genPath();
		
	}
	
	//暴力迪杰斯特拉
	public static void dijkstra(int[][] allGraph){
		visited = new boolean[allGraph.length];
		pre = new int[allGraph.length];
		Arrays.fill(visited, false);
		visited[0] = true;
		pre[0] = -1;
		int visiting = 0, tmpNode = 0, tmpCost;
		while (visiting != allGraph.length - 1){
			//找寻下一个节点
			tmpCost = Integer.MAX_VALUE;
			for (int i = 0; i < allGraph.length; i++){
				if (!visited[i] && allGraph[0][i] < tmpCost){
					tmpCost = allGraph[0][i];
					tmpNode = i;
				}
			}
			
			//更新花费表
			for (int i = 0; i < allGraph.length; i++){
				if ((allGraph[0][tmpNode] < Integer.MAX_VALUE && allGraph[tmpNode][i] < Integer.MAX_VALUE) && ((allGraph[0][tmpNode] + allGraph[tmpNode][i]) < allGraph[0][i])){
					allGraph[0][i] = (allGraph[0][tmpNode] + allGraph[tmpNode][i]);
					pre[i] = tmpNode;
				}
			}
			//更新访问情况
			visited[tmpNode] = true;
			visiting = tmpNode;
			//System.out.println("已遍历点"+visiting);
		}
	}
	
	//该函数用于生成｛判断经过约束到达目标至少需要经过几个节点｝的矩阵
	public static void graphTransPassNum(int cstrntNum){
		graphTrans(cstrntNum);
		//checkMatrix(allGraph);
		for (int i = 0; i < allGraph.length; i++){
			for (int j = 0; j < allGraph[0].length; j++){
				if (allGraph[i][j] < Integer.MAX_VALUE && allGraph[i][j] > 0){
					allGraph[i][j] = 1;
				}
			}
		}
	}
	
	
	//该函数用于生成拆点后的邻接矩阵
	public static void graphTrans(int cstrntNum){
		int layer = (1 << cstrntNum);
		int size = nodeNum * layer;
		allGraph = new int[size][size];
		for (int i = 0; i < size; i++){
			Arrays.fill(allGraph[i], Integer.MAX_VALUE);
			for (int j = 0; j < size; j++){
				if (i/nodeNum == j/nodeNum){
					allGraph[i][j] = graph[i%nodeNum][j%nodeNum];
				}
			}
		}
		//加入必经点约束
		for (int i = 0; i < nodeCstrnt.size(); i++){
			int offset = (1 << i);
			for (int j = 0; j < layer; j += 2*offset){
				for (int k = j; k < j + offset; k++){
					allGraph[k*nodeNum+nodeCstrnt.get(i)][(k+offset)*nodeNum+nodeCstrnt.get(i)] = 0;
					allGraph[(k+offset)*nodeNum+nodeCstrnt.get(i)][k*nodeNum+nodeCstrnt.get(i)] = 0;
				}
			}
		}
		//加入必经过边约束
		for (int i = 0; i < edgeCstrnt.size(); i++){
			int offset = (1 << i + nodeCstrnt.size());
			for (int j = 0; j < layer; j += 2*offset){
				for (int k = j; k < j + offset; k++){
					allGraph[k*nodeNum+edgeCstrnt.get(i).beginNode()][(k+offset)*nodeNum+edgeCstrnt.get(i).endNode()] = edgeCstrnt.get(i).length();
					allGraph[(k+offset)*nodeNum+edgeCstrnt.get(i).endNode()][k*nodeNum+edgeCstrnt.get(i).beginNode()] = edgeCstrnt.get(i).length();
					allGraph[k*nodeNum+edgeCstrnt.get(i).endNode()][(k+offset)*nodeNum+edgeCstrnt.get(i).beginNode()] = edgeCstrnt.get(i).length();
					allGraph[(k+offset)*nodeNum+edgeCstrnt.get(i).beginNode()][k*nodeNum+edgeCstrnt.get(i).endNode()] = edgeCstrnt.get(i).length();
				}
			}
		}
	}
	
	//读取文件中的数据
	public static void readData(String file) throws Exception{
		FileInputStream fis = new FileInputStream(file);
		Scanner sc = new Scanner(fis);
		while (sc.hasNext()){
			nodeNum = sc.nextInt();
			edgeNum = sc.nextInt();
			graph = new int[nodeNum][nodeNum];
			for (int i = 0; i < nodeNum; i++){
				Arrays.fill(graph[i], Integer.MAX_VALUE);
			}
			for (int i = 0; i < nodeNum; i++){
				graph[i][i] = 0;
			}
			for (int i = 0; i < edgeNum; i++){
				int begin = sc.nextInt(), end = sc.nextInt(), len = sc.nextInt();
				graph[begin][end] = len;
				graph[end][begin] = len;
			}
			//开始读入约束
			limitNodeNum = sc.nextInt();
			nodeCstrntNum = sc.nextInt();
			edgeCstrntNum = sc.nextInt();
			for (int i = 0; i < nodeCstrntNum; i++){
				nodeCstrnt.add(sc.nextInt());
			}
			for (int i = 0; i < edgeCstrntNum; i++){
				edgeCstrnt.add(new Edge(sc.nextInt(), sc.nextInt(), sc.nextInt()));
			}
		}
		sc.close();
		fis.close();
	}
	//生成路径
	public static void genPath(){
		int node = allGraph.length - 1;
		path = new ArrayList<Integer>();
		while (pre[node] != -1){
			if ((node-pre[node])%nodeNum != 0){
				path.add(node%nodeNum);
			}
			node = pre[node];
		}
		path.add(node);
	}
	//输出路径
	public static void showPath(){
		System.out.print("建议路径为 ：");
		int cost = 0;
		for (int i = path.size() - 1; i > 0; i--){
			System.out.printf("%d -> ",path.get(i));
			cost += graph[path.get(i)][path.get(i-1)];
		}
		System.out.printf("%d, 总花费为%d, 总共经过%d个点。\n",path.get(0),cost,path.size());
	}
	
	
	public static void readString(String file) throws Exception{
		
		FileInputStream fis = new FileInputStream(file);
		Scanner sc = new Scanner(fis);
		while (sc.hasNext()){
			System.out.println(sc.nextInt());
		}
		sc.close();
		fis.close();
	}
	
	
	public static void checkMatrix(int[][] graph){
		System.out.println("矩阵为：");
		for (int i = 0; i < graph.length; i++){
			System.out.printf("%d:\t",i);
			for (int j = 0; j < graph[0].length; j++){
				System.out.printf("%d\t",graph[i][j]);
			}
			System.out.print("\n");
		}
	}

}
