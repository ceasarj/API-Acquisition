package acquisition;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import model.VideoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class YoutubeSource implements Source<VideoModel> {

    private YouTube youtube;
    private List<VideoModel> videos;
    private String query;
    private String pageToken;

    private final long MAX_ITEMS = 50;

    public YoutubeSource(String query){
        this.query = query;
        this.videos = new ArrayList<>();
        this.pageToken = "";

        this.youtube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(), request -> {
        }).setApplicationName("video-statistics").build();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Collection<VideoModel> next() {
        return null;
    }

    private void search() {
        while(pageToken != null) {
            try {
                // set up the search url
                YouTube.Search.List search = youtube.search()
                        .list("id,snippet")
                        .setKey(System.getenv("YOUTUBE_KEY"))
                        .setQ(query)
                        .setPageToken(pageToken)
                        .setMaxResults(MAX_ITEMS);

                // excecute request and get response
                SearchListResponse response = search.execute();

                // get token for the next page
                pageToken = response.getNextPageToken();

                // conver json response to a list
                List<SearchResult> searchResultList = response.getItems();
                Iterator<SearchResult> searchResultIterator = searchResultList.iterator();

                List<VideoModel> vms = new ArrayList<>();
                // iterate through all results
                while (searchResultIterator.hasNext()) {
                    SearchResult video = searchResultIterator.next();
                    ResourceId rId = video.getId();

                    // check for only youtube videos
                    if (rId.getKind().equals("youtube#video")) {
                        System.out.println(video.getSnippet().getTitle());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
