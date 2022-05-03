package com.techdroid.sudoku;

import java.util.ArrayList;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.techdroid.sudoku/generate";
    private MethodChannel channel;
    @Override
     public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine){
         super.configureFlutterEngine(flutterEngine);
         BinaryMessenger messenger = flutterEngine.getDartExecutor().getBinaryMessenger();
         channel = new MethodChannel(messenger,CHANNEL);
         channel.setMethodCallHandler((methodCall,result) -> {
                 if(methodCall.method.equals("generate_grid")){
                    int N = (int) call.argument("N");
                    int[] res = new int[N];
                    int trials = 0;
                    
                    while(true)
                    {
                        SudokuGridGenerator sudoku = new SudokuGridGenerator(N);
                        trials++;
                        //average solution time hovers around 7 microseconds (~0.000007 seconds)
                        //not including time it takes for print statements, which eats up BUTT-LOADS of time and slows down the output 10-fold
                        res = sudoku.generateGrid();
                        System.out.println("Grid Generated");
                        if(sudoku.isPerfect(res)) 
                        {
                            System.out.println("PERFECT GRID #" + String.format("%,d",(trials)));
                            break;
                        }
                    }
                    System.out.println("Returning");
                    result.success(res);
                 }else if(methodCall.method.equals("validate_grid")){
                    int[][] grid = (int[]) call.argument("grid");
                    int N = (int) call.argument("N");
                    SudokuValidator sv = new SudokuValidator(N);
                    result.success(sv.isValidConfig(board));
                }
                else if(methodCall.method.equals("get_hint")){
                    int[][] grid = (int[]) call.argument("grid");
                    int N = (int) call.argument("N");
                    SudokuHintGenerator sv = new SudokuHintGenerator(N);
                    List<Integer> points = sv.getRandomPoint(grid);
                    	if (sv.solveSudoku(grid, 0, 0)){
                            Map<String,Object> res = sv.getNextValue(grid,points);
                            System.out.println(res);
                            result.success(res);
                        }
                    result.success(null);
                }
             }
         );
     }
}
