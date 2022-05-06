import 'dart:developer' as dev;

import 'package:flip_card/flip_card.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sudoku/native/sudoku.dart';
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
  List<List<int>> indices = [];
  int _i = 0;
  int zeros = 0;
  int _j = 0;
  _onKeyTapped(String val) async {
    dev.log("Pressed key $val :: $_i | $_j");
    var appProvider = Provider.of<AppState>(context, listen: false);
    if (val == "Restart") {
      _restart();
      return;
    }
    if (appProvider.actualBoard[_i][_j] != 0) {
      dev.log("Contains ${appProvider.actualBoard[_i][_j]}");
      return;
    }
    if (board[_i][_j] == 0) {
      zeros -= 1;
    }
    setState(() {
      board[_i][_j] = int.parse(val);
      dev.log("Updated board with $val at $_i :: $_j");
    });
    bool? valid = await sudoku?.isValidPosition(board, _i, _j);

    dev.log("Valid :: $valid");
    if (valid == null) {
      dev.log("Error while validating");
    } else {
      if (!valid) {
        bool p = false;
        for (List<int> i in indices) {
          if (i[0] == _i && i[1] == _j) {
            p = true;
            break;
          }
        }
        if (!p) {
          indices.add([_i, _j]);
          flipCardKeys[_i][_j].currentState?.toggleCard();
          dev.log("Toggled wrong :: $_i :: $_j");
        }
      } else {
        var rl = [];
        for (List<int> ind in indices) {
          bool? validi =
              await sudoku?.isValidPosition(board, ind[0], ind[1]) ?? false;
          if (validi) {
            rl.add(ind);
            flipCardKeys[ind[0]][ind[1]].currentState?.toggleCard();
          }
        }
        indices.removeWhere((element) => rl.contains(element));
      }
      if (valid && zeros == 0) {
        dev.log("Won");
      }
      dev.log("Indoces :: $indices");
    }
  }

  _onUpArrowTapped() {
    var appProvider = Provider.of<AppState>(context, listen: false);
    _i = _i - (_i == 0 ? 0 : 1);
    appProvider.setPosition(row: _i, col: _j);
    dev.log("Pressed up");
  }

  _onDownArrowTapped() {
    var appProvider = Provider.of<AppState>(context, listen: false);
    _i = _i + (_i == (board.length - 1) ? 0 : 1);
    appProvider.setPosition(row: _i, col: _j);
    dev.log("Pressed down");
  }

  _onLeftArrowTapped() {
    var appProvider = Provider.of<AppState>(context, listen: false);
    _j = _j - (_j == 0 ? 0 : 1);
    appProvider.setPosition(row: _i, col: _j);
    dev.log("Pressed left");
  }

  _onRightArrowTapped() {
    var appProvider = Provider.of<AppState>(context, listen: false);
    _j = _j + (_j == (board.length - 1) ? 0 : 1);
    appProvider.setPosition(row: _i, col: _j);
    dev.log("Pressed right");
  }

  List<List<GlobalKey<FlipCardState>>> flipCardKeys = [];
  List<List<int>> board = [];

  // List<List<int>> board =
  //     List.generate(9, (index) => List.generate(9, (index) => 0));
  List<List<GlobalKey<BoardTileState>>> frontTileKeys = [];
  List<List<GlobalKey<BoardTileState>>> backTileKeys = [];

  Sudoku? sudoku;

  @override
  void initState() {
    super.initState();
    sudoku = Sudoku();
    var appProvider = Provider.of<AppState>(context, listen: false);
    appProvider.N = 9;
    _generateBoard();
  }

  _generateBoard() async {
    indices = [];
    var appProvider = Provider.of<AppState>(context, listen: false);
    int N = appProvider.N;
    flipCardKeys
      ..clear()
      ..addAll(List.generate(
          N, (index) => List.generate(N, (_) => GlobalKey<FlipCardState>())));
    frontTileKeys = List.generate(
        N, (index) => List.generate(N, (_) => GlobalKey<BoardTileState>()));
    backTileKeys = List.generate(
        N, (index) => List.generate(N, (_) => GlobalKey<BoardTileState>()));
    board = await sudoku?.generateGrid(N) ??
        List.generate(N, (index) => List.generate(N, (index) => 0));

    appProvider.from(board);
    setStartingPositions();
    for (var l1 in board) {
      for (var l2 in l1) {
        if (l2 == 0) {
          zeros += 1;
        }
      }
    }
    dev.log("Total Zeros :: $zeros");
    setState(() {});
  }

  setStartingPositions() {
    var appProvider = Provider.of<AppState>(context, listen: false);
    _i = -1;
    _j = -1;
    for (int i = 0; i < appProvider.N; i++) {
      for (int j = 0; j < appProvider.N; j++) {
        if (appProvider.actualBoard[i][j] == 0) {
          _i = i;
          _j = j;
          appProvider.setPosition(row: i, col: j);
          break;
        }
      }
      if (_i != -1 && _j != -1) {
        break;
      }
    }
  }

  _restart() {
    _i = 0;
    _j = 0;
    var appProvider = Provider.of<AppState>(context, listen: false);
    board = appProvider.copy();
    setStartingPositions();
    for (List<int> ind in indices) {
      flipCardKeys[ind[0]][ind[1]].currentState?.toggleCard();
    }
    indices = [];
    setState(() {});
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
              flipCardKeys: flipCardKeys,
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
