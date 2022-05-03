package com.techdroid.sudoku;
import java.lang.Math;
import java.util.Collections;
import java.util.ArrayList;

/*
 * per box formula
 * this formula goes through each box instead of the natural order
 * ((i / this.sqrt_n) % this.sqrt_n) * this.N + ((i % this.multi) / this.N) * this.sqrt_n + (i / this.multi) * this.multi + (i %this.sqrt_n)
 *
 * get box origin formula
 * this formula gives the index of the origin of the box that contains index i (0-80)
 * ((i % this.N) / this.sqrt_n) * this.sqrt_n + (i / this.multi) * this.multi
 * 
 * get row origin formula
 * this formula gives the index of the origin of the row that contains index i (0-80)
 * (i / this.N) * this.N
 *
 * get column origin formula
 * this formula gives the index of the origin of the column that contains index i (0-80)
 * i % this.N
 *
 * get box origin formula
 * this formula gives the index of origin of box # i (0-8)
 * (i * this.sqrt_n) % this.N + ((i * this.sqrt_n) / this.N) * this.multi
 *
 * get row origin formula
 * this formula gives the index of origin of row # i (0-8)
 * i*this.N
 *
 * get box origin formula
 * this formula gives the index of origin of column # i (0-8)
 * i
 *
 * box step formula
 * this formula runs through a box shape (i must be less than this.N)
 * boxOrigin + (i / this.sqrt_n) * this.N + (i % this.sqrt_n)
 *
 * row step formula
 * rowOrigin + i
 *
 * col step formula
 * colOrigin + i*this.N
*/

public class SudokuGridGenerator
{
   int N = 9;
   int sqrt_n = 3;
   int multi = 27;
   int total = 81;

   public SudokuGridGenerator(int n){
      this.N = n;
      this.sqrt_n = (int) Math.sqrt(this.N);
      this.multi = this.sqrt_n * this.N;
      this.total = this.N * this.N;
   }
   
   public static void main(String[] args)
   {
      int trials = 0;
      while(true)
      {
         SudokuGridGenerator sudoku = new SudokuGridGenerator(16);
         trials++;
         //average solution time hovers around 7 microseconds (~0.000007 seconds)
         //not including time it takes for print statements, which eats up BUTT-LOADS of time and slows down the output 10-fold
         int[] grid2 = sudoku.generateGrid();
         if(sudoku.isPerfect(grid2)) 
         {System.out.println("PERFECT GRID #" + String.format("%,d",(trials)));
         sudoku.printGrid(grid2);
         break;

      }
      }
   }
   
