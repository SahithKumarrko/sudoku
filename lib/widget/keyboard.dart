import 'package:flutter/material.dart';

const _keys = [
  ['1', '2', '3'],
  ['4', '5', '6'],
  ['7', '8', '9'],
  ['U', 'D', 'L', 'R']
];

class Keyboard extends StatelessWidget {
  const Keyboard({
    Key? key,
    required this.onKeyTapped,
    required this.onUpArrowTapped,
    required this.onDownArrowTapped,
    required this.onLeftArrowTapped,
    required this.onRightArrowTapped,
  }) : super(key: key);

  final void Function(String) onKeyTapped;

  final VoidCallback onUpArrowTapped;

  final VoidCallback onDownArrowTapped;

  final VoidCallback onLeftArrowTapped;

  final VoidCallback onRightArrowTapped;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: _keys
          .map(
            (keyRow) => Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: keyRow.map((letter) {
                if (letter == 'U') {
                  return _KeyboardButton.up(onTap: onUpArrowTapped);
                } else if (letter == 'U') {
                  return _KeyboardButton.up(onTap: onUpArrowTapped);
                } else if (letter == 'U') {
                  return _KeyboardButton.up(onTap: onUpArrowTapped);
                } else if (letter == 'U') {
                  return _KeyboardButton.up(onTap: onUpArrowTapped);
                }
                return _KeyboardButton(
                  onTap: () => onKeyTapped(letter),
                  letter: letter,
                  backgroundColor: Colors.grey,
                );
              }).toList(),
            ),
          )
          .toList(),
    );
  }
}

class _KeyboardButton extends StatelessWidget {
  const _KeyboardButton({
    Key? key,
    this.height = 36,
    this.width = 36,
    required this.onTap,
    required this.backgroundColor,
    required this.letter,
  }) : super(key: key);

  factory _KeyboardButton.up({required VoidCallback onTap}) {
    return _KeyboardButton(
        onTap: onTap, backgroundColor: Colors.grey, letter: 'U');
  }

  final double height;
  final double width;
  final String letter;
  final VoidCallback onTap;
  final Color backgroundColor;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3.0, horizontal: 2.0),
      child: Material(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(4),
        child: InkWell(
          onTap: onTap,
          child: Container(
            height: height,
            width: width,
            alignment: Alignment.center,
            child: Text(
              letter,
              style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
            ),
          ),
        ),
      ),
    );
  }
}
