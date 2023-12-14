package com.example.usercenter.Test;

import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class MyPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Selectable xpath = html.xpath("//*[@id=\"__next\"]/div/main/div[3]/div[1]/nav/ul/li[9]/a/@href");
        if(xpath.get()!= null);{
        int num = xpath.get().charAt(xpath.get().length()-1)-'0';
        String subStr = xpath.get().substring(0,xpath.get().length()-1);
        String url = "secureblink.com" + xpath.get()+num;
        }
    }

    @Override
    public Site getSite() {
        return Site.me();
    }

    public static void main(String[] args) {
        Spider.create(new MyPageProcessor()).
                setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover())).
                addUrl("https://www.secureblink.com/threat-research").run();

    }
}
