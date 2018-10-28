package maas.tutorials;

import java.util.List;
import java.util.Vector;

public class Books {

	public static List<String> getBookList() {
		List<String> bookNames = new Vector<>();
    	bookNames.add("Harry Potter"); // 2 copies
    	bookNames.add("Lord of the Rings"); // 3
    	bookNames.add("Game of Thrones"); // 5
    	bookNames.add("1984"); //5
    	bookNames.add("Hunger Games"); //5
    	return bookNames;
	}
}
