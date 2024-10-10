package com.example.sudoku_solver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sudoku_solver.*;


@RestController
@RequestMapping("/api/sudokusolver")
public class SudokuController {

	@PostMapping
	public Sudoku solveGrid(@RequestBody Sudoku puzzle) {
		//Sudoku puzzle = new Sudoku(grid);
		puzzle.solve();
		return puzzle;
	}
}
