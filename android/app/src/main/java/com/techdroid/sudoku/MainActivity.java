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
            int N = (int) methodCall.argument("N");
                 if(methodCall.method.equals("generate_grid")){
                    
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
                    ArrayList<ArrayList<Integer>> arr_grid = methodCall.argument("grid");
                    int[][] grid = new int[N][N];
                    for(int i=0;i<arr_grid.size();i++){
                        grid[i] = arr_grid.get(i).stream().mapToInt(v->v).toArray();
                    }
                    System.out.println("Received grid");

                    SudokuValidator sv = new SudokuValidator(N);
                    boolean valid = sv.isValidConfig(grid);
                    sv.print(grid);
                    System.out.println("Valid :: "+valid);
                    
                    result.success(valid);
                }else if(methodCall.method.equals("validate")){
                    ArrayList<ArrayList<Integer>> arr_grid = methodCall.argument("grid");
                    int row = methodCall.argument("row");
                    int col = methodCall.argument("col");
                    int[][] grid = new int[N][N];
                    for(int i=0;i<arr_grid.size();i++){
                        grid[i] = arr_grid.get(i).stream().mapToInt(v->v).toArray();
                    }
                    System.out.println("Received grid");

                    SudokuValidator sv = new SudokuValidator(N);
                    boolean valid = sv.isValid(grid,row,col);
                    sv.print(grid);
                    System.out.println("Valid :: "+valid);
                    
                    result.success(valid);
                }
                else if(methodCall.method.equals("get_hint")){
                    ArrayList<ArrayList<Integer>> arr_grid = methodCall.argument("grid");
                    int[][] grid = new int[N][N];
                    for(int i=0;i<arr_grid.size();i++){
                        grid[i] = arr_grid.get(i).stream().mapToInt(v->v).toArray();
                    }
                    SudokuHintGenerator sv = new SudokuHintGenerator(N);
                    List<Integer> points = sv.getRandomPoint(grid);
                    	if (sv.solveSudoku(grid, 0, 0)){
                            Map<String,Object> res = sv.getNextValue(grid,points);
                            System.out.println(res);
                            result.success(res);
                        }else{
                            result.success(null);
                        }
                    
                }
             }
         );
     }
}
