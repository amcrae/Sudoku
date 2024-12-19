import json
from flask import Flask, request, jsonify
from sudoku.sudoku import Sudoku

app = Flask(__name__)

@app.route("/")
def hello_world():
    return "<p>Hello, Flask World!</p>"

@app.route("/api/sudokusolver", methods=['POST'])
def post_solve_sudoku():
    rqobj = json.loads(request.data)
    osrc = rqobj["originalSource"]
    puzzle = Sudoku()
    puzzle.grid = rqobj["grid"]
    puzzle.original_source = osrc
    puzzle.solve()
    answer = {
        "originalSource": puzzle.original_source,
        "solveMessage": puzzle.solve_message,
        "grid":puzzle.grid
    }
    return jsonify(answer)
