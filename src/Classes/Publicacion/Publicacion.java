package Classes.Publicacion;

import java.util.Set;

public final class Publicacion {
    private int id;
    private Set<String> tags;
    private boolean activo;
    private Obra obra;

    // builder
    public Publicacion() { }
    public Publicacion(int id, Set<String> tags, boolean activo, Obra obra) {
        this.id = id;
        this.tags = tags;
        this.activo = activo;
        this.obra = obra;
    }

    // getters & setters
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }
}
