package program.view;

import java.util.Comparator;

public class MiscColumnSorter implements Comparator<String>{


    int colIndex;
    boolean ascending;


	public MiscColumnSorter(int colIndex, boolean ascending) {
		this.colIndex=colIndex;
		this.ascending=ascending;
	}

	@Override
	public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	}

}
