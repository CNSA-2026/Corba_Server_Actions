package ClienteTest;

import BufferApp.Buffer;
import BufferApp.BufferHelper;
import BufferApp.Noticia;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class TestValidacionesNoticias {

	private static Noticia noticia(String fecha, String interes, String titulo, String descripcion, String[] etiquetas) {
		return new Noticia(fecha, interes, titulo, descripcion, etiquetas);
	}

	private static void assertCaso(Buffer buffer, String nombre, Noticia n, boolean esperado) {
		boolean actual = buffer.put(n);
		System.out.println(nombre + " -> esperado=" + esperado + ", actual=" + actual + (actual == esperado ? " [OK]" : " [FALLO]"));
	}

	public static void main(String[] args) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			Buffer buffer = BufferHelper.narrow(ncRef.resolve_str("Buffer"));

			buffer.fijarLimiteNoticias(10);

			assertCaso(buffer, "Valida", noticia("25/12/2025", "alta", "Festival Almeria", "El proximo verano se celebrara un festival con gran impacto en la ciudad.", new String[]{"#musica", "#festivalAlmeria"}), true);
			assertCaso(buffer, "Fecha invalida", noticia("2025-12-25", "alta", "Titulo valido", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"#ok"}), false);
			assertCaso(buffer, "Interes invalido", noticia("25/12/2025", "urgente", "Titulo valido", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"#ok"}), false);
			assertCaso(buffer, "Titulo corto", noticia("25/12/2025", "media", "abc", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"#ok"}), false);
			assertCaso(buffer, "Descripcion corta", noticia("25/12/2025", "media", "Titulo correcto", "muy corta", new String[]{"#ok"}), false);
			assertCaso(buffer, "Etiqueta invalida", noticia("25/12/2025", "baja", "Titulo correcto", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"etiquetaSinHash"}), false);
			assertCaso(buffer, "Demasiadas etiquetas", noticia("25/12/2025", "baja", "Titulo correcto", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"#a", "#b", "#c", "#d", "#e", "#f", "#g"}), false);
			assertCaso(buffer, "Interes con espacios y mayusculas", noticia("25/12/2025", "  ALTA  ", "Titulo correcto", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"#ok"}), true);
			assertCaso(buffer, "Titulo con espacios no contados", noticia("25/12/2025", "media", "   Titulo      con      espacios   ", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"#ok"}), true);
			assertCaso(buffer, "Etiqueta con espacios alrededor", noticia("25/12/2025", "baja", "Titulo correcto", "Descripcion suficientemente larga para cumplir la restriccion minima.", new String[]{"   #etiqueta   "}), true);
		} catch (Exception e) {
			System.out.println("ERROR en test de validaciones: " + e);
			e.printStackTrace(System.out);
		}
	}
}
