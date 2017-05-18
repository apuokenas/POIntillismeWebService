package lt.tumenas.pointillisme.ws;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pois")
public class PointOfInterest {

  private int id;
  private String name;
  private String description;
  private double latitude;
  private double longitude;
  private String image;
  private String address;

  // Access can be package-private (no public access modifier needed)
  int getId() {
    return id;
  }

  // Access can be package-private (no public access modifier needed to all setters and getters)
  String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  String getDescription() {
    return description;
  }

  void setDescription(String description) {
    this.description = description;
  }

  double getLatitude() {
    return latitude;
  }

  void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  double getLongitude() {
    return longitude;
  }

  void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  String getImage() {
    return image;
  }

  void setId(int id) {
    this.id = id;
  }

  void setImage(String image) {
    this.image = image;
  }

  String getAddress() {
    return address;
  }

  void setAddress(String address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return "{id: " + id + ", name: " + name + ", description: " + description
      + ", lat: " + latitude + " , lon: " + longitude + ", image: "
      + image + ", address: " + address + "}";
  }

}
