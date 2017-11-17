/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.domencai.one.bean;

import java.io.Serializable;
import java.util.List;

public class BookDetail implements Serializable {

    /**
     * _id : 5816b415b06d1d32157790b1
     * title : 圣墟
     * author : 辰东
     * longIntro : 在破败中崛起，在寂灭中复苏。沧海成尘，雷电枯竭，那一缕幽雾又一次临近大地，世间的枷锁被打开了，一个全新的世界就此揭开神秘的一角……
     * cover : /agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F1228859
     * %2F_1228859_441552.jpg%2F
     * majorCate : 玄幻
     * minorCate : 东方玄幻
     * creater : iPhone 5s (UK+Europe+Asis+China)
     * rating : {"count":19676,"score":8.707,"isEffect":true}
     * hasCopyright : true
     * buytype : 2
     * sizetype : -1
     * superscript :
     * currency : 0
     * contentType : txt
     * _le : false
     * allowMonthly : false
     * allowVoucher : true
     * allowBeanVoucher : false
     * hasCp : true
     * postCount : 51753
     * latelyFollower : 308441
     * followerCount : 0
     * wordCount : 2915161
     * serializeWordCount : 7976
     * retentionRatio : 72.02
     * updated : 2017-11-16T05:03:40.011Z
     * isSerial : true
     * chaptersCount : 760
     * lastChapter : 第759章 蛋蛋的忧伤
     * gender : ["male"]
     * tags : ["玄幻","东方玄幻"]
     * advertRead : true
     * cat : 东方玄幻
     * donate : false
     * copyright : 阅文集团正版授权
     * _gg : false
     * discount : null
     */

    public String _id;
    public String title;
    public String author;
    public String longIntro;
    public String cover;
    public String majorCate;
    public String minorCate;
    public String creater;
    public RatingBean rating;
    public boolean hasCopyright;
    public int buytype;
    public int sizetype;
    public String superscript;
    public int currency;
    public String contentType;
    public boolean _le;
    public boolean allowMonthly;
    public boolean allowVoucher;
    public boolean allowBeanVoucher;
    public boolean hasCp;
    public int postCount;
    public int latelyFollower;
    public int followerCount;
    public int wordCount;
    public int serializeWordCount;
    public String retentionRatio;
    public String updated;
    public boolean isSerial;
    public int chaptersCount;
    public String lastChapter;
    public boolean advertRead;
    public String cat;
    public boolean donate;
    public String copyright;
    public boolean _gg;
    public Object discount;
    public List<String> gender;
    public List<String> tags;

    public static class RatingBean {
        /**
         * count : 19676
         * score : 8.707
         * isEffect : true
         */

        public int count;
        public double score;
        public boolean isEffect;
    }
}
