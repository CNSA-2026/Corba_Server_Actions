package BufferApp;

/**
* BufferApp/NoticiaHolder.java .
* Holder CORBA para parametro out de tipo Noticia.
*/
public final class NoticiaHolder implements org.omg.CORBA.portable.Streamable
{
  public BufferApp.Noticia value = null;

  public NoticiaHolder ()
  {
  }

  public NoticiaHolder (BufferApp.Noticia initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = BufferApp.NoticiaHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    BufferApp.NoticiaHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return BufferApp.NoticiaHelper.type ();
  }
}
