package edu.eetac.dsa.flatmates.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import java.util.List;

/**
 * Created by Admin on 24/11/2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrupoUsuario {
    @InjectLinks({})
    private List<Link> links;
    private String grupoid;
    private String userid;
    private int puntos;
    private String loginid;

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getGrupoid() {
        return grupoid;
    }

    public void setGrupoid(String grupoid) {
        this.grupoid = grupoid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
