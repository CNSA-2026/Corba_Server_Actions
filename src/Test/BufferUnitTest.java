package ClienteTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import BufferApp.BufferOperations;
import BufferApp.Noticia;
import BufferApp.NoticiaHolder;

public class BufferUnitTest {

	private static class FakeBuffer implements BufferOperations {
		private final List<Noticia> datos = new ArrayList<Noticia>();
		private int limite = 2;
		private boolean apagado;

		@Override
		public synchronized int num_elementos() {
			return datos.size();
		}

		@Override
		public synchronized boolean put(Noticia noticia) {
			if (noticia == null || datos.size() >= limite) {
				return false;
			}
			datos.add(copiar(noticia));
			return true;
		}

		@Override
		public synchronized boolean get(NoticiaHolder noticia) {
			if (datos.isEmpty()) {
				noticia.value = vacia();
				return false;
			}
			noticia.value = copiar(datos.remove(0));
			return true;
		}

		@Override
		public synchronized boolean read(NoticiaHolder noticia) {
			if (datos.isEmpty()) {
				noticia.value = vacia();
				return false;
			}
			noticia.value = copiar(datos.get(0));
			return true;
		}

		@Override
		public synchronized void fijarLimiteNoticias(int numero_maximo) {
			if (numero_maximo < 0) {
				numero_maximo = 0;
			}
			limite = numero_maximo;
			while (datos.size() > limite) {
				datos.remove(datos.size() - 1);
			}
		}

		@Override
		public void shutdown() {
			apagado = true;
		}

		public boolean isApagado() {
			return apagado;
		}

		private Noticia copiar(Noticia n) {
			String[] etiquetas = n.etiquetas == null ? new String[0] : n.etiquetas.clone();
			return new Noticia(n.fecha, n.interes, n.titulo, n.descripcion, etiquetas);
		}

		private Noticia vacia() {
			return new Noticia("", "", "", "", new String[0]);
		}
	}

	private FakeBuffer buffer;
	private NoticiaHolder holder;

	@BeforeEach
	public void setUp() {
		buffer = new FakeBuffer();
		holder = new NoticiaHolder();
	}

	private Noticia noticia(String titulo, String etiqueta) {
		return new Noticia("20/03/2026", "media", titulo,
				"Descripcion valida para pruebas unitarias del buffer.", new String[] { etiqueta });
	}

	@Test
	public void putYLimite() {
		assertTrue(buffer.put(noticia("n1", "#uno")));
		assertTrue(buffer.put(noticia("n2", "#dos")));
		assertFalse(buffer.put(noticia("n3", "#tres")));
		assertEquals(2, buffer.num_elementos());
	}

	@Test
	public void readNoDestructivoYGetFIFO() {
		buffer.put(noticia("n1", "#uno"));
		buffer.put(noticia("n2", "#dos"));

		assertTrue(buffer.read(holder));
		assertEquals("n1", holder.value.titulo);
		assertEquals(2, buffer.num_elementos());

		assertTrue(buffer.get(holder));
		assertEquals("n1", holder.value.titulo);
		assertTrue(buffer.get(holder));
		assertEquals("n2", holder.value.titulo);
	}

	@Test
	public void operacionesEnVacio() {
		assertFalse(buffer.read(holder));
		assertFalse(buffer.get(holder));
		assertEquals("", holder.value.titulo);
	}

	@Test
	public void fijarLimiteRecortaYShutdown() {
		buffer.put(noticia("a", "#a"));
		buffer.put(noticia("b", "#b"));
		buffer.fijarLimiteNoticias(1);
		assertEquals(1, buffer.num_elementos());
		assertTrue(buffer.read(holder));
		assertEquals("a", holder.value.titulo);
		buffer.shutdown();
		assertTrue(buffer.isApagado());
	}
}
