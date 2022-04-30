import 'dart:developer' as dev;
import 'dart:math';

import 'package:flip_card/flip_card.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sudoku/provider/app_state.dart';
import 'package:sudoku/provider/app_theme_provider.dart';
import 'package:sudoku/widget/board.dart';
import 'package:sudoku/widget/board_tile.dart';
import 'package:sudoku/widget/change_theme_button.dart';
import 'package:sudoku/widget/keyboard.dart';

const platform = MethodChannel("com.techdroid.sudoku/generate");

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp])
      .then((_) {
    runApp(MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (context) => ThemeProvider(),
        ),
        ChangeNotifierProvider(
          create: (context) => AppState(),
        ),
      ],
      builder: (context, _) {
        return const MyApp();
      },
    ));
  });
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    var window = WidgetsBinding.instance!.window;
    var themeProvider = Provider.of<ThemeProvider>(context, listen: false);
    // This callback is called every time the brightness changes.
    window.onPlatformBrightnessChanged = () {
      var brightness = window.platformBrightness;
      themeProvider.toggleTheme(brightness == Brightness.dark);
    };

    WidgetsBinding.instance!.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance!.removeObserver(this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var themeProvider = Provider.of<ThemeProvider>(context, listen: true);
    return MaterialApp(
      title: 'Sudoku',
      themeMode: themeProvider.themeMode,
      theme: AppThemes.lightTheme,
      darkTheme: AppThemes.darkTheme,
      home: const Home(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  int _i = 0;
  int _j = 0;
  _onKeyTapped(String val) {
    dev.log("Pressed key $val");
    setState(() {
      board[_i][_j] = int.parse(val);
      dev.log("Updated board with $val at $_i :: $_j");
    });
  }

  _onUpArrowTapped() {}
  _onDownArrowTapped() {}
  _onLeftArrowTapped() {}
  _onRightArrowTapped() {}

  final List<List<GlobalKey<FlipCardState>>> _flipCardKeys = List.generate(
      9, (index) => List.generate(9, (_) => GlobalKey<FlipCardState>()));
  final List<List<int>> _board =
      List.generate(9, (index) => List.generate(9, (index) => 0));
  List<List<int>> board =
      List.generate(9, (index) => List.generate(9, (index) => 0));
  List<List<GlobalKey<BoardTileState>>> frontTileKeys = List.generate(
      9, (index) => List.generate(9, (_) => GlobalKey<BoardTileState>()));
  List<List<GlobalKey<BoardTileState>>> backTileKeys = List.generate(
      9, (index) => List.generate(9, (_) => GlobalKey<BoardTileState>()));

  @override
  void initState() {
    super.initState();
    _generate_board();
  }

  _generate_board() async {
    List<int?>? value;
    try {
      value = await platform.invokeMethod<List<int>>("generate_grid");
      int j = 0;
      for (int i = 0; i < 81; i++) {
        if (i != 0 && i % 9 == 0) {
          j++;
        }
        _board[j][i % 9] = value!.elementAt(i) ?? 0;
        if (Random().nextBool()) {
          board[j][i % 9] = _board[i % 9][j];
        }
      }
    } catch (e) {
      dev.log("$e");
    }
    dev.log("Value :: $value");
    dev.log("\nBoard :: $_board");
    setState(() {});
  }

  _restart() {
    _flipCardKeys
      ..clear()
      ..addAll(List.generate(
          9, (index) => List.generate(9, (_) => GlobalKey<FlipCardState>())));
    _generate_board();
  }

  _tileTapped(int i, int j) {
    _i = i;
    _j = j;
    dev.log("Pressed on $i :: $j");
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        appBar: AppBar(
          iconTheme: Theme.of(context).iconTheme,
          backgroundColor: Theme.of(context).scaffoldBackgroundColor,
          actions: const [ChangeTheme()],
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            Board(
              board: board,
              flipCardKeys: _flipCardKeys,
              onTap: _tileTapped,
              frontTileKeys: frontTileKeys,
              backTileKeys: backTileKeys,
            ),
            Keyboard(
                onKeyTapped: _onKeyTapped,
                onUpArrowTapped: _onUpArrowTapped,
                onDownArrowTapped: _onDownArrowTapped,
                onLeftArrowTapped: _onLeftArrowTapped,
                onRightArrowTapped: _onRightArrowTapped),
          ],
        ),
      ),
    );
  }
}
