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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PageRank {

	private final static double LAMBDA=0.15;
	private final static double TAU=0.01;

	// read the file and save the information of all the pages and their incoming links and outgoing links
	private static HashMap<String, ArrayList<String>> createGraph(HashMap<String, ArrayList<String>> pagesWithInlinks, HashMap<String, ArrayList<String>> pagesWithOutlinks ){
		String fileName = "links.srt"; // file to read
		String line = null; 
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while((line = bufferedReader.readLine()) != null) { // read new line
				String[] edge=line.split("\t"); // split string into array 
				if(edge.length==2){ // if the string contains two words
					String src=edge[0];
					String dest=edge[1];

					ArrayList<String> incomingLinks; // list for incoming links
					if(!pagesWithInlinks.containsKey(dest)){ // if page is not in the hashmap
						incomingLinks=new ArrayList<String>(); // create a new list
					}
					else{ // if page is in the hashmap
						incomingLinks=pagesWithInlinks.get(dest); // get the list
					}
					incomingLinks.add(src); // add the new src page to list
					pagesWithInlinks.put(dest, incomingLinks); // add the dest and its list of incoming links to hashmap

					ArrayList<String> outgoingLinks; // list of outgoing links
					if(!pagesWithOutlinks.containsKey(src)){ // if page is not in the hashmap
						outgoingLinks=new ArrayList<String>(); // create a new list
					}
					else{ // if page is in the hashmap
						outgoingLinks=pagesWithOutlinks.get(src); // get the list
					}
					outgoingLinks.add(dest); // add the new dest page to list
					pagesWithOutlinks.put(src, outgoingLinks); // add the src and its list of outgoing links to hashmap
				}
				else{
					System.out.println("Read an invalid line");
				}
			}   
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file: "+fileName);                
		}
		catch(IOException ex) {
			System.out.println("Error reading file: "+fileName); 
		}
		return pagesWithInlinks;

	}

	// combine the pages with incoming links and pages with outgoing links to get all pages
	private static HashSet<String> getAllPages(HashMap<String, ArrayList<String>> in, HashMap<String, ArrayList<String>> out){
		HashSet<String> all=new HashSet<String>();
		for (String s : out.keySet()) {
			all.add(s);
		}
		for (String s : in.keySet()) {
			all.add(s);
		}
		return all;
	}
	

	// write the first 50 pages with the most incoming links
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void inlinks(HashMap m) throws IOException{
		List entriesList = new LinkedList<ArrayList<String>>(m.entrySet()); //cover hashmap to list
		Collections.sort(entriesList, new Comparator(){ //use Comparator to sort the list
			public int compare(Object a, Object b) {
				return ((Comparable) ((ArrayList<String>) ((Map.Entry) (b)).getValue()).size())
						.compareTo(((ArrayList<String>) ((Map.Entry) (a)).getValue()).size());
			}
		});
		Iterator it = entriesList.iterator();
		int count=1;
		String fileName = "inlinks.txt"; // file to write
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName)); // create writer
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if(count<=50){ // write the first 50 pages
				//System.out.println(count+" *** "+entry.getKey()+" *** "+((ArrayList<String>) entry.getValue()).size());
				bufferedWriter.write(entry.getKey()+"\t"+count+"\t"+((ArrayList<String>) entry.getValue()).size());
				bufferedWriter.newLine();
			}
			count++;
		}
		bufferedWriter.close();
	}
	
	// a method to create a hashmap with key:String and value:Double
	private static HashMap<String,Double> createHashMap(HashSet<String> s, double v){
		HashMap<String, Double> m=new HashMap<String, Double>();
		for(String key : s)
		{
			m.put(key, v);
		}
		return m;
	}

	// calculate the pagerank
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap<String, Double> rank(HashMap<String, ArrayList<String>> out, HashSet all) throws InterruptedException{
		ArrayList allPages=new ArrayList(all); // create a list of pages
		int numPages=all.size(); // number of all pages |P|
		HashMap iPageRank=createHashMap(all,1.0/numPages);// this is I (current pagerank estimate) and initial value is 1/|P|
		HashMap rPageRank=createHashMap(all,LAMBDA/numPages); // this is R (resulting better pagerank estimate)
		double convergence=1; // set convergence to 1
		while(convergence>TAU){ // if convergence > tau
			for(int i=0;i<numPages;i++){
				String s=(String) allPages.get(i);
				rPageRank.put(s, LAMBDA/numPages); // set all R to 1/|P|
			}
			for(int i=0;i<numPages;i++){ // for all pages
				String page=(String) allPages.get(i);
				if(out.containsKey(page)){ // if current page has outgoing links
					ArrayList myOutPages=out.get(page);
					for(int j=0;j<myOutPages.size();j++){ // for each outgoing link
						String f=(String) myOutPages.get(j);
						// recalculate the pagerank
						rPageRank.put(f, (double)rPageRank.get(f)+(1-LAMBDA)*(double)iPageRank.get(page)/(double)myOutPages.size());
					}
				}// else if current page has outgoing links, we do nothing for now and will handle it at the end
			}
			
			// calculate convergence
			double sumOfDiff=0;
			for(int k=0;k<numPages;k++){
				String currPage=(String) allPages.get(k);
				double r=(double)rPageRank.get(currPage);
				double d=Math.pow((double)iPageRank.get(currPage)-r, 2);// (i-r)^2
				sumOfDiff+=d; // sum all (i-r)^2
				iPageRank.put(currPage, r); // update the pagerank
			}
			convergence=Math.sqrt(sumOfDiff); // calculate convergence
			//System.out.println("convergence: "+convergence);
		} // end while 
		
		double sumAllPR=0; // sum of all pageranks
		for(int k=0;k<numPages;k++){
			String currPage=(String) allPages.get(k);
			sumAllPR+=(double)iPageRank.get(currPage); // sum up all pagerank
		}
		
		// update page rank
		for(int g=0;g<numPages;g++){
			String curPage=(String) allPages.get(g);
			iPageRank.put(curPage, (double)iPageRank.get(curPage)/sumAllPR); //scale the pagerank so that they will sum up to 1
		}
		
		//sort the pages by pagerank
		List entriesList = new LinkedList(iPageRank.entrySet()); // cover hashmap to list
		Collections.sort(entriesList, new Comparator(){ // use Collection.sort to sort
			public int compare(Object a, Object b) {
				return ((Comparable) ((Map.Entry) (b)).getValue()).compareTo(((Map.Entry) (a)).getValue());
			}
		});
		LinkedHashMap sortedPages = new LinkedHashMap(); // create a linked hashmap
		Iterator it = entriesList.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedPages.put(entry.getKey(), entry.getValue()); // put the sorted pages to the linked hashmap
		}
		return sortedPages;
	}

	public static void main(String arg[]) throws IOException, InterruptedException{
		HashMap<String, ArrayList<String>> in=new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> out=new HashMap<String, ArrayList<String>>();
		
		//////////////Part A/////////////////
		createGraph(in,out); // store information about pages, incoming links and outgoing links
		inlinks(in); // PartA: write the first 50 pages with most incoming links to the file
		
		//////////////Part B//////////////////
		HashSet<String> allPages=getAllPages(in,out);
		LinkedHashMap<String,Double> gg=rank(out,allPages);// calculate page rank
		String fileName = "pagerank.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
		int count=0;
		//  write the first 50 pages with the highest page rank to the file
		for(Map.Entry<String, Double> entry : gg.entrySet()){ 
			String s=entry.getKey()+"\t"+(count+1)+"\t"+entry.getValue();
			//System.out.printf("%s\t%s%n", entry.getKey(), entry.getValue()); 
			bufferedWriter.write(s);
			count++;
			if(count>=50){
				break;
			}
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}
}
