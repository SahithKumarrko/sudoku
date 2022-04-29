package com.techdroid.sudoku;

import java.util.ArrayList;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import androidx.annotation.NonNull;

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
                    int[] res = new int[81];
                    int trials = 0;
                    while(true)
      {
         SudokuGridGenerator sudoku = new SudokuGridGenerator();
         trials++;
         //average solution time hovers around 7 microseconds (~0.000007 seconds)
         //not including time it takes for print statements, which eats up BUTT-LOADS of time and slows down the output 10-fold
         res = sudoku.generateGrid();
         System.out.println("Grid Generated");
         if(sudoku.isPerfect(res)) 
         {System.out.println("PERFECT GRID #" + String.format("%,d",(trials)));
         
         break;

      }
    }
    System.out.println("Returning");
                    result.success(res);
                 }
             }
         );
     }
}
