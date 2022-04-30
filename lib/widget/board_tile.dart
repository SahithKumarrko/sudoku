import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sudoku/provider/app_state.dart';

class BoardTile extends StatefulWidget {
  const BoardTile(
      {Key? key, required this.letter, required this.i, required this.j})
      : super(key: key);
  final int letter;
  final int i;
  final int j;
  @override
  State<BoardTile> createState() => BoardTileState();
}

class BoardTileState extends State<BoardTile> {
  @override
  Widget build(BuildContext context) {
    var appProvider = Provider.of<AppState>(context, listen: false);
    return Container(
      margin: const EdgeInsets.all(4),
      height: 24,
      width: 24,
      alignment: Alignment.center,
      decoration: BoxDecoration(
        border: (appProvider.i == widget.i && appProvider.j == widget.j)
            ? Border.all(color: Colors.red)
            : Border.all(color: Colors.black),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        widget.letter == 0 ? "" : widget.letter.toString(),
        style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
      ),
    );
  }
}
