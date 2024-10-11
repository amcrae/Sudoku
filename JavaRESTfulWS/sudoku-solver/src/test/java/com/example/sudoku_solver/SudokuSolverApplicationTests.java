package com.example.sudoku_solver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SudokuSolverApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("TestOutput from Sudoku unit testing");
	}
	
	@Test
	void testExample1() {
		String[][] eg1qs = {
		    {null,null,null, null,null,"2" , "4" ,null,null},
		    {null,null,"1" , "8" ,"3" ,null, null,null,"9" },
		    {null,"8" ,null, "5" ,null,null, "1" ,"7" ,"2" },
		    {null,null,"6" , "9" ,"2" ,null, "7" ,null,"5" },
		    {"2" ,null,null, null,null,null, null,null,"6" },
		    {"5" ,null,"9" , null,"1" ,"6" , "8" ,null,null},
		    {"1" ,"7" ,"2" , null,null,"8" , null,"4" ,null},
		    {"3" ,null,null, null,"9" ,"7" , "5" ,null,null},
		    {null,null,"8" , "3" ,null,null, null,null,null}
		};
		SymbolGrid input = SymbolGrid.fromStringArrays(eg1qs);
		var puzzle = new Sudoku(input);
		puzzle.setOriginalSource("Handy Sudoku Issue 233 Number 1");
		//System.out.println(eg1);
		String[][] eg1as = {
			{"6","9","5","1","7","2","4","3","8"},
			{"7","2","1","8","3","4","6","5","9"},
			{"4","8","3","5","6","9","1","7","2"},
			{"8","4","6","9","2","3","7","1","5"},
			{"2","1","7","4","8","5","3","9","6"},
			{"5","3","9","7","1","6","8","2","4"},
			{"1","7","2","6","5","8","9","4","3"},
			{"3","6","4","2","9","7","5","8","1"},
			{"9","5","8","3","4","1","2","6","7"}
		};
		SymbolGrid expected = SymbolGrid.fromStringArrays(eg1as);
		puzzle.solve();
		//System.out.println("Arrays okay");
		SymbolGrid actual = puzzle.getGrid();
		Assertions.assertEquals(expected, actual);
	}

}
