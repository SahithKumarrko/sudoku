import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sudoku/provider/app_theme_provider.dart';
import 'package:sudoku/widget/change_theme_button.dart';

const platform = MethodChannel("com.techdroid.sudoku/generate");
//   void _incrementCounter() async {
//     List<int?>? value;
//     try {
//       value = await platform.invokeMethod<List<int>>("generate_grid");
//     } catch (e) {
//       log("$e");
//     }
//     log("Value :: $value");
//   }

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(ChangeNotifierProvider(
    create: (context) => ThemeProvider(),
    builder: (context, _) {
      return const MyApp();
    },
  ));
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
  @override
  Widget build(BuildContext context) {
    return const SafeArea(
      child: Scaffold(
        body: Center(
          child: ChangeTheme(),
        ),
      ),
    );
  }
}
