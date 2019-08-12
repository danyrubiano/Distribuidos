package cl.usach.sd;

import java.util.ArrayList;
/**
 * Clase la cual vamos a utilizar para enviar datos de un Peer a otro
 */

/* Se debe tener el camino guardado de los saltos que se hacen a otros nodos en la red
 *  para retornar la consulta.
 *  Mensaje tiene el nodo emisor, la consulta y el nodo receptor
 *  Mensaje: [NodoEmisor, NodoReceptor, Consulta, respuesta, IdaOVuelta, Camino]
 */
public class Message {
	
	// Guarda el id del nodo emisor
	private int emisor;
	// Guarda el id del nodo destino
	private int destino;
	// Guarda la consulta generada
	private int consulta;
	// Identifica si va de ida o de vuelta, 1: Ida, 0: Vuelta
	private int idaVuelta; 
	// Guarda la respuesta
	private int respuesta;
	// Lista para guardar el camino
	private ArrayList<Integer> Camino;
	// guarda la posicion que va seteando en el camino, para luego retornar la respuesta
	private int contador;
    
	public Message(int emisor, int destino, int consulta, int idaVuelta, int respuesta, ArrayList<Integer> Camino, int contador) {

		this.setConsulta(consulta);
		this.setDestino(destino);
		this.setEmisor(emisor);
		this.setCamino(Camino);
		this.setIdaVuelta(idaVuelta);
		this.setRespuesta(respuesta);
		this.setContador(contador);
	}

	public int getDestino() {
		return destino;
	}

	public void setDestino(int destino) {
		this.destino = destino;
	}

	public int getConsulta() {
		return consulta;
	}

	public void setConsulta(int consulta) {
		this.consulta = consulta;
	}
	
	public int getEmisor() {
		return emisor;
	}

	public void setEmisor(int emisor) {
		this.emisor = emisor;
	}
	
	public ArrayList<Integer> getCamino() {
		return Camino;
	}

	public void setCamino(ArrayList<Integer> Camino) {
		this.Camino = Camino;
	}
	
	public int getIdaVuelta() {
		return idaVuelta;
	}

	public void setIdaVuelta(int idaVuelta) {
		this.idaVuelta = idaVuelta;
	}
	
	public int getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(int respuesta) {
		this.respuesta = respuesta;
	}
	
	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}
}
