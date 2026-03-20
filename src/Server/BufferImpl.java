package Server;

import org.omg.CORBA.ORB;
import BufferApp._BufferImplBase;

// TODO: Auto-generated Javadoc
/**
 * The Class BufferImpl.
 */
class BufferImpl extends _BufferImplBase {
	
	/** The orb. */
	private ORB orb;
	
	/** The buf. */
	private String buf[];
	
	/** The elementos. */
	private int elementos;
	
	/** The max elementos. */
	private static int maxElementos = 5;

	/**
	 * Instantiates a new buffer impl.
	 */
	// implementa el metodo constructor
	BufferImpl() {
		buf = new String[maxElementos];
		elementos = 0;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#put(java.lang.String)
	 */
	// implementa el metodo put()
	public boolean put(String elemento) {
		if (elementos < maxElementos) {
			buf[elementos] = elemento;
			elementos++;
			System.out.println(buf[elementos - 1] + "\tElementos: " + elementos);
			return true;
		} else {
			elemento = "PILA LLENA";
			System.out.println("PILA LLENA");
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#get(org.omg.CORBA.StringHolder)
	 */
	// implementa el metodo get()
	public boolean get(org.omg.CORBA.StringHolder elemento) {
		int i;
		if (elementos >= 3) {
			elemento.value = buf[0];
			for (i = 0; i < maxElementos - 1; i++)
				buf[i] = buf[i + 1];
			elementos--;
			return true;
		} else {
			elemento.value = "No se puede recibir hasta que no haya mas de 3 mensajes";
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#read(org.omg.CORBA.StringHolder)
	 */
	// implementa el metodo read()
	public boolean read(org.omg.CORBA.StringHolder elemento) {
		if (elementos >= 3) {
			elemento.value = buf[0];
			return true;
		} else
			elemento.value="No se puede leer hasta que no haya mas de 3 mensajes";
			return false;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#num_elementos()
	 */
	public int num_elementos() {
		
		return elementos;
	}

	/* (non-Javadoc)
	 * @see BufferApp.BufferOperations#shutdown()
	 */
	// implementa el metodo shutdown()
	public void shutdown() {
		orb.shutdown(false);
	}
}
