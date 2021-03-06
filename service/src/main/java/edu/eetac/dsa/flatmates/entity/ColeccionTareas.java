package edu.eetac.dsa.flatmates.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 08/11/2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColeccionTareas {
    @InjectLinks({
    })
    private List<Link> links;
    private List<tareas> tareas = new ArrayList<>();

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<tareas> getTareas() {
        return tareas;
    }

    public void setTareas(List<tareas> tareas) {
        this.tareas = tareas;
    }
}