   /**
   *Generates a valid this.N by this.N Sudoku grid with 1 through this.N appearing only once in every box, row, and column
   *@return an array of size this.total containing the grid
   */
   public int[] generateGrid()
   {
         ArrayList<Integer> arr = new ArrayList<Integer>(this.N);
         int[] grid = new int[this.total];
         for(int i = 1; i <= this.N; i++) arr.add(i);
      
         //loads all boxes with numbers 1 through this.N
         for(int i = 0; i < this.total; i++)
         {
            if(i%this.N == 0) Collections.shuffle(arr);
            int perBox = ((i / this.sqrt_n) % this.sqrt_n) * this.N + ((i % this.multi) / this.N) * this.sqrt_n + (i / this.multi) * this.multi + (i %this.sqrt_n);
            grid[perBox] = arr.get(i%this.N);
         }
         
         //tracks rows and columns that have been sorted
         boolean[] sorted = new boolean[this.total];
         
         for(int i = 0; i < this.N; i++)
         {
            boolean backtrack = false;
            //0 is row, 1 is column
            for(int a = 0; a<2; a++)
            {
               //every number 1-this.N that is encountered is registered
               boolean[] registered = new boolean[this.N + 1]; //index 0 will intentionally be left empty since there are only number 1-this.N.
               int rowOrigin = i * this.N;
               int colOrigin = i;
            
               ROW_COL: for(int j = 0; j < this.N; j++)
               {
                  //row/column stepping - making sure numbers are only registered once and marking which cells have been sorted
                  int step = (a%2==0? rowOrigin + j: colOrigin + j*this.N);
                  int num = grid[step];
                  
                  if(!registered[num]) registered[num] = true;
                  else //if duplicate in row/column
                  {
                     //box and adjacent-cell swap (BAS method)
                     //checks for either unregistered and unsorted candidates in same box,
                     //or unregistered and sorted candidates in the adjacent cells
                     for(int y = j; y >= 0; y--) 
                     {
                        int scan = (a%2==0? i * this.N + y: i + this.N * y);
                        if(grid[scan] == num)
                        {
                           //box stepping
                           for(int z = (a%2==0? (i%this.sqrt_n + 1) * this.sqrt_n: 0); z < this.N; z++)
                           {
                              if(a%2 == 1 && z%this.sqrt_n <= i%this.sqrt_n)
                                 continue;
                              int boxOrigin = ((scan % this.N) / this.sqrt_n) * this.sqrt_n + (scan / this.multi) * this.multi;
                              int boxStep = boxOrigin + (z / this.sqrt_n) * this.N + (z % this.sqrt_n);
                              int boxNum = grid[boxStep];
                              if((!sorted[scan] && !sorted[boxStep] && !registered[boxNum])
                                 || (sorted[scan] && !registered[boxNum] && (a%2==0? boxStep%this.N==scan%this.N: boxStep/this.N==scan/this.N)))
                              {
                                 grid[scan] = boxNum;
                                 grid[boxStep] = num;
                                 registered[boxNum] = true;
                                 continue ROW_COL;
                              }
                              else if(z == this.N - 1) //if z == 8, then break statement not reached: no candidates available
                              {
                                 //Preferred adjacent swap (PAS)
                                 //Swaps x for y (preference on unregistered numbers), finds occurence of y
                                 //and swaps with z, etc. until an unregistered number has been found
                                 int searchingNo = num;
                                             
                                 //noting the location for the blindSwaps to prevent infinite loops.
                                 boolean[] blindSwapIndex = new boolean[this.total];
                                 
                                 //loop of size 18 to prevent infinite loops as well. Max of 18 swaps are possible.
                                 //at the end of this loop, if continue or break statements are not reached, then
                                 //fail-safe is executed called Advance and Backtrack Sort (ABS) which allows the 
                                 //algorithm to continue sorting the next row and column before coming back.
                                 //Somehow, this fail-safe ensures success.
                                 for(int q = 0; q < this.multi; q++)
                                 {
                                    SWAP: for(int b = 0; b <= j; b++)
                                    {
                                       int pacing = (a%2==0? rowOrigin+b: colOrigin+b*this.N);
                                       if(grid[pacing] == searchingNo)
                                       {
                                          int adjacentCell = -1;
                                          int adjacentNo = -1;
                                          int decrement = (a%2==0? this.N: 1);
                                          
                                          for(int c = 1; c < this.sqrt_n - (i % this.sqrt_n); c++)
                                          {
                                             adjacentCell = pacing + (a%2==0? (c + 1)*this.N: c + 1);
                                             
                                             //this creates the preference for swapping with unregistered numbers
                                             if(   (a%2==0 && adjacentCell >= this.total)
                                                   || (a%2==1 && adjacentCell % this.N == 0)) adjacentCell -= decrement;
                                             else
                                             {
                                                adjacentNo = grid[adjacentCell];
                                                if(i%this.sqrt_n!=0
                                                               || c!=1 
                                                               || blindSwapIndex[adjacentCell]
                                                               || registered[adjacentNo])
                                                   adjacentCell -= decrement;
                                             }
                                             adjacentNo = grid[adjacentCell];
                                             
                                             //as long as it hasn't been swapped before, swap it
                                             if(!blindSwapIndex[adjacentCell])
                                             {
                                                blindSwapIndex[adjacentCell] = true;
                                                grid[pacing] = adjacentNo;
                                                grid[adjacentCell] = searchingNo;
                                                searchingNo = adjacentNo;
                                                      
                                                if(!registered[adjacentNo])
                                                {
                                                   registered[adjacentNo] = true;
                                                   continue ROW_COL;
                                                }
                                                break SWAP;
                                             }
                                          }
                                       }
                                    }
                                 }
                                 //begin Advance and Backtrack Sort (ABS)
                                 backtrack = true;
                                 break ROW_COL;
                              }
                           }
                        }
                     }
                  }
               }
               
               if(a%2==0)
                  for(int j = 0; j < this.N; j++) sorted[i*this.N+j] = true; //setting row as sorted
               else if(!backtrack) 
                  for(int j = 0; j < this.N; j++) sorted[i+j*this.N] = true; //setting column as sorted
               else //reseting sorted cells through to the last iteration
               {
                  backtrack = false;
                  for(int j = 0; j < this.N; j++) sorted[i*this.N+j] = false;
                  for(int j = 0; j < this.N; j++) sorted[(i-1)*this.N+j] = false;
                  for(int j = 0; j < this.N; j++) sorted[i-1+j*this.N] = false;
                  i-=2;
               }
            }
         }
         
         if(!isPerfect(grid)) throw new RuntimeException("ERROR: Imperfect grid generated.");
         
         return grid;
   }
   
