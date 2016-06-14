package acquisition.youtube;

import java.util.ArrayList;

/**
 * Created by ceejay562 on 6/14/2016.
 */
public class YoutubeCollectorApp {
    public static void main(String[] args){
        YoutubeSource source = new YoutubeSource("batman");
        while(source.hasNext()){
            System.out.println(((ArrayList)source.next()).get(0));
        }
    }

}
