package ClienteTest;

import BufferApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class Cliente {
	static Buffer bufferImpl;

	private static Noticia crearNoticia(String fecha, String interes, String titulo, String descripcion, String[] etiquetas) {
		return new Noticia(fecha, interes, titulo, descripcion, etiquetas);
	}

	private static String noticiaToString(Noticia n) {
		StringBuilder sb = new StringBuilder();
		sb.append("[")
			.append(n.fecha).append(" | ")
			.append(n.interes).append(" | ")
			.append(n.titulo).append(" | etiquetas=");
		if (n.etiquetas != null) {
			for (int i = 0; i < n.etiquetas.length; i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(n.etiquetas[i]);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static void main(String args[]) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = "Buffer";
			bufferImpl = BufferHelper.narrow(ncRef.resolve_str(name));
			NoticiaHolder elem = new NoticiaHolder();

			Noticia n1 = crearNoticia("25/12/2025", "alta", "Festival en Almeria", "Evento musical previsto para el proximo verano en la costa de Almeria.", new String[] {"#musica", "#festivalAlmeria"});
			Noticia n2 = crearNoticia("02/01/2026", "baja", "Cierre de calle", "La calle principal estara cerrada por mantenimiento durante el fin de semana.", new String[] {"#trafico"});
			Noticia nInvalida = crearNoticia("2026-01-02", "media", "abc", "corta", new String[] {"sinHashtag"});

			System.out.println("Referencia:" + bufferImpl);
			System.out.println("put n1: " + bufferImpl.put(n1));
			System.out.println("put n2: " + bufferImpl.put(n2));
			System.out.println("put invalida: " + bufferImpl.put(nInvalida));

			System.out.println("read: " + bufferImpl.read(elem) + "\t" + noticiaToString(elem.value));
			System.out.println("get: " + bufferImpl.get(elem) + "\t" + noticiaToString(elem.value));
			System.out.println("get: " + bufferImpl.get(elem) + "\t" + noticiaToString(elem.value));

			bufferImpl.fijarLimiteNoticias(1);
			System.out.println("num tras fijar limite=1: " + bufferImpl.num_elementos());

			System.out.println("put n1 (de nuevo): " + bufferImpl.put(n1));
			System.out.println("put n2 (de nuevo, deberia fallar por limite): " + bufferImpl.put(n2));
			System.out.println(bufferImpl.num_elementos()+"");
			bufferImpl.shutdown();
		} catch (Exception e) {
			System.out.println("ERROR : " + e);
			e.printStackTrace(System.out);
		}
	}
}
