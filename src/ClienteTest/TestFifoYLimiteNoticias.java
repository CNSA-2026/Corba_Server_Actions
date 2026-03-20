package ClienteTest;

import BufferApp.Buffer;
import BufferApp.BufferHelper;
import BufferApp.Noticia;
import BufferApp.NoticiaHolder;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class TestFifoYLimiteNoticias {

	private static Noticia noticia(String fecha, String titulo, String etiqueta) {
		return new Noticia(fecha, "media", titulo, "Descripcion de prueba con longitud suficiente para cumplir el minimo de validacion.", new String[]{etiqueta});
	}

	private static void printResultado(String test, boolean ok) {
		System.out.println(test + (ok ? " [OK]" : " [FALLO]"));
	}

	public static void main(String[] args) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			Buffer buffer = BufferHelper.narrow(ncRef.resolve_str("Buffer"));
			NoticiaHolder out = new NoticiaHolder();

			buffer.fijarLimiteNoticias(3);

			boolean p1 = buffer.put(noticia("01/01/2026", "Noticia uno", "#n1"));
			boolean p2 = buffer.put(noticia("02/01/2026", "Noticia dos", "#n2"));
			boolean p3 = buffer.put(noticia("03/01/2026", "Noticia tres", "#n3"));
			boolean p4 = buffer.put(noticia("04/01/2026", "Noticia cuatro", "#n4"));
			printResultado("Limite inicial (3 inserciones y 1 rechazo)", p1 && p2 && p3 && !p4);

			boolean readOk = buffer.read(out);
			printResultado("Read no destructivo", readOk && "Noticia uno".equals(out.value.titulo) && buffer.num_elementos() == 3);

			boolean g1 = buffer.get(out);
			boolean fifo1 = g1 && "Noticia uno".equals(out.value.titulo);
			boolean g2 = buffer.get(out);
			boolean fifo2 = g2 && "Noticia dos".equals(out.value.titulo);
			printResultado("FIFO en get", fifo1 && fifo2);

			buffer.put(noticia("05/01/2026", "Noticia cinco", "#n5"));
			buffer.put(noticia("06/01/2026", "Noticia seis", "#n6"));
			buffer.fijarLimiteNoticias(1);
			boolean trimCount = buffer.num_elementos() == 1;
			buffer.read(out);
			boolean trimPolicy = "Noticia tres".equals(out.value.titulo);
			printResultado("Recorte al reducir limite", trimCount && trimPolicy);

			buffer.get(out);
			boolean emptyRead = !buffer.read(out);
			boolean emptyGet = !buffer.get(out);
			printResultado("Get/Read en vacio", emptyRead && emptyGet);
		} catch (Exception e) {
			System.out.println("ERROR en test FIFO/limite: " + e);
			e.printStackTrace(System.out);
		}
	}
}
