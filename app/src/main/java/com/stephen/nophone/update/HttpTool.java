package com.stephen.nophone.update;

import android.content.Context;
import android.util.Log;

import com.stephen.nophone.MyApplication;
import com.stephen.nophone.R;
import com.stephen.nophone.tool.SPTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stephen on 17-7-15.
 * 根据url，获取网页源码
 */

public class HttpTool {

    private static final String TAG = HttpTool.class.getSimpleName();
    private static final String VERSION_FILE = "https://github.com/lc-xie/NoPhone/blob/master/version.txt";
    private static final String PATTERN = "<td id=\"LC1\" class=\"blob-code blob-code-inner js-file-line\">(.*?)</td>";

    /**
     * 发送网络请求，获取源码，by HttpUrlConnection
     *
     * @param url 链接
     * @return 网页源码
     */
    public static String getHtmlByHttpUrlConnection(String url) {
        //定义一个缓冲字符输入流
        BufferedReader in = null;
        try {
            URL resultUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) resultUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0");
            InputStream inputStream = connection.getInputStream();//获取网页源码
            //return OkHttpUtil.inputStreamToString(inputStream);
            return inputStreamToString(inputStream);
        } catch (Exception e) {
            Log.e(TAG, "发送GET请求出现异常！" + e);
            e.printStackTrace();
            // 网络请求不在主线程，不能打Toast
            // Toast.makeText(MyApplication.getContext(), "网络请求出错！", Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        //将网页源码转换成String
        BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        StringBuffer res = new StringBuffer();
        String line = "";
        while ((line = buff.readLine()) != null) {
            res.append(line);
        }
        return res.toString();
    }

    // 获取最新的版本号
    public static String curPublishedData(String html) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // 判断是否需要更新
    public static boolean update() {
        String html = getHtmlByHttpUrlConnection(VERSION_FILE);
        String updateMsg = curPublishedData(html);
        if (updateMsg == null) return false;
        String lastVersion = MyApplication.getContext().getResources().getString(R.string.current_version);
        //Log.e(TAG, updateMsg + "  " + lastVersion);
        if (updateMsg.equals(lastVersion)) return false;
        return true;
    }

}
