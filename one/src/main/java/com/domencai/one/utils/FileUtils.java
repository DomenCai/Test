package com.domencai.one.utils;

import android.util.Log;

import com.domencai.one.App;

import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Domen、on 2017/11/24.
 */

public class FileUtils {
    private final static int BUFFER_SIZE = 2048;
    private static final String KEY_CHAPTER = "chapter";
    private static final String KEY_BOOK = "book";
    private static final String KEY_RECOMMEND = "recommend";

    public static void writeRecommend(String bookId, String content) {
        saveFile(getFilePath(KEY_RECOMMEND) + File.separator + bookId, content);
    }

    public static void writeChapters(String bookId, String content) {
        saveFile(getFilePath(KEY_CHAPTER) + File.separator + bookId, content);
    }

    public static String readChapters(String bookId) {
        return getFileContent(getFilePath(KEY_CHAPTER) + File.separator + bookId);
    }

    public static void writerBody(int chapter, String body) {
        String bookId = BookManager.getInstance().getBookId();
        saveFile(getFilePath(KEY_BOOK + File.separator + bookId) + File.separator + chapter, body);
    }

    public static String readBody(int chapter) {
        String bookId = BookManager.getInstance().getBookId();
        return getFileContent(getFilePath(KEY_BOOK + File.separator + bookId) + File.separator + chapter);
    }

    private static void saveFile(String path, String content) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            fileWriter.write(content);
        } catch (IOException e) {
            Log.w("FileUtils", "saveFile: " + e.toString());
        } finally {
            close(fileWriter);
        }
    }

    private static String getFileContent(String path) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            char[] data = new char[BUFFER_SIZE];
            int count;
            while ((count = fileReader.read(data, 0, BUFFER_SIZE)) != -1) {
                charArrayWriter.write(data, 0, count);
            }
            return charArrayWriter.toString();
        } catch (IOException e) {
            Log.w("FileUtils", "getFileContent: " + e.toString());
        } finally {
            close(fileReader);
        }
        return "";
    }

    private static String getFilePath(String dir) {
        File file = App.getAppContext().getExternalFilesDir(dir);
        return file == null ? App.getAppContext().getFilesDir().getPath() : file.getPath();
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.w("FileUtils", "close: " + e.toString());
            }
        }
    }
}
