from typing import List
from dataclasses import dataclass
import logging

class Sudoku:
    """Sudoku puzzle solver.

       Currently handles novice-level puzzles.
    """
    DIGITS = ['1','2','3','4','5','6','7','8','9']
    
    _NONE_GRID = [ [None for c in range(9)] for r in range(9)]

    @property
    def grid(self):
        return self._grid
    
    @grid.setter
    def grid(self, g:List[List[str]]):
        self._grid = g

    def __init__(self, grid=_NONE_GRID):
        self._logger = logging.getLogger('Sudoku')
        self._grid = grid
        self.original_source = None
        self.solve_message = None
        self._possibles = Sudoku.all_possibles()

    @staticmethod
    def all_possibles():
        answer = []
        for i in range(9):
            row = []
            for j in range(9):
                row.append(set(Sudoku.DIGITS))
            answer.append(row)
        return answer

    def _count_possibles(self) -> int:
        answer = 0
        for row in self._possibles:
            for cell in row:
                answer += len(cell)
        return answer

    # ----Rules sufficient for novice level: -------------------------

    def _eliminate_row_poss(self, rindex:int, col:int, answer:str):
        row = self._possibles[rindex]
        for cindex in range(len(row)):
            if (cindex!=col):
                poss=row[cindex]
                poss.discard(answer)

    def _eliminate_col_poss(self, row:int, cindex:int, answer:str):
        for rindex in range(len(self._possibles)):
            if rindex != row:
                pset = self._possibles[rindex][cindex]
                pset.discard(answer)

    @dataclass
    class GridRange:
        row_min: int
        row_max: int
        col_min: int
        col_max: int
    
    @staticmethod
    def subsquare_range(row:int, col:int):
        """Produce index limits for a subsquare of the sudoku board.
            answer is the subsquare containing (row,col).
        """
        answer = Sudoku.GridRange(0,0,0,0)
        answer.row_min = row-(row % 3)
        answer.row_max = answer.row_min+2
        answer.col_min = col-(col%3)
        answer.col_max = answer.col_min+2
        return answer
    
    def _eliminate_sqr_poss(self, row:int, col:int, answer: str):
        sqr_range = Sudoku.subsquare_range(row,col)
        for rindex in range(sqr_range.row_min,sqr_range.row_max+1):
            rowa = self._possibles[rindex]
            for cindex in range(sqr_range.col_min,sqr_range.col_max+1):
                if not(rindex==row and cindex==col):
                    pset = rowa[cindex]
                    pset.discard(answer)

    def _eliminate_possibles(self, row:int, col:int, answer:str):
        """Eliminate answer as a non-possibility in related positions.
        """
        self._eliminate_row_poss(row,col, answer)
        self._eliminate_col_poss(row,col,answer)
        self._eliminate_sqr_poss(row,col,answer)

    def _exclude_givens(self):
        """ Where a position has been given a digit, eliminate that possibility
            from all the other positions related by the sudoku rules.
        """
        for rindex in range(len(self._grid)):
            row = self._grid[rindex]
            for cindex in range(len(row)):
                element = row[cindex]
                if element != None and element != " ":
                    self._eliminate_possibles(rindex,cindex,element)
                    self._possibles[rindex][cindex] = set([element])

    def _fix_solitaries(self):
        """Where a position has been found to have only 1 possibility remaining,
            fix the value in the board to that possibility.
        """
        for rindex in range(len(self._possibles)):
            row = self._possibles[rindex]
            for cindex in range(len(row)):
                poss = row[cindex]
                if len(poss) == 1:
                    fix = poss.pop()
                    if self._grid[rindex][cindex] == None:
                        self._logger.info(f"Found {rindex},{cindex} is {fix}")
                    self._grid[rindex][cindex] = fix

    # ----Solved?---------------------------------------------------

    def was_solved(self):
        """Check if the Sudoku is solved."""
        target = frozenset(Sudoku.DIGITS)
        # row condition
        for rindex in range(9):
            row = self._grid[rindex]
            found = set()
            for cindex in range(9):
                element = row[cindex]
                found.add(element)
            if not(found == target):
                return False
        # col condition
        for cindex in range(9):
            found = set()
            for rindex in range(9):
                element = self._grid[rindex][cindex]
                found.add(element)
            if not(found == target):
                return False
        # square condition
        for srow in range(0,9,3):
            for scol in range(0,9,3):
                sqr_indices = Sudoku.subsquare_range(srow,scol)
                found = set()
                for rindex in range(sqr_indices.row_min,sqr_indices.row_max+1):
                    for cindex in range(sqr_indices.col_min,sqr_indices.col_max+1):
                        elem = self._grid[rindex][cindex]
                        found.add(elem)
                if not(found == target):
                    return False
        # no difference from target
        return True
    
    def solve(self):
        self._logger.info("Began Sudoku solve")
        self._possibles = Sudoku.all_possibles()
        deltaPossibles = -1
        its = 0
        while deltaPossibles<0:
            poss = self._count_possibles()
            self._exclude_givens()
            self._fix_solitaries()
            possPrime = self._count_possibles()
            deltaPossibles = possPrime - poss
            self._logger.info(f"Poss delta was {deltaPossibles}")
            its += 1
        if self.was_solved():
            self.solve_message = f"Solved in {its} iterations"
        else:
            self.solve_message = f"Unsolved after {its} iterations"