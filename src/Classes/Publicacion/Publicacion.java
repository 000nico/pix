package Classes.Publicacion;

import java.util.HashSet;
import java.util.Set;

// publicacion: la obra que se ve en el feed, junto con sus tags
public final class Publicacion {
    private int id;
    private Set<String> tags;
    private boolean activo;
    private Obra obra;

    //constructores
    public Publicacion() { }

    public Publicacion(int id, Set<String> tags, boolean activo, Obra obra) {
        this.id = id;
        this.tags = tags;
        this.activo = activo;
        this.obra = obra;
    }

    //getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }
}