   /**
   *Prints a visual representation of a this.Nxthis.N Sudoku grid
   *@param grid an array with length this.total to be printed
   */
   public void printGrid(int[] grid)
   {
      if(grid.length != this.total) throw new IllegalArgumentException("The grid must be a single-dimension grid of length this.total");
      for(int i = 0; i < this.total; i++)
      {
         System.out.print("["+grid[i]+"] "+(i%this.N == (this.N - 1)?"\n":""));
      }
   }
   
   /**
   *Tests an int array of length this.total to see if it is a valid Sudoku grid. i.e. 1 through this.N appearing once each in every row, column, and box
   *@param grid an array with length this.total to be tested
   *@return a boolean representing if the grid is valid
   */
   public boolean isPerfect(int[] grid)
   
   {
    System.out.println("Validating");
      if(grid.length != this.total) throw new IllegalArgumentException("The grid must be a single-dimension grid of length this.total");

      //tests to see if the grid is perfect
      
         //for every box
      for(int i = 0; i < this.N; i++)
      {
         boolean[] registered = new boolean[this.N + 1];
         registered[0] = true;
         int boxOrigin = (i * this.sqrt_n) % this.N + ((i * this.sqrt_n) / this.N) * this.multi;
         for(int j = 0; j < this.N; j++)
         {
            int boxStep = boxOrigin + (j / this.sqrt_n) * this.N + (j % this.sqrt_n);
            int boxNum = grid[boxStep];
            registered[boxNum] = true;
         }
         for(boolean b: registered) 
            if(!b) return false;
      }
      
         //for every row
      for(int i = 0; i < this.N; i++)
      {
         boolean[] registered = new boolean[this.N + 1];
         registered[0] = true;
         int rowOrigin = i * this.N;
         for(int j = 0; j < this.N; j++)
         {
            int rowStep = rowOrigin + j;
            int rowNum = grid[rowStep];
            registered[rowNum] = true;
         }
         for(boolean b: registered) 
            if(!b) return false;
      }
      
         //for every column
      for(int i = 0; i < this.N; i++)
      {
         boolean[] registered = new boolean[this.N + 1];
         registered[0] = true;
         int colOrigin = i;
         for(int j = 0; j < this.N; j++)
         {
            int colStep = colOrigin + j*this.N;
            int colNum = grid[colStep];
            registered[colNum] = true;
         }
         for(boolean b: registered) 
            if(!b) return false;
      }
      
      return true;
   }
}