package com.domencai.one.api;

import com.domencai.one.bean.BookDetail;
import com.domencai.one.bean.BookListDetail;
import com.domencai.one.bean.BookListTags;
import com.domencai.one.bean.BookLists;
import com.domencai.one.bean.BookMixAToc;
import com.domencai.one.bean.BookRecommend;
import com.domencai.one.bean.BooksByCats;
import com.domencai.one.bean.CategoryList;
import com.domencai.one.bean.CategoryListLv2;
import com.domencai.one.bean.ChapterRead;
import com.domencai.one.bean.HotWord;
import com.domencai.one.bean.RankingList;
import com.domencai.one.bean.Rankings;
import com.domencai.one.bean.Recommend;
import com.domencai.one.bean.RecommendBookList;
import com.domencai.one.bean.SearchDetail;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Domen、on 2017/11/13.
 */

public interface BookApi {
    /**
     * 获取推荐的书单
     * @param gender male、female
     */
    @GET("/book/recommend")
    Observable<Recommend> getRecommend(@Query("gender") String gender);

    @GET("/book-list/{bookId}/recommend?limit=4")
    Observable<RecommendBookList> getRecommendBookList(@Path("bookId") String bookId);

    @GET("/book/{bookId}/recommend")
    Observable<BookRecommend> getRecommendBook(@Path("bookId") String bookId);

    @GET("/book/{bookId}")
    Observable<BookDetail> getBookDetail(@Path("bookId") String bookId);

    /**
     * 获取所有章节的信息
     */
    @GET("/mix-atoc/{bookId}")
    Observable<BookMixAToc> getBookMixAToc(@Path("bookId") String bookId);

    /**
     * 获取章节的具体内容
     * @param url 章节信息里面的link
     */
    @GET("http://chapter2.zhuishushenqi.com/chapter/{url}")
    Observable<ChapterRead> getChapterRead(@Path("url") String url);




    /*------------------------------------------------------------------*/
    /*----------------------------  各排行榜  ---------------------------*/
    /*------------------------------------------------------------------*/
    /**
     * 获取所有排行榜
     */
    @GET("/ranking/gender")
    Observable<RankingList> getRanking();

    /**
     * 获取单一排行榜
     * @param rankingId 所有排行榜列表获得，其他家的只有周榜
     *                  周榜：rankingId->_id
     *                  月榜：rankingId->monthRank
     *                  总榜：rankingId->totalRank
     */
    @GET("/ranking/{rankingId}")
    Observable<Rankings> getRanking(@Path("rankingId") String rankingId);




    /*------------------------------------------------------------------*/
    /*----------------------------  主题书单  ---------------------------*/
    /*------------------------------------------------------------------*/
    /**
     * 获取主题书单标签列表
     */
    @GET("/book-list/tagType")
    Observable<BookListTags> getBookListTags();

    /**
     * 获取主题书单列表
     * 本周最热：duration=last-seven-days&sort=collectorCount
     * 最新发布：duration=all&sort=created
     * 最多收藏：duration=all&sort=collectorCount
     *
     * @param tag    主题书单标签列表中的一个
     * @param gender male、female
     * @param limit  20
     */
    @GET("/book-list")
    Observable<BookLists> getBookLists(@Query("duration") String duration,
                                       @Query("sort") String sort,
                                       @Query("start") String start,
                                       @Query("limit") String limit,
                                       @Query("tag") String tag,
                                       @Query("gender") String gender);

    /**
     * 获取书单详情
     * @param bookListId 主题书单列表中的id
     */
    @GET("/book-list/{bookListId}")
    Observable<BookListDetail> getBookListDetail(@Path("bookListId") String bookListId);



    /*------------------------------------------------------------------*/
    /*----------------------------  分类阅读  ---------------------------*/
    /*------------------------------------------------------------------*/
    /**
     * 获取分类
     */
    @GET("/cats/lv2/statistics")
    Observable<CategoryList> getCategoryList();

    /**
     * 获取二级分类
     */
    @GET("/cats/lv2")
    Observable<CategoryListLv2> getCategoryListLv2();

    /**
     * 按分类获取书籍列表
     *
     * @param gender male、female
     * @param type   hot(热门)、new(新书)、reputation(好评)、over(完结)
     * @param major  玄幻 （必传）
     * @param minor  东方玄幻、异界大陆、异界争霸、远古神话
     * @param limit  50
     */
    @GET("/book/by-categories")
    Observable<BooksByCats> getBooksByCats(@Query("gender") String gender,
                                           @Query("type") String type,
                                           @Query("major") String major,
                                           @Query("minor") String minor,
                                           @Query("start") int start,
                                           @Query("limit") int limit);




    /*------------------------------------------------------------------*/
    /*----------------------------  搜索书籍  ---------------------------*/
    /*------------------------------------------------------------------*/
    @GET("/book/hot-word")
    Observable<HotWord> getHotWord();

    /**
     * 书籍查询
     */
    @GET("/book/fuzzy-search")
    Observable<SearchDetail> searchBooks(@Query("query") String query);
}
