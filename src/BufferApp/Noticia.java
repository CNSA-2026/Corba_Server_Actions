package BufferApp;

/**
* BufferApp/Noticia.java .
* Adaptado para representar una noticia en el buffer CORBA.
*/
public final class Noticia implements org.omg.CORBA.portable.IDLEntity
{
  public String fecha = "";
  public String interes = "";
  public String titulo = "";
  public String descripcion = "";
  public String[] etiquetas = null;

  public Noticia ()
  {
  }

  public Noticia (String _fecha, String _interes, String _titulo, String _descripcion, String[] _etiquetas)
  {
    fecha = _fecha;
    interes = _interes;
    titulo = _titulo;
    descripcion = _descripcion;
    etiquetas = _etiquetas;
  }
}
