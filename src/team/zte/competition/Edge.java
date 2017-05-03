package team.zte.competition;

public class Edge {
	private int begin;
	private int end;
	private int len;
	public int beginNode(){return begin;}
	public int endNode(){return end;}
	public int length(){return len;}
	Edge(int begin, int end, int len){
		this.begin = begin;
		this.end = end;
		this.len = len;
	}
}
