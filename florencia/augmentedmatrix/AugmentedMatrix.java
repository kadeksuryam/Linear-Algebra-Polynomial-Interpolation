package florencia.augmentedmatrix;

import java.util.Scanner;
import java.util.Arrays;
import florencia.matrix.*;
import java.lang.Math;

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
        int rc = s.nextInt();
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
                // this.printAugmentedMatrix();
                // System.out.println();
            }
            // Make the pivot equal to 1
            this.augRowMultiplier(k, 1/this.leftMatrix.arr[k][pivot]);
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
        for(int k=this.leftMatrix.rowCount-1;k>=1;k--)
        {
            if(this.leftMatrix.arr[k][k]==0) continue;
            else
            {
                // Reduce into Reduced Echelon Form
                for(int i=k-1;i>=0;i--)
                {
                    double multiplier = -this.leftMatrix.arr[i][k]/(this.leftMatrix.arr[k][k]);
                    this.augRowArithmetic(i, k, multiplier);
                    // this.printAugmentedMatrix();
                    // System.out.println();
                }
            }
        }
        
    }

	public void gaussElimination()
	{
        this.forwardElimination();

    }
    
	public void gaussJordanElimination()
	{
        this.forwardElimination();
        if(!invalidEquation)
        {
            this.backwardElimination();
            // this.convertToSolutionValid();
        }
        else System.out.println("Invalid/Inconsistent Equation");
    
        
    }

    public Matrix inverseMatrix()
    {
        this.leftMatrix.inputMatrix();
        this.rightMatrix.setIdentityMatrix(this.leftMatrix.rowCount);
        this.gaussJordanElimination();
        return this.rightMatrix;
    }

    public void interpolateGraph()
    {
        int n = s.nextInt();
        
        Matrix mx=new Matrix(n,n), my=new Matrix(n,1);
        AugmentedMatrix augGraph = new AugmentedMatrix(mx, my);

        for(int i=0;i<n;i++)
        {
            double x=s.nextDouble(),y=s.nextDouble();
            for(int j=0;j<n;j++)
            {
                augGraph.leftMatrix.arr[i][j] = Math.pow(x,j);
            }
            augGraph.rightMatrix.arr[i][0] = y;
        }


        augGraph.gaussJordanElimination();
        augGraph.printAugmentedMatrix();

        for(int i=0;i<n;i++) augGraph.rightMatrix.arr[i][0]=(double) Math.round(augGraph.rightMatrix.arr[i][0]*10000.0)/10000.0;

        System.out.print("f(x) approximately equal to ");
        for(int i=0;i<n;i++) 
        {
            if(i==0) System.out.print(augGraph.rightMatrix.arr[i][0] + " +");
            else if(i==n-1) System.out.print(augGraph.rightMatrix.arr[i][0] + "X^" + i);
            else System.out.print(augGraph.rightMatrix.arr[i][0] + "X^" + i + " +");
        }
        System.out.println();

        System.out.print("Input x for approximation:");
        double x=s.nextDouble();

        double result=0;
        for(int i=0;i<n;i++) result+=augGraph.rightMatrix.arr[i][0]*Math.pow(x,i);
        System.out.println("Value of f("+x+") is equal to " + result + ".");

    }

    public void Cramer(){
        double solution = 0;
        Determinant matDet = new Determinant(this.leftMatrix);

        if (this.leftMatrix.isSquare() && (matDet.determinantLaplaceExpansion() != 0)){
            for (int i = 0; i < this.leftMatrix.rowCount; i++){
                Matrix modVal = new Matrix(this.leftMatrix);
                for (int j = 0; j < this.leftMatrix.colCount; j++){
                    modVal.arr[i][j] = this.rightMatrix.arr[i][j];
                }
                Determinant modValDet = new Determinant(modVal);
                solution = modValDet.determinantLaplaceExpansion() / matDet.determinantLaplaceExpansion();
                System.out.print("x"+i+" = "+solution+"\n");
            }
        } else {
            System.out.print("This method is not valid for this type of matrix");
        }
    }
    public void convertToSolutionValid()
    {
        System.out.println("The solutions are: ");
        for(int i=0;i<this.leftMatrix.rowCount;i++) System.out.println("x" + (i+1) + " = " + this.rightMatrix.arr[i][0]);
    }
    
    // Use after reducing matrix only.
    public double[][] infiniteSolutionMatrix()
    {
        double[][] result = new double[this.leftMatrix.colCount+1][this.leftMatrix.colCount+1];
        for(double[] row:result) Arrays.fill(row,0);

        // Moving constant into result array
        for(int i=0;i<this.rightMatrix.rowCount;i++) result[i][0]=this.rightMatrix.arr[i][0];

        for(int i=0;i<this.leftMatrix.rowCount;i++)
        {
            boolean foundOne=false;
            for(int j=0;j<this.leftMatrix.colCount;j++)
            {  
                //Searching for main one in echelon matrix
                if(this.leftMatrix.arr[i][j]==1) foundOne=true;

                //Convert all coeficients into its negative
                if(foundOne && this.leftMatrix.arr[i][j]!=0)
                {
                    result[i][j+1]=this.leftMatrix.arr[i][j]*-1;
                }
            }
        }
        


        return result;
    }

    public void convertToSolutionInfinite()
    {
        System.out.println("The solutions are: ");

        double[][] res = this.infiniteSolutionMatrix();

        for(int i=0;i<this.leftMatrix.colCount;i++)
        {
            boolean printed=false;
            System.out.print("X" + (i+1) + " = ");
            
            for(int j=0;j<this.leftMatrix.colCount+1;j++)
            {
                if(res[i][j]!=0 && i+1!=j)
                {
                    if(!printed)
                    {
                        printed=true;
                        if(j==0) System.out.print(res[i][j] + " ");
                        else System.out.print(res[i][j]>0?(res[i][j] + "X" + j):(" - " + Math.abs(res[i][j])+"X"+j));
                    }
                    else
                    {
                        System.out.print(res[i][j]>0?(" + " + res[i][j] + "X" + j):(" - " + Math.abs(res[i][j])+"X"+j));
                    }
                }
            }
            if(!printed) System.out.print("X" + (i+1));
            System.out.println();
        }
    }

}