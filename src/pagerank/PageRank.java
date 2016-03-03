package pagerank;

import com.sun.org.apache.xpath.internal.SourceTree;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PageRank {

	private final static double LAMDA=0.15;

	private static HashMap<String, ArrayList<String>> createGraph(HashMap<String, ArrayList<String>> pagesWithInlinks, HashMap<String, ArrayList<String>> pagesWithOutlinks ){
		String fileName = "links.srt";
		String line = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				String[] edge=line.split("\t");
				if(edge.length==2){
					String src=edge[0];
					String des=edge[1];

					ArrayList<String> incomingLinks;
					if(!pagesWithInlinks.containsKey(des)){
						incomingLinks=new ArrayList<String>();
					}
					else{
						incomingLinks=pagesWithInlinks.get(des);
					}
					incomingLinks.add(src);
					pagesWithInlinks.put(des, incomingLinks);

					ArrayList<String> outgoingLinks;
					if(!pagesWithOutlinks.containsKey(src)){
						outgoingLinks=new ArrayList<String>();
					}
					else{
						outgoingLinks=pagesWithOutlinks.get(src);
					}
					outgoingLinks.add(des);
					pagesWithOutlinks.put(src, outgoingLinks);
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
		System.out.println("Size in: "+ pagesWithInlinks.size());
		System.out.println("Size out: "+ pagesWithOutlinks.size());
		return pagesWithInlinks;

	}

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

	private static double[] rank(HashMap<String, ArrayList<String>> in, HashMap<String, ArrayList<String>> out, HashSet all) throws InterruptedException{
		//		ArrayList listOfPagesWithInPages = new ArrayList(m.entrySet());
		//		ArrayList listOfPages=new ArrayList();
		//		for(Entry<String, ArrayList<String>> entry : m.entrySet()){
		//			listOfPages.add(entry.getKey());
		//		}
		ArrayList allPages=new ArrayList(all);
		boolean end=false;
		boolean end2=false;
		int numPages=all.size();
		double[] a=new double[numPages];
		double[] b=new double[numPages];
		for(int i=0;i<numPages;i++){
			a[i]=1/(double)numPages;
			System.out.println("a[]="+a[i]);
		}
		while(true){
			for(int i=0;i<numPages;i++){
				b[i]=LAMDA/numPages;
				System.out.println("b[]="+b[i]);
			}
			for(int i=0;i<numPages;i++){
				String page=(String) allPages.get(i);
				System.out.println("page: "+page);
				if(out.containsKey(page)){
					ArrayList myOutPages=out.get(page);
					for(int j=0;j<myOutPages.size();j++){
						int pageIndex=allPages.indexOf(myOutPages.get(j));
						int pageIndex2=allPages.indexOf(page);
						System.out.println("?="+b[pageIndex]+"+"+(1-LAMDA)+"*"+a[pageIndex2]+"/"+myOutPages.size());
						b[pageIndex]=b[pageIndex]+(1-LAMDA)*a[pageIndex2]/(double)myOutPages.size();

						System.out.println(myOutPages.get(j)+":"+b[pageIndex]);
					}
				}
				else{// no outgoing
					for(int j=0;j<numPages;j++){
						b[j]=b[j]+(1-LAMDA)*a[i]/numPages;
						System.out.println("no out");
					}
				}
				//Thread.sleep(2000);
				for(int k=0;k<numPages;k++){
					System.out.println(a[k]+"-"+b[k]);
					if(Math.abs(a[k]-b[k])<(0.001)){
						if(end==false&&end==false)
							end2=true;
						else if(end==false)
							end=true;
					}
					else{
						end=false;
						end2=false;
					}
					a[k]=b[k];
				}
				if(end){
					return b;
				}
			}

		}
		//return b;
	}

    private static double[] rank2(HashMap<String, ArrayList<String>> in, HashMap<String, ArrayList<String>> out, HashSet all) throws InterruptedException{
        ArrayList allPages=new ArrayList(all);
        boolean end=false;
        boolean end2=false;
        int numPages=all.size();
        double[] a=new double[numPages];
        double[] b=new double[numPages];
        for(int i=0;i<numPages;i++){
            a[i]=1/(double)numPages;
            System.out.println("a[]="+a[i]);
        }
        while(true){
            for(int i=0;i<numPages;i++){
                b[i]=LAMDA/numPages;
                System.out.println("b[]="+b[i]);
            }
            for(int i=0;i<numPages;i++) {
                String page=(String) allPages.get(i);
                if(in.containsKey(page)){
                    ArrayList myin=in.get(page);
                    for(int j=0;j<myin.size();j++){
                        int pageIndex=allPages.indexOf(myin.get(j));
                        int pageIndex2=allPages.indexOf(page);
                        b[pageIndex2]=b[pageIndex2]+0.85*a[pageIndex]/out.get(myin.get(j)).size();
                        System.out.println(page+": "+b[pageIndex2]);
                    }
                }

            }
//            for(int i=0;i<numPages;i++){
//                String page=(String) allPages.get(i);
//                System.out.println("page: "+page);
//                if(out.containsKey(page)){
//                    ArrayList myOutPages=out.get(page);
//                    for(int j=0;j<myOutPages.size();j++){
//                        int pageIndex=allPages.indexOf(myOutPages.get(j));
//                        int pageIndex2=allPages.indexOf(page);
//                        System.out.println("?="+b[pageIndex]+"+"+(1-LAMDA)+"*"+a[pageIndex2]+"/"+myOutPages.size());
//                        b[pageIndex]=b[pageIndex]+(1-LAMDA)*a[pageIndex2]/(double)myOutPages.size();
//
//                        System.out.println(myOutPages.get(j)+":"+b[pageIndex]);
//                    }
//                }
//                else{// no outgoing
//                    for(int j=0;j<numPages;j++){
//                        b[j]=b[j]+(1-LAMDA)*a[i]/numPages;
//                        System.out.println("no out");
//                    }
//                }
                Thread.sleep(2000);
                for(int k=0;k<numPages;k++){
                    System.out.println(a[k]+"-"+b[k]);
                    if(Math.abs(a[k]-b[k])<(0.001)){
                        if(end==false&&end==false)
                            end2=true;
                        else if(end==false)
                            end=true;
                    }
                    else{
                        end=false;
                        end2=false;
                    }
                    a[k]=b[k];
                }
                if(end){
                    return b;
                }
            }

        }
        //return b;



    public static void main(String arg[]) throws IOException, InterruptedException{
		long lStartTime = new Date().getTime();
//		HashMap<String, ArrayList<String>> in=new HashMap<String, ArrayList<String>>();
//		HashMap<String, ArrayList<String>> out=new HashMap<String, ArrayList<String>>();
//		createGraph(in,out);
//		HashSet all=getAllPages(in,out);
//		System.out.println("Number of Pages: " + all.size());
		HashMap<String, ArrayList<String>> in=new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> out=new HashMap<String, ArrayList<String>>();
		ArrayList ab=new ArrayList();
		ab.add("b");
		ab.add("a");
		ArrayList ac=new ArrayList();
		ac.add("a");
		ac.add("c");
		ArrayList bc=new ArrayList();
		bc.add("b");
		bc.add("c");
		in.put("a", bc);
		in.put("b", ac);
		in.put("c", ab);
		out.put("a", bc);
		out.put("b", ac);
		out.put("c", ab);
		HashSet all=getAllPages(in,out);
		double[] gg=rank2(in,out,all);
		for(int s=0;s<all.size();s++){
			System.out.println(gg[s]);
		}

		long lEndTime = new Date().getTime();
		long difference = lEndTime - lStartTime;
		System.out.println("Elapsed seconds: " + difference/1000+"s");
	}
}
