package pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	private static void inlinks(HashMap m) throws IOException{
		List entriesList = new LinkedList<ArrayList<String>>(m.entrySet());
		Collections.sort(entriesList, new Comparator(){
			public int compare(Object a, Object b) {
				return ((Comparable) ((ArrayList<String>) ((Map.Entry) (b)).getValue()).size())
						.compareTo(((ArrayList<String>) ((Map.Entry) (a)).getValue()).size());
			}
		});
		Iterator it = entriesList.iterator();
		int count=1;
		String fileName = "inlinks.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			
			if(count<=50){ // print the first 50 pages
				System.out.println(count+" *** "+entry.getKey()+" *** "+((ArrayList<String>) entry.getValue()).size());
				bufferedWriter.write(entry.getKey()+"\t"+((ArrayList<String>) entry.getValue()).size());
				bufferedWriter.newLine();
			}
			count++;
		}
		bufferedWriter.close();
	}

	public static void main(String arg[]) throws IOException{
		long lStartTime = new Date().getTime();

		inlinks(createGraph());

		long lEndTime = new Date().getTime();
		long difference = lEndTime - lStartTime;
		System.out.println("Elapsed seconds: " + difference/1000+"s");
	}
}
