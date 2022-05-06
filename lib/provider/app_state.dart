import 'dart:developer';

import 'package:flutter/cupertino.dart';

class AppState extends ChangeNotifier {
  int i = 0;
  int j = 0;
  int N = 9;
  List<List<int>> actualBoard = [];
  setPosition({required int row, required int col}) {
    i = row;
    j = col;
    log("Moving to $i : $j  ($row :: $col)");
    notifyListeners();
  }

  from(List<List<int>> board) {
    actualBoard = [];
    for (var i = 0; i < N; i++) {
      actualBoard.add([]);
      for (var j = 0; j < N; j++) {
        actualBoard[i].add(board[i][j]);
      }
    }
  }

  List<List<int>> copy() {
    List<List<int>> board = [];
    for (var i = 0; i < N; i++) {
      board.add([]);
      for (var j = 0; j < N; j++) {
        board[i].add(actualBoard[i][j]);
      }
    }
    return board;
  }
}
