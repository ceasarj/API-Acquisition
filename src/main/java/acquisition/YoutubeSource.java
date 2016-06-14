package acquisition;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.*;

import com.google.api.services.youtube.model.VideoStatistics;
import model.VideoModel;

public class YoutubeSource implements Source<VideoModel> {

    private YouTube youtube;
    private Stack<List<VideoModel>> videos;
    private String query;
    private String pageToken;

    private final long MAX_ITEMS = 50;

    public YoutubeSource(String query){
        this.query = query;
        this.videos = new Stack<>();
        this.pageToken = "";

        this.youtube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(), request -> {
        }).setApplicationName("video-statistics").build();
    }

    @Override
    public boolean hasNext() {
        search();
        return !videos.empty();
    }

    @Override
    public Collection<VideoModel> next() {
        return videos.pop();
    }

    private void search() {
        List<VideoModel> videoModels = new ArrayList<>();
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
                        videoModels.add(getVideoModel(video));
                    }
                }

                if(videoModels.size() > 0)
                    videos.push(videoModels);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private VideoModel getVideoModel(SearchResult video) {
        // set up the video model
        VideoModel vm = new VideoModel();
        vm.id = video.getId().getVideoId();
        vm.title = video.getSnippet().getTitle();
        vm.publishedDate = video.getSnippet().getPublishedAt().toString();
        vm.word = query;
        return vm;
    }

    public static void main(String[] args){
        YoutubeSource ys = new YoutubeSource("Batman");
        while(ys.hasNext()){
            ArrayList<VideoModel> vm = (ArrayList)ys.next();
            System.out.println(vm.get(0).title);
        }
    }
}
