package com.domencai.puzzle.custom;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Domen、on 2017/8/31.
 */

public class Puzzle {
    private int mBlankCol;
    private int mBlankRow;
    private int mColCount;
    private int mRowCount;
    private int[] mArray;

    private Puzzle(int colCount, int rowCount) {
        mColCount = colCount;
        mRowCount = rowCount;
        initArray(colCount, rowCount);
    }

    private void initArray(int colCount, int rowCount) {
        int len = colCount * rowCount;
        mArray = new int[len];

        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < len; i++) {
            list.add(i);
        }

        Random random = new Random();
        for (int i = 0; i < len; i++) {
            mArray[i] = list.remove(random.nextInt(list.size()));
            if (mArray[i] == 0) {
                setBlankGrid(i);
            }
        }

        if (isNullSolution()) {
            if (mArray[0] == 0) {
                setBlankGrid(2);
            } else if (mArray[2] == 0) {
                setBlankGrid(0);
            }
            int temp = mArray[0];
            mArray[0] = mArray[2];
            mArray[2] = temp;
        }
    }

    private void setBlankGrid(int num) {
        mBlankCol = num % mColCount;
        mBlankRow = num / mColCount;
    }

    private boolean isNullSolution() {
        int reverse = 0;
        int len = mArray.length;

        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (mArray[i] > mArray[j]) {
                    reverse++;
                }
            }
        }

        boolean selfParity = (reverse + mBlankCol + mBlankRow) % 2 == 0;
        boolean desParity = (len - 1 + mColCount + mRowCount) % 2 == 0;
        return selfParity != desParity;
    }

    private boolean hasFinish() {
        for (int i = 0; i < mArray.length - 1; i++) {
            if (mArray[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    private void print() {
        for (int i = 0; i < mArray.length; i++) {
            System.out.printf(String.format("%2d", mArray[i])
                    + ((i + 1) % mColCount == 0 ? "\n" : ", "));
        }
    }

    private void toBottom() {
        if (mBlankRow > 0) {
            exchange(mBlankRow * mColCount + mBlankCol, (--mBlankRow) * mColCount + mBlankCol);
            print();
        }
    }

    private void toTop() {
        if (mBlankRow < mRowCount - 1) {
            exchange(mBlankRow * mColCount + mBlankCol, (++mBlankRow) * mColCount + mBlankCol);
            print();
        }
    }

    private void toRight() {
        if (mBlankCol > 0) {
            exchange(mBlankRow * mColCount + mBlankCol, mBlankRow * mColCount + (--mBlankCol));
            print();
        }
    }

    private void toLeft() {
        if (mBlankCol < mColCount - 1) {
            exchange(mBlankRow * mColCount + mBlankCol, mBlankRow * mColCount + (++mBlankCol));
            print();
        }
    }

    private void exchange(int from, int to) {
        mArray[from] = mArray[to];
        mArray[to] = 0;
    }
}
