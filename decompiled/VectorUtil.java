import Jama.Matrix;


public class VectorUtil
{
    public static Matrix meanVector(final Matrix X) {
        final int N = X.getColumnDimension();
        final int n = X.getRowDimension();
        final Matrix mean = new Matrix(n, 1);
        double temp = 0.0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < N; ++j) {
                temp += X.get(i, j);
            }
            temp /= N;
            mean.set(i, 0, temp);
            temp = 0.0;
        }
        return mean;
    }
    
    public static Matrix covarianceMatrix(final Matrix X, final Matrix mean) {
        final int N = X.getColumnDimension();
        final int n = X.getRowDimension();
        Matrix covariance = new Matrix(n, n);
        for (int i = 0; i < N; ++i) {
            final Matrix vector = X.getMatrix(0, n - 1, i, i);
            final Matrix centeredVector = vector.minus(mean);
            covariance = covariance.plus(centeredVector.times(centeredVector.transpose()));
        }
        return covariance.times((double)(1 / N));
    }
    
    public static Matrix covarianceMatrix(final Matrix X) {
        final Matrix mean = meanVector(X);
        return covarianceMatrix(X, mean);
    }
}