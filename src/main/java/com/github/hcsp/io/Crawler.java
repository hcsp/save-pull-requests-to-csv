package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        List<String[]> result = new ArrayList<>();

        String WEB_SITE = "https://github.com/" + repo + "/pulls";

        HttpGet httpGet = new HttpGet( WEB_SITE );

        //获取结果集
        CloseableHttpResponse response = httpClient.execute( httpGet );
        //获取请求体
        HttpEntity entity = response.getEntity();

        //转换成流
        InputStream IS = entity.getContent();

        String HTML = IOUtils.toString( IS, StandardCharsets.UTF_8 );
        /**读取服务器返回过来的json字符串数据 并解析成节点**/
        Document document = Jsoup.parse( HTML );

        //解析节点
        Elements titles = document.select( ".js-issue-row" );
        Elements authors = document.select( ".opened-by" );

        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get( i ).child( 0 ).child( 1 ).child( 0 ).text();
            String author = authors.get( i ).child( 1 ).text();
            String id = titles.get( i ).attr( "id" ).replaceAll( "\\D+", "" );
            result.add(new String[] {id,author,title});
        }

        WriteCSV(result,csvFile);
    }

    public static void WriteCSV(List<String[]> rows,File csvFile) throws IOException {
        CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(csvFile)));
        //第一次写入表头
        writer.writeNext(new String[]{"number", "author", "title"});
        //
        writer.writeAll(rows);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile( "csv", "" );
        savePullRequestsToCSV( "golang/go", 10, tmp );
    }
}
