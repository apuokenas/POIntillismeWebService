package lt.tumenas.pointillisme.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pois")
public class POIConverter {

  // Access can be private (no need to be without access modifer)
  private PointOfInterest entity = null;

  public POIConverter() {
    entity = new PointOfInterest();
  }

  // Access can be package-private (no public access modifier needed)
  POIConverter(PointOfInterest poi) {
    entity = poi;
  }

  @XmlElement
  public int getId() {
    return entity.getId();
  }

  public void setId(int id) {
    this.entity.setId(id);
  }

  public PointOfInterest getEntity() {
    return entity;
  }

  @XmlElement
  public String getName() {
    return entity.getName();
  }

  public void setName(String name) {
    this.entity.setName(name);
  }

  @XmlElement
  public String getDescription() {
    return entity.getDescription();
  }

  public void setDescription(String description) {
    this.entity.setDescription(description);
  }

  @XmlElement
  public double getLatitude() {
    return entity.getLatitude();
  }

  public void setLatitude(double latitude) {
    this.entity.setLatitude(latitude);
  }

  @XmlElement
  public double getLongitude() {
    return entity.getLongitude();
  }

  public void setLongitude(double longitude) {
    this.entity.setLongitude(longitude);
  }


  public void setImage(String image) {
    this.entity.setImage(image);
  }

  @XmlElement
  public String getImage() {
    return entity.getImage();
  }

  @XmlElement
  public String getAddress() {
    return entity.getAddress();
  }

  public void setAddress(String address) {
    this.entity.setAddress(address);
  }

}
