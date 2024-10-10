package com.example.sudoku_solver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sudoku puzzle solver. 
 * Currently handles novice-level and intermediate-level puzzles only.
 * 
 * @author Andrew McRae
 * First version 2024-10-05.
 * */
public class Sudoku {

	private SymbolGrid board;
	private String originalSource;
	private String solveMessage;
	private Set<String>[][] possibles;
	private static Logger logger = LoggerFactory.getLogger(Sudoku.class);

	public Sudoku() {
		this.board = new SymbolGrid();
		this.possibles = new HashSet[9][9];  //Cannot specify type parameter.
		resetPossibles();
	}

	public Sudoku(SymbolGrid givens) {
		this();
		this.board = givens;
	}
	
	public SymbolGrid getGrid() {
		return board;
	}

	public void setGrid(SymbolGrid grid) {
		this.board = grid;
	}

	public String getOriginalSource() {
		return originalSource;
	}

	public void setOriginalSource(String originalSource) {
		this.originalSource = originalSource;
	}

	public String getSolveMessage() {
		return solveMessage;
	}

	public void setSolveMessage(String solveMessage) {
		this.solveMessage = solveMessage;
	}
	
	private static List<String> ALL_DIGITS = Arrays.asList(new String[] {"1","2","3","4","5","6","7","8","9"} );
	
