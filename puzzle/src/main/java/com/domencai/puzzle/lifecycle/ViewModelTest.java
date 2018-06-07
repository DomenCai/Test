package com.domencai.puzzle.lifecycle;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Domen、on 2017/9/5.
 */

public class ViewModelTest extends ViewModel {

    private MediatorLiveData<String> mMediatorLiveData = new MediatorLiveData<>();
    private MutableLiveData<Integer> mMutableLiveData = new MutableLiveData<>();

    public ViewModelTest() {
        mMediatorLiveData.addSource(mMutableLiveData, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Log.w("ViewModelTest", "onChanged: " + integer);
                mMediatorLiveData.removeSource(mMutableLiveData);
//                mMediatorLiveData.addSource();
                if (integer != null && integer > 50) {
                    Log.w("ViewModelTest", "onChanged: integer > 50, integer = " + integer);
                } else {
                    mMediatorLiveData.addSource(mMutableLiveData, new Observer<Integer>() {
                        @Override
                        public void onChanged(@Nullable Integer integer) {
                            mMediatorLiveData.setValue(String.valueOf(integer));
                        }
                    });
                }
            }
        });
    }

    MediatorLiveData<String> getMediatorLiveData() {
        return mMediatorLiveData;
    }

    void setValue(int i) {
        Log.i("ViewModelTest", "setValue: " + i);
        mMutableLiveData.setValue(i);
    }
}
