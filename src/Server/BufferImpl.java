package Server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;

import BufferApp.Noticia;
import BufferApp.NoticiaHolder;
import BufferApp._BufferImplBase;

// TODO: Auto-generated Javadoc
/**
 * The Class BufferImpl.
 */
class BufferImpl extends _BufferImplBase {
	
	/** The orb. */
	private ORB orb;
	
	/** Lista FIFO de noticias. */
	private final List<Noticia> buf;

	/** The max elementos. */
	private int maxElementos = 5;

	private static final Pattern FECHA_PATTERN = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}");
	private static final Pattern ETIQUETA_PATTERN = Pattern.compile("#\\S+");

	/**
	 * Instantiates a new buffer impl.
	 */
	// implementa el metodo constructor
	BufferImpl() {
		buf = new ArrayList<Noticia>();
	}

	public void setORB(ORB orbVal) {
		orb = orbVal;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#put(java.lang.String)
	 */
	// implementa el metodo put()
	public synchronized boolean put(Noticia noticia) {
		if (!esNoticiaValida(noticia)) {
			return false;
		}
		if (buf.size() >= maxElementos) {
			System.out.println("BUFFER LLENO");
			return false;
		}
		buf.add(copiarNoticia(noticia));
		System.out.println("Insertada noticia. Elementos: " + buf.size());
		return true;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#get(org.omg.CORBA.StringHolder)
	 */
	// implementa el metodo get()
	public synchronized boolean get(NoticiaHolder noticia) {
		if (buf.isEmpty()) {
			noticia.value = noticiaVacia();
			return false;
		}
		noticia.value = copiarNoticia(buf.remove(0));
		return true;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#read(org.omg.CORBA.StringHolder)
	 */
	// implementa el metodo read()
	public synchronized boolean read(NoticiaHolder noticia) {
		if (buf.isEmpty()) {
			noticia.value = noticiaVacia();
			return false;
		}
		noticia.value = copiarNoticia(buf.get(0));
		return true;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#num_elementos()
	 */
	public synchronized int num_elementos() {
		return buf.size();
	}

	public synchronized void fijarLimiteNoticias(int numero_maximo) {
		if (numero_maximo < 0) {
			numero_maximo = 0;
		}
		maxElementos = numero_maximo;
		while (buf.size() > maxElementos) {
			buf.remove(buf.size() - 1);
		}
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#shutdown()
	 */
	// implementa el metodo shutdown()
	public void shutdown() {
		if (orb != null) {
			orb.shutdown(false);
		}
	}

	private boolean esNoticiaValida(Noticia noticia) {
		if (noticia == null) {
			return false;
		}
		if (!esFechaValida(noticia.fecha)) {
			return false;
		}
		if (!esInteresValido(noticia.interes)) {
			return false;
		}
		if (!longitudNoBlancosEnRango(noticia.titulo, 5, 30)) {
			return false;
		}
		if (!longitudNoBlancosEnRango(noticia.descripcion, 20, 250)) {
			return false;
		}
		return etiquetasValidas(noticia.etiquetas);
	}

	private boolean esFechaValida(String fecha) {
		return fecha != null && FECHA_PATTERN.matcher(fecha.trim()).matches();
	}

	private boolean esInteresValido(String interes) {
		if (interes == null) {
			return false;
		}
		String normalized = interes.trim().toLowerCase();
		return "alta".equals(normalized) || "media".equals(normalized) || "baja".equals(normalized);
	}

	private boolean longitudNoBlancosEnRango(String texto, int min, int max) {
		if (texto == null) {
			return false;
		}
		String sinBlancos = texto.replaceAll("\\s+", "");
		int len = sinBlancos.length();
		return len >= min && len <= max;
	}

	private boolean etiquetasValidas(String[] etiquetas) {
		if (etiquetas == null || etiquetas.length < 1 || etiquetas.length > 6) {
			return false;
		}
		for (int i = 0; i < etiquetas.length; i++) {
			if (etiquetas[i] == null || !ETIQUETA_PATTERN.matcher(etiquetas[i].trim()).matches()) {
				return false;
			}
		}
		return true;
	}

	private Noticia copiarNoticia(Noticia n) {
		String[] etiquetas = n.etiquetas == null ? new String[0] : n.etiquetas.clone();
		return new Noticia(n.fecha, n.interes, n.titulo, n.descripcion, etiquetas);
	}

	private Noticia noticiaVacia() {
		return new Noticia("", "", "", "", new String[0]);
	}
}
