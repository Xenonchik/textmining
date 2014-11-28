import java.io.File;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import crawl.Controller;
import db.SongAnalyser;

/**
 * Blahblahblah
 *
 * @author Replicating Rat
 * @version 3.6.7
 * @since 3.6.7
 */
public class TMApp {
	public static void main(String[] args) throws Exception {

		Controller.parse();
		SongAnalyser analyser = new SongAnalyser();
		analyser.countWords();
		Map<String, Integer> wordsCount = analyser.getWordsCount();

		Scanner s = new Scanner( Paths.get(TMApp.class.getResource("stopwords.txt").toURI()).toFile() );
		while (s.hasNext()){
			String word = s.next();
			if(wordsCount.containsKey(word)) {
				wordsCount.remove(word);
			}
		}
		s.close();

		ValueComparator bvc =  new ValueComparator(wordsCount);
		TreeMap<String,Integer> sorted_map = new TreeMap<>(bvc);
		sorted_map.putAll(wordsCount);

		int count = 1;
		for(Entry<String, Integer> entry : sorted_map.entrySet()) {
			System.out.println(count + " " + entry.getKey() + ": " + entry.getValue());
			count++;
		}
	}

	static class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;
		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
