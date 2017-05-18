package lt.tumenas.pointillisme.ws;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/poi")
public class PoiService {

  /**
   * Database configuration parameters
   */
  private static final String DB_USER = "root"; // Database user ID, default is root
  private static final String DB_PASSWORD = "&FTGuBJQwxtv%7ao"; // Database password
  private static final String DB_NAME = "poi_db";

  @Context
  private UriInfo uri;

  public PoiService() {
  }

  /**
   * The directory where the images are stored. This directory must exist before running the service.
   */
  private static final java.nio.file.Path BASE_DIR = Paths.get(System.getProperty("user.home"), "Documents", "POIImages");

  /**
   * Database connection stings param to be used by the driver.
   *
   * Tag 'param' is not allowed here
   */
  private static final String CONNECTION_PARAM = String.format("jdbc:mysql://localhost/%s?user=%s&password=%s", DB_NAME, DB_USER,
    DB_PASSWORD);

  /**
   * Exposing the GET service to consume.
   *
   * '@return' tag description shall not be missing
   * @return List<POIConverter> of POIs
   */
  @GET
  @Path("/pois")
  @Produces(MediaType.APPLICATION_JSON)
  public List<POIConverter> getPois() {
    // Explicit type argument POIConverter can be replaced with <>
    final List<POIConverter> list = new ArrayList<>();
    Connection con = null;
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      con = DriverManager.getConnection(CONNECTION_PARAM);

      Statement st = con.createStatement();
      String sql = ("SELECT * FROM poi_tb;");
      ResultSet resultSet = st.executeQuery(sql);

      while (resultSet.next()) {
        PointOfInterest poi = new PointOfInterest();
        poi.setId(resultSet.getInt("id"));
        poi.setName(resultSet.getString("name"));
        poi.setDescription(resultSet.getString("description"));
        poi.setLatitude(resultSet.getDouble("latitude"));
        poi.setLongitude(resultSet.getDouble("longitude"));
        poi.setImage(resultSet.getString("image"));
        poi.setAddress(resultSet.getString("address"));

        POIConverter converter = new POIConverter(poi);
        list.add(converter);
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      closeDatabaseConnection(con);
    }

    return list;
  }

  /**
   * Creating POI.
   *
   * Tag description shall not be missing missing
   * @param poi PointOfInterest
   * @return String
   */
  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public String createPoi(PointOfInterest poi) {
    int result = insertOrUpdatePoi(poi);

    if (result > 0) {
      return "Created";
    } else {
      return "Failed to create";
    }
  }

  /**
   * Deleting POI.
   *
   * Tag description shall not be missing missing
   * @param id int
   * @return String
   */
  @DELETE
  @Path("/delete/{poiId}")
  @Produces(MediaType.TEXT_PLAIN)
  public String deletePoi(@PathParam("poiId") int id) {
    Connection con = null;
    int result = 0;
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      con = DriverManager.getConnection(CONNECTION_PARAM);
      Statement statement = con.createStatement();
      // SQL dialect shall be configured.
      String sqlQuery = "DELETE from poi_tb where id=" + id + ";";
      result = statement.executeUpdate(sqlQuery);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      closeDatabaseConnection(con);
    }

