package team.zte.competition;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main {
	static final String file = "test/data.txt";
	static int nodeNum = 0, edgeNum = 0, nodeCstrntNum = 0, edgeCstrntNum = 0;
	static int[][] graph;//邻接矩阵
	static int[][] allGraph;//拆点后的邻接矩阵
	static ArrayList<Integer> nodeCstrnt = new ArrayList<Integer>();//必须经过的点
	static ArrayList<Integer> edgeCstrnt = new ArrayList<Integer>();//必须经过的点
	
	public static void main(String[] args){
		try {
			readData(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		checkMatrix(graph);
		graphTrans(nodeCstrntNum+edgeCstrntNum);
		checkMatrix(allGraph);
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
			for (int i = 0; i < edgeNum; i++){
				int begin = sc.nextInt(), end = sc.nextInt(), len = sc.nextInt();
				graph[begin][end] = len;
				graph[end][begin] = len;
			}
			//开始读入约束
			nodeCstrntNum = sc.nextInt();
			edgeCstrntNum = sc.nextInt();
			for (int i = 0; i < nodeCstrntNum; i++){
				nodeCstrnt.add(sc.nextInt());
			}
			for (int i = 0; i < edgeCstrntNum; i++){
				edgeCstrnt.add(sc.nextInt());
			}
		}
		sc.close();
		fis.close();
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
			for (int j = 0; j < graph[0].length; j++){
				System.out.printf("%d\t",graph[i][j]);
			}
			System.out.print("\n");
		}
	}
	
	
}
