package com.example.sudoku_solver;

import java.util.ArrayList;

/** Defines a class such that the binding/parsing from a JSON matrix will succeed. 
 * */
public class SymbolGrid extends ArrayList<ArrayList<String>> {
	
	public static SymbolGrid fromStringArrays(String[][] a) {
		SymbolGrid g = new SymbolGrid();
		for (int y = 0; y<a.length; y++) {
			ArrayList<String> row = new ArrayList<>();
			for (int x =0; x<a[y].length; x++) {
				row.add(a[y][x]);
			}
			g.add(row);
		}
		return g;
	}
}
