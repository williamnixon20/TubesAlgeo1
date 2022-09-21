package matrix;

import java.util.*;
import matrix.coordinate.*;

public class Matrix {
  static final int MAX_DIMENSION = 1000;
  /**
   * private artinya tidak bisa diakses dari luar
   * segala interaksi dengan field harus dilakukan dengan fungsi di kelas matriks
   * (this.)
   */
  private double[][] mat;
  private int rowEff = 0;
  private int colEff = 0;
  private boolean isValid;
  private Scanner scanner;

  /**
   * 
   * @param row     Dimensi baris yang ingin dimiliki matriks
   * @param col     Dimensi kolom yang ingin dimiliki matriks
   * @param scanner Scanner u/matriks
   */
  public Matrix(int row, int col, boolean isValid, Scanner scanner) {
    this.rowEff = row;
    this.colEff = col;
    this.isValid = isValid;
    this.scanner = scanner;
    this.mat = new double[row][col];
  }

  /**
   * @return Index baris terakhir di matriks
   */
  public int getRowLastIdx() {
    return this.rowEff - 1;
  }

  /**
   * @return Index kolom terakhir di matriks
   */
  public int getColLastIdx() {
    return this.colEff - 1;
  }

  /**
   * 
   * @return Banyak baris efektif
   */
  public int getRowLength() {
    return this.rowEff;
  }

  /**
   * 
   * @return Banyak kolom efektif
   */
  public int getColLength() {
    return this.colEff;
  }

  /**
   * 
   * Menimpa element pada index [row][col] dengan value
   */
  public void setMatrixElement(int row, int col, double value) {
    this.mat[row][col] = value;
  }

  public void changeMatrixValidity(boolean isValid) {
    this.isValid = isValid;
  }

  /**
   * Mengisi matriks sesuai input
   */
  public void readMatrix() {
    System.out.println("Silahkan masukkan matriks " + getRowLength() + "x" + getColLength() + ":");
    for (int i = 0; i < this.rowEff; i++) {
      for (int j = 0; j < this.colEff; j++) {
        this.mat[i][j] = this.scanner.nextDouble();
      }
    }
  }

  /**
   * Menulis matriks ke layar
   */
  public void writeMatrix() {
    if (this.isValid) {
      for (int i = 0; i < getRowLength(); i++) {
        for (int j = 0; j < this.colEff; j++) {
          System.out.printf("%.2f ", this.mat[i][j]);
        }
        System.out.print("\n");
      }
    }
  }

  /**
   * 
   * @param fromRow    baris yang akan menjadi acuan OBE
   * @param toRow      baris yang akan dilakukan OBE
   * @param multiplier konstanta pengali di dalam OBE
   */
  public void doRowOperation(int fromRow, int toRow, double multiplier) {
    for (int col = 0; col < this.colEff; col++) {
      this.mat[toRow][col] += this.mat[fromRow][col] * multiplier;
    }
  }

  /**
   * 
   * @param fromCol    kolom yang akan menjadi acuan OBE
   * @param toCol      kolom yang akan dilakukan OBE
   * @param multiplier konstanta pengali di dalam OBE
   */
  public void doColOperation(int fromCol, int toCol, double multiplier) {
    for (int row = 0; row < this.rowEff; row++) {
      this.mat[row][toCol] += this.mat[row][fromCol] * multiplier;
    }
  }

  /**
   * 
   * @param row        indeks baris yang akan dikalikan oleh konstanta
   * @param multiplier konstanta pengali di dalam OBE
   */
  public void multiplyRow(int row, double multiplier) {
    for (int col = 0; col < this.colEff; col++) {
      this.mat[row][col] *= multiplier;
    }
  }

  /**
   * @param firstRow  indeks baris yang akan ditukar dengan baris kedua
   * @param secondRow indeks baris yang akan ditukar dengan baris pertama
   */
  public void swapRow(int firstRow, int secondRow) {
    double temp;
    for (int col = 0; col < getColLength(); col++) {
      temp = this.mat[firstRow][col];
      this.mat[firstRow][col] = this.mat[secondRow][col];
      this.mat[secondRow][col] = temp;
    }
  }

  /**
   * @param row indeks baris yang akan dicari indeks letak leading koefisiennya
   * @return indeks kolom dari leading koefisiennya, 1000 (MAX_DIMENSION) jika
   *         baris nol semua
   */
  public int getLeadingCoeffIdx(int row) {
    for (int col = 0; col < getColLength(); col++) {
      if (this.mat[row][col] != 0) {
        return col;
      }
    }
    return MAX_DIMENSION;
  }

  /**
   * 
   * @param startRow baris awal mulai pengecekan pivot
   * @return Koordinat matriks dari pivot, elemen dengan leading koefisien paling
   *         kiri
   */
  public Coordinate getPivot(int startRow) {
    Coordinate pivot = new Coordinate(startRow, getLeadingCoeffIdx(startRow));
    for (int row = startRow + 1; row < getRowLength(); row++) {
      int leadCoeff = getLeadingCoeffIdx(row);

      if (leadCoeff < pivot.getCol()) {
        pivot.setRow(row);
        pivot.setCol(leadCoeff);
      }
    }

    return pivot;
  }

  /**
   * Mengubah matriks menjadi bentuk Eselon Baris
   */
  public void toREF() {
    Coordinate pivot = getPivot(0);
    int curLead = pivot.getCol();
    for (int row = 0; row < getRowLength() - 1; row++) {
      if (curLead < MAX_DIMENSION) {
        if (pivot.getRow() != row) {
          swapRow(pivot.getRow(), row);
        }

        if (this.mat[row][curLead] != 1) {
          multiplyRow(row, 1 / this.mat[row][curLead]);
        }

        for (int xrow = row + 1; xrow < getRowLength(); xrow++) {
          int nextLead = getLeadingCoeffIdx(xrow);
          if (curLead == nextLead) {
            double multiplier = (-1) * this.mat[xrow][curLead] / this.mat[row][curLead];
            doRowOperation(row, xrow, multiplier);
          }
        }
      }
      pivot = getPivot(row + 1);
      curLead = pivot.getCol();
    }

    if (curLead < MAX_DIMENSION && this.mat[getRowLastIdx()][curLead] != 1) {
      multiplyRow(getRowLastIdx(), 1 / this.mat[getRowLastIdx()][curLead]);
    }
  }

  public void toRREF() {
    toREF();
    for (int row = getRowLastIdx(); row > 0; row--) {
      int curLead = getLeadingCoeffIdx(row);
      if (curLead < MAX_DIMENSION) {
        for (int xrow = row - 1; xrow >= 0; xrow--) {
          if (this.mat[xrow][curLead] != 0) {
            double multiplier = (-1) * this.mat[xrow][curLead] / this.mat[row][curLead];
            doRowOperation(row, xrow, multiplier);
          }
        }
      }
    }
  }
}