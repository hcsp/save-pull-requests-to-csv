package com.github.hcsp.io;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.net.www.http.HttpClient;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {}

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/golang/go/pulls");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        try{
            System.out.println(httpResponse.getStatusLine());
            HttpEntity entity1 = httpResponse.getEntity();
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is,"UTF-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".Box-row");
            StringBuilder sb = new StringBuilder();
            for (Element el: elements
                 ) {
                Element numberElemnt = el.select(".Box-row").select("div[class='mt-1 text-small text-gray']").select(">span").first();
                Element tilteElement = el.select(".Box-row").select("a[id*='issue']").first();
                Element authorElement = el.select(".Box-row").select("div[class='mt-1 text-small text-gray']").select(">span[class='opened-by']").select("a[class='muted-link']").first();
                if(numberElemnt != null && tilteElement != null && authorElement != null)
                {
                    String fullNumberText = numberElemnt.text();
                    sb.append(fullNumberText.substring(1,fullNumberText.indexOf("opened")));
                    sb.append(",");
                    sb.append(authorElement.childNodes().get(0).toString());
                    sb.append(",");
                    sb.append(tilteElement.childNodes().get(0).toString());
                    sb.append("\r\n");
                }
            }

            System.out.println(sb.toString());
        }finally {
            httpResponse.close();
        }
    }
}



