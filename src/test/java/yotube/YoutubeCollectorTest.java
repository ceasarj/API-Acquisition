package yotube;

import acquisition.youtube.VideoModel;
import acquisition.youtube.YoutubeCollector;
import acquisition.youtube.YoutubeSource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by ceejay562 on 6/29/2016.
 */
public class YoutubeCollectorTest {

    private YoutubeCollector collector;
    private List<VideoModel> videos;

    @Before
    public void setup(){
        collector = new YoutubeCollector();
    }

    @Test
    public void testMungee(){
        List<VideoModel>
    }

}
