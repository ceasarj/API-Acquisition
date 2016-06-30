package yotube;

import acquisition.Source;
import acquisition.youtube.VideoModel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ceejay562 on 6/30/2016.
 */
public class MockYoutubeSource implements Source<Collection<VideoModel>> {



    @Override
    public Collection<Collection<VideoModel>> next(){
        return null;
    }

    @Override
    public boolean hasNext(){
        return true;
    }

    public ArrayList<VideoModel> populateData(){
        List<VideoModel> vms = new ArrayList<VideoModel>();
        vms.add(new VideoModel());
    }
}
