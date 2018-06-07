package com.domencai.puzzle.custom;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Domen、on 2017/9/7.
 */

public class MySnapHelper extends LinearSnapHelper {

    private static final int MAX_MOVE = 6;

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
            int velocityY) {
        final View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        int targetSnapPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
        if (targetSnapPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        if (targetSnapPosition > currentPosition + MAX_MOVE) {
            targetSnapPosition = currentPosition + MAX_MOVE;
        } else if (targetSnapPosition < currentPosition - MAX_MOVE) {
            targetSnapPosition = currentPosition - MAX_MOVE;
        }
        return targetSnapPosition;
    }

}
