package florencia.augmentedmatrix;

import florencia.matrix.*;
import java.util.Scanner;
import java.util.Vector;
import java.util.Arrays;
import java.util.Iterator;

import java.lang.Math;
import java.io.*;

public class AugmentedMatrix
{
    private Scanner s = new Scanner(System.in);
    public Matrix leftMatrix;
    public Matrix rightMatrix;

    private boolean invalidEquation=false;

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= CONSTRUCTOR -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
    // Creates a new Augmented Matrix
    public AugmentedMatrix()
    {
        leftMatrix = new Matrix();
        rightMatrix = new Matrix();
    }

    // Creates a new Augmented Matrix
    public AugmentedMatrix(Matrix m1, Matrix m2)
    {
        leftMatrix = m1;
        rightMatrix = m2;
    }
    
    // Create a copy of an augmented Matrix
    public AugmentedMatrix(AugmentedMatrix augMat)
    {
        leftMatrix = augMat.leftMatrix;
        rightMatrix = augMat.rightMatrix;
    }
    
    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= SELECTOR -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
    // Returns left side matrix
    public Matrix getLeftMatrix()
    {
        return this.leftMatrix;
    }

    // Returns right side matrix
    public Matrix getRightMatrix()
    {
        return this.rightMatrix;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= INPUT OUTPUT PROCEDURE -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
    // Input into augmented matrix with its augmented form
    public void inputLinearEquation()
    {
        System.out.print("Masukkan Baris :");
        int rc = s.nextInt();
        System.out.print("Masukkan Kolom :");
        int cc = s.nextInt();
        this.leftMatrix = new Matrix(rc,cc-1);
        this.rightMatrix = new Matrix(rc,1);

        for(int i=0;i<rc;i++)
        {
            for(int j=0;j<cc-1;j++) this.leftMatrix.arr[i][j]=s.nextDouble();
            this.rightMatrix.arr[i][0]=s.nextDouble();
        }
    }

    // Print augmented matrix with separator
    public void printAugmentedMatrix()
    {
        for(int i=0;i<this.leftMatrix.rowCount;i++)
        {
            for(int j=0;j<this.leftMatrix.colCount;j++) System.out.print(this.getLeftMatrix().arr[i][j]+"\t");
            System.out.print("|\t");
            for(int j=0;j<this.rightMatrix.colCount;j++) System.out.print(this.getRightMatrix().arr[i][j]+"\t");
            System.out.println();
        }
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= ELEMENTARY ROW OPERATIONS (OBE) -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
    // Swaps row1 and row2 in an augmented matrix
    public void augRowSwap(int row1, int row2)
    {
        this.leftMatrix.rowSwap(row1, row2);
        this.rightMatrix.rowSwap(row1, row2);
    }
    
    // Does an arithmetic operation on two rows in an augmented matrix
    public void augRowArithmetic(int reducedRow, int reducingRow, double multiplier)
    {
        this.leftMatrix.rowArithmetic(reducedRow, reducingRow, multiplier);
        this.rightMatrix.rowArithmetic(reducedRow, reducingRow, multiplier);
    }
    
    // Multiplies one row with a constant
    public void augRowMultiplier(int multipliedRow, double multiplier)
    {
        this.leftMatrix.rowMultiplier(multipliedRow, multiplier);
        this.rightMatrix.rowMultiplier(multipliedRow, multiplier);
    }
    
    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= GAUSS ELIMINATION -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
    // if consistent, return 1, if inconsistent, return 2, if invalid, return 3
    // Use this function after reducing matrix.
    public int validateAugMat(int row)
    { 
        if(this.leftMatrix.isRowZero(row))
        {
            if(this.rightMatrix.arr[row][0]==0) return 2;
            else return 3;
        }
        else return 1;
    }

    // Reduce augmented matrix into echelon form
    public void forwardElimination()
    {
        int rc=this.leftMatrix.rowCount,cc=this.leftMatrix.colCount;

        // Reduce matrix
        for(int k=0;k<rc-1;k++)
        {
            // Find pivot to avoid dividing by zero
            int pivot=0;
            while(pivot<cc && this.leftMatrix.arr[k][pivot]==0) pivot++;
            if(pivot>=cc) continue;

            for(int i=k+1;i<rc;i++)
            {
                double multiplier = -this.leftMatrix.arr[i][pivot]/(this.leftMatrix.arr[k][pivot]);
                this.augRowArithmetic(i, k, multiplier);
            }
        }

        // Sorting pivot
        for(int i=0;i<rc-1;i++)
        {
            int rowpivot=i;
            int currentpivot=0;
            while(currentpivot<cc && this.leftMatrix.arr[i][currentpivot]==0) currentpivot++;
            for(int j=i+1;j<rc;j++)
            {
                int pivot=0;
                while(pivot<cc && this.leftMatrix.arr[j][pivot]==0) pivot++;
                if(pivot<currentpivot)
                {
                    currentpivot=pivot;
                    rowpivot=j;
                }
            }
            
            this.augRowSwap(i, rowpivot);
        }
        
        // Change all row into echelon form by multiplying
        for(int i=0;i<rc;i++)
        {
            int pivot=0;
            while(pivot<cc && this.leftMatrix.arr[i][pivot]==0) pivot++;
            if(pivot<cc) this.augRowMultiplier(i, (double)1/this.leftMatrix.arr[i][pivot]);     
        }

        // Fix signed zero
        this.leftMatrix.fixSignedZero();
        this.rightMatrix.fixSignedZero();

        // Detect Invalid Equation
        if(this.rightMatrix.colCount==1)
        {
            int row=0;
            while(!invalidEquation && row<this.leftMatrix.rowCount)
            {
                if(this.leftMatrix.isRowZero(row) && this.rightMatrix.arr[row][0]!=0) invalidEquation=true;
                else row++;
            }
        }
    }
    
    // Reduce augmented matrix into reduced echelon form (if applied after forwardElimintation() method)
    public void backwardElimination()
    {
        int rc=this.leftMatrix.rowCount,cc=this.leftMatrix.colCount;

        // Reduce matrix
        for(int k=rc-1;k>=1;k--)
        {
            // Find pivot to avoid dividing by zero
            int pivot=0;
            while(pivot<cc && this.leftMatrix.arr[k][pivot]==0) pivot++;
            if(pivot>=cc) continue;

            for(int i=k-1;i>=0;i--)
            {
                double multiplier = -this.leftMatrix.arr[i][pivot]/(this.leftMatrix.arr[k][pivot]);
                this.augRowArithmetic(i, k, multiplier);
            }
        }
        
    }

    // Gauss elimination method
	public AugmentedMatrix gaussElimination()
	{
        AugmentedMatrix result = new AugmentedMatrix(this);

        result.forwardElimination();

        return result;
    }
    
    // Gauss-Jordan elmination method
	public AugmentedMatrix gaussJordanElimination()
	{
        AugmentedMatrix result = new AugmentedMatrix(this);

        result.forwardElimination();
        if(!invalidEquation) result.backwardElimination();
    
        return result;    
    }

    // Make Matrix with inverse
    public Matrix makeInverseSPL()
    {
        Determinant det = new Determinant(this.leftMatrix);
        Matrix result = new Matrix();
        Matrix inv = new Matrix();

        inv = det.inverseAdjoint();
        result = result.kaliMatrix(inv, this.rightMatrix);
        
        return result;

    }

    // Make matrix from interpolation
    public AugmentedMatrix makeInterpolationMatrix()
    {
        System.out.print("Masukkan jumlah titik :");
        
        int n = s.nextInt();
        AugmentedMatrix result = new AugmentedMatrix(new Matrix(n,n), new Matrix(n,1));

        for(int i=0;i<n;i++)
        {
            System.out.print("Masukkan titik-titik dalam format x y :");
            double x=s.nextDouble(),y=s.nextDouble();
            for(int j=0;j<n;j++) result.leftMatrix.arr[i][j] = Math.pow(x,j);
            result.rightMatrix.arr[i][0] = y;
        }

        result.gaussJordanElimination();

        return result;
    }
    
    // Print the solution for interpolation
    public void convertToInterpolation(AugmentedMatrix aug) 
    {
        int n=aug.leftMatrix.rowCount;

        for(int i=0;i<n;i++) aug.rightMatrix.arr[i][0]=(double) Math.round(aug.rightMatrix.arr[i][0]*10000.0)/10000.0;
        
        System.out.print("f(x) diaproksimasi ");

        for(int i=0;i<n;i++) 
        {
            if(i==0) 
            {
                System.out.print(aug.rightMatrix.arr[i][0] + " +");
            }
            else if(i==n-1) 
            {
                System.out.print(aug.rightMatrix.arr[i][0] + "X^" + i);
            }
            else 
            {
                System.out.print(aug.rightMatrix.arr[i][0] + "X^" + i + " +");
            }
        }
        System.out.println();
    
        System.out.print("Masukkan x untuk aproksimasi:");
        double x=s.nextDouble();
    
        double result=0;
        for(int i=0;i<n;i++) result+=aug.rightMatrix.arr[i][0]*Math.pow(x,i);
        System.out.println("Nilai dari f("+x+") adalah " + result + ".");
    }

    // Cramer method for matrix solution
    public void Cramer(){
        double solution = 0;
        Matrix mat = new Matrix();
        mat.deepCopy(this.leftMatrix);
        Determinant matDet = new Determinant(mat);
        Matrix modVal = new Matrix();
        if (mat.isSquare() && (matDet.determinantLaplaceExpansion() != 0)){
            for (int i = 0; i < mat.rowCount; i++){
                modVal.deepCopy(mat);
                for (int j = 0; j < mat.colCount; j++){
                    modVal.arr[j][i] = this.rightMatrix.arr[j][0];
                }
                Determinant modValDet = new Determinant(modVal);
                solution = modValDet.determinantLaplaceExpansion() / matDet.determinantLaplaceExpansion();
                System.out.print("X"+(i+1)+" = "+solution+"\n");
            }
        } else {
            System.out.print("Matriks tidak valid.");
        }
    }
    
    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= OUTPUT SOLUTION -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
 
    // Use after reducing matrix only.
    public double[][] infiniteSolutionMatrix()
    {
        // Initizialize
        double[][] result = new double[this.leftMatrix.colCount+1][this.leftMatrix.colCount+1];
        for(double[] row:result) Arrays.fill(row,0);

        int cc=this.leftMatrix.colCount;
        
        // Moving coefficients then make it negative unless it's at the same diagonal
        for(int i=0;i<this.leftMatrix.rowCount;i++)
        {
            int pivot=0;
            while(pivot<cc && this.leftMatrix.arr[i][pivot]==0) pivot++;
            if(pivot>=cc) continue;

            result[pivot][0]=this.rightMatrix.arr[i][0];
            for(int j=pivot+1;j<this.leftMatrix.colCount;j++)
            {  
                //Convert all coeficients into its negative
                result[pivot][j+1]=this.leftMatrix.arr[i][j]*-1;
            }
        }
        return result;
    }

    // Used only after reducing to echelon matrix, to substitute after reducing matrix
    public void backwardSubstitution()
    {
        double[][] res = this.infiniteSolutionMatrix();
        int solutionRow = this.leftMatrix.colCount;
        int solutionCol = this.leftMatrix.colCount+1;

        for(int k=solutionRow-1;k>=1;k--)
        {
            for(int i=k-1;i>=0;i--)
            {
                if(res[k][k+1]!=0)
                {
                    double multiplier = -res[i][k]/res[k][k];
                    for(int j=0;j<solutionCol;j++) res[i][j]+=multiplier*res[k][j];
                }
            }
        }
    }

    // Convert Infinite Solution Matrix To Solution, when infinite/consistent only.
    public void convertToSolutionInfinite() throws Exception
    {
        System.out.println("Solusinya : ");

        double[][] res = this.infiniteSolutionMatrix();
        int solutionRow = this.leftMatrix.colCount;
        int solutionCol = this.leftMatrix.colCount+1;
        Vector<Integer> infiniteList = new Vector<Integer>();

        for(int i=0;i<solutionRow;i++)
        {
            boolean printed=false;
            System.out.print("X" + (i+1) + " = ");
            
            for(int j=0;j<solutionCol;j++)
            {
                if(res[i][j]!=0 && i+1!=j)
                {
                    if(!printed)
                    {
                        printed=true;
                        if(j==0) 
                        {
                            System.out.print(res[i][j] + " ");
                        }
                        else 
                        {
                            System.out.print(res[i][j]>0?(res[i][j] + "X" + j):(" - " + Math.abs(res[i][j])+"X"+j));
                        }
                    }
                    else
                    {
                        System.out.print(res[i][j]>0?(" + " + res[i][j] + "X" + j):(" - " + Math.abs(res[i][j])+"X"+j));
                    }
                }
            }
            if(!printed)
            {
                if(res[i][i+1]==1)
                { 
                    System.out.println("0.0");
                    printed=true;
                }
                else
                {
                    infiniteList.add(i+1);
                    System.out.print("X" + (i+1));
                }
            }
            System.out.println();
        }

        if(!infiniteList.isEmpty()){
            Iterator<Integer> val = infiniteList.iterator();
            while(val.hasNext())
            {
                System.out.print("X" + val.next() + ", ");
            }
            System.out.println("merupakan bilangan real");
        }
    }

    /*-----------File I/O------------*/
    // Read .txt file into augmented matrix
    public void textToAug() throws Exception{
        Matrix matrixFile = new Matrix(101, 101);
        int x = 0, y = 0;
        String filenameAug = " ";

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("File harus terletak di folder 'test'!");
		System.out.print("Masukkan nama file yang terdapat matriks augmented : ");
        filenameAug = reader.readLine();
		File file = new File(new File("./test/"+filenameAug).getCanonicalPath());
        BufferedReader br = null;

		try{
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
				y = 0;
                String[] values = line.split(" ");
                for (String str : values){
                    double str_double = Double.parseDouble(str);
                    matrixFile.arr[x][y] = str_double;
                    y++;
				}
                x++;
			}
			matrixFile.rowCount = x;
			matrixFile.colCount = y;
            System.out.println("Matriks augmented berhasil dibuat!");
        }
		catch(Exception Exception){
            System.out.println("File tidak ditemukan!");
        }

        this.leftMatrix = new Matrix(matrixFile.rowCount, matrixFile.colCount-1);
        this.rightMatrix = new Matrix(matrixFile.rowCount, 1);
        for (int i = 0; i < matrixFile.rowCount; i++){
            for (int j = 0; j < matrixFile.colCount; j++){
                if (j == matrixFile.colCount-1){
                    this.rightMatrix.arr[i][0] = matrixFile.arr[i][j];
                } else {
                    this.leftMatrix.arr[i][j] = matrixFile.arr[i][j];
                }
            }
        }
		//taken and modified from https://www.daniweb.com/programming/software-development/threads/324267/reading-file-and-store-it-into-2d-array-and-parse-it
    }

    // Write augmented matrix to .txt file
	public void AugToText() throws Exception{
		String matrixFilename = " ";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Masukkan nama file yang akan dijadikan output : ");
		matrixFilename = reader.readLine();
		StringBuilder builder = new StringBuilder();
        int sumCol = this.leftMatrix.colCount+this.rightMatrix.colCount;
        for (int i = 0; i < this.leftMatrix.rowCount; i++){
            for (int j = 0; j < sumCol; j++){
                if (j == sumCol-1){
                    builder.append(this.rightMatrix.arr[i][0]);
                } else {
                    builder.append(this.leftMatrix.arr[i][j]);
                }
                if (j < sumCol-1){
                    builder.append(" ");
                }
            }
            if (i != sumCol){
                builder.append("\n");
            }
        }
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./output/"+matrixFilename).getCanonicalPath()));
		writer.write(builder.toString());
		writer.close();
		System.out.println("File "+matrixFilename+" terletak di folder 'output'.");
    }

    // Print the solution for interpolation in file
    public void convertToInterpolationToText(AugmentedMatrix aug) throws Exception
    {
        PrintStream file = new PrintStream(new File("./output/output.txt").getCanonicalPath());
        PrintStream console = System.out;

        int n=aug.leftMatrix.rowCount;

        for(int i=0;i<n;i++) aug.rightMatrix.arr[i][0]=(double) Math.round(aug.rightMatrix.arr[i][0]*10000.0)/10000.0;

        System.setOut(file);
        System.out.println("convertToInterpolation");
        System.out.print("f(x) diaproksimasi ");

        for(int i=0;i<n;i++) 
        {
            if(i==0) 
            {
                System.setOut(file);
                System.out.print(aug.rightMatrix.arr[i][0] + " +");
            }
            else if(i==n-1) 
            {
                System.setOut(file);
                System.out.print(aug.rightMatrix.arr[i][0] + "X^" + i);
            }
            else 
            {
                System.setOut(file);
                System.out.print(aug.rightMatrix.arr[i][0] + "X^" + i + " +");
            }
        }
        System.setOut(file);
        System.out.println();
    
        System.setOut(console);
        System.out.print("Masukkan x untuk aproksimasi:");
        
        double x=s.nextDouble();

        System.setOut(file);
        System.out.println("Masukkan x untuk aproksimasi:"+x);
    
        double result=0;
        for(int i=0;i<n;i++) result+=aug.rightMatrix.arr[i][0]*Math.pow(x,i);

        System.setOut(file);
        System.out.println("Nilai dari f("+x+") adalah " + result + ".");
        System.out.println("---------------------------------------------------------------------------------------------");
        System.setOut(console);
    }

    // Cramer method for matrix solution in file
    public void CramerToText() throws Exception {
        PrintStream file = new PrintStream(new File("./output/output.txt").getCanonicalPath());
        PrintStream console = System.out;

        System.setOut(file);
        System.out.println("Cramer");

        double solution = 0;
        Matrix mat = new Matrix();
        mat.deepCopy(this.leftMatrix);
        Determinant matDet = new Determinant(mat);
        Matrix modVal = new Matrix();
        if (mat.isSquare() && (matDet.determinantLaplaceExpansion() != 0)){
            for (int i = 0; i < mat.rowCount; i++){
                modVal.deepCopy(mat);
                for (int j = 0; j < mat.colCount; j++){
                    modVal.arr[j][i] = this.rightMatrix.arr[j][0];
                }
                Determinant modValDet = new Determinant(modVal);
                solution = modValDet.determinantLaplaceExpansion() / matDet.determinantLaplaceExpansion();

                System.setOut(file);
                System.out.print("X"+(i+1)+" = "+solution+"\n");
            }
            System.setOut(file);
            System.out.println("---------------------------------------------------------------------------------------------");
            System.setOut(console);
        } else {
            System.out.print("Matrix tidak valid.");
        }
    }

    // Convert Infinite Solution Matrix To Solution, when infinite/consistent only. in file
    public void convertToSolutionInfiniteToText() throws Exception
    {
        PrintStream file = new PrintStream(new File("./output/output.txt").getCanonicalPath());
        PrintStream console = System.out;

        System.setOut(file);
        System.out.println("convertToSolutionInfinite");
        System.out.println("Solusinya: ");

        double[][] res = this.infiniteSolutionMatrix();
        int solutionRow = this.leftMatrix.colCount;
        int solutionCol = this.leftMatrix.colCount+1;
        Vector<Integer> infiniteList = new Vector<Integer>();

        for(int i=0;i<solutionRow;i++)
        {
            boolean printed=false;

            System.setOut(file);
            System.out.print("X" + (i+1) + " = ");
            
            for(int j=0;j<solutionCol;j++)
            {
                if(res[i][j]!=0 && i+1!=j)
                {
                    if(!printed)
                    {
                        printed=true;
                        if(j==0) 
                        {
                            System.setOut(file);
                            System.out.print(res[i][j] + " ");
                        }
                        else 
                        {
                            System.setOut(file);
                            System.out.print(res[i][j]>0?(res[i][j] + "X" + j):(" - " + Math.abs(res[i][j])+"X"+j));
                        }
                    }
                    else
                    {
                        System.setOut(file);
                        System.out.print(res[i][j]>0?(" + " + res[i][j] + "X" + j):(" - " + Math.abs(res[i][j])+"X"+j));
                    }
                }
            }
            if(!printed)
            {
                if(res[i][i+1]==1)
                { 
                    System.setOut(file);
                    System.out.println("0.0");
                    printed=true;
                }
                else
                {
                    infiniteList.add(i+1);
                    System.setOut(file);
                    System.out.print("X" + (i+1));
                }
            }
            System.setOut(file);
            System.out.println();
        }

        if(!infiniteList.isEmpty()){
            Iterator<Integer> val = infiniteList.iterator();
            while(val.hasNext())
            {
                System.setOut(file);
                System.out.print("X" + val.next() + ", ");
            }
            System.setOut(file);
            System.out.println("merupakan bilangan real");
        }
        System.setOut(file);
        System.out.println("---------------------------------------------------------------------------------------------");
        System.setOut(console);
    }
}