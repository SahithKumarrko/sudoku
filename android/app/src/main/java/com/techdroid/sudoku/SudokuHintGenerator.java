package com.techdroid.sudoku;
import java.util.ArrayList;
import java.lang.Math;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
public class SudokuHintGenerator {

	// this.N is the size of the 2D matrix this.N*this.N
	int N = 9;
	int sqrt = 3;

	public SudokuHintGenerator(int N){
		this.N = N;
		this.sqrt = (int) Math.sqrt(this.N);
	}

	/* Takes a partially filled-in grid and attempts
	to assign values to all unassigned locations in
	such a way to meet the requirements for
	Sudoku solution (non-duplication across rows,
	columns, and boxes) */
	boolean solveSudoku(int grid[][], int row,
							int col)
	{

		/*if we have reached the this.N - 1th
		row and 9th column (0
		indexed matrix) ,
		we are returning true to avoid further
		backtracking	 */
		if (row == this.N - 1 && col == this.N)
			return true;

		// Check if column value becomes 9 ,
		// we move to next row
		// and column start from 0
		if (col == this.N) {
			row++;
			col = 0;
		}

		// Check if the current position
		// of the grid already
		// contains value >0, we iterate
		// for next column
		if (grid[row][col] != 0)
			return solveSudoku(grid, row, col + 1);

		for (int num = 1; num < 10; num++) {

			// Check if it is safe to place
			// the num (1-9) in the
			// given row ,col ->we move to next column
			if (isSafe(grid, row, col, num)) {

				/* assigning the num in the current
				(row,col) position of the grid and
				assuming our assigned num in the position
				is correct */
				grid[row][col] = num;

				// Checking for next
				// possibility with next column
				if (solveSudoku(grid, row, col + 1))
					return true;
			}
			/* removing the assigned num , since our
			assumption was wrong , and we go for next
			assumption with diff num value */
			grid[row][col] = 0;
		}
		return false;
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

	// Check whether it will be legal
	// to assign num to the
	// given row, col
	 boolean isSafe(int[][] grid, int row, int col,
						int num)
	{

		// Check if we find the same num
		// in the similar row , we
		// return false
		for (int x = 0; x <= this.N - 1; x++)
			if (grid[row][x] == num)
				return false;

		// Check if we find the same num
		// in the similar column ,
		// we return false
		for (int x = 0; x <= this.N - 1; x++)
			if (grid[x][col] == num)
				return false;

		// Check if we find the same num
		// in the particular 3*3
		// matrix, we return false
		int startRow = row - row % 3, startCol
									= col - col % 3;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (grid[i + startRow][j + startCol] == num)
					return false;

		return true;
	}

	// Driver Code
	// public static void main(String[] args)
	// {
	// 	int grid[][] = { { 3, 0, 6, 5, 0, this.N - 1, 4, 0, 0 },
	// 					{ 5, 2, 0, 0, 0, 0, 0, 0, 0 },
	// 					{ 0, this.N - 1, 7, 0, 0, 0, 0, 3, 1 },
	// 					{ 0, 0, 3, 0, 1, 0, 0, this.N - 1, 0 },
	// 					{ 9, 0, 0, this.N - 1, 6, 3, 0, 0, 5 },
	// 					{ 0, 5, 0, 0, 9, 0, 6, 0, 0 },
	// 					{ 1, 3, 0, 0, 0, 0, 2, 5, 0 },
	// 					{ 0, 0, 0, 0, 0, 0, 0, 7, 4 },
	// 					{ 0, 0, 5, 2, 0, 6, 3, 0, 0 } };
	// 	SudokuHintGenerator s = new SudokuHintGenerator();
	// 	List<Integer> points = s.getRandomPoint(grid);
	// 	if (s.solveSudoku(grid, 0, 0)){
	// 		s.print(grid);
	// 		Map<String,Object> res = s.getNextValue(grid,points);
	// 		System.out.println(res);
	// 	}
			
	// 	else
	// 		System.out.println("No Solution exists");
	// }
	public List<Integer> getRandomPoint(int[][] grid){
		List<List<Integer>> arr = new ArrayList<>();
		for(int i=0;i<this.N;i++){
			int col = -1;
			for(int j=0;j<this.N;j++){
				if(grid[i][j]==0){
					col = j;
					break;
				}
			}	
			if(col!=-1){
				List<Integer> a = new ArrayList<>();
				a.add(i);
				a.add(col);
				arr.add(a);
			}
		}
		if(arr.size()==0){
			return new ArrayList<>();
		}
		int rnd = new Random().nextInt(arr.size());
		return arr.get(rnd);
	}
	public Map<String,Object> getNextValue(int[][] grid,List<Integer> points){
		Map<String, Object> obj = new HashMap<>();
		
		obj.put("row", -1);
		obj.put("col", -1);
		obj.put("value", -1);
		if(solveSudoku(grid,0,0)){
			if(points.size()!=0){
				obj.put("row", points.get(0));
				obj.put("col", points.get(1));
				obj.put("value", grid[points.get(0)][points.get(1)]);
			}
		}
		return obj;
	}
}
