package co.iyubinest.mononoke.data.mates;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Mate implements Parcelable {

  private final String name, avatar, github, location, role, status;
  private final Gender gender;
  private final List<String> languages, tags;

  public Mate(String name, String avatar, String github, Gender gender,
      String location, String role, String status, List<String> languages,
      List<String> tags) {
    this.name = name;
    this.avatar = avatar;
    this.github = github;
    this.gender = gender;
    this.location = location;
    this.role = role;
    this.status = status;
    this.languages = languages;
    this.tags = tags;
  }

  public String name() {
    return name;
  }

  public String avatar() {
    return avatar;
  }

  public String github() {
    return github;
  }

  public Gender gender() {
    return gender;
  }

  public String location() {
    return location;
  }

  public String role() {
    return role;
  }

  public String status() {
    return status;
  }

  public List<String> languages() {
    return languages;
  }

  public List<String> tags() {
    return tags;
  }

  public enum Gender {
    MALE, FEMALE, OTHER, NA;

    public static Gender from(String gender) {
      if (gender.equals("Male")) return MALE;
      if (gender.equals("Female")) return FEMALE;
      if (gender.equals("Other")) return OTHER;
      return NA;
    }
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
    dest.writeString(this.location);
    dest.writeString(this.role);
    dest.writeString(this.status);
    dest.writeInt(this.gender == null ? -1 : this.gender.ordinal());
    dest.writeStringList(this.languages);
    dest.writeStringList(this.tags);
  }

  protected Mate(Parcel in) {
    this.name = in.readString();
    this.avatar = in.readString();
    this.github = in.readString();
    this.location = in.readString();
    this.role = in.readString();
    this.status = in.readString();
    int tmpGender = in.readInt();
    this.gender = tmpGender == -1 ? null : Gender.values()[tmpGender];
    this.languages = in.createStringArrayList();
    this.tags = in.createStringArrayList();
  }

  public static final Creator<Mate> CREATOR = new Creator<Mate>() {
    @Override
    public Mate createFromParcel(Parcel source) {
      return new Mate(source);
    }

    @Override
    public Mate[] newArray(int size) {
      return new Mate[size];
    }
  };
}