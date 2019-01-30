package maas.tutorials;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

@SuppressWarnings("serial")
public class BookSellerAgent extends Agent {

	// The catalogue of books for sale (maps the title of a book to its price)
	private Hashtable<String, Integer> catalogue;
	
	// The catalogue of books for sale (maps the title of a book to the total amount of books)
	private Hashtable<String, Integer> catalogueNumberOfBooks;
	
	// The catalogue of eBooks for sale (maps the title of a eBook to its price)
	private Hashtable<String, Integer> catalogueEbooks;

	// Put agent initializations here
	protected void setup() {
		System.out.println(getAID().getLocalName() + ": Hello! Seller-agent  is ready.");
		
		// Create the catalogue
		catalogue = new Hashtable<>();
		catalogueNumberOfBooks = new Hashtable<>();
		catalogueEbooks = new Hashtable<>();

		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		//Set the books
		List<String> bookList = Books.getBookList();
		int agentNumber = Integer.parseInt(getAID().getLocalName().substring(6));
		
		//First book (3 copies)
		catalogue.put(bookList.get(agentNumber % bookList.size()), agentNumber * 5);
		catalogueNumberOfBooks.put(bookList.get(agentNumber % bookList.size()), 3);

		// Second book (2 copies) (5 copies and 4 sellers make 20 copies)
		catalogue.put(bookList.get((agentNumber + 1) % bookList.size()), agentNumber * 5);
		catalogueNumberOfBooks.put(bookList.get((agentNumber + 1) % bookList.size()), 2);
		
		// Two eBooks (they are expensive!)
		catalogueEbooks.put(bookList.get((agentNumber + 2) % bookList.size()), agentNumber * 200);
		catalogueEbooks.put(bookList.get((agentNumber + 3) % bookList.size()), agentNumber * 200);
		
		// Add the behaviour serving requests for offer from buyer agents
		addBehaviour(new OfferRequestsServer());
		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Printout a dismissal message
		System.out.println("Seller-agent " + getAID().getLocalName() + " terminating.");
	}

	public void updateCatalogue(final String title, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(title, Integer.valueOf(price));
			}
		});
	}

	/**
	 * Inner class OfferRequestsServer. This is the behaviour used by Book-seller
	 * agents to serve incoming requests for offer from buyer agents. If the
	 * requested book is in the local catalogue the seller agent replies with a
	 * PROPOSE message specifying the price. Otherwise a REFUSE message is sent
	 * back.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				
				//Check book first
				Integer price = (Integer) catalogue.get(title);
				//Check eBook if no book found
				if (price == null) {
					price = (Integer) catalogueEbooks.get(title);
				}
				
				if (price != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(price.intValue()));
				} else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	} // End of inner class OfferRequestsServer
	
	/**
	 * Inner class PurchaseOrdersServer. This is the behaviour used by Book-seller
	 * agents to serve incoming offer acceptances (i.e. purchase orders) from buyer
	 * agents. The seller agent removes the purchased book from its catalogue and
	 * replies with an INFORM message to notify the buyer that the purchase has been
	 * sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

				boolean isBook = true;
				//Check for book first
				Integer price = (Integer) catalogue.get(title);
				//Check for eBook is not found
				if (price == null) {
					price = (Integer) catalogueEbooks.get(title);
					isBook = false;
				}
				
				
				if (price != null) {
					reply.setPerformative(ACLMessage.INFORM);
					
					//Decrease amount of books if book is sold
					if (isBook) {
						int numberOfBooks = catalogueNumberOfBooks.get(title);
						--numberOfBooks;
						catalogueNumberOfBooks.put(title, numberOfBooks);
						
						//If last book was sold, remove it
						if (numberOfBooks == 0) {
							catalogue.remove(title);
							catalogueNumberOfBooks.remove(title);
						}
					}
					
					System.out.println(getAID().getLocalName() + ": " + title + " sold to agent " + msg.getSender().getLocalName());
				} else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	} // End of inner class OfferRequestsServer
}