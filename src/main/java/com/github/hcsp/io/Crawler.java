package com.github.hcsp.io;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.util.ArrayList;

import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pull");
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
// The underlying HTTP connection is still held by the response object
// to allow the response content to be streamed directly from the network socket.
// In order to ensure correct deallocation of system resources
// the user MUST call CloseableHttpResponse#close() from a finally clause.
// Please note that if response content is not fully consumed the underlying
// connection cannot be safely re-used and will be shut down and discarded
// by the connection manager.
        try {
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document doc = Jsoup.parse(html);
            Elements newsHeadlines = doc.select(".js-active-navigation-container").select(".js-navigation-item");
            List<String> list = new ArrayList<>();
            list.add("number,author,title");
            int i = 0;
            for (Element element : newsHeadlines) {
                String text = element.child(0).child(1).child(0).text();
                String id = element.child(0).child(1).child(0).attr("id").split("_")[1];
                String name = element.getElementsByClass("Link--muted").text().split(" ")[0];
                list.add(id + "," + name + "," + text);
                i++;
                if (i == n) {
                    break;
                }
            }
            Files.write(csvFile.toPath(), list);
            EntityUtils.consume(entity1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response1.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
