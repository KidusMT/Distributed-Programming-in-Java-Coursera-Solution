package edu.coursera.distributed;

import edu.coursera.distributed.util.MPI;
import edu.coursera.distributed.util.MPI.MPIException;

/**
 * A wrapper class for a parallel, MPI-based matrix multiply implementation.
 */
public class MatrixMult {

    public static void parallelMatrixMultiply(Matrix a, Matrix b, Matrix c,
                                              final MPI mpi) throws MPIException {

        int myRank = mpi.MPI_Comm_rank(mpi.MPI_COMM_WORLD);
        int size = mpi.MPI_Comm_size(mpi.MPI_COMM_WORLD);

        int nRows = c.getNRows();
        int rowChunck = (nRows + size - 1)/size;
        int startRow = myRank * rowChunck;
        int endRow = (myRank+1) * rowChunck;
        if (endRow > nRows) endRow = nRows;

        mpi.MPI_Bcast(a.getValues(), 0, a.getNRows() * a.getNCols(), 0, mpi.MPI_COMM_WORLD);
        mpi.MPI_Bcast(b.getValues(), 0, b.getNRows() * b.getNCols(), 0, mpi.MPI_COMM_WORLD);

        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < c.getNCols(); j++) {
                c.set(i, j, 0.0);

                for (int k = 0; k < b.getNRows(); k++) {
                    c.incr(i, j, a.get(i, k) * b.get(k, j));
                }
            }
        }

        if (myRank == 0){
            MPI.MPI_Request[] requests = new MPI.MPI_Request[size -1];
            for (int i = 1; i <size;i++){
                int rankStartRow = i *rowChunck;
                int rankEndRow = (i+1) *rowChunck;
                if (rankEndRow > nRows) rankEndRow = nRows;

                int rowOffset = rankStartRow * c.getNCols();
                int nElements = (rankEndRow-rankStartRow) * c.getNCols();

                requests[i-1] = mpi.MPI_Irecv(c.getValues(), rowOffset,
                        nElements, i, i, mpi.MPI_COMM_WORLD);


            }
            mpi.MPI_Waitall(requests);
        }else {
            mpi.MPI_Send(c.getValues(), startRow * c.getNCols(),
                    (endRow - startRow) * c.getNCols(), 0, myRank,
                    mpi.MPI_COMM_WORLD);
        }
    }
}