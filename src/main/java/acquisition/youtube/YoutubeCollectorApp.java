package acquisition.youtube;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ceejay562 on 6/14/2016.
 */
public class YoutubeCollectorApp {
    public static void main(String[] args){
        YoutubeSource source = new YoutubeSource("batman");

        YoutubeCollector collector = new YoutubeCollector();

        while(source.hasNext()){
            Collection<VideoModel> rawData = source.next();

            Collection<VideoModel> cleanedData = collector.mungee(rawData);

            // check if there is still video data after cleaning
            if(cleanedData.size() > 0)
                collector.save(cleanedData);
        }
    }

}
