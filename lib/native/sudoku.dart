import 'dart:math';

import 'package:flutter/services.dart';

class DifficultyMode {
  static const double easy = 0.40;
  static const double medium = 0.60;
  static const double hard = 0.80;
}

class Sudoku {
  var platform = const MethodChannel("com.techdroid.sudoku/generate");

  Future<List<List<int>>> generateGrid(int N,
      {double mode = DifficultyMode.easy}) async {
    List<List<int>> _board =
        List.generate(N, (index) => List.generate(N, (index) => 0));
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent('N', () => N);
    List<int?>? value =
        await platform.invokeMethod<List<int>>("generate_grid", args);
    if (value != null) {
      int j = 0;
      // 40% - easy, 60% - medium, 80% - hard
      int show = (N * N * mode).round();
      var indx = [];
      var indL = List.generate(N * N, (index) => index);
      for (int i = 0; i < show; i++) {
        var ind = indL[Random().nextInt(indL.length)];
        indx.add(ind);
        indL.remove(ind);
      }
      for (int i = 0; i < N * N; i++) {
        if (i != 0 && i % N == 0) {
          j++;
        }
        if (!indx.contains(i)) {
          _board[j][i % N] = value.elementAt(i) ?? 0;
        }
      }
    }
    return _board;
  }

  Future<bool> isValidGrid(List<List<int>> grid) async {
    bool valid = false;
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent('grid', () => grid);
    args.putIfAbsent('N', () => grid.length);
    valid = await platform.invokeMethod<bool>("validate_grid", args) ?? false;
    return valid;
  }

  Future<bool> isValidPosition(List<List<int>> grid, int row, int col) async {
    bool valid = false;
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent('grid', () => grid);
    args.putIfAbsent('N', () => grid.length);
    args.putIfAbsent('row', () => row);
    args.putIfAbsent('col', () => col);
    valid = await platform.invokeMethod<bool>("validate", args) ?? false;
    return valid;
  }

  Future<Map<String, int>?> getHint(List<List<int>> grid) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent('grid', () => grid);
    args.putIfAbsent('N', () => grid.length);
    Map<String, int>? hint = {};
    Map<dynamic, dynamic>? options =
        await platform.invokeMethod<Map<dynamic, dynamic>>("get_hint", args);
    if (options != null) {
      hint.putIfAbsent("row", () => options["row"]);
      hint.putIfAbsent("col", () => options["col"]);
      hint.putIfAbsent("value", () => options["value"]);
    }
    return hint;
  }
}
