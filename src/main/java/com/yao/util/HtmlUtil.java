package com.yao.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.List;

public class HtmlUtil {

    private static String dfs(Node node){
        if(node instanceof TextNode){
            return ((TextNode) node).text();
        }

        List<Node> childNodes = node.childNodes();
        StringBuffer sb = new StringBuffer();
        for(Node child: childNodes){
            sb.append(dfs(child));
        }
        return sb.toString();
    }

    public static String html2Str(String html) {
        Document doc = Jsoup.parse(html);
        return dfs(doc);
    }
}
