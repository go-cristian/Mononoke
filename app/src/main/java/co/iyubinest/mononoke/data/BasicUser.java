package co.iyubinest.mononoke.data;

import android.os.Parcel;
import java.util.List;

public class BasicUser implements User {

  public static final Creator<BasicUser> CREATOR = new Creator<BasicUser>() {
    @Override
    public BasicUser createFromParcel(Parcel source) {
      return new BasicUser(source);
    }

    @Override
    public BasicUser[] newArray(int size) {
      return new BasicUser[size];
    }
  };

  private final String name;
  private final String avatar;
  private final String github;
  private final String role;
  private final String location;
  private final String status;
  private final List<String> languages;
  private final List<String> tags;

  private BasicUser(String name, String avatar, String github, String role,
      String location, String status, List<String> languages,
      List<String> tags) {
    this.name = name;
    this.avatar = avatar;
    this.github = github;
    this.role = role;
    this.location = location;
    this.status = status;
    this.languages = languages;
    this.tags = tags;
  }

  private BasicUser(Parcel in) {
    this.name = in.readString();
    this.avatar = in.readString();
    this.github = in.readString();
    this.role = in.readString();
    this.location = in.readString();
    this.status = in.readString();
    this.languages = in.createStringArrayList();
    this.tags = in.createStringArrayList();
  }

  public static User create(String name, String avatar, String github,
      String role, String location, String status, List<String> languages,
      List<String> tags) {
    return new BasicUser(name, avatar, github, role, location, status,
        languages, tags);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String avatar() {
    return avatar;
  }

  @Override
  public String github() {
    return github;
  }

  @Override
  public String role() {
    return role;
  }

  @Override
  public String location() {
    return location;
  }

  @Override
  public String status() {
    return status;
  }

  @Override
  public List<String> languages() {
    return languages;
  }

  @Override
  public List<String> tags() {
    return tags;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.avatar);
    dest.writeString(this.github);
    dest.writeString(this.role);
    dest.writeString(this.location);
    dest.writeString(this.status);
    dest.writeStringList(this.languages);
    dest.writeStringList(this.tags);
  }
}