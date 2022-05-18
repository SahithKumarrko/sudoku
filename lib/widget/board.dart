import 'package:flip_card/flip_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sudoku/provider/app_state.dart';
import 'package:sudoku/widget/board_tile.dart';

class Board extends StatefulWidget {
  const Board({
    Key? key,
    required this.frontTileKeys,
    required this.backTileKeys,
    required this.board,
    required this.flipCardKeys,
    required this.onTap,
  }) : super(key: key);

  final List<List<int>> board;

  final List<List<GlobalKey<FlipCardState>>> flipCardKeys;

  final List<List<GlobalKey<BoardTileState>>> frontTileKeys;
  final List<List<GlobalKey<BoardTileState>>> backTileKeys;

  final void Function(int, int) onTap;

  @override
  State<Board> createState() => _BoardState();
}

class _BoardState extends State<Board> {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: widget.board
          .asMap()
          .map((i, row) => MapEntry(
                i,
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: row
                      .asMap()
                      .map(
                        (j, n) => MapEntry(
                            j,
                            InkWell(
                              onTap: () {
                                widget.onTap(i, j);
                                var appProvider = Provider.of<AppState>(context,
                                    listen: false);
                                var t1 = appProvider.i;
                                var t2 = appProvider.j;
                                appProvider.i = i;
                                appProvider.j = j;
                                widget.frontTileKeys[t1][t2].currentState
                                    ?.setState(() {
                                  print("Updating front state");
                                });
                                widget.backTileKeys[t1][t2].currentState
                                    ?.setState(() {
                                  print("Updating back state");
                                });
                                widget.frontTileKeys[i][j].currentState
                                    ?.setState(() {
                                  print("Updating front state");
                                });
                                widget.backTileKeys[i][j].currentState
                                    ?.setState(() {
                                  print("Updating back state");
                                });
                                // AppState.controller?.forward(from: 0);
                              },
                              child: FlipCard(
                                fill: Fill.fillBack,
                                key: widget.flipCardKeys[i][j],
                                flipOnTouch: false,
                                direction: FlipDirection.VERTICAL,
                                front: BoardTile(
                                  letter: n,
                                  key: widget.frontTileKeys[i][j],
                                  i: i,
                                  j: j,
                                ),
                                back: BoardTile(
                                  letter: n,
                                  key: widget.backTileKeys[i][j],
                                  backgroundColor: Colors.red,
                                  i: i,
                                  j: j,
                                ),
                              ),
                            )),
                      )
                      .values
                      .toList(),
                ),
              ))
          .values
          .toList(),
    );
  }
}
