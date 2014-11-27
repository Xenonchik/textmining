package crawl;

import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ManowarCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	Map<String, WebURL> visitedUrls = new HashMap<>();

	boolean nextName = false;

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        if(!href.startsWith("http://www.lyricsfreak.com/m/manowar/")){
            return false;
        }
		if(this.visitedUrls.containsKey(url.getURL())) {
			return false;
		} else {
			this.visitedUrls.put(url.getURL(), url);
		}

		return !FILTERS.matcher(href).matches();
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			// parse html
			Document doc = Jsoup.parse(html);
			String songName = doc.select("h2.lyric-song-head").first().text();
			String songText = doc.select("div#content_h").first().text();

			if (songText != null && songText != "") {
			// save to db
				MongoClient mongoClient = null;
				try {
					mongoClient = new MongoClient("localhost", 27017);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				DB db = mongoClient.getDB("testmining");
				DBCollection coll = db.getCollection("songs");

				BasicDBObject song = new BasicDBObject("name", songName)
						.append("text", songText);
				coll.insert(song);
			}

		}
	}
}