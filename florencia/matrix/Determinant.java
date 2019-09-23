package florencia.matrix;

//import java.lang.FdLibm.Pow;
import java.lang.Math;

public class Determinant
{
    Matrix mat;
    public Determinant(Matrix m)
    {
        this.mat = m;
    }

    public double determinantRowReduction()
    {
        double det = 1;
        boolean checker = true;
        if (this.mat.isSquare()){
            this.mat.normBackwardElimination();
            for (int i = 0; i < this.mat.colCount && checker; i++){
                if (this.mat.arr[i][i] == 0){
                    checker = false;
                    det = 0;
                } else {
                    det *= this.mat.arr[i][i];
                }
            }
            return det;
        } else {
            System.out.print("Matrix is not valid, you need square matrix!");
            return -99999;
        }
    }
    
    public double determinantSarrus()
    {
        double det = 0, sumPlus = 0, sumMin = 0;
        if (this.mat.isSquare() && this.mat.getRowCount() == 3){
            sumPlus = (this.mat.arr[0][0]*this.mat.arr[1][1]*this.mat.arr[2][2]) + (this.mat.arr[0][1]*this.mat.arr[1][2]*this.mat.arr[2][0]) + (this.mat.arr[0][2]*this.mat.arr[1][0]*this.mat.arr[2][1]);
            sumMin  = (this.mat.arr[2][0]*this.mat.arr[1][1]*this.mat.arr[0][2]) + (this.mat.arr[2][1]*this.mat.arr[1][2]*this.mat.arr[0][0]) + (this.mat.arr[2][2]*this.mat.arr[1][0]*this.mat.arr[0][1]);
            det = sumPlus - sumMin;
            return det;
        } else {
            System.out.print("Matrix is not valid, use another method!");
            return -99999;
        }
    }
    
    public double determinantLaplaceExpansion()
    {
        double det = 0;
        Matrix MT = new Matrix(this.mat);

        if (this.mat.isSquare()){
            for (int j = 0; j < MT.colCount; j++){
                det += (Math.pow(-1, (0+j)) * MT.arr[0][j] * this.getCofactor(0, j));
            }
            return det;
        } else {
            System.out.print("Matrix is not valid, you need square matrix!");
            return -99999;
        }
    }

    public Matrix getminorEntry(int i, int j)
    {
        int row, col;
        Matrix MT = new Matrix(this.mat.rowCount-1, this.mat.colCount-1);
        row = 0;
        for(int k=0;k<this.mat.rowCount;k++)
        {
            col = 0; 
            for(int l=0;l<this.mat.colCount;l++)
            { 
                if((k != i)&&(l != j))
                {
                    MT.arr[row][col] = this.mat.arr[k][l];
                    col ++;
                }    
            }
            if(k != i) row ++;
        }
        return MT;
    } 

    public double getCofactor(int i, int j)
    {
        double result, det;
        Matrix MT;
        MT = this.getminorEntry(i,j);
        
        Determinant d = new Determinant(MT);
        det = d.determinantRowReduction();
        result = det * Math.pow(-1,(i+j));
        return result;
    }

    public Matrix getMatrixCofactor()
    {
        Matrix MT = new Matrix(this.mat.colCount, this.mat.rowCount);
        for(int i=0;i<this.mat.rowCount;i++)
        {
            for(int j=0;j<this.mat.colCount;j++)
            {
                MT.arr[i][j] = this.getCofactor(i,j);
            }
        }
        return MT;
    }

    public Matrix getAdjoint()
    {
        Matrix MT = new Matrix(this.mat.colCount, this.mat.rowCount);
        MT = this.getMatrixCofactor();
        MT = MT.transpose();
        return MT;
    }

    public Matrix inverseAdjoint()
    {
        Matrix MT = new Matrix(this.mat.colCount, this.mat.rowCount);
        MT = this.getAdjoint();
        double x= (double)1/this.determinantRowReduction();
        MT = MT.kaliKons(x);
        return MT;
    }
}