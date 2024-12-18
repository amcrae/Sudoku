import unittest
from sudoku.sudoku import Sudoku

class TestSudoku(unittest.TestCase):

    def test_no1(self):
        no1grid = [
            [None,None,None, None,None,'2' , '4' ,None,None],
            [None,None,'1' , '8' ,'3' ,None, None,None,'9' ],
            [None,'8' ,None, '5' ,None,None, '1' ,'7' ,'2' ],
            [None,None,'6' , '9' ,'2' ,None, '7' ,None,'5' ],
            ['2' ,None,None, None,None,None, None,None,'6' ],
            ['5' ,None,'9' , None,'1' ,'6' , '8' ,None,None],
            ['1' ,'7' ,'2' , None,None,'8' , None,'4' ,None],
            ['3' ,None,None, None,'9' ,'7' , '5' ,None,None],
            [None,None,'8' , '3' ,None,None, None,None,None]
        ]
        no1soln = [
            ['6' ,'9' ,'5' , '1' ,'7' ,'2' , '4' ,'3' ,'8' ],
            ['7' ,'2' ,'1' , '8' ,'3' ,'4' , '6' ,'5' ,'9' ],
            ['4' ,'8' ,'3' , '5' ,'6' ,'9' , '1' ,'7' ,'2' ],
            ['8' ,'4' ,'6' , '9' ,'2' ,'3' , '7' ,'1' ,'5' ],
            ['2' ,'1' ,'7' , '4' ,'8' ,'5' , '3' ,'9' ,'6' ],
            ['5' ,'3' ,'9' , '7' ,'1' ,'6' , '8' ,'2' ,'4' ],
            ['1' ,'7' ,'2' , '6' ,'5' ,'8' , '9' ,'4' ,'3' ],
            ['3' ,'6' ,'4' , '2' ,'9' ,'7' , '5' ,'8' ,'1' ],
            ['9' ,'5' ,'8' , '3' ,'4' ,'1' , '2' ,'6' ,'7' ]
        ]
        puzzle = Sudoku()
        puzzle.grid = no1grid
        puzzle.original_source = "Handy Sudoku Issue 233 No 1"
        puzzle.solve()
        for x in range(9):
            for y in range(9):
                self.assertEqual(
                    puzzle.grid[x][y],
                    no1soln[x][y],
                    f"Mismtach at {x},{y}"
                )
        self.assertTrue(puzzle.was_solved(),"not solved!")