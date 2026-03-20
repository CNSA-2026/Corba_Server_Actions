package BufferApp;

/**
* BufferApp/NoticiaHelper.java .
* Helper CORBA para serializacion de Noticia.
*/
abstract public class NoticiaHelper
{
  private static String _id = "IDL:BufferApp/Noticia:1.0";

  public static void insert (org.omg.CORBA.Any a, BufferApp.Noticia that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static BufferApp.Noticia extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc (_id);
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "fecha",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "interes",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "titulo",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "descripcion",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, org.omg.CORBA.ORB.init ().create_string_tc (0));
          _members0[4] = new org.omg.CORBA.StructMember (
            "etiquetas",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (BufferApp.NoticiaHelper.id (), "Noticia", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static BufferApp.Noticia read (org.omg.CORBA.portable.InputStream istream)
  {
    BufferApp.Noticia value = new BufferApp.Noticia ();
    value.fecha = istream.read_string ();
    value.interes = istream.read_string ();
    value.titulo = istream.read_string ();
    value.descripcion = istream.read_string ();
    value.etiquetas = org.omg.CORBA.StringSeqHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, BufferApp.Noticia value)
  {
    if (value == null)
    {
      value = new BufferApp.Noticia("", "", "", "", new String[0]);
    }
    ostream.write_string (value.fecha == null ? "" : value.fecha);
    ostream.write_string (value.interes == null ? "" : value.interes);
    ostream.write_string (value.titulo == null ? "" : value.titulo);
    ostream.write_string (value.descripcion == null ? "" : value.descripcion);
    org.omg.CORBA.StringSeqHelper.write (ostream, value.etiquetas == null ? new String[0] : value.etiquetas);
  }
}
