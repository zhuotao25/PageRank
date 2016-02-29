package pagerank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PageRank {

	private static HashMap<String, ArrayList<String>> createGraph(){
		HashMap<String, ArrayList<String>> vertices=new HashMap<String, ArrayList<String>>();
		String fileName = "links.srt";
		String line = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				String[] edge=line.split("\t");
				if(edge.length==2){
					String v1=edge[0];
					String v2=edge[1];
					ArrayList<String> incomingLinks;
					if(!vertices.containsKey(v2)){
						incomingLinks=new ArrayList<String>();
					}
					else{
						incomingLinks=vertices.get(v2);
					}
					incomingLinks.add(v1);
					vertices.put(v2, incomingLinks);
				}
				else{
					System.out.println("Read an invalid line");
				}
			}   
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex) {
			System.out.println( "Unable to open file: " +fileName);                
		}
		catch(IOException ex) {
			System.out.println( "Error reading file: " +fileName); 
		}
		System.out.println("**Done**");
		System.out.println("Size: "+ vertices.size());
		return vertices;

	}

	public static void main(String arg[]){
		long lStartTime = new Date().getTime();
		
		createGraph();
		
		long lEndTime = new Date().getTime();
		long difference = lEndTime - lStartTime;
		System.out.println("Elapsed seconds: " + difference/1000+"s");
	}
}
