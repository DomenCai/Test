package com.domencai.one.bean;

import java.util.List;

/**
 * Created by Domen、on 2017/11/17.
 */

public class BookRecommend {

    public List<BooksBean> books;

    public static class BooksBean {

        public String _id;
        public String title;
        public String author;
        public String site;
        public String cover;
        public String shortIntro;
        public String lastChapter;
        public double retentionRatio;
        public int latelyFollower;
        public String majorCate;
        public String minorCate;
        public String cat;
    }
}
