package co.iyubinest.mononoke.data.team.get;
import io.reactivex.Flowable;
import java.util.List;
import retrofit2.http.GET;

public interface TeamService {
  @GET("/team")
  Flowable<List<TeamResponse>> team();
  class TeamResponse {
    public String name;
    public String avatar;
    public String github;
    public String location;
    public List<String> languages, tags;
    public Integer role;
  }
}