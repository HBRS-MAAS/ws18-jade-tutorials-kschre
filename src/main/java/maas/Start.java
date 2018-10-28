package maas;

import java.util.List;
import java.util.Vector;
import maas.tutorials.BookBuyerAgent;
import maas.tutorials.Books;

public class Start {
    public static void main(String[] args) {
    	    	List<String> bookNames = Books.getBookList();
    	
    	// Change the buyer agents here
    	final int numberOfBuyerAgents = 20;
    	
    	//Required for shutting down correctly
    	BookBuyerAgent.numberOfAgents = numberOfBuyerAgents;
    	final int numberOfSellerAgents = 4;
    	
    	List<String> buyAgents = new Vector<>();
    	for (int i = 0; i < numberOfBuyerAgents; ++i) {
    		StringBuilder sb = new StringBuilder();
    		sb.append("buyer");
    		sb.append(i + 1);
    		sb.append(":maas.tutorials.BookBuyerAgent");
        	buyAgents.add(sb.toString());
    	}
    	
    	List<String> sellAgents = new Vector<>();
    	for (int i = 0; i < numberOfSellerAgents; ++i) {
    		StringBuilder sb = new StringBuilder();
    		sb.append("seller");
    		sb.append(i + 1);
    		sb.append(":maas.tutorials.BookSellerAgent");
        	sellAgents.add(sb.toString());
    	}
    	
    	List<String> cmd = new Vector<>();
    	cmd.add("-agents");
    	StringBuilder sb = new StringBuilder();
    	int i = 0;
    	for (String a : buyAgents) {
    		sb.append(a);
    		sb.append("(" + bookNames.get(i % bookNames.size()) + ");");
    		++i;
    	}
    	
    	i = 0;
    	for (String a : sellAgents) {
    		sb.append(a);
    		sb.append(";");
    		++i;
    	}
    	
    	cmd.add(sb.toString());
    	System.out.println(cmd);
        jade.Boot.main(cmd.toArray(new String[cmd.size()]));
    }
}
