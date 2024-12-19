# Python Sudoku Solver

Ported manually from Java version on 18-DEC-2024, initially supporting only the novice level puzzles.

## Python unit test

The unit test should be auto discovered by unittest module.

```shell
  python3 -m unittest
```

## JSON API in Flask Framework

Run the application:

```shell
  virtualenv .env
  source .env/bin/activate
  pip3 install -r requirements.txt
  flask --app sudoku.flaskapi run
```

1. Install Postman
2. Run Postman
3. Click File | Import
4. Open the "Sudoku solver.postman_collection.json" file.
5. Choose the "Solve Issue233No1" request and then Send it.

Sample output:  
![Novice1_Postman_ScreenShot.png](Novice1_Postman_ScreenShot.png?raw=true)
