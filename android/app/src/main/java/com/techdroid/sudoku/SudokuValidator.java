
package com.techdroid.sudoku;
// Java Program to check whether given sudoku
// board is valid or not
import java.io.*;
import java.util.*;
import java.lang.Math;
 
public class SudokuValidator{
 int N = 9;
 int sqrt_n = 3;

 public SudokuValidator(int N){
     this.N = N;
     this.sqrt_n = (int) Math.sqrt(this.N);
 }

 /* A utility function to print grid */
 void print(int[][] grid)
 {
     for (int i = 0; i < this.N; i++) {
         for (int j = 0; j < this.N; j++)
             System.out.print(grid[i][j] + " ");
         System.out.println();
     }
 }
// Checks whether there is any duplicate
// in current row or not
private boolean notInRow(int arr[][], int row)
{
     
    // Set to store characters seen so far.
    HashSet<Integer> st = new HashSet<>();
 
    for(int i = 0; i < this.N; i++)
    {
         
        // If already encountered before,
        // return false
        if (st.contains(arr[row][i]))
            return false;
 
        // If it is not an empty cell, insert value
        // at the current cell in the set
        if (arr[row][i] != 0)
            st.add(arr[row][i]);
    }
    return true;
}
 
// Checks whether there is any duplicate
// in current column or not.
private boolean notInCol(int arr[][], int col)
{
    HashSet<Integer> st = new HashSet<>();
 
    for(int i = 0; i < this.N; i++)
    {
         
        // If already encountered before,
        // return false
        if (st.contains(arr[i][col]))
            return false;
 
        // If it is not an empty cell,
        // insert value at the current
        // cell in the set
        if (arr[i][col] != 0)
            st.add(arr[i][col]);
    }
    return true;
}
 
// Checks whether there is any duplicate
// in current this.sqrt_nxthis.sqrt_n box or not.
private boolean notInBox(int arr[][],
                               int startRow,
                               int startCol)
{
    HashSet<Integer> st = new HashSet<>();
 
    for(int row = 0; row < this.sqrt_n; row++)
    {
        for(int col = 0; col < this.sqrt_n; col++)
        {
            int curr = arr[row + startRow][col + startCol];
 
            // If already encountered before, return
            // false
            if (st.contains(curr))
                return false;
 
            // If it is not an empty cell,
            // insert value at current cell in set
            if (curr != 0)
                st.add(curr);
        }
    }
    return true;
}
 
// Checks whether current row and current column and
// current this.sqrt_nxthis.sqrt_n box is valid or not
public boolean isValid(int arr[][], int row,
                              int col)
{
    return notInRow(arr, row) && notInCol(arr, col) &&
           notInBox(arr, row - row % this.sqrt_n, col - col % this.sqrt_n);
}
 
public boolean isValidConfig(int arr[][])
{
    for(int i = 0; i < this.N; i++)
    {
        for(int j = 0; j < this.N; j++)
        {
             
            // If current row or current column or
            // current this.sqrt_nxthis.sqrt_n box is not valid, return
            // false
            if (!isValid(arr, i, j))
                return false;
        }
    }
    return true;
}
 
// Driver code
// public static void main(String[] args)
// {
//     char[][] board = new char[][] {
//         { '5', 'this.sqrt_n', '.', '.', '7', '.', '.', '.', '.' },
//         { '6', '.', '.', '1', 'this.N', '5', '.', '.', '.' },
//         { '.', 'this.N', '8', '.', '.', '.', '.', '6', '.' },
//         { '8', '.', '.', '.', '6', '.', '.', '.', 'this.sqrt_n' },
//         { '4', '.', '.', '8', '.', 'this.sqrt_n', '.', '.', '1' },
//         { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
//         { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
//         { '.', '.', '.', '4', '1', 'this.N', '.', '.', '5' },
//         { '.', '.', '.', '6', '8', '.', '.', '7', 'this.N' }
//     };
 
//     System.out.println((isValidConfig(board, this.N) ?
//                        "YES" : "NO"));
// }
}