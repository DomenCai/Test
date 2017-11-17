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

import java.util.List;

/**
 * Created by lfh on 2016/8/15.
 */
public class CategoryList {

    public List<MaleBean> male;
    public List<MaleBean> female;

    public static class MaleBean {
        /**
         * name : 玄幻
         * bookCount : 489549
         * monthlyCount : 14078
         * icon : /icon/玄幻_.png
         */

        public String name;
        public int bookCount;
        public String icon;
    }
}