    if (result > 0) {
      return "Deleted";
    } else {
      return "Failed to delete";
    }
  }

  /**
   * Uploading POI info.
   *
   * @param uploadedInputStream, fileDetail, body
   * @return String
   */
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public String addPoiMultipart(@FormDataParam("file") InputStream uploadedInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("poi") String body) {
    int result = -1;
    PointOfInterest poi = new PointOfInterest();

    try {
      JSONObject jsonObj = new JSONObject(body);
      String id = jsonObj.optString("id");

      if (id != null && !id.toLowerCase().equals("null")) {
        poi.setId(Integer.parseInt(id));
      }

      poi.setDescription(jsonObj.optString("description"));
      poi.setName(jsonObj.optString("name"));
      poi.setLatitude(jsonObj.optDouble("latitude"));
      poi.setLongitude(jsonObj.optDouble("longitude"));
      poi.setAddress(jsonObj.optString("address"));
    } catch (JSONException e1) {
      e1.printStackTrace();
    }
    try {
      // Create BASE_DIR folder directory if it doesn't exist
      if (Files.notExists(BASE_DIR)) {
        Files.createDirectories(BASE_DIR);
      }

      // Copy the file to its location
      Files.copy(uploadedInputStream, BASE_DIR.resolve(fileDetail.getFileName()), StandardCopyOption.REPLACE_EXISTING);

      String filePath = uri.getBaseUri() + "poi/";

      // Add the image URL to the database
      poi.setImage(filePath + fileDetail.getFileName());
      System.out.println("Image Path = " + poi.getImage());
      result = insertOrUpdatePoi(poi);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (result > 0) {
      return "Uploaded";
    } else {
      return "Failed to upload";
    }
  }

  private int insertOrUpdatePoi(PointOfInterest poi) {
    Connection con = null;
    int result = -1;

    try {
      // Load JDBC driver class
      Class.forName("com.mysql.jdbc.Driver").newInstance();

      // Establishing mySQL connection
      con = DriverManager.getConnection(CONNECTION_PARAM);

      // SQL dialect shall be configured.
      String sqlCheck = "SELECT * FROM poi_tb WHERE id = ?;";
      PreparedStatement prpStatementCheck = con.prepareStatement(sqlCheck);
      prpStatementCheck.setInt(1, poi.getId());
      ResultSet rsCheck = prpStatementCheck.executeQuery();

      boolean exists = false;
      while (rsCheck.next()) {
        System.out.println("Record was found and is to be updated!");
        exists = true;
      }

      if (!exists) {
        // INSERT statement.
        // SQL dialect shall be configured.
        String insertQuery = "INSERT INTO poi_tb(name, description, latitude, longitude, image, address) VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement insertStatement = con.prepareStatement(insertQuery);
        insertStatement.setString(1, poi.getName());
        insertStatement.setString(2, poi.getDescription());
        insertStatement.setDouble(3, poi.getLatitude());
        insertStatement.setDouble(4, poi.getLongitude());
        insertStatement.setString(5, poi.getImage());
        insertStatement.setString(6, poi.getAddress());
        result = insertStatement.executeUpdate();
      } else {
        // SQL dialect shall be configured.
        String updateQuery = "UPDATE poi_tb SET name=?, description=?, latitude=?, longitude=?, image=?, address=? WHERE id=?;";
        PreparedStatement updateStatement = con.prepareStatement(updateQuery);
        updateStatement.setString(1, poi.getName());
        updateStatement.setString(2, poi.getDescription());
        updateStatement.setDouble(3, poi.getLatitude());
        updateStatement.setDouble(4, poi.getLongitude());
        updateStatement.setString(5, poi.getImage());
        updateStatement.setString(6, poi.getAddress());
        updateStatement.setInt(7, poi.getId());
        result = updateStatement.executeUpdate();
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      closeDatabaseConnection(con);
    }

    return result;
  }

  /**
   * Download a JPEG file.
   */
  @GET
  @Path("{name}.jpg")
  @Produces("image/jpeg")
  public InputStream getJpegImage(@PathParam("name") String fileName) throws IOException {
    fileName += ".jpg";
    return attachFileStream(fileName);
  }

  /**
   * Download a PNG file.
   */
  @GET
  @Path("{name}.png")
  @Produces("image/png")
  public InputStream getPngImage(@PathParam("name") String fileName) throws IOException {
    fileName += ".png";
    return attachFileStream(fileName);
  }

  private InputStream attachFileStream(String fileName) throws IOException {
    java.nio.file.Path dest = BASE_DIR.resolve(fileName);

    if (!Files.exists(dest)) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    return Files.newInputStream(dest);
  }

  private void closeDatabaseConnection(Connection con) {
    try {
      if (!con.isClosed()) {
        con.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
