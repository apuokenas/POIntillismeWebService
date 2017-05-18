package lt.tumenas.pointillisme.ws;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

  private JAXBContext context;
  private Class[] types = {POIConverter.class};

  public JAXBContextResolver() throws Exception {
    this.context = new JSONJAXBContext(
      JSONConfiguration.mapped().arrays("pois").build(),
      types);

  }

  public JAXBContext getContext(Class objectType) {
    for (Class type : types) {
      if (type == objectType) {
        return context;
      }
    }

    return null;
  }

}
