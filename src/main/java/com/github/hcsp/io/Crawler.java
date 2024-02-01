package com.github.hcsp.io;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://github.com/" + repo + "/pulls");
        try (CloseableHttpResponse response = client.execute(get)) {
            String content = EntityUtils.toString(response.getEntity());
            Document doc = Jsoup.parse(content);
            List<String> titles = doc.select(".flex-auto.min-width-0.p-2").select(".Link--primary").eachText();
            List<String> number_author = doc.select(".opened-by").eachText();
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
            bw.append("number,author,title");
            for (int i = 0; i < titles.size() && i < n; i++) {
                bw.newLine();
                String[] temp = number_author.get(i).split(" ");
                String number = temp[0].substring(1);
                String author = temp[temp.length - 1];
                String title = titles.get(i);
                bw.append(number).append(",").append(author).append(",").append(title);
            }
            bw.flush();
            bw.close();
        }
    }

    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile("csv", "");
        savePullRequestsToCSV("golang/go", 1, tmp);
    }
}