	/**
	 * Establish the no-constraints state where all digits could be in all positions.
	 */
	private void resetPossibles() {
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				HashSet<String> all = new HashSet<String>(
					ALL_DIGITS
				);
				this.possibles[r][c] = all;
			}
		}
	}
	
	private int countPossibles() {
		int answer = 0;
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				answer += this.possibles[r][c].size();
			}
		}
		return answer;
	}

	
    // ----Rules sufficient for novice level-------------------------

	
    private void eliminate_row_poss(int rindex, int col, String answer){
        var row = this.possibles[rindex];
        for (int cindex = 0; cindex < row.length; cindex++) {
            if (cindex != col) {
                var pset = row[cindex];
                pset.remove(answer);
            }
        }
    }

    private void eliminate_col_poss(int row, int col, String answer) {
        for (int rindex = 0; rindex < this.possibles.length; rindex++) {
            if (rindex!=row) {
                var rowa = this.possibles[rindex];
                var pset = rowa[col];
                pset.remove(answer);
            }
        }
    }

    private static class GridRange {
    	public int row_min;
    	public int row_max;
    	public int col_min;
    	public int col_max;
    }
    
    /** Produce index limits for a subsquare of the sudoku board. */
    private static GridRange subsquare_ranges(int row, int col) {
        //answer is the subsquare containing (row,col).
        GridRange answer = new Sudoku.GridRange();
        answer.row_min = row-(row%3);
        answer.col_min = col-(col%3);
        answer.row_max = answer.row_min+2;
        answer.col_max = answer.col_min+2;
        return answer;
    }

    private void eliminate_sqr_poss(int row, int col, String answer) {
        var sqr_range = subsquare_ranges(row,col);
        for (int rindex = sqr_range.row_min; rindex <= sqr_range.row_max; rindex++) {
            var rowa = this.possibles[rindex];
            for (int cindex = sqr_range.col_min; cindex <= sqr_range.col_max; cindex++) {
                if (!(rindex==row && cindex==col)) {
                    var pset = rowa[cindex];
                    pset.remove(answer);
                }
            }
        }
    }

    /** Eliminate answer as a non-possibility in related positions.
     * 
     */
    private void eliminate_possibles(int row, int col, String answer) {
        this.eliminate_row_poss(row, col, answer);
        this.eliminate_col_poss(row, col, answer);
        this.eliminate_sqr_poss(row, col, answer);
    }

    /** Where a position has been given a digit, eliminate that possibility
     * from all the other positions related by the sudoku rules.
     * 
     */
    private void exclude_givens() {
        for (int rindex = 0; rindex < this.board.size(); rindex++) {
            var row = this.board.get(rindex);
            for (int cindex = 0; cindex < row.size(); cindex++) {
                var element = row.get(cindex);
                if (element != null && element != " ") {
                    this.eliminate_possibles(rindex,cindex,element);
                    this.possibles[rindex][cindex] = new HashSet<String>();
                    this.possibles[rindex][cindex].add(element);
                }
            }
        }
    }
	

    /** Where a position has been found to have only 1 possibility remaining,
     * fix the value in the board to that possibility.
     */
    private void fix_solitaries() {
        for (int rindex = 0; rindex < this.possibles.length; rindex++) {
            var row = this.possibles[rindex];
            for (int cindex = 0; cindex < row.length; cindex++) {
                var poss = row[cindex];
                if (poss.size()==1) {
                    var fix = poss.toArray(new String[1])[0];
                    if (this.board.get(rindex).get(cindex) == null)
                    	logger.info(String.format("Found %d,%d is %s",rindex,cindex,fix));
                    this.board.get(rindex).set(cindex, fix);
                }
            }
        }        
    }

    // ----Intermediate level----------------------------------------


    private boolean poss_elsewhere_in_row(String poss, int rindex, int col) {
        var row = this.possibles[rindex];
        for (int cindex = 0; cindex < row.length; cindex++) {
            if (cindex != col) {
                var pset = row[cindex];
                if (pset.contains(poss)) return true;
            }
        }
        return false;
    }
    
    private boolean poss_elsewhere_in_col(String poss, int row, int col) {
        for (int rindex = 0; rindex < this.possibles.length; rindex++) {
            if (rindex!=row) {
                var rowa = this.possibles[rindex];
                var pset = rowa[col];
                if (pset.contains(poss)) return true;
            }
        }
        return false;
    }    
    
    private boolean poss_elsewhere_in_sqr(String poss, int row, int col) {
        GridRange sqr_range = this.subsquare_ranges(row,col);
        for (int rindex = sqr_range.row_min; rindex <= sqr_range.row_max; rindex++) {
            var rowa = this.possibles[rindex];
            for (int cindex = sqr_range.col_min; cindex <= sqr_range.col_max; cindex++) {
                if (!(rindex==row && cindex==col)) {
                    var pset = rowa[cindex];
                    if(pset.contains(poss)) return true;
                }
            }
        }
        return false;
    }
    
    /** Check if any of the possibilities in possibles do not occur elsewhere
     * in the same row or col or square, and return that possibility.
     * Return null if all possibilities in possibles are found elsewhere.
    */
    private String find_singular_poss(Set<String> possibles, int rindex, int cindex) {
        for (String poss : possibles) {
            var in_row = this.poss_elsewhere_in_row(poss,rindex,cindex);
            var in_col = this.poss_elsewhere_in_col(poss,rindex,cindex);
            var in_sqr = this.poss_elsewhere_in_sqr(poss,rindex,cindex);
            //Any of the 3 rules can determine the possibility is singular.
            var singular = !in_row || !in_col || !in_sqr;
            if (singular) return poss;
        }
        return null;
    }    
    
    /** Where a possibility occurs in only one position in the
     * same row, or column, or square, it must be answer.
    */
    private void fix_group_inevitables() {
        for (int rindex = 0; rindex < this.board.size(); rindex++) {
            var row = this.board.get(rindex);
            for (int cindex = 0; cindex < row.size(); cindex++) {
                String element = row.get(cindex);
                var posses = this.possibles[rindex][cindex];
                // Looking for cells which are still unknown.
                if ((element == null) && posses.size()>1) {
                    var singular = this.find_singular_poss(posses,rindex,cindex);
                    if (singular!=null) {
                    	logger.info(String.format("Found %d,%d is %s.", rindex,cindex,singular));
                    	row.set(cindex, singular);
                    }
                }
            }
        }
    }
    
    // ----Solved?---------------------------------------------------
    
    /** Check if the Sudoku is solved.
     * Cannot be called is_solved due to creating an implicit property by introspectors.
    */
    public boolean was_solved() {
        var target = new HashSet<String>(ALL_DIGITS);
        //row condition
        for (int rindex = 0; rindex < this.board.size(); rindex++) {
            var row = this.board.get(rindex);
            var found = new HashSet<String>();
            for (int cindex = 0; cindex < row.size(); cindex++) {
                var element = row.get(cindex);
                found.add(element);
            }
            if (!found.equals(target)) return false;
        }
        // col condition
        for (int cindex = 0; cindex < 9; cindex++) {
            var found = new HashSet<String>();
            for (int rindex = 0; rindex < this.board.size(); rindex++) {
                var row = this.board.get(rindex);
                var element = row.get(cindex);
                found.add(element);
            }
            if (!found.equals(target)) return false;
        }
        // square condition
        for (var srow=0; srow<9;srow+=3) {
            for (var scol=0; scol<9; scol+=3) {
                var sqr_elems = subsquare_ranges(srow,scol);
                var found = new HashSet<String>();
                for (var rindex=sqr_elems.row_min; rindex<=sqr_elems.row_max;rindex++){
                    for (var cindex=sqr_elems.col_min; cindex<=sqr_elems.col_max;cindex++){
                        var elem = this.board.get(rindex).get(cindex);
                        found.add(elem);
                    }
                }
                if (!found.equals(target)) return false;
            }
        }
        //no difference from target found.
        return true;
    }
    
	public void solve() {
		logger.info("Ran solve()");
		resetPossibles();
		int deltaPossibles = 0;
		int its = 0;
		do {
			int poss = countPossibles();
			exclude_givens();
			this.fix_solitaries();
			this.fix_group_inevitables();
			this.setSolveMessage("novice solve");
			int possPrime = countPossibles();
			deltaPossibles = possPrime-poss;
			logger.info(String.format("Poss delta was %d", deltaPossibles));
			its+=1;
		} while (deltaPossibles<0);
		if (was_solved()) {
			this.solveMessage = String.format("Solved in %d iterations", its);
		} else {
			this.solveMessage = String.format("Unsolved after %d iterations", its);
		}
	}
}
